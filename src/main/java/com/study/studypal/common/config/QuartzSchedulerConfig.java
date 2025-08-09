package com.study.studypal.common.config;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.errorCode.ConfigErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzSchedulerConfig {
    private final Scheduler scheduler;
    private final Environment environment;

    @PostConstruct
    public void scheduleJobs() {
        String[] jobPackages = getJobPackagesFromConfig();
        ClassPathScanningCandidateComponentProvider scanner = createJobClassScanner();

        for (String pkg : jobPackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(pkg.trim());

            for (BeanDefinition bd : candidateComponents) {
                Class<? extends Job> jobClass = loadJobClass(bd);
                String jobKey = toJobKey(jobClass.getSimpleName());
                String cronExpression = getCronExpression(jobKey);

                if(cronExpression != null) {
                    registerJob(jobClass, jobKey, cronExpression);
                    log.info("Registered Job: {} (cron: {})", jobKey, cronExpression);
                }
                else {
                    log.info("No cron expression for job: {}", jobKey);
                }
            }
        }
    }

    private String[] getJobPackagesFromConfig() {
        String packages = environment.getProperty("quartz.job-packages");
        if (packages == null || packages.isBlank()) {
            throw new BaseException(ConfigErrorCode.MISSING_JOB_PACKAGES);
        }
        return packages.split(",");
    }

    private ClassPathScanningCandidateComponentProvider createJobClassScanner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Job.class));
        return scanner;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Job> loadJobClass(BeanDefinition bd) {
        try {
            Class<?> clazz = Class.forName(bd.getBeanClassName());
            if (!Job.class.isAssignableFrom(clazz)) {
                throw new BaseException(ConfigErrorCode.INVALID_JOB_CLASS, bd.getBeanClassName());
            }
            return (Class<? extends Job>) clazz;
        } catch (ClassNotFoundException e) {
            throw new BaseException(ConfigErrorCode.CLASS_NOT_FOUND, bd.getBeanClassName());
        }
    }

    private String getCronExpression(String jobKey) {
        return environment.getProperty("quartz.jobs." + jobKey);
    }

    private String toJobKey(String className) {
        // InvitationCleanUpJob -> invitation-clean-up
        return className
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("-Job$", "")
                .toLowerCase();
    }

    private void registerJob(Class<? extends Job> jobClass, String jobName, String cronExpression) {
        JobKey jobKey = new JobKey(jobName);

        try {
            // If the job already exists, skip the registration
            if (scheduler.checkExists(jobKey)) {
                log.info("Job {} already exists. Skipping registration.", jobName);
                return;
            }

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(jobName + "Trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            throw new BaseException(ConfigErrorCode.REGISTER_JOB_FAILED, jobName);
        }
    }
}

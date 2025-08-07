package com.study.studypal.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AssignableTypeFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzSchedulerConfig {
    private final Scheduler scheduler;
    private final Environment environment;

    @PostConstruct
    public void scheduleJobs() throws Exception {
        //Get job packages from config
        String[] jobPackages = environment.getProperty("quartz.job-packages").split(",");

        // Scanner Quartz Job
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Job.class));

        for (String pkg : jobPackages) {
            for (BeanDefinition bd : scanner.findCandidateComponents(pkg.trim())) {
                Class<?> clazz = Class.forName(bd.getBeanClassName());

                if (Job.class.isAssignableFrom(clazz)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Job> jobClass = (Class<? extends Job>) clazz;

                    //Create jobKey from class name
                    String jobKey = toJobKey(clazz.getSimpleName());
                    String cronExpression = environment.getProperty("quartz.jobs." + jobKey);

                    if (cronExpression != null) {
                        registerJob(jobClass, jobKey, cronExpression);
                        log.info("Registered Job: {} (cron: {})", jobKey, cronExpression);
                    } else {
                        log.info("No cron expression for job: {}", jobKey);
                    }
                }
            }
        }
    }

    private String toJobKey(String className) {
        // InvitationCleanUpJob -> invitation-clean-up
        return className
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("-Job$", "")
                .toLowerCase();
    }

    private void registerJob(Class<? extends Job> jobClass, String jobName, String cronExpression) throws SchedulerException {
        //If the job already exists, skip the registration
        JobKey jobKey = new JobKey(jobName);
        if (scheduler.checkExists(jobKey)) {
            System.out.printf("Job %s already exists. Skipping registration.%n", jobName);
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
    }
}

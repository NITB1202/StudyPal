package com.study.studypal.file.job;

import com.study.studypal.file.config.FileProperties;
import com.study.studypal.file.service.internal.FileInternalService;
import com.study.studypal.file.service.internal.FolderInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileCleanUpJob implements Job {
  private final FileInternalService fileService;
  private final FolderInternalService folderService;
  private final FileProperties props;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(props.getFileCutoffDays());
    fileService.hardDeleteFilesBefore(cutoffTime);
    folderService.purgeEmptySoftDeletedFolders();
  }
}

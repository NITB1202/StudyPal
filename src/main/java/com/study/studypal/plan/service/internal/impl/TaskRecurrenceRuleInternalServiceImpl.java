package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskRecurrenceRule;
import com.study.studypal.plan.exception.TaskRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.TaskRecurrenceRuleRepository;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskRecurrenceRuleInternalServiceImpl implements TaskRecurrenceRuleInternalService {
  private final TaskRecurrenceRuleRepository ruleRepository;

  @Override
  public void validateClonedTaskDuration(LocalDateTime startDate, LocalDateTime dueDate) {
    if (!startDate.toLocalDate().equals(dueDate.toLocalDate()))
      throw new BaseException(TaskRecurrenceRuleErrorCode.RECURRING_TASK_DURATION_INVALID);
  }

  @Override
  public boolean isRootOrClonedTask(Task task) {
    return task.getParentTask() != null || ruleRepository.existsByTaskId(task.getId());
  }

  @Override
  public boolean isRootTask(Task task) {
    return ruleRepository.existsByTaskId(task.getId());
  }

  @Override
  public void updateRootTask(Task oldTask, Task newTask) {
    TaskRecurrenceRule rule =
        ruleRepository
            .findByTaskId(oldTask.getId())
            .orElseThrow(
                () -> new BaseException(TaskRecurrenceRuleErrorCode.RECURRENCE_RULE_NOT_FOUND));

    rule.setTask(newTask);
    ruleRepository.save(rule);
  }
}

package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.model.TaskManagement;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<TaskManagement> findById(Long id);
    TaskManagement save(TaskManagement task);
    List<TaskManagement> findAll();
    List<TaskManagement> findByReferenceIdAndReferenceType(Long referenceId,
                                                           com.yourcompany.workforcemgmt.common.model.enums.ReferenceType referenceType);
    List<TaskManagement> findByAssigneeIdIn(List<Long> assigneeIds);
}

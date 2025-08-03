package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.model.TaskActivity;

import java.util.List;

public interface TaskActivityRepository {
    List<TaskActivity> findByTaskId(Long taskId);
    TaskActivity save(TaskActivity activity);

}

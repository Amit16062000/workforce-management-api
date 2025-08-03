package com.yourcompany.workforcemgmt.Service.impl;

import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.TaskActivity;
import com.yourcompany.workforcemgmt.model.enums.Priority;

import java.util.List;

public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);
    TaskManagementDto updateTaskPriority(Long taskId, Priority priority);
    List<TaskManagementDto> getTasksByPriority(Priority priority, Long assigneeId);
    TaskManagementDto addComment(Long taskId, String comment, String user);
    List<TaskActivity> getTaskHistory(Long taskId);
}

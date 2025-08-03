package com.yourcompany.workforcemgmt.Service;

import com.yourcompany.workforcemgmt.Service.impl.TaskManagementService;
import com.yourcompany.workforcemgmt.common.exception.ResourceNotFoundException;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.mapper.ITaskManagementMapper;
import com.yourcompany.workforcemgmt.model.TaskActivity;
import com.yourcompany.workforcemgmt.model.TaskManagement;
import com.yourcompany.workforcemgmt.model.enums.ActivityType;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import com.yourcompany.workforcemgmt.model.enums.Task;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import com.yourcompany.workforcemgmt.repository.TaskActivityRepository;
import com.yourcompany.workforcemgmt.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    private final TaskRepository taskRepository;
    private final TaskActivityRepository activityRepository;
    private final ITaskManagementMapper taskMapper;

    public TaskManagementServiceImpl(TaskRepository taskRepository,
                                     TaskActivityRepository activityRepository,
                                     ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");

            TaskManagement savedTask = taskRepository.save(newTask);
            addActivity(savedTask.getId(), ActivityType.CREATED,
                    "Task created for " + item.getReferenceType(), "System");
            createdTasks.add(savedTask);
        }
        return taskMapper.modelListToDtoList(createdTasks);
    }

    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
                addActivity(task.getId(), ActivityType.STATUS_CHANGE,
                        "Status changed to " + item.getTaskStatus(), "System");
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }

    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!tasksOfType.isEmpty()) {
                TaskManagement taskToKeep = tasksOfType.get(0);
                taskToKeep.setAssigneeId(request.getAssigneeId());
                taskRepository.save(taskToKeep);
                addActivity(taskToKeep.getId(), ActivityType.ASSIGNEE_CHANGE,
                        "Task reassigned to " + request.getAssigneeId(), "System");

                for (int i = 1; i < tasksOfType.size(); i++) {
                    TaskManagement taskToCancel = tasksOfType.get(i);
                    taskToCancel.setStatus(TaskStatus.CANCELLED);
                    taskRepository.save(taskToCancel);
                    addActivity(taskToCancel.getId(), ActivityType.STATUS_CHANGE,
                            "Task cancelled due to reassignment", "System");
                }
            } else {
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                newTask.setPriority(Priority.MEDIUM);
                TaskManagement savedTask = taskRepository.save(newTask);
                addActivity(savedTask.getId(), ActivityType.CREATED,
                        "Task created and assigned to " + request.getAssigneeId(), "System");
            }
        }
        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }

    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .filter(task -> {
                    long taskStartTime = task.getTaskDeadlineTime() - 86400000;
                    return (taskStartTime >= request.getStartDate() && taskStartTime <= request.getEndDate()) ||
                            (taskStartTime < request.getStartDate() && task.getStatus() != TaskStatus.COMPLETED);
                })
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }

    @Override
    public TaskManagementDto updateTaskPriority(Long taskId, Priority priority) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setPriority(priority);
        addActivity(taskId, ActivityType.PRIORITY_CHANGE,
                "Priority changed to " + priority, "System");
        return taskMapper.modelToDto(taskRepository.save(task));
    }

    @Override
    public List<TaskManagementDto> getTasksByPriority(Priority priority, Long assigneeId) {
        List<TaskManagement> tasks;
        if (assigneeId != null) {
            tasks = taskRepository.findByAssigneeIdIn(List.of(assigneeId));
        } else {
            tasks = taskRepository.findAll();
        }

        return taskMapper.modelListToDtoList(tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList()));
    }

    @Override
    public TaskManagementDto addComment(Long taskId, String comment, String user) {
        TaskManagement task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        addActivity(taskId, ActivityType.COMMENT, comment, user);
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskActivity> getTaskHistory(Long taskId) {
        return activityRepository.findByTaskId(taskId);
    }

    private void addActivity(Long taskId, ActivityType type, String description, String user) {
        TaskActivity activity = new TaskActivity();
        activity.setTaskId(taskId);
        activity.setType(type);
        activity.setDescription(description);
        activity.setUser(user);
        activity.setTimestamp(LocalDateTime.now());
        activityRepository.save(activity);
    }
}

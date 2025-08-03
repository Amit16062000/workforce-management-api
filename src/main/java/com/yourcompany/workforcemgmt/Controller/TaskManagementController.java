package com.yourcompany.workforcemgmt.Controller;

import com.yourcompany.workforcemgmt.Service.impl.TaskManagementService;
import com.yourcompany.workforcemgmt.common.model.response.Response;
import com.yourcompany.workforcemgmt.dto.*;
import com.yourcompany.workforcemgmt.model.TaskActivity;
import com.yourcompany.workforcemgmt.model.enums.Priority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {
    private final TaskManagementService taskManagementService;

    public TaskManagementController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping("/{id}")
    public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
        return new Response<>(taskManagementService.findTaskById(id));
    }

    @PostMapping("/create")
    public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
        return new Response<>(taskManagementService.createTasks(request));
    }

    @PostMapping("/update")
    public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
        return new Response<>(taskManagementService.updateTasks(request));
    }

    @PostMapping("/assign-by-ref")
    public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
        return new Response<>(taskManagementService.assignByReference(request));
    }

    @PostMapping("/fetch-by-date")
    public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
        return new Response<>(taskManagementService.fetchTasksByDate(request));
    }

    @PostMapping("/update-priority")
    public Response<TaskManagementDto> updateTaskPriority(
            @RequestParam Long taskId,
            @RequestParam Priority priority) {
        return new Response<>(taskManagementService.updateTaskPriority(taskId, priority));
    }

    @GetMapping("/by-priority/{priority}")
    public Response<List<TaskManagementDto>> getTasksByPriority(
            @PathVariable Priority priority,
            @RequestParam(required = false) Long assigneeId) {
        return new Response<>(taskManagementService.getTasksByPriority(priority, assigneeId));
    }

    @PostMapping("/{taskId}/comment")
    public Response<TaskManagementDto> addComment(
            @PathVariable Long taskId,
            @RequestParam String comment,
            @RequestParam String user) {
        return new Response<>(taskManagementService.addComment(taskId, comment, user));
    }

    @GetMapping("/{taskId}/history")
    public Response<List<TaskActivity>> getTaskHistory(@PathVariable Long taskId) {
        return new Response<>(taskManagementService.getTaskHistory(taskId));
    }
}

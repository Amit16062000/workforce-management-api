package com.yourcompany.workforcemgmt.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yourcompany.workforcemgmt.model.enums.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateTaskRequest {
    private List<RequestItem> requests;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RequestItem {
        private Long taskId;
        private TaskStatus taskStatus;
        private String description;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public TaskStatus getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(TaskStatus taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public List<RequestItem> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestItem> requests) {
        this.requests = requests;
    }
}

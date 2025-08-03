package com.yourcompany.workforcemgmt.repository;

import com.yourcompany.workforcemgmt.model.TaskActivity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskActivityRepository implements TaskActivityRepository {

    private final Map<Long, TaskActivity> activityStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public List<TaskActivity> findByTaskId(Long taskId) {
        return new ArrayList<>(activityStore.values()).stream()
                .filter(activity -> activity.getTaskId().equals(taskId))
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public TaskActivity save(TaskActivity activity) {
        if (activity.getId() == null) {
            activity.setId(idCounter.incrementAndGet());
        }
        activityStore.put(activity.getId(), activity);
        return activity;
    }
}

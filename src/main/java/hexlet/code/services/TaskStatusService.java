package hexlet.code.services;

import hexlet.code.dtos.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repositories.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus findStatusById(Long id) {
        return taskStatusRepository.findById(id).get();
    }
    public TaskStatus createStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus updateStatus(Long id, TaskStatusDto statusDto) {
        TaskStatus status = findStatusById(id);
        status.setName(statusDto.getName());
        return taskStatusRepository.save(status);
    }

    public void deleteStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}

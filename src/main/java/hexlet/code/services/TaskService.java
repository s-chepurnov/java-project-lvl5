package hexlet.code.services;

import com.querydsl.core.types.Predicate;
import hexlet.code.dtos.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelService labelService;

    public Iterable<Task> findAll(Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    public Task findTaskById(Long id) {
        return taskRepository.findById(id).get();
    }

    public Task createTask(TaskDto taskDto) {
        Task task = fromDto(taskDto);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).get();
        updateFromDto(task, taskDto);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private Task fromDto(final TaskDto dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        task.setAuthor(getAuthor(dto));
        task.setExecutor(getExecutor(dto));

        task.setTaskStatus(getStatus(dto));
        task.setLabels(getLabels(dto));

        return task;
    }

    private void updateFromDto(Task task, TaskDto dto) {
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        task.setTaskStatus(getStatus(dto));
        task.setLabels(getLabels(dto));
        task.setExecutor(getExecutor(dto));
    }

    private User getExecutor(TaskDto dto) {
        Long executorId = dto.getExecutorId();
        if (executorId != null) {
            User executor = userService.findUserById(executorId);
            return executor;
        }
        return null;
    }

    private User getAuthor(TaskDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authorEmail = auth.getName();
        User author = userService.findUserByEmail(authorEmail);
        //if author null then exception
        return author;
    }

    private Set<Label> getLabels(TaskDto dto) {
        Set<Long> labelIds = dto.getLabelIds();
        Set<Label> labels = labelIds.stream().map(id -> labelService.findLabelById(id))
                .collect(Collectors.toSet());
        return labels;
    }

    private TaskStatus getStatus(TaskDto dto) {
        Long taskStatusId = dto.getTaskStatusId();
        TaskStatus taskStatus = taskStatusService.findStatusById(taskStatusId);
        //if task status null then exception
        return taskStatus;
    }


}

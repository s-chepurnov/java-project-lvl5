package hexlet.code.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.dtos.TaskDto;
import hexlet.code.dtos.exceptions.ResponseError;
import hexlet.code.model.Task;
import hexlet.code.repositories.TaskRepository;
import hexlet.code.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "javainuseapi")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    public static final String ID = "/{id}";
    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.name
            """;

    @Operation(summary = "Get Tasks by Predicate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Task.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Error getting all tasks",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @GetMapping
    public Iterable<Task> getAll(
            @Parameter(description = "Predicate based on query params")
            @QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskService.findAll(predicate);
    }

    @Operation(summary = "Create new Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Error creating task",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Task create(
            @Parameter(schema = @Schema(implementation = TaskDto.class))
            @RequestBody @Valid final TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Get Task by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @GetMapping(ID)
    public Task findById(
            @Parameter(description = "id of the task to be searched")
            @PathVariable final long id) {
        return taskService.findTaskById(id);
    }

    @Operation(summary = "Update Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @PutMapping(ID)
    public Task update(
            @Parameter(description = "id of the task to be updated")
            @PathVariable final long id,
            @Parameter(schema = @Schema(implementation = TaskDto.class))
            @RequestBody @Valid final TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(summary = "Delete Task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Only author can delete this task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void delete(
            @Parameter(description = "id of the task to be deleted")
            @PathVariable final long id) {
        taskService.deleteTask(id);
    }
}

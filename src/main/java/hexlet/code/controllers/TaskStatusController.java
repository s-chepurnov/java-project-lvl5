package hexlet.code.controllers;

import hexlet.code.dtos.TaskStatusDto;
import hexlet.code.dtos.exceptions.ResponseError;
import hexlet.code.model.TaskStatus;
import hexlet.code.services.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
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
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/statuses")
@SecurityRequirement(name = "javainuseapi")
@AllArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public static final String ID = "/{id}";

    @Operation(summary = "Create new Task Status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task status created",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TaskStatus.class))}
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseError.class))}),
        @ApiResponse(responseCode = "403", description = "Access Denied",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseError.class))}),
        @ApiResponse(responseCode = "404", description = "Error creating task status",
                content = @Content),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                content = @Content)
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus create(
            @Parameter(schema = @Schema(implementation = TaskStatusDto.class))
            @RequestBody @Valid final TaskStatusDto taskStatus) {
        return taskStatusService.createStatus(taskStatus);
    }

    @Operation(summary = "Get Task statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = TaskStatus.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Error getting all task statuses",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusService.findAll();
    }

    @Operation(summary = "Get Task status by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @GetMapping(ID)
    public TaskStatus getById(
            @Parameter(description = "id of the task status to be searched")
            @PathVariable final long id) {
        return taskStatusService.findStatusById(id);
    }

    @Operation(summary = "Update Task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @PutMapping(ID)
    public TaskStatus update(
            @Parameter(description = "id of the task status to be updated")
            @PathVariable final long id,
            @Parameter(schema = @Schema(implementation = TaskStatusDto.class))
            @RequestBody @Valid final TaskStatusDto taskStatus) {
        return taskStatusService.updateStatus(id, taskStatus);
    }

    @Operation(summary = "Delete Task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @DeleteMapping(ID)
    public void delete(
            @Parameter(description = "id of the task status to be deleted")
            @PathVariable final long id) {
        taskStatusService.deleteStatus(id);
    }
}

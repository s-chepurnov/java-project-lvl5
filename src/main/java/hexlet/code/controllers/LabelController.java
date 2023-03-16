package hexlet.code.controllers;

import hexlet.code.dtos.LabelDto;
import hexlet.code.dtos.exceptions.ResponseError;
import hexlet.code.model.Label;
import hexlet.code.services.LabelService;
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
@RequestMapping("/api/labels")
@SecurityRequirement(name = "javainuseapi")
@AllArgsConstructor
public class LabelController {

    private final LabelService labelService;

    public static final String ID = "/{id}";

    @Operation(summary = "Get all Labels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content =
                @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Label.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Error getting all labels",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)

    })
    @GetMapping
    public List<Label> getAll() {
        return labelService.findAll();
    }

    @Operation(summary = "Create new Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Label.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Error creating label",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)

    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Label create(
            @Parameter(schema = @Schema(implementation = LabelDto.class))
            @RequestBody @Valid final LabelDto label) {
        return labelService.createLabel(label);
    }

    @Operation(summary = "Get label by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Label with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @GetMapping(ID)
    public Label findById(
            @Parameter(description = "id of the label to be searched")
            @PathVariable final long id) {
        return labelService.findLabelById(id);
    }

    @Operation(summary = "Update Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Label with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @PutMapping(ID)
    public Label update(
            @Parameter(description = "id of the label to be updated")
            @PathVariable final long id,
            @Parameter(schema = @Schema(implementation = LabelDto.class))
            @RequestBody @Valid final LabelDto label) {
        return labelService.updateLabel(id, label);
    }

    @Operation(summary = "Delete Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))}),
            @ApiResponse(responseCode = "404", description = "Label with that id not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content)
    })
    @DeleteMapping(ID)
    public void delete(
            @Parameter(description = "id of the label to be deleted")
            @PathVariable final long id) {
        labelService.deleteLabel(id);
    }
}


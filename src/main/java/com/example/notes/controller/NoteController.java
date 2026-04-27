package com.example.notes.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.notes.dto.ApiResponse;
import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.dto.PagedResponse;
import com.example.notes.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "CRUD operations for note management")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
        @Operation(
            summary = "Create a note",
            description = "Creates a new note with validated title and content",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                required = true,
                description = "Note payload",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Create note",
                        value = "{\n  \"title\": \"Daily Plan\",\n  \"content\": \"Finish Spring Boot API tasks\"\n}"))))
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Note created successfully"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(@Valid @RequestBody NoteRequest request) {
        NoteResponse response = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Note created successfully", response));
    }

    @GetMapping
        @Operation(summary = "Get notes", description = "Retrieves notes with pagination and sorting")
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public ResponseEntity<ApiResponse<PagedResponse<NoteResponse>>> getAllNotes(
            @Parameter(description = "Zero-based page index", example = "0", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "createdAt", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc", example = "desc", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Page<NoteResponse> notesPage = noteService.getAllNotes(PageRequest.of(page, size, sort));
        PagedResponse<NoteResponse> responseData = new PagedResponse<>(
                notesPage.getContent(),
                notesPage.getNumber(),
                notesPage.getSize(),
                notesPage.getTotalElements(),
                notesPage.getTotalPages(),
                notesPage.isLast());

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Notes retrieved successfully", responseData));
    }

    @GetMapping("/{id}")
        @Operation(summary = "Get note by id", description = "Retrieves a single note by its identifier")
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found", content = @Content),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public ResponseEntity<ApiResponse<NoteResponse>> getNoteById(@PathVariable String id) {
        NoteResponse note = noteService.getNoteById(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Note retrieved successfully", note));
    }

    @PutMapping("/{id}")
        @Operation(
            summary = "Update note",
            description = "Updates title and content of an existing note",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                required = true,
                description = "Updated note payload",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Update note",
                        value = "{\n  \"title\": \"Updated Plan\",\n  \"content\": \"Complete integration tests\"\n}"))))
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Note updated successfully"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found", content = @Content),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public ResponseEntity<ApiResponse<NoteResponse>> updateNote(@PathVariable String id,
                                                                @Valid @RequestBody NoteRequest request) {
        NoteResponse note = noteService.updateNote(id, request);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Note updated successfully", note));
    }

    @DeleteMapping("/{id}")
        @Operation(summary = "Delete note", description = "Deletes a note by its identifier")
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Note deleted successfully"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Note not found", content = @Content),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable String id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Note deleted successfully", null));
    }
}

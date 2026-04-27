package com.example.notes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.service.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @Test
    void createNote_ShouldReturnCreated() throws Exception {
        NoteRequest request = new NoteRequest("My title", "My content");
        NoteResponse response = new NoteResponse("id1", "My title", "My content", Instant.now(), Instant.now());

        when(noteService.createNote(any(NoteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value("id1"));
    }

    @Test
    void getAllNotes_ShouldReturnPagedData() throws Exception {
        NoteResponse response = new NoteResponse("id1", "Title", "Content", Instant.now(), Instant.now());
        Page<NoteResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(noteService.getAllNotes(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/notes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.items[0].id").value("id1"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void updateNote_ShouldReturnOk() throws Exception {
        NoteRequest request = new NoteRequest("Updated", "Updated content");
        NoteResponse response = new NoteResponse("id1", "Updated", "Updated content", Instant.now(), Instant.now());

        when(noteService.updateNote(eq("id1"), any(NoteRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/notes/id1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated"));
    }

    @Test
    void deleteNote_ShouldReturnOk() throws Exception {
        doNothing().when(noteService).deleteNote("id1");

        mockMvc.perform(delete("/api/notes/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Note deleted successfully"));
    }

    @Test
    void createNote_ShouldFailValidationForBlankTitle() throws Exception {
        NoteRequest request = new NoteRequest("", "content");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}

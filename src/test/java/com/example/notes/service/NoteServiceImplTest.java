package com.example.notes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.exception.ResourceNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note(
                "n1",
                "Test title",
                "Test content",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void createNote_ShouldTrimAndReturnSavedNote() {
        NoteRequest request = new NoteRequest("  Hello  ", "  World  ");

        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note toSave = invocation.getArgument(0);
            toSave.setId("n2");
            return toSave;
        });

        NoteResponse created = noteService.createNote(request);

        assertEquals("n2", created.getId());
        assertEquals("Hello", created.getTitle());
        assertEquals("World", created.getContent());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void getAllNotes_ShouldReturnPagedResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Note> notePage = new PageImpl<>(java.util.List.of(note), pageable, 1);

        when(noteRepository.findAll(pageable)).thenReturn(notePage);

        Page<NoteResponse> result = noteService.getAllNotes(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("n1", result.getContent().get(0).getId());
    }

    @Test
    void getNoteById_ShouldThrowWhenMissing() {
        when(noteRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> noteService.getNoteById("missing"));
    }

    @Test
    void updateNote_ShouldUpdateFieldsAndSave() {
        when(noteRepository.findById("n1")).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteResponse updated = noteService.updateNote("n1", new NoteRequest(" Updated ", " Updated content "));

        assertEquals("Updated", updated.getTitle());
        assertEquals("Updated content", updated.getContent());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void deleteNote_ShouldDeleteWhenFound() {
        when(noteRepository.findById("n1")).thenReturn(Optional.of(note));

        noteService.deleteNote("n1");

        verify(noteRepository).delete(note);
    }
}

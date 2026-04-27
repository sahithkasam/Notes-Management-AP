package com.example.notes.service;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.exception.ResourceNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public NoteResponse createNote(NoteRequest request) {
        Instant now = Instant.now();
        Note note = new Note();
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        Note saved = noteRepository.save(note);
        return toResponse(saved);
    }

    @Override
    public Page<NoteResponse> getAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public NoteResponse getNoteById(String id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        return toResponse(note);
    }

    @Override
    public NoteResponse updateNote(String id, NoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent().trim());
        note.setUpdatedAt(Instant.now());

        Note saved = noteRepository.save(note);
        return toResponse(saved);
    }

    @Override
    public void deleteNote(String id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt());
    }
}

package com.example.notes.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;

public interface NoteService {

    NoteResponse createNote(NoteRequest request);

    Page<NoteResponse> getAllNotes(Pageable pageable);

    NoteResponse getNoteById(String id);

    NoteResponse updateNote(String id, NoteRequest request);

    void deleteNote(String id);
}

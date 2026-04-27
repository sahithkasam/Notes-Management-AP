package com.example.notes.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.notes.model.Note;

public interface NoteRepository extends MongoRepository<Note, String> {
}

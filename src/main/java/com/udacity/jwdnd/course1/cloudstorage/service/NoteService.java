package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public Integer createNote(Note note) {
        return noteMapper.insertNote(note);
    }

    public Note getNote(Integer noteId) {
        return noteMapper.getNote(noteId);
    }

    public List<Note> getAllNotes() {
        return noteMapper.getAllNotes();
    }

    public void updateNote(Note updatedNote) {
        noteMapper.updateNote(updatedNote);
    }

    public void deleteNote(Integer noteId) {
        noteMapper.deleteNote(noteId);
    }
}

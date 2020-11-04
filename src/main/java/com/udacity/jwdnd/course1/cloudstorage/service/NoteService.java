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

    public List<Note> getAllNotes(Integer userId) {
        return noteMapper.getAllNotes(userId);
    }

    public int updateNote(Note updatedNote) {
        return noteMapper.updateNote(updatedNote);
    }

    public int deleteNote(Integer noteId) {
        return noteMapper.deleteNote(noteId);
    }
}

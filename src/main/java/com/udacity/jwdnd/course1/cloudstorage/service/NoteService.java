package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoteService {
    @Autowired
    private NoteMapper noteMapper;

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

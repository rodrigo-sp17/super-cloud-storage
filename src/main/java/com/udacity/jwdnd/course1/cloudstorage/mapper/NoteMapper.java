package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {

    @Select("SELECT * FROM NOTES")
    List<Note> getAllNotes();

    @Select("SELECT * FROM NOTES WHERE noteid=#{noteId}")
    Note getNote(Integer noteId);

    @Insert("INSERT INTO NOTES(notetitle, notedescription, userid) " +
            "VALUES(#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    Integer insertNote(Note note);

    @Update("UPDATE NOTES SET notetitle=#{noteTitle}," +
            " notedescription=#{noteDescription} " +
            "WHERE noteid=#{noteId}")
    void updateNote(Note note);

    @Delete("DELETE FROM NOTES WHERE noteid=#{noteId}")
    void deleteNote(Integer noteId);
}
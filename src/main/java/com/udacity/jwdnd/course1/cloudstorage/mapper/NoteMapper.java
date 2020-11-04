package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {

    @Select("SELECT * FROM NOTES WHERE userid=#{userId}")
    List<Note> getAllNotes(Integer userId);

    @Select("SELECT * FROM NOTES WHERE noteid=#{noteId}")
    Note getNote(Integer noteId);

    @Insert("INSERT INTO NOTES(notetitle, notedescription, userid) " +
            "VALUES(#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int insertNote(Note note);

    @Update("UPDATE NOTES SET notetitle=#{noteTitle}," +
            " notedescription=#{noteDescription} " +
            "WHERE noteid=#{noteId}")
    int updateNote(Note note);

    @Delete("DELETE FROM NOTES WHERE noteid=#{noteId}")
    int deleteNote(Integer noteId);
}

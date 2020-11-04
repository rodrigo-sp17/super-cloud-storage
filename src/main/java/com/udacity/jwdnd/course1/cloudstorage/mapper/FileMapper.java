package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {

    @Select("SELECT * FROM FILES WHERE userid=#{userId}")
    List<File> getAllFiles(Integer userId);

    @Select("SELECT * FROM FILES WHERE fileId=#{fileId}")
    File getFile(Integer fileId);

    @Select("SELECT * FROM FILES WHERE filename=#{fileName}")
    File getFileByUsername(String fileName);

    @Insert("INSERT INTO FILES(filename, contenttype, filesize, userid, filedata) " +
            "VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insertFile(File file);

    @Update("UPDATE FILES SET filename = #{fileName}, " +
            "contenttype = #{contentType}, " +
            "filesize = #{fileSize}, " +
            "userid = #{userId}, " +
            "filedata = #{fileData} " +
            "WHERE" +
            "fileId = #{fileId}")
    int updateFile(File updatedFile);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    int deleteFile(Integer fileId);
}

package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {

    @Select("SELECT * FROM CREDENTIALS")
    List<Credential> getAllCredentials();

    @Select("SELECT * FROM CREDENTIALS WHERE url=#{url} AND username=#{userName}")
    Credential getCredential(String url, String username);

    @Insert("INSERT INTO CREDENTIALS(url, username, key, password, userid) " +
            "VALUES(#{url}, #{userName}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    Integer insertCredential(Credential credential);

    @Update("UPDATE CREDENTIALS SET url = #{url}, " +
            "username = #{userName}, " +
            "password = #{password}," +
            "key = #{key} " +
            "WHERE credentialid=#{credentialId}")
    void updateCredential(Credential updatedCredential);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialid = #{credentialId}")
    void deleteCredential(Integer credentialId);


}

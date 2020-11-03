package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper,
                             EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getAllCredentials() {
        return credentialMapper.getAllCredentials();
    }

    public Credential getCredential(String url, String username) {
        return credentialMapper.getCredential(url, username);
    }

    public int createCredential(Credential credential) {
        return credentialMapper.insertCredential(encryptPassword(credential));
    }

    public int editCredential(Credential editedCredential) {
        return credentialMapper.updateCredential(encryptPassword(editedCredential));
    }

    public int deleteCredential(Integer credentialId) {
        return credentialMapper.deleteCredential(credentialId);
    }

    /**
     * Encrypts the password of a credential, giving it an encoded key and an encrypted password
     * @param credential the credential to have its password encrypted
     * @return  new instance of Credential with the encoded key and encrypted password
     */
    private Credential encryptPassword(final Credential credential) {
        String password = credential.getPassword();

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);

        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);
        return new Credential(
                credential.getCredentialId(),
                credential.getUrl(),
                credential.getUserName(),
                encodedKey,
                encryptedPassword,
                credential.getUserId());
    }
}

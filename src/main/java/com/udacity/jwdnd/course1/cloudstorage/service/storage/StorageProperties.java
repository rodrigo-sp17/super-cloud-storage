package com.udacity.jwdnd.course1.cloudstorage.service.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("storage")
public class StorageProperties {

    // Folder location for storing files
    private String location = "L:\\Documentos\\CS\\Udacity\\" +
            "nd035-c1-spring-boot-basics-project-starter-master\\starter\\cloudstorage\\uploaded";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

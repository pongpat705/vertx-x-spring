package org.maozdeveloper.upskill.springxvertx.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileModel {

    private String name;
    private MultipartFile file;
}

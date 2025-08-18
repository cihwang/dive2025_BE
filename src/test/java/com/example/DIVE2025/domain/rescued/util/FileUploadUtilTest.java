package com.example.DIVE2025.domain.rescued.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileUploadUtilTest {

    @Autowired
    private FileUploadUtil fileUploadUtil;


    @Test
    void uploadImageFromUrl() {
        fileUploadUtil.uploadImageFromUrl("http://openapi.animal.go.kr/openapi/service/rest/fileDownloadSrvc/files/shelter/2025/07/20250728170773.jpg","3","426340202500575");
    }
}
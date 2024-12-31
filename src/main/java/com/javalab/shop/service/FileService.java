package com.javalab.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * 파일 서비스
 * - 파일 업로드, 파일 삭제 등의 기능을 제공하는 서비스 클래스
 */
@Service
@Slf4j
public class FileService {

    /**
     * 파일 업로드
     * @param uploadPath    : 파일 업로드 경로
     * @param originalFileName : 업로드할 파일의 원본 파일명
     * @param fileData : 업로드할 파일의 데이터
     * @return
     * @throws Exception
     */
    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception{

        // 1. 파일명이 중복되지 않도록 UUID를 이용하여 파일명 생성
        UUID uuid = UUID.randomUUID();
        // 2. 파일명의 확장자를 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 3. 파일명 생성(uuid + 확장자)
        String saveFileName = uuid.toString() + extension;
        // 4. 파일 업로드 경로 + 파일명, 이렇게 만든 경로로 파일을 객체 생성
        String fileUploadFullUrl = uploadPath + "/" + saveFileName;
        // 5. 파일 객체를 생성하여 파일을 업로드할 수 있는 출력 스트림을 생성
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        // 6. 파일 데이터를 출력 스트림을 이용하여 파일에 기록
        fos.write(fileData);
        fos.close();
        // 7. 파일명을 반환
        return saveFileName;
    }

    /**
     * 파일 삭제
     * - 파일을 삭제하는 메서드
     * - 여기서 파일은 파일 시스템인가? DB인가? 파일 시스템
     * @param filePath
     * @throws Exception
     */
    public void deleteFile(String filePath) throws  Exception{
        File deleteFile = new File(filePath);

        if(deleteFile.exists()){
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}

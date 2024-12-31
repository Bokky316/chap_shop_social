package com.javalab.shop.service;

import com.javalab.shop.entity.ItemImg;
import com.javalab.shop.repository.ItemImgRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 이미지 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    /**
     * 상품 이미지 저장
     * - 상품 이미지 정보를 저장하는 메서드
     * - 상품 이미지 파일(itemImgFile)을 업로드하고, 상품 이미지 정보(itemImg)를 저장한다.
     */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename(); // 원본 파일명
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드 (StringUtil : Thymeleaf의 유틸리티 클래스)
        if(!StringUtils.isEmpty(oriImgName)) {
            // FileService의 uploadFile() 메서드를 호출하여 파일 업로드
            // itemImeLocation : 업로드할 파일 경로
            // oriImgName : 원본 파일명
            // itemImgFile.getBytes() : 파일 데이터
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    /**
     * 상품 이미지 수정
     * - 상품 이미지 정보를 수정하는 메서드
     * - 상품의 이미지 id와 이미지 파일 정보를 전달 받아서 이미지 정보를 수정한다.
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        // 1. 상품 이미지 조회, 영속화 - 이미지 정보를 수정하기 위해 조회
        ItemImg itemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

        // 2. 화면에서 받아온 파일이 존재할 경우 기존 파일 삭제
        if (!itemImgFile.isEmpty()) {
            // 2.1 기존 파일 삭제, 여기서 기존이랑? - 기존 이미지 파일을 삭제
            fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
        }
        // 3. 화면에서 받아온 파일이 존재할 경우 새로운 파일 업로드하기 위해서 필요한 변수 선언
        String oriImgName = itemImgFile.getOriginalFilename(); // 화면에서 받아온 파일면
        String imgName = "";
        String imgUrl = "";

        // 4. 파일 업로드(화면에서 받아온 파일이 존재할 경우)
        if(!StringUtils.isEmpty(oriImgName)) {
            // 4.1 파일 업로드
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 4.2 이미지 URL, 이미지 URL은 상대경로로 저장, 이렇게 저장한 값이 DB에 저장됨
            imgUrl = "/images/item/" + imgName;
        }

        // 5. 상품 이미지 정보 수정, 이렇게 수정하면 JPA가 변경감지하여 수정된 내용을 DB에 반영
        // updateItemImg() 메서드는 ItemImg 엔티티의 메서드로 영속화 되어 있는 ItemImg 엔티티의 정보를 수정하게 되고
        // JPA가 변경감지하여 수정된 내용을 DB에 반영.
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
    }
}

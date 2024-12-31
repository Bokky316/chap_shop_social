package com.javalab.shop.dto;

import com.javalab.shop.entity.ItemImg;
import lombok.*;
import org.modelmapper.ModelMapper;

/**
 * ItemImg 엔티티와 ItemImgDto 간의 데이터 변환을 담당하는 클래스
 * - 상품 등록시 화면에서 전달받은 이미지 파일 정보를 저장하기 위해 사용
 * - 데이터베이스에서 조회한 ItemImg 엔티티를 화면에 전달하기 위해 사용
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemImgDto {

    private Long id;

    private String imgName;        // 이미지 파일명
    private String oriImgName;     // 원본 이미지 파일명
    private String imgUrl;         // 이미지 파일 경로
    private String repImgYn;       // 대표 이미지 여부

    private static ModelMapper modelMapper = new ModelMapper();

    /**
     * ItemImg 엔티티를 ItemImgDto로 변환하는 메서드
     * static 메서드로 선언하여 외부에서 객체 생성 없이 사용 가능하도록 함.
     */
    public static ItemImgDto entityToDto(ItemImg itemImg){
        ItemImgDto itemImgDto = modelMapper.map(itemImg, ItemImgDto.class);
        return itemImgDto;
    }
/*
    public static ItemImgDto entityToDto(ItemImg itemImg){
        ItemImgDto itemImgDto = ItemImgDto.builder()
                .id(itemImg.getId())
                .imgName(itemImg.getOriImgName())
                .oriImgName(itemImg.getImgName())
                .imgUrl(itemImg.getImgUri())
                .repImgYn(itemImg.getRepimgYn())
                .build();

        return itemImgDto;
    }
 */
}
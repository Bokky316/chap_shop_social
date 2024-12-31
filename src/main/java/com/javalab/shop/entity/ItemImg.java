package com.javalab.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 이미지를 저장하는 엔티티
 */
@Entity
@Getter@Setter
public class ItemImg extends BaseEntity {

    @Id
    @Column(name="item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName;      //이미지 파일명

    private String oriImgName;  //원본 이미지 파일명

    private String imgUrl;      //조회 이미지 경로

    private String repimgYn;     //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // 외래키 지정
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}

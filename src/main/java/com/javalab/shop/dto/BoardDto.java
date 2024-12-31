package com.javalab.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
@Builder
public class BoardDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int boardNo;
    private String title;
    private String content;
    private String memberId;
    private int hitNo;
    private Date regDate;
    private int replyGroup;
    private int replyOrder;
    private int replyIndent;
}
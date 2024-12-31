package com.javalab.shop.exception;

/**
 * 사용자 정의 예외(내가 만든 거)
 */
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
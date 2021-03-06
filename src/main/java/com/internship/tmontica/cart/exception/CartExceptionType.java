package com.internship.tmontica.cart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CartExceptionType {
    INVALID_CART_ADD_FORM("cart add form", "카트 추가 폼이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_CART_UPDATE_FORM("cart update form", "카트 수정 폼이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ACCESS_CART_DATA("user id", "해당 카트 데이터에 권한이 없습니다", HttpStatus.FORBIDDEN),
    DEFAULT_OPTION_NOT_SELECTED("option", "디폴트 옵션(HOT/ICE)이 선택되지 않았습니다", HttpStatus.BAD_REQUEST);


    private String field;
    private String message;
    private HttpStatus responseType;
}

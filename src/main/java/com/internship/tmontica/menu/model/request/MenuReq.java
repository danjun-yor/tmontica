package com.internship.tmontica.menu.model.request;

import com.internship.tmontica.menu.validaton.ValidMenuReq;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@ValidMenuReq
public class MenuReq {
    //TODO : validation..
    @NotEmpty(message = "메뉴이름(영어)가 명시되어야 합니다.")
    private String nameEng;
    @NotEmpty(message = "메뉴이름이 명시되어야 합니다.")
    private String nameKo;
    private String description;
    @NotNull(message = "이미지가 존재해야 합니다.")
    private MultipartFile imgFile;
    @Min(value = 0, message = "상품 가격은 0이상 이어야 합니다.")
    private int productPrice;
    @Min(value = 0 , message = "판매 가격은 0이상 이어야 합니다.")
    private int sellPrice;
    @Min(value = 0, message = "할인율은 0이상 이어야 합니다.")
    @Max(value = 100 , message = "할인율은 100이하 이어야 합니다.")
    private int discountRate;
    @NotEmpty(message = "카테고리(영어)가 명시되어야 합니다.")
    private String categoryEng;
    @NotEmpty(message = "카테고리가 명시되어야 합니다.")
    private String categoryKo;
    @NotNull
    @Min(value = 0, message = "재고는 0이상 이어야 합니다.")
    private int stock;
    @NotNull(message = "이달의 메뉴 여부가 명시되어야 합니다.")
    private boolean monthlyMenu;
    private boolean usable = true;

    private List<Integer> optionIds = new ArrayList<>();

}

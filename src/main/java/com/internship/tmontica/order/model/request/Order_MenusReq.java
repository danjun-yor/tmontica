package com.internship.tmontica.order.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Order_MenusReq {
    @NotNull
    private int cartId;

}
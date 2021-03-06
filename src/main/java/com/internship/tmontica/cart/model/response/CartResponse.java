package com.internship.tmontica.cart.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private int size;
    private int totalPrice;
    private List<CartMenusResponse> menus;
}

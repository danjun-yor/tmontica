package com.internship.tmontica.cart.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartUpdateRequest {

    @Min(1)
    private int quantity;
}

package com.internship.tmontica.option;

import lombok.*;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {
    @NotNull
    private String name;
    @Min(0)
    private int price;
    @NotNull
    private String type;
    private int id;

    public Option(String name , int price, String type){
        this.name = name;
        this.price = price;
        this.type = type;
    }
}

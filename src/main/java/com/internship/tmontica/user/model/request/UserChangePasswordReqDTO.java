package com.internship.tmontica.user.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserChangePasswordReqDTO {

    @NotNull
    private String id;
    @NotNull
    private String password;
    @NotNull
    private String newPassword;

}
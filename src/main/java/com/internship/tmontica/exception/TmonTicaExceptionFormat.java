package com.internship.tmontica.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TmonTicaExceptionFormat {

    private String field;
    private String message;
    private List<FieldError> errors;

    public TmonTicaExceptionFormat(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public TmonTicaExceptionFormat(String field, String message, List<FieldError> errors){
        this.field = field;
        this.message = message;
        this.errors = errors;
    }

    public TmonTicaExceptionFormat(String field, String message, BindingResult bindingResult){
        this.field = field;
        this.message = message;
        this.errors = FieldError.of(bindingResult);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError{
        private String field;
        private String value;
        private String reason;

    public static List<TmonTicaExceptionFormat.FieldError> of(final String field, final String value, final String reason) {
        List<TmonTicaExceptionFormat.FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new TmonTicaExceptionFormat.FieldError(field, value, reason));
        return fieldErrors;
    }

    private static List<TmonTicaExceptionFormat.FieldError> of(final BindingResult bindingResult) {
        final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
        return fieldErrors.stream()
                .map(error -> new TmonTicaExceptionFormat.FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
    }
    }
}

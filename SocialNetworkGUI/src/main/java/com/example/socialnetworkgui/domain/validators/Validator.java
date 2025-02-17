package com.example.socialnetworkgui.domain.validators;

@FunctionalInterface
public interface Validator<T> {
    /**
     *
     * @param entity refers the entity that will be validated
     */
    void validate(T entity);
}
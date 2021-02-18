package ru.nomia.catalog.controller.dto.converter;


public interface Converter<M, D> {

    M toModel(D dto);

    D toDto(M model);

}

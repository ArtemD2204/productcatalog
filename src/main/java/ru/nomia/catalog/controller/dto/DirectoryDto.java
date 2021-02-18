package ru.nomia.catalog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
public class DirectoryDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    Long parentId;
}

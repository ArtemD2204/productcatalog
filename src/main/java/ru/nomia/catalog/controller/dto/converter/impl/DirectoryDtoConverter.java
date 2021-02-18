package ru.nomia.catalog.controller.dto.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nomia.catalog.controller.dto.DirectoryDto;
import ru.nomia.catalog.controller.dto.ProductDto;
import ru.nomia.catalog.controller.dto.converter.Converter;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.model.Product;
import ru.nomia.catalog.service.DirectoryService;
import ru.nomia.catalog.service.ProductService;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectoryDtoConverter implements Converter<Directory, DirectoryDto> {
    private final ProductService productService;
    private final DirectoryService directoryService;

    @Override
    public Directory toModel(DirectoryDto dto) {
        if (dto == null)
            return null;
        Long parentId = dto.getParentId();
        Directory parent = parentId == null ? null : directoryService.get(parentId);
        if (dto.getId() == null) {
            return new Directory(null, dto.getName(), parent, new ArrayList<Directory>(), new ArrayList<Product>());
        }
        Directory directory = directoryService.get(dto.getId());
        if(directory != null) {
            directory.setName(dto.getName());
            directory.setParent(parent);
        }
        return directory;
    }

    @Override
    public DirectoryDto toDto(Directory model) {
        if (model == null)
            return null;
        Long parentId = model.getParent() == null ? null : model.getParent().getId();
        return new DirectoryDto(model.getId(), model.getName(), parentId);
    }
}

package ru.nomia.catalog.controller.dto.converter.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nomia.catalog.controller.dto.ProductDto;
import ru.nomia.catalog.controller.dto.converter.Converter;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.model.Product;
import ru.nomia.catalog.service.DirectoryService;
import ru.nomia.catalog.service.ProductService;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductDtoConverter implements Converter<Product, ProductDto> {
    private final ProductService productService;
    private final DirectoryService directoryService;

    @Override
    public Product toModel(ProductDto dto) {
        if (dto == null)
            return null;
        Long directoryId = dto.getDirectoryId();
        Directory directory = directoryId == null ? null : directoryService.get(directoryId);
        if (dto.getId() == null) {
            return new Product(null, dto.getName(), dto.getPrice(), directory);
        }
        Product product = productService.get(dto.getId());
        if(product != null) {
            product.setName(dto.getName());
            product.setPrice(dto.getPrice());
            product.setDirectory(directory);
        }
        return product;
    }

    @Override
    public ProductDto toDto(Product model) {
        if (model == null)
            return null;
        Long directoryId = model.getDirectory() == null ? null : model.getDirectory().getId();
        return new ProductDto(model.getId(), model.getName(), model.getPrice(), directoryId);
    }
}

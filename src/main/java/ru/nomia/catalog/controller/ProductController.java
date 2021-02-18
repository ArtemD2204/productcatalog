package ru.nomia.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nomia.catalog.controller.dto.ProductDto;
import ru.nomia.catalog.controller.dto.converter.Converter;
import ru.nomia.catalog.model.Product;
import ru.nomia.catalog.service.DirectoryService;
import ru.nomia.catalog.service.ProductService;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductController {

    private final ProductService productService;
    private final Converter<Product, ProductDto> productDtoConverter;
    private final DirectoryService directoryService;

    // получить продукт по id
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long id) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Product vo = productService.get(id);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(productDtoConverter.toDto(vo), HttpStatus.OK);
    }

    // создать продукт
    @PostMapping("/product/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductDto dto) throws Exception {
        if(dto.getId() != null) {
            return new ResponseEntity<>("id is not null in " + dto, HttpStatus.BAD_REQUEST);
        }
        if(dto.getDirectoryId() == null || !directoryService.exists(dto.getDirectoryId())) {
            return new ResponseEntity<>("invalid directoryId in " + dto, HttpStatus.BAD_REQUEST);
        }
        Product vo = productDtoConverter.toModel(dto);
        productService.create(vo);
        return new ResponseEntity<>(productDtoConverter.toDto(vo), HttpStatus.OK);
    }

    // удалить продукт
    @PostMapping("/product/{id}/delete")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        productService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // обновить продукт
    @PostMapping("/product/{id}/update")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        if (id == null || !id.equals(dto.getId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(dto.getDirectoryId() == null || !directoryService.exists(dto.getDirectoryId())) {
            return new ResponseEntity<>("invalid directoryId in " + dto, HttpStatus.BAD_REQUEST);
        }
        Product vo = productDtoConverter.toModel(dto);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        productService.update(vo);
        return new ResponseEntity<>(productDtoConverter.toDto(vo), HttpStatus.OK);
    }
}

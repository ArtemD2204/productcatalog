package ru.nomia.catalog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nomia.catalog.controller.dto.DirectoryDto;
import ru.nomia.catalog.controller.dto.ProductDto;
import ru.nomia.catalog.controller.dto.converter.Converter;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.model.Product;
import ru.nomia.catalog.service.DirectoryService;
import ru.nomia.catalog.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectoryController {

    private final DirectoryService directoryService;
    private final Converter<Directory, DirectoryDto> directoryDtoConverter;
    private final ProductService productService;
    private final Converter<Product, ProductDto> productDtoConverter;

    /**
     *
     * @param size размер страницы
     * @param page номер страницы
     * @return возвращает список корневых каталогов
     */
    @GetMapping("/directory/rootlist/{size}/{page}")
    public ResponseEntity<List<DirectoryDto>> getRootList(@PathVariable("size") Integer size,
                                                                   @PathVariable("page") Integer page) {
        List<Directory> voList = directoryService.getRootList(size, page);
        List<DirectoryDto> dtoList = voList.stream().map(directoryDtoConverter::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    /**
     *
     * @param id идентификатор родительского каталога
     * @param size размер страницы
     * @param page номер страницы
     * @return возвращает список подкаталогов
     */
    @GetMapping("/directory/{id}/subdirlist/{size}/{page}")
    public ResponseEntity<List<DirectoryDto>> getChildlist(@PathVariable("id") Long id,
                                                           @PathVariable("size") Integer size,
                                                           @PathVariable("page") Integer page) {
        Directory directory = directoryService.get(id);
        if (directory == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<DirectoryDto> dtoList = directoryService.getOneSortedPageOfSubDirs(directory, size, page)
                .stream().map(directoryDtoConverter::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    // переместить из других каталогов или добавить новые подкаталоги в каталог с идентификатором id
    @PostMapping("/directory/{id}/subdirlist/add")
    public ResponseEntity<?> addSubDirs(@PathVariable Long id, @RequestBody List<DirectoryDto> newSubDirDtoList) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Directory directory = directoryService.get(id);
        if (directory == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Directory> newSubDirs = new ArrayList<>();
        for(DirectoryDto newSubDirDto : newSubDirDtoList) {
            Directory subDir = directoryDtoConverter.toModel(newSubDirDto);
            if (subDir == null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            newSubDirs.add(subDir);
        }
        directoryService.addChildren(directory, newSubDirs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // сделать каталоги корневыми (удалить из списка List<Directory> children (подкаталоги) каталога с идентификатором id)
    @PostMapping("/directory/{id}/subdirlist/deletefromlist")
    public ResponseEntity<?> deleteSubDirs(@PathVariable Long id, @RequestBody List<Long> childIdList) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Directory vo = directoryService.get(id);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        directoryService.deleteFromChildList(vo, childIdList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // обновить свойства подкаталогов из списка List<Directory>children каталога с идентификатором id
    @PostMapping("/directory/{id}/subdirlist/update")
    public ResponseEntity<Object> updateSubDirs(@PathVariable Long id, @RequestBody List<DirectoryDto> subDirDtoList) throws Exception {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Directory vo = directoryService.get(id);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Directory> subDirs = new ArrayList<>();
        for(DirectoryDto subDirDto : subDirDtoList) {
            if(subDirDto.getParentId() == null)
                throw new Exception("subDirDto.parentId is null");
            if(subDirDto.getId() == null)
                throw new Exception("subDirDto.id is null");
            if(!id.equals(subDirDto.getParentId()))
                throw new Exception("subDirDto.parentId is not equals parent.id");

            Directory subDir = directoryService.get(subDirDto.getId());
            if (subDir == null)
                return new ResponseEntity<>("subDir not found", HttpStatus.NOT_FOUND);
            if(subDir.getParent()==null || !subDirDto.getParentId().equals(subDir.getParent().getId())) {
                return new ResponseEntity<>(subDirDto + " has wrong parentId", HttpStatus.BAD_REQUEST);
            }
            subDir = directoryDtoConverter.toModel(subDirDto);
            subDirs.add(subDir);
        }
        directoryService.saveDirs(subDirs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // получить каталог по id
    @GetMapping("/directory/{id}")
    public ResponseEntity<DirectoryDto> getDirectory(@PathVariable("id") Long id) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Directory vo = directoryService.get(id);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(directoryDtoConverter.toDto(vo), HttpStatus.OK);
    }

    // создать каталог
    @PostMapping("/directory/create")
    public ResponseEntity<?> createDirectory(@RequestBody DirectoryDto dto) {
        if(dto.getId() != null) {
            return new ResponseEntity<>("id is not null in " + dto, HttpStatus.BAD_REQUEST);
        }
        Directory vo = directoryDtoConverter.toModel(dto);
        directoryService.create(vo);
        return new ResponseEntity<>(directoryDtoConverter.toDto(vo), HttpStatus.OK);
    }

    // удалить каталог
    @PostMapping("/directory/{id}/delete")
    public ResponseEntity<?> deleteDirectory(@PathVariable Long id) {
        if (id == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        directoryService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // обновить каталог
    @PostMapping("/directory/{id}/update")
    public ResponseEntity<DirectoryDto> updateDirectory(@PathVariable Long id, @RequestBody DirectoryDto dto) {
        if (id == null || !id.equals(dto.getId()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Directory vo = directoryDtoConverter.toModel(dto);
        if (vo == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        directoryService.update(vo);
        return new ResponseEntity<>(directoryDtoConverter.toDto(vo), HttpStatus.OK);
    }

    /**
     *
     * @param id идентификатор каталога
     * @param size размер страницы
     * @param page номер страницы
     * @return возвращает список продуктов, входящих в подкаталог
     */
    @GetMapping("/directory/{id}/productlist/{size}/{page}")
    public ResponseEntity<List<ProductDto>> getProducts(@PathVariable("id") Long id,
                                                     @PathVariable("size") Integer size,
                                                     @PathVariable("page") Integer page) {
        Directory directory = directoryService.get(id);
        if (directory == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<ProductDto> dtoList = productService.getOneSortedPageOfProducts(directory, size, page)
                .stream().map(productDtoConverter::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
    /**
     * Еще можно добавить следующие запросы:
     *
     * Переместить из других каталогов или добавить новые товары в каталог с идентификатором id
     * /directory/{id}/productlist/add
     * Обработка этого запроса выполняется по аналогии с методом:
     * @see #addSubDirs(Long, List)
     *
     * Обновить товары из каталога с идентификатором id
     * /directory/{id}/productlist/update
     * Обработка этого запроса выполняется по аналогии с методом:
     * @see #updateSubDirs(Long, List)
     */
}

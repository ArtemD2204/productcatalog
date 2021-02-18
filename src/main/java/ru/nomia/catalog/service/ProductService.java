package ru.nomia.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.model.Product;
import ru.nomia.catalog.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {

    private final ProductRepository productRepository;

    public Product get(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Product product) {
        return productRepository.save(product);
    }

    public void remove(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getOneSortedPageOfProducts(Directory dir, Integer size, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        return productRepository.findAllByDirectory(dir, pageRequest);
    }
}

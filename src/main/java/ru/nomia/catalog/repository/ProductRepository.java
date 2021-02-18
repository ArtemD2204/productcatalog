package ru.nomia.catalog.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDirectory(Directory directory, Pageable pageable);
}

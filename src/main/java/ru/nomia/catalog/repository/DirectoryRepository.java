package ru.nomia.catalog.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nomia.catalog.model.Directory;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    List<Directory> findAllByParent(Directory parent, Pageable pageRequest);
    List<Directory> findAllByParentIsNull(Pageable pageRequest);
}

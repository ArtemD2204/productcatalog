package ru.nomia.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nomia.catalog.model.Directory;
import ru.nomia.catalog.repository.DirectoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectoryService {

    private final DirectoryRepository directoryRepository;

    public Directory get(Long id) {
        return directoryRepository.findById(id).orElse(null);
    }

    public Directory create(Directory directory) {
        return directoryRepository.save(directory);
    }

    public Directory update(Directory directory) {
        return directoryRepository.save(directory);
    }

    public void remove(Long id) {
        directoryRepository.deleteById(id);
    }

    @Transactional
    public void addChildren(Directory directory, List<Directory> newChildList) {
        for (Directory subDir : newChildList) {
            subDir.setParent(directory);
            directoryRepository.save(subDir);
        }
    }

    @Transactional
    public void deleteFromChildList(Directory parentDir, List<Long> childIdList) {
        for(Long subDirId : childIdList) {
            Directory subDir = get(subDirId);
            if (subDir.getParent() != null && parentDir.getId().equals(subDir.getParent().getId())) {
                subDir.setParent(null);
                directoryRepository.save(subDir);
            }
        }
    }

    @Transactional
    public void saveDirs(List<Directory> dirs) {
        for(Directory dir : dirs) {
            directoryRepository.save(dir);
        }
    }

    public List<Directory> getRootList(Integer size, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        return directoryRepository.findAllByParentIsNull(pageRequest);
    }

    public List<Directory> getOneSortedPageOfSubDirs(Directory dir, Integer size, Integer page) {
        Sort sort = Sort.by(Sort.Order.asc("name"));
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return directoryRepository.findAllByParent(dir, pageRequest);
    }

    public boolean exists(Long id) {
        return directoryRepository.existsById(id);
    }
}

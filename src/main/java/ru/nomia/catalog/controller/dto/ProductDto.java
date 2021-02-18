package ru.nomia.catalog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import ru.nomia.catalog.model.Directory;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;


/**
 * @author Artem Dikov
 */
@AllArgsConstructor
@Data
public class ProductDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private Double price;
    private Long directoryId;
}

package ru.nomia.catalog.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author Artem Dikov
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "directory")
public class Directory {
    @Id
    @SequenceGenerator(name = "directory_seq", sequenceName = "directory_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "directory_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Basic
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    Directory parent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE})
    List<Directory> children;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "directory", cascade = {CascadeType.REMOVE})
    List<Product> products;
}

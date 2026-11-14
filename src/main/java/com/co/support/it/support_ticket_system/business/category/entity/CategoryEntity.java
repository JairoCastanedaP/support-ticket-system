package com.co.support.it.support_ticket_system.business.category.entity;

import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@AttributeOverride(
        name = "active",
        column = @Column(name = "status", nullable = false)
)
@Getter
@Setter
public class CategoryEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 250)
    private String description;
}

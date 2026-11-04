package com.co.support.it.support_ticket_system.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 250)
    private String description;

    @Column(nullable = false)
    private boolean status=true;
}

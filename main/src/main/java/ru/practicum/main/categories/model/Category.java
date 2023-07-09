package ru.practicum.main.categories.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //todo убедиться в том, что builder не нужен
    @Column(nullable = false, unique = true)
    private String name;
}
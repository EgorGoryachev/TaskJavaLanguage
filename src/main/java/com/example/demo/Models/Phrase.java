package com.example.demo.Models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import lombok.Data;
@Entity
@Data
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phrase_seq")
    @SequenceGenerator(name = "phrase_seq", sequenceName = "phrase_seq", allocationSize = 1)
    private Long id;

    private String originalText;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Language originalLanguage;

    @OneToMany(mappedBy = "phrase", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Translation> translations;
}
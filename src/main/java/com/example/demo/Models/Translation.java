package com.example.demo.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "translation_seq")
    @SequenceGenerator(name = "translation_seq", sequenceName = "translation_seq", allocationSize = 1)
    private Long id;

    private String translatedText;

    @ManyToOne
    @JsonBackReference
    private Phrase phrase;

    @ManyToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "language_id")
    private Language language;
}

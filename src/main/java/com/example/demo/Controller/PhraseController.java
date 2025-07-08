package com.example.demo.Controller;

import com.example.demo.Models.Phrase;
import com.example.demo.Models.Translation;
import com.example.demo.ResourceNotFoundException;
import com.example.demo.Services.LanguageService;
import com.example.demo.Services.PhraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/phrases")
public class PhraseController {

    @Autowired
    private PhraseService phraseService;
    @Autowired
    private LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<Phrase>> getPhrases(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        if (categoryId != null && search != null) {
            return ResponseEntity.ok(phraseService.findByCategoryAndTextContaining(categoryId, search));
        } else if (categoryId != null) {
            return ResponseEntity.ok(phraseService.findByCategory(categoryId));
        } else if (search != null) {
            return ResponseEntity.ok(phraseService.findByTextContaining(search));
        } else {
            return ResponseEntity.ok(phraseService.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phrase> getPhraseById(@PathVariable Long id) {
        Phrase phrase = phraseService.findById(id);
        return ResponseEntity.ok(phrase);
    }

    @PostMapping
    public ResponseEntity<Phrase> addPhrase(@RequestBody Phrase phrase) {
        phrase.setId(null);

        // Validate original language
        if (phrase.getOriginalLanguage() == null || phrase.getOriginalLanguage().getCode() == null) {
            throw new IllegalArgumentException("Original language code is required");
        }

        // Validate translation languages
        if (phrase.getTranslations() != null) {
            for (Translation t : phrase.getTranslations()) {
                t.setId(null);
                if (t.getLanguage() == null || t.getLanguage().getCode() == null) {
                    throw new IllegalArgumentException("Language code is required for translations");
                }
                // Verify language exists
                try {
                    languageService.findByCode(t.getLanguage().getCode());
                } catch (ResourceNotFoundException e) {
                    throw new IllegalArgumentException("Invalid language code: " + t.getLanguage().getCode());
                }
            }
        }

        return ResponseEntity.ok(phraseService.save(phrase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Phrase> updatePhrase(@PathVariable Long id, @RequestBody Phrase phrase) {
        return ResponseEntity.ok(phraseService.update(id, phrase));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhrase(@PathVariable Long id) {
        phraseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{phraseId}/translations")
    public ResponseEntity<List<Translation>> getTranslations(@PathVariable Long phraseId) {
        return ResponseEntity.ok(phraseService.getTranslations(phraseId));
    }

    @PostMapping("/{phraseId}/translations")
    public ResponseEntity<Translation> addTranslation(
            @PathVariable Long phraseId,
            @RequestBody Translation translation) {
        return ResponseEntity.ok(phraseService.addTranslation(phraseId, translation));
    }

    @PutMapping("/{phraseId}/translations/{translationId}")
    public ResponseEntity<Translation> updateTranslation(
            @PathVariable Long phraseId,
            @PathVariable Long translationId,
            @RequestBody Translation translation) {
        return ResponseEntity.ok(phraseService.updateTranslation(phraseId, translationId, translation));
    }

    @DeleteMapping("/{phraseId}/translations/{translationId}")
    public ResponseEntity<Void> deleteTranslation(
            @PathVariable Long phraseId,
            @PathVariable Long translationId) {
        phraseService.deleteTranslation(translationId);
        return ResponseEntity.noContent().build();
    }
}
package com.example.demo.Services;

import com.example.demo.ResourceNotFoundException;
import com.example.demo.Models.Phrase;
import com.example.demo.Models.Translation;
import com.example.demo.Models.Language;
import com.example.demo.Repositories.PhraseRepository;
import com.example.demo.Repositories.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PhraseService {

    private final PhraseRepository phraseRepository;
    private final TranslationRepository translationRepository;
    private final LanguageService languageService;

    @Autowired
    public PhraseService(PhraseRepository phraseRepository,
                         TranslationRepository translationRepository,
                         LanguageService languageService) {
        this.phraseRepository = phraseRepository;
        this.translationRepository = translationRepository;
        this.languageService = languageService;
    }

    public List<Phrase> findAll() {
        return phraseRepository.findAll();
    }

    public Phrase findById(Long id) {
        return phraseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phrase not found with id: " + id));
    }

    public List<Phrase> findByCategory(Long categoryId) {
        return phraseRepository.findByCategoryId(categoryId);
    }

    public List<Phrase> findByTextContaining(String search) {
        return phraseRepository.findByOriginalTextContainingIgnoreCase(search);
    }

    public List<Phrase> findByCategoryAndTextContaining(Long categoryId, String search) {
        return phraseRepository.findByCategoryIdAndOriginalTextContainingIgnoreCase(categoryId, search);
    }

    public Phrase save(Phrase phrase) {
        // Ensure originalLanguage is a managed entity
        if (phrase.getOriginalLanguage() != null && phrase.getOriginalLanguage().getId() == null) {
            Language language = languageService.findByCode(phrase.getOriginalLanguage().getCode());
            phrase.setOriginalLanguage(language);
        }

        // First save the phrase without translations to generate ID
        Phrase savedPhrase = phraseRepository.save(phrase);

        // Then process translations
        if (phrase.getTranslations() != null) {
            for (Translation translation : phrase.getTranslations()) {
                translation.setPhrase(savedPhrase);
                // Ensure language exists
                Language language = languageService.findByCode(translation.getLanguage().getCode());
                translation.setLanguage(language);
                translationRepository.save(translation);
            }
        }

        return savedPhrase;
    }

    public Phrase update(Long id, Phrase phraseDetails) {
        Phrase phrase = findById(id);
        phrase.setOriginalText(phraseDetails.getOriginalText());
        phrase.setOriginalLanguage(phraseDetails.getOriginalLanguage());
        phrase.setCategory(phraseDetails.getCategory());
        return phraseRepository.save(phrase);
    }

    public void delete(Long id) {
        Phrase phrase = findById(id);
        // Удаляем связанные переводы сначала
        translationRepository.deleteByPhraseId(id);
        phraseRepository.delete(phrase);
    }

    public List<Translation> getTranslations(Long phraseId) {
        return translationRepository.findByPhraseId(phraseId);
    }

    public Translation addTranslation(Long phraseId, Translation translation) {
        Phrase phrase = findById(phraseId);
        translation.setPhrase(phrase);

        // Проверяем язык
        languageService.findById(translation.getLanguage().getId());

        // Проверяем, нет ли уже перевода для этого языка
        Optional<Translation> existingTranslation = translationRepository
                .findByPhraseIdAndLanguageId(phraseId, translation.getLanguage().getId());

        if (existingTranslation.isPresent()) {
            throw new IllegalArgumentException("Translation for this language already exists");
        }

        return translationRepository.save(translation);
    }

    public Translation updateTranslation(Long phraseId, Long translationId, Translation translationDetails) {
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found"));

        if (!translation.getPhrase().getId().equals(phraseId)) {
            throw new IllegalArgumentException("Translation does not belong to the specified phrase");
        }

        translation.setTranslatedText(translationDetails.getTranslatedText());
        return translationRepository.save(translation);
    }

    public void deleteTranslation(Long translationId) {
        if (!translationRepository.existsById(translationId)) {
            throw new ResourceNotFoundException("Translation not found with id: " + translationId);
        }
        translationRepository.deleteById(translationId);
    }

    public Optional<Translation> getTranslationForLanguage(Long phraseId, String languageCode) {
        return translationRepository.findByPhraseIdAndLanguageCode(phraseId, languageCode);
    }
}
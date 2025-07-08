package com.example.demo.Services;

import com.example.demo.ResourceNotFoundException;
import com.example.demo.Models.Language;
import com.example.demo.Repositories.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Autowired
    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    public Language findById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found with id: " + id));
    }

    public Language findByCode(String code) {
        return languageRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found with code: " + code));
    }

    public Language save(Language language) {
        return languageRepository.save(language);
    }

    public Language update(Long id, Language languageDetails) {
        Language language = findById(id);
        language.setCode(languageDetails.getCode());
        language.setName(languageDetails.getName());
        return languageRepository.save(language);
    }

    public void delete(Long id) {
        Language language = findById(id);
        languageRepository.delete(language);
    }

    public boolean existsByCode(String code) {
        return languageRepository.existsByCode(code);
    }
}
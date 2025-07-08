package com.example.demo.Repositories;


import com.example.demo.Models.Phrase;
import com.example.demo.Models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByPhraseId(Long phraseId);
    void deleteByPhraseId(Long phraseId);
    Optional<Translation> findByPhraseIdAndLanguageId(Long phraseId, Long languageId);
    Optional<Translation> findByPhraseIdAndLanguageCode(Long phraseId, String languageCode);
}

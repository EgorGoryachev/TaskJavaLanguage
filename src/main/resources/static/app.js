
const API_URL = 'http://localhost:8080/api';
let currentLanguage = 'en';
document.addEventListener('DOMContentLoaded', loadCategories);
let currentCategoryId = null;
let currentPhrase = null;
const modal = new bootstrap.Modal(document.getElementById('phraseModal'));


async function loadCategories() {
    try {
        const response = await fetch(`${API_URL}/categories`);
        const categories = await response.json();

        const container = document.getElementById('categoriesContainer');
        container.innerHTML = '';

        categories.forEach(category => {
            const categoryCard = document.createElement('div');
            categoryCard.className = 'col-md-4 mb-4';
            categoryCard.innerHTML = `
                <div class="card text-center phrase-card" onclick="loadPhrases(${category.id}, '${category.name}')">
                    <div class="card-body">
                        <div class="category-icon">${getCategoryIcon(category.name)}</div>
                        <h5 class="card-title">${category.name}</h5>
                    </div>
                </div>
            `;
            container.appendChild(categoryCard);
        });
    } catch (error) {
        console.error('Ошибка загрузки категорий:', error);
    }
}

function getCategoryIcon(categoryName) {
    const icons = {
        'Основные слова': '⭐',
        'Ресторан': '🍽️',
        'Транспорт': '🚌',
        'Отель': '🏨',
        'Медицина': '🏥'
    };
    return icons[categoryName] || '💬';
}

async function loadPhrases(categoryId, categoryName) {
    try {
        currentCategoryId = categoryId;
        currentLanguage = document.getElementById('languageSelect').value;

        const response = await fetch(`${API_URL}/phrases?categoryId=${categoryId}`);
        const phrases = await response.json();

        document.getElementById('currentCategory').textContent = categoryName;
        document.getElementById('categoriesContainer').style.display = 'none';
        document.getElementById('phrasesContainer').style.display = 'block';

        const phrasesList = document.getElementById('phrasesList');
        phrasesList.innerHTML = '';

        phrases.forEach(phrase => {
            const translation = getTranslation(phrase, currentLanguage);
            const phraseItem = document.createElement('div');
            phraseItem.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
            phraseItem.innerHTML = `
                <div class="flex-grow-1">
                    <strong>${phrase.originalText}</strong>
                    <div class="text-muted">${translation}</div>
                </div>
                <div class="btn-group">
                    <button class="btn btn-sm btn-outline-primary" onclick="speakPhrase(event, '${translation}')">
                        🔊
                    </button>
                    <button class="btn btn-sm btn-outline-warning" onclick="showEditPhraseModal(${phrase.id})">
                        ✏️
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deletePhrase(${phrase.id})">
                        🗑️
                    </button>
                </div>
            `;
            phrasesList.appendChild(phraseItem);
        });
    } catch (error) {
        console.error('Ошибка загрузки фраз:', error);
    }
}


function getTranslation(phrase, languageCode) {
    if (!phrase.translations) return 'Нет перевода';

    const translation = phrase.translations.find(t => t.language.code === languageCode);
    return translation ? translation.translatedText : 'Нет перевода';
}

function speakPhrase(event, text) {
    event.stopPropagation();
    if ('speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = currentLanguage === 'en' ? 'en-US' : 'es-ES';
        speechSynthesis.speak(utterance);
    } else {
        alert('Браузер не поддерживает синтез речи');
    }
}

function backToCategories() {
    document.getElementById('categoriesContainer').style.display = 'block';
    document.getElementById('phrasesContainer').style.display = 'none';
}

document.getElementById('languageSelect').addEventListener('change', function() {
    if (document.getElementById('phrasesContainer').style.display === 'block') {
        const currentCategory = document.getElementById('currentCategory').textContent;
        const categoryId = currentCategoryId;
        if (currentCategory && categoryId) {
            loadPhrases(categoryId, currentCategory);
        }
    }
});

async function searchPhrases() {
    const searchText = document.getElementById('searchInput').value.trim();
    if (!searchText) return;

    try {
        currentLanguage = document.getElementById('languageSelect').value;
        const response = await fetch(`${API_URL}/phrases?search=${encodeURIComponent(searchText)}`);
        const phrases = await response.json();

        document.getElementById('currentCategory').textContent = `Результаты поиска: "${searchText}"`;
        document.getElementById('categoriesContainer').style.display = 'none';
        document.getElementById('phrasesContainer').style.display = 'block';

        const phrasesList = document.getElementById('phrasesList');
        phrasesList.innerHTML = '';

        if (phrases.length === 0) {
            phrasesList.innerHTML = '<div class="alert alert-info">Ничего не найдено</div>';
            return;
        }

        phrases.forEach(phrase => {
            const phraseItem = document.createElement('div');
            phraseItem.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
            phraseItem.innerHTML = `
                <div>
                    <strong>${phrase.originalText}</strong>
                    <div class="text-muted">${getTranslation(phrase, currentLanguage)}</div>
                    <small class="text-muted">${phrase.category.name}</small>
                </div>
                <button class="btn btn-sm btn-outline-primary" onclick="speakPhrase(event, '${getTranslation(phrase, currentLanguage)}')">
                    🔊
                </button>
            `;
            phrasesList.appendChild(phraseItem);
        });
    } catch (error) {
        console.error('Ошибка поиска:', error);
    }
}
async function loadPhrases(categoryId, categoryName) {
    try {
        currentCategoryId = categoryId;

        currentLanguage = document.getElementById('languageSelect').value;

        const response = await fetch(`${API_URL}/phrases?categoryId=${categoryId}`);
        if (!response.ok) throw new Error('Ошибка загрузки фраз');
        const phrases = await response.json();

        document.getElementById('currentCategory').textContent = categoryName;
        document.getElementById('categoriesContainer').style.display = 'none';
        document.getElementById('phrasesContainer').style.display = 'block';

        const phrasesList = document.getElementById('phrasesList');
        phrasesList.innerHTML = '';

        if (phrases.length === 0) {
            phrasesList.innerHTML = '<div class="alert alert-info">В этой категории пока нет фраз</div>';
            return;
        }

        phrases.forEach(phrase => {
            const translation = getTranslation(phrase, currentLanguage);
            const phraseItem = document.createElement('div');
            phraseItem.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
            phraseItem.innerHTML = `
                <div>
                    <strong>${phrase.originalText}</strong>
                    <div class="text-muted">${translation}</div>
                </div>
                <button class="btn btn-sm btn-outline-primary" onclick="speakPhrase(event, '${translation}')">
                    🔊
                </button>
            `;
            phrasesList.appendChild(phraseItem);
        });

    } catch (error) {
        console.error('Ошибка загрузки фраз:', error);
        const phrasesList = document.getElementById('phrasesList');
        phrasesList.innerHTML = `
            <div class="alert alert-danger">
                Произошла ошибка при загрузке фраз. Пожалуйста, попробуйте позже.
                <button class="btn btn-sm btn-secondary mt-2" onclick="loadPhrases(${categoryId}, '${categoryName}')">
                    Повторить попытку
                </button>
            </div>
        `;
    }
}

async function showAddPhraseModal() {
    try {
        currentPhrase = null;
        document.getElementById('modalTitle').textContent = 'Добавить фразу';
        document.getElementById('phraseForm').reset();
        document.getElementById('phraseId').value = '';

        const response = await fetch(`${API_URL}/categories`);
        if (!response.ok) throw new Error('Ошибка загрузки категорий');
        const categories = await response.json();

        const select = document.getElementById('categorySelect');
        select.innerHTML = categories.map(c =>
            `<option value="${c.id}">${c.name}</option>`
        ).join('');

        const modalElement = document.getElementById('phraseModal');
        const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        modal.show();

    } catch (error) {
        console.error('Ошибка при открытии модального окна:', error);
        alert('Не удалось загрузить данные для формы');
    }
}

async function showEditPhraseModal(phraseId) {
    currentPhrase = phraseId;
    document.getElementById('modalTitle').textContent = 'Редактировать фразу';

    const response = await fetch(`${API_URL}/phrases/${phraseId}`);
    const phrase = await response.json();

    document.getElementById('phraseId').value = phrase.id;
    document.getElementById('originalText').value = phrase.originalText;
    document.getElementById('categorySelect').value = phrase.category.id;

    const enTranslation = phrase.translations.find(t => t.language.code === 'en');
    const esTranslation = phrase.translations.find(t => t.language.code === 'es');
    document.getElementById('translationEn').value = enTranslation?.translatedText || '';
    document.getElementById('translationEs').value = esTranslation?.translatedText || '';

    modal.show();
}

async function savePhrase() {
    const form = document.getElementById('phraseForm');
    if (!form.checkValidity()) return;

    const phraseData = {
        originalText: document.getElementById('originalText').value,
        originalLanguage: { code: 'ru' },
        category: { id: document.getElementById('categorySelect').value },
        translations: [
            {
                language: { code: 'en' },
                translatedText: document.getElementById('translationEn').value
            },
            {
                language: { code: 'es' },
                translatedText: document.getElementById('translationEs').value
            }
        ]
    };

    try {
        const url = currentPhrase ? `${API_URL}/phrases/${currentPhrase}` : `${API_URL}/phrases`;
        const method = currentPhrase ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(phraseData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Ошибка сохранения');
        }

        modal.hide();
        loadPhrases(currentCategoryId, document.getElementById('currentCategory').textContent);
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось сохранить фразу: ' + error.message);
    }
}

async function deletePhrase(phraseId) {
    if (!confirm('Вы уверены, что хотите удалить эту фразу?')) return;

    try {
        const response = await fetch(`${API_URL}/phrases/${phraseId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Ошибка удаления');

        loadPhrases(currentCategoryId, document.getElementById('currentCategory').textContent);
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Не удалось удалить фразу');
    }
}

const resetButton = document.querySelector('.reset-btn');
resetButton.addEventListener('click', function () {
    const checkboxes = document.querySelectorAll('.filter-header input[type="checkbox"]');
    checkboxes.forEach(checkbox => {
        checkbox.checked = false;
    });
    const categoryButtons = document.querySelectorAll('.categories-container .category-btn');
    categoryButtons.forEach(button => {
        button.classList.remove('selected');
    });
    const notes = document.querySelectorAll('.post-it-container .post-it');
    notes.forEach(note => {
        note.classList.remove('hidden');
    });
    const filterContainer = document.querySelector('.filter-container');
    const confirmationMessage = document.createElement('div');
    confirmationMessage.textContent = 'Filters have been reset!';
    confirmationMessage.className = 'reset-confirmation';
    filterContainer.appendChild(confirmationMessage);
    setTimeout(() => {
        confirmationMessage.remove();
    }, 2000);
});

const categoryButtons = document.querySelectorAll('.categories-container .category-btn');
categoryButtons.forEach(button => {
    button.addEventListener('click', function () {
        const selectedCategory = this.textContent.trim().toUpperCase();
        filterNotesByCategory(selectedCategory);
    });
});

function filterNotesByCategory(category) {
    const notes = document.querySelectorAll('.post-it-container .post-it');
    notes.forEach(note => {
        const noteCategory = note.getAttribute('data-category');
        if (noteCategory === category || category === 'ALL') {
            note.classList.remove('hidden');
        } else {
            note.classList.add('hidden');
        }
    });
}

const reminderCheckbox = document.getElementById('reminderCheckbox');
reminderCheckbox.addEventListener('change', function () {
    if (reminderCheckbox.checked) {
        filterReminderNotes(true);
    } else {
        filterReminderNotes(false);
    }
});

function filterReminderNotes(showReminder) {
    const notes = document.querySelectorAll('.post-it-container .post-it');
    notes.forEach(note => {
        const isReminderActive = note.getAttribute('data-rappelActive') === 'true';
        if (showReminder) {
            if (isReminderActive) {
                note.classList.remove('hidden');
            } else {
                note.classList.add('hidden');
            }
        } else {
            note.classList.remove('hidden');
        }
    });
}

const importanceCheckbox = document.querySelector('.filter-header input[type="checkbox"]');
importanceCheckbox.addEventListener('change', function () {
    if (importanceCheckbox.checked) {
        filterImportantNotes(true);
    } else {
        filterImportantNotes(false);
    }
});

function filterImportantNotes(showImportant) {
    const notes = document.querySelectorAll('.post-it-container .post-it');
    notes.forEach(note => {
        const isImportant = note.querySelector('.star');
        if (showImportant) {
            if (isImportant) {
                note.classList.remove('hidden');
            } else {
                note.classList.add('hidden');
            }
        } else {
            note.classList.remove('hidden');
        }
    });
}

const showAllNotesButton = document.getElementById('showAllNotesButton');
const showArchivedNotesButton = document.getElementById('showArchivedNotesButton');

if (window.location.href.includes('archived=true')) {
    toggleButtonState(showArchivedNotesButton, showAllNotesButton);
} else {
    toggleButtonState(showAllNotesButton, showArchivedNotesButton);
}

showAllNotesButton.addEventListener('click', function () {
    filterNotes(false);
    toggleButtonState(showAllNotesButton, showArchivedNotesButton);
    setTimeout(function () {
        window.location.href = '/notes?archived=false';
    }, 100);
});

showArchivedNotesButton.addEventListener('click', function () {
    filterNotes(true);
    toggleButtonState(showArchivedNotesButton, showAllNotesButton);
    setTimeout(function () {
        window.location.href = '/notes?archived=true';
    }, 100);
});

function toggleButtonState(selectedButton, deselectedButton) {
    selectedButton.classList.add('selected');
    selectedButton.classList.remove('deselected');
    deselectedButton.classList.remove('selected');
    deselectedButton.classList.add('deselected');
}

document.getElementById("closeFilterWindow").addEventListener("click", function () {
    const filterSection = document.querySelector(".filter-section");
    const pageContainer = document.querySelector(".page-container");
    filterSection.classList.toggle("active");
    pageContainer.classList.toggle("filter-active");
});

document.getElementById("showFilter").addEventListener("click", function () {
    const filterSection = document.querySelector(".filter-section");
    const pageContainer = document.querySelector(".page-container");
    filterSection.classList.toggle("active");
    pageContainer.classList.toggle("filter-active");
});

document.getElementById('filterByButton').addEventListener('click', function () {
    const filterSection = document.getElementById('filterSection');
    if (filterSection.style.display === "block") {
        filterSection.style.display = "none";
    } else {
        filterSection.style.display = "block";
    }
});

const addNoteButton = document.getElementById('addNoteButton');
const modal = document.getElementById('addNoteModal');
const addNoteForm = document.getElementById('addNoteForm');

const rappelActiveCheckbox = document.getElementById('rappelActive');
const rappelDateContainer = document.getElementById('rappelDateContainer');

addNoteButton.addEventListener('click', () => {
    modal.style.display = 'flex';
});

window.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
});

rappelActiveCheckbox.addEventListener('change', () => {
    if (rappelActiveCheckbox.checked) {
        rappelDateContainer.style.display = 'block';
    } else {
        rappelDateContainer.style.display = 'none';
    }
});

const objetInput = document.getElementById('objet');
const descriptionInput = document.getElementById('description');
const categorieSelect = document.getElementById('categorie');

objetInput.addEventListener('blur', () => {
    if (!objetInput.value) {
        document.getElementById('objetError').style.display = 'block';
    } else {
        document.getElementById('objetError').style.display = 'none';
    }
});

descriptionInput.addEventListener('blur', () => {
    if (!descriptionInput.value) {
        document.getElementById('descriptionError').style.display = 'block';
    } else {
        document.getElementById('descriptionError').style.display = 'none';
    }
});

categorieSelect.addEventListener('blur', () => {
    if (!categorieSelect.value) {
        document.getElementById('categorieError').style.display = 'block';
    } else {
        document.getElementById('categorieError').style.display = 'none';
    }
});

addNoteForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    let isValid = true;

    document.querySelectorAll('.error-message').forEach((el) => el.style.display = 'none');

    const formData = new FormData(addNoteForm);
    const data = Object.fromEntries(formData.entries());
    data.important = formData.get('important') === 'on';

    if (!data.objet) {
        document.getElementById('objetError').style.display = 'block';
        isValid = false;
    }

    if (!data.description) {
        document.getElementById('descriptionError').style.display = 'block';
        isValid = false;
    }

    if (!data.categorie) {
        document.getElementById('categorieError').style.display = 'block';
        isValid = false;
    }

    data.rappelActive = formData.has('rappelActive');

    if (rappelActiveCheckbox.checked && formData.get('rappelDate')) {
        data.rappelDate = formData.get('rappelDate');
    } else {
        data.rappelDate = null;
    }

    if (isValid) {
        try {
            const response = await fetch('/api/notes', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                alert('Note ajoutée avec succès!');
                modal.style.display = 'none';
                location.reload();
            } else {
                alert('Erreur lors de l’ajout de la note.');
            }
        } catch (error) {
            console.error(error);
            alert('Une erreur est survenue.');
        }
    } else {
        alert("Veuillez remplir tous les champs obligatoires.");
    }
});

function closeModal() {
    document.querySelector('.modal').style.display = 'none';
}
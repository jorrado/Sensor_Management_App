// Popups
var modalCreate = document.getElementById("createGatewayPopup");
var modalEdit = document.getElementById("editGatewayPopup");
var modalDelete = document.getElementById("deleteGatewayPopup");

// Bouton ouvrir Create
var btnCreate = document.getElementById("openCreateBtn");

// Éléments fermeture popups
var exitCreate = document.getElementById("closeCreate");
var exitEdit = document.getElementById("closeEdit");
var exitDelete = document.getElementById("closeDelete");

// Ouvrir la popup Create
if (btnCreate) {
    btnCreate.addEventListener("click", () => {
        refreshCsrfToken();
        modalCreate.style.display = "block";
    });
}

// Fermer la popup Create
if (exitCreate) {
    exitCreate.addEventListener("click", () => {
        resetCreateModalFields();
        resetCreateError();
        modalCreate.style.display = "none";
    });
}

// Fermer la popup Edit
if (document.getElementById("closeEdit")) {
    var exitEdit = document.getElementById("closeEdit");
    exitEdit.addEventListener("click", () => {
        resetEditModalFields();
        resetEditError();
        modalEdit.style.display = "none";
    });
}

// Fermer la popup Delete
if (exitDelete) {
    exitDelete.addEventListener("click", () => {
        resetDeleteError();
        modalDelete.style.display = "none";
    });
}

// Fermer popup au clic extérieur
window.onclick = function(event) {
    [modalCreate, modalEdit, modalDelete].forEach(modal => {
        if (modal && event.target === modal) {
            modal.style.display = "none";
            if (modal === modalCreate) {
                resetCreateModalFields();
                resetCreateError();
            }else if (modal === modalEdit) {
                resetEditModalFields();
                resetEditError();
            }else if (modal === modalDelete) {
                resetDeleteError();
            }
        }
    });
}

// Ouvre la popup si erreur détectée
document.addEventListener("DOMContentLoaded", function () {
    if (document.querySelector('.error-message-create') && modalCreate) {
        modalCreate.style.display = "block";
    }
    if (document.querySelector('.error-message-edit') && modalEdit) {
        modalEdit.style.display = "block";
    }
    if (document.querySelector('.error-message-delete') && modalDelete) {
        modalDelete.style.display = "block";
        const deleteBtn = modalDelete.querySelector('button[type="submit"]');
        if (deleteBtn) deleteBtn.style.display = 'none';
    }
});

// Réinitialiser les champs du formulaire Create
function resetCreateModalFields() {
    const inputs = modalCreate.querySelectorAll("input");
    inputs.forEach(input => input.value = "");
    const select = modalCreate.querySelector("select");
    if (select) select.value = "true";
}

// Réinitialiser l'affichage de l'erreur dans Create
function resetCreateError() {
    const errorDiv = modalCreate.querySelector(".alert-danger");
    if (errorDiv) errorDiv.style.display = "none";
    const errorInputs = modalCreate.querySelectorAll(".input-error");
    errorInputs.forEach(input => input.classList.remove("input-error"));
}

// Réinitialiser les champs du formulaire Edit
function resetEditModalFields() {
    const inputs = modalEdit.querySelectorAll("input");
    inputs.forEach(input => {
        input.value = "";
    });
    const select = modalEdit.querySelector("select");
    if (select) select.value = "";
}

// Réinitialiser l'affichage de l'erreur dans Edit
function resetEditError() {
    const errorDiv = modalEdit.querySelector(".alert-danger");
    if (errorDiv) errorDiv.style.display = "none";
    const errorInputs = modalEdit.querySelectorAll(".input-error");
    errorInputs.forEach(input => input.classList.remove("input-error"));
}

// Réinitialiser l'affichage de l'erreur dans Delete
function resetDeleteError() {
    const errorDiv = document.querySelector('.error-message-delete');
    if (errorDiv) {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
    }
    const deleteBtn = modalDelete.querySelector('button[type="submit"]');
    if (deleteBtn) deleteBtn.style.display = 'inline-block';
}

// Ouvre la popup delete et met à jour le formulaire
document.querySelectorAll('.openDeletePopup').forEach(btn => {
    btn.addEventListener('click', e => {
        e.preventDefault();
        const id = btn.getAttribute('data-id');
        const form = document.getElementById('deleteForm');
        form.action = `/manage-gateways/delete/${id}`;
        document.getElementById('deleteGatewayPopup').style.display = 'block';
    });
});

// Pour renvoyer les champs date sous le format YYYY-MM-DD
flatpickr(".datepicker", {
    dateFormat: "Y-m-d"
});

// === Navigation back reload ===
window.addEventListener('pageshow', function(event) {
    if (event.persisted) {
        if (modalEdit) modalEdit.style.display = "none";
        modalCreate.style.display = "none";
        modalDelete.style.display = "none";
        window.location.href = "/home?" + new Date().getTime();
    }
});

function refreshCsrfToken() {
    fetch('/csrf-token')
    .then(response => response.json())
    .then(data => {
        const input = document.querySelector('form#myForm input[name="' + data.parameterName + '"]');
        if (input) {
            input.value = data.token;
        }
    });
}

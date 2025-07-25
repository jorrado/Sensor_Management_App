// Récupérer les popups
var modalCreate = document.getElementById("createGatewayPopup");
var modalEdit = document.getElementById("editGatewayPopup");
var modalDelete = document.getElementById("deleteGatewayPopup");

// Récupérer le bouton qui ouvre la popup Create
var btnCreate = document.getElementById("openCreateBtn");

// Récupérer les éléments qui ferment les popups
var exitCreate = document.getElementById("closeCreate");
var exitEdit = document.getElementById("closeEdit");
if (document.getElementById("closeEdit")) {
    var exitEdit = document.getElementById("closeEdit");
    exitEdit.addEventListener("click", () => {
        modalEdit.style.display = "none";
    });
}
var cancelDelete = document.getElementById("cancelDelete");

// Quand l'utilisateur clique sur ouvrir la popup Create
if (btnCreate) {
    btnCreate.addEventListener("click", () => {
        modalCreate.style.display = "block";
    });
}

// Quand l'utilisateur clique sur Exit, fermer la popup Create
if (exitCreate) {
    exitCreate.addEventListener("click", () => {
        modalCreate.style.display = "none";
        resetModalFields();
        resetError();
    });
}

// Fermer Edit
//if (exitEdit) {
//    exitEdit.addEventListener("click", () => {
//        modalEdit.style.display = "none";
//    });
//}

// Fermer Delete
if (cancelDelete) {
    cancelDelete.addEventListener("click", () => {
        modalDelete.style.display = "none";
    });
}

// Quand l'utilisateur clique en dehors d'une popup, la fermer
window.onclick = function(event) {
    [modalCreate, modalEdit, modalDelete].forEach(modal => {
        if (modal && event.target === modal) {
            modal.style.display = "none";
            if (modal === modalCreate) {
                resetModalFields();
                resetError();
            }
        }
    });
}

// Si l'erreur est levée alors ouvrir la popup Create, sinon fermer les popups
document.addEventListener("DOMContentLoaded", function () {
    if (modalCreate) modalCreate.style.display = "none";
    if (modalDelete) modalDelete.style.display = "none";
    if (document.querySelector(".alert-danger")) {
        modalCreate.style.display = "block";
    }
});

// Fonction pour réinitialiser les champs du formulaire Create
function resetModalFields() {
    const inputs = modalCreate.querySelectorAll("input");
    inputs.forEach(input => input.value = "");
    const select = modalCreate.querySelector("select");
    if (select) select.value = "true";
}

// Fonction pour réinitialiser l'affichage de l'erreur
function resetError() {
    const errorDiv = modalCreate.querySelector(".alert-danger");
    if (errorDiv) errorDiv.style.display = "none";
}

//let currentForm;
//document.querySelectorAll(".deleteForm").forEach(form => {
//    form.addEventListener('click', (event) => {
//        event.preventDefault();
//        currentForm = form;
//        modalDelete.style.display = "block";
//    });
//});
document.querySelectorAll('.openDeletePopup').forEach(btn => {
    btn.addEventListener('click', e => {
      e.preventDefault();
      const id = btn.getAttribute('data-id');
      const form = document.getElementById('deleteForm');
      form.action = `/manage-gateways/delete/${id}`;
      document.getElementById('deleteGatewayPopup').style.display = 'block';
    });
});

//document.getElementById('confirmDelete').addEventListener('click', () => {
//    if (currentForm) currentForm.submit();
//});

document.getElementById('cancelDelete').addEventListener('click', () => {
    modalDelete.style.display = 'none';
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

// Pour renvoyer les champs date sous le format YYYY-MM-DD
flatpickr(".datepicker", {
    dateFormat: "Y-m-d"
});

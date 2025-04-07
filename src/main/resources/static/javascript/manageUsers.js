// Récupérer les popups modales
var modalCreate = document.getElementById("createUserModal");
var modalEdit = document.getElementById("editUserModal");

// Récupérer le bouton qui ouvre la modale Create
var btnCreate = document.getElementById("openModalBtn");

// Récupérer les éléments qui ferment les modales
var exitModalCreate = document.getElementsByClassName("closeCreate")[0];
var exitModalEdit = document.getElementsByClassName("closeEdit")[0];

// Quand l'utilisateur clique sur ouvrir la modale Create
btnCreate.onclick = function() {
    modalCreate.style.display = "block";
}

// Quand l'utilisateur clique sur Exit, fermer la modale Create
exitModalCreate.onclick = function() {
    modalCreate.style.display = "none";
    resetModalFields();
    resetError();
}

// Quand l'utilisateur clique sur Exit, fermer la modale Edit
exitModalEdit.onclick = function() {
    modalEdit.style.display = "none";
}

// Quand l'utilisateur clique en dehors d'une modale, la fermer
window.onclick = function(event) {
    if (event.target == modalCreate) {
        modalCreate.style.display = "none";
        resetModalFields();
        resetError();
    }
    if (event.target == modalEdit) {
        modalEdit.style.display = "none";
    }
}

// Ouvre la modal Create si une erreur existe
if (document.querySelector(".alert-danger")) {
    modalCreate.style.display = "block";
}

// Fonction pour réinitialiser les champs du formulaire Create
function resetModalFields() {
    const inputs = modalCreate.querySelectorAll("input");
    inputs.forEach(input => input.value = "");
    const select = modalCreate.querySelector("select");
    if (select) {
        select.value = "USER";
    }
}

// Fonction pour réinitialiser l'affichage de l'erreur
function resetError() {
    const errorDiv = modalCreate.querySelector(".alert-danger");
    if (errorDiv) {
        errorDiv.style.display = "none";
    }
}

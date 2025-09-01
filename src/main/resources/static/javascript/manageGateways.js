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
        const select = modalCreate.querySelector("select[name='frequencyPlan']");
        if (select) select.value = "";
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
    if (select) select.value = "";
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

// === Navigation back/reload ===
window.addEventListener('pageshow', function(event) {
    const navType = performance.getEntriesByType('navigation')[0]?.type;
    if (document.querySelector('.error-message-create')
      || document.querySelector('.error-message-edit')
      || document.querySelector('.error-message-delete')) {
        history.replaceState(null, null, window.location.href);
    }
    if (event.persisted || navType === 'back_forward' || navType === 'reload') {
        ['#deleteGatewayPopup', '#editGatewayPopup', '#createGatewayPopup'].forEach(sel => {
            document.querySelectorAll(sel).forEach(el => el.style.display = 'none');
        });
        ['.error-message-create', '.error-message-edit', '.error-message-delete'].forEach(sel => {
            document.querySelectorAll(sel).forEach(el => el.remove());
        });
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

document.addEventListener('DOMContentLoaded', () => {
  const gateways = Array.isArray(window.GATEWAYS) ? window.GATEWAYS : [];

  const tableBody = document.getElementById('gatewayTableBody');
  const buildingFilter = document.getElementById('buildingFilter');
  const searchInput = document.getElementById('searchInput');
  const dateInput = document.getElementById('dateFilter');

// Ajoute/enlève la classe "empty" selon la valeur
function updateBuildingPlaceholderStyle() {
  if (!buildingFilter) return;
  const isEmpty = !buildingFilter.value;
  buildingFilter.classList.toggle('empty', isEmpty);
}
  updateBuildingPlaceholderStyle();

  // Focus/blur border sur search (remplace les attributs inline)
  if (searchInput) {
    searchInput.addEventListener('focus', () => searchInput.style.borderColor = '#440d64');
    searchInput.addEventListener('blur', () => searchInput.style.borderColor = '#662179');
  }

  // Convertit une syntaxe SQL LIKE (avec %) en RegExp JS
  function sqlLikeToRegex(pattern) {
    let escaped = pattern.replace(/[.+^${}()|[\]\\]/g, '\\$&');
    escaped = escaped.replace(/%/g, '.*');
    return new RegExp('^' + escaped + '$', 'i');
  }

  // Vérifie si une valeur matche avec "LIKE %" ou commence par la recherche
  function matchesLikeOrIncludes(value, query) {
    if (!query) return true;
    const v = String(value ?? '');
    if (query.includes('%')) {
      return sqlLikeToRegex(query).test(v);
    }
    return v.toLowerCase().startsWith(query.toLowerCase());
  }

  // Remplit le select "Building"
  function populateBuildings() {
    if (!buildingFilter) return;
    const uniques = Array.from(new Set(
      (gateways || []).map(g => (g.buildingName || '').trim()).filter(Boolean)
    )).sort();

    // Nettoie d'abord tout sauf le placeholder
    buildingFilter.querySelectorAll('option:not([value=""])').forEach(o => o.remove());

    for (const b of uniques) {
      const opt = document.createElement('option');
      opt.value = b;
      opt.textContent = b;
      buildingFilter.appendChild(opt);
    }
    updateBuildingPlaceholderStyle();
  }

  // Construit les lignes HTML
  function renderRows(rows) {
    if (!tableBody) return;
    tableBody.innerHTML = '';
    rows.forEach(gw => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${gw.gatewayId ?? ''}</td>
        <td>${gw.ipAddress ?? ''}</td>
        <td>${
          gw.frequencyPlan === 'EU_863_870_TTN' ? 'Europe' :
          gw.frequencyPlan === 'US_902_928_FSB_2' ? 'United States' :
          gw.frequencyPlan === 'AU_915_928_FSB_2' ? 'Australia' :
          gw.frequencyPlan === 'CN_470_510_FSB_11' ? 'China' :
          gw.frequencyPlan === 'AS_920_923' ? 'Asia' : (gw.frequencyPlan ?? '')
        }</td>
        <td>${gw.createdAt ?? ''}</td>
        <td>${gw.buildingName ?? ''}</td>
        <td>
          <div class="button-container">
            <a href="/manage-gateways/monitoring/${gw.gatewayId}/view?ip=${gw.ipAddress}">
              <img src="/image/monitoring-data.svg" alt="Monitor">
            </a>
            <a href="/manage-gateways/edit/${gw.gatewayId}">
              <img src="/image/edit-icon.svg" alt="Edit">
            </a>
            <a href="#" class="openDeletePopup" data-id="${gw.gatewayId}">
              <img src="/image/delete-icon.svg" alt="Delete">
            </a>
          </div>
        </td>
      `;
      tableBody.appendChild(row);
    });

    // Re-binde les boutons "delete" ajoutés dynamiquement
    tableBody.querySelectorAll('.openDeletePopup').forEach(btn => {
      btn.addEventListener('click', e => {
        e.preventDefault();
        const id = btn.getAttribute('data-id');
        const form = document.getElementById('deleteForm');
        form.action = `/manage-gateways/delete/${id}`;
        document.getElementById('deleteGatewayPopup').style.display = 'block';
      });
    });
  }

  // Applique tous les filtres
  function applyFilters() {
    const b = buildingFilter?.value || '';
    const q = searchInput?.value || '';
    const d = (dateInput?.value || '').trim();

    let rows = (gateways || []).slice();

    // filtre par building (exact)
    if (b) rows = rows.filter(g => (g.buildingName || '') === b);

    // filtre texte (gatewayId ou ipAddress)
    if (q) {
      rows = rows.filter(g =>
        matchesLikeOrIncludes(g.gatewayId, q) ||
        matchesLikeOrIncludes(g.ipAddress, q)
      );
    }

    // filtre date >=
    if (d) {
      const dDate = new Date(d);
      rows = rows.filter(g => {
        if (!g.createdAt) return false;
        const gDate = new Date(g.createdAt);
        return gDate >= dDate;
      });
    }

    renderRows(rows);
  }

  // Écouteurs
  buildingFilter?.addEventListener('change', () => {
    updateBuildingPlaceholderStyle();
    applyFilters();
  });
  searchInput?.addEventListener('input', applyFilters);
  dateInput?.addEventListener('input', applyFilters);

  // Boutons clear (croix)
  document.getElementById('clearBuilding')?.addEventListener('click', () => {
    if (!buildingFilter) return;
    buildingFilter.value = '';
    updateBuildingPlaceholderStyle();
    applyFilters();
  });

  const clearSearch = document.querySelector('#searchInput + span');
  clearSearch?.addEventListener('click', () => {
    if (!searchInput) return;
    searchInput.value = '';
    applyFilters();
  });

  const clearDate = document.querySelector('#dateFilter + span');
  clearDate?.addEventListener('click', () => {
    if (!dateInput) return;
    dateInput.value = '';
    applyFilters();
  });

  // Init
  populateBuildings();
  renderRows(gateways || []);
  applyFilters();
});

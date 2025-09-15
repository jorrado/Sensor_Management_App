// Popups (si tu veux ajouter Create/Edit/Delete plus tard)
var modalCreate = document.getElementById("createSensorPopup");
var modalDelete = document.getElementById("deleteSensorPopup");
var btnCreate   = document.getElementById("openCreateBtn");
var exitCreate  = document.getElementById("closeCreate");
var exitDelete  = document.getElementById("closeDelete");

// Données injectées par Thymeleaf
document.addEventListener('DOMContentLoaded', () => {
  const sensors = Array.isArray(window.SENSORS) ? window.SENSORS : [];

  const tableBody     = document.getElementById('sensorTableBody');
  const buildingFilter = document.getElementById('buildingFilter');
  const statusFilter   = document.getElementById('statusFilter');
  const searchInput    = document.getElementById('searchInput');
  const dateInput      = document.getElementById('dateFilter');
  const paginationEl   = document.getElementById('pagination');

  const PAGE_SIZE = 10;
  let currentPage = 1;
  let filteredRows = [];

  function updateSelectPlaceholderStyle(select) {
    if (!select) return;
    const isEmpty = !select.value;
    select.classList.toggle('empty', isEmpty);
  }

  if (buildingFilter) updateSelectPlaceholderStyle(buildingFilter);
  if (statusFilter) updateSelectPlaceholderStyle(statusFilter);

  function sqlLikeToRegex(pattern) {
    let escaped = pattern.replace(/[.+^${}()|[\]\\]/g, '\\$&');
    escaped = escaped.replace(/%/g, '.*');
    return new RegExp('^' + escaped + '.*', 'i');
  }

  function matchesLikeOrIncludes(value, query) {
    if (!query) return true;
    const v = String(value ?? '');
    if (query.includes('%')) {
      return sqlLikeToRegex(query).test(v);
    }
    return v.toLowerCase().startsWith(query.toLowerCase());
  }

  // Peupler le filtre Building
  function populateBuildings() {
    if (!buildingFilter) return;
    const uniques = Array.from(new Set(
      (sensors || []).map(s => (s.buildingName || '').trim()).filter(Boolean)
    )).sort();

    buildingFilter.querySelectorAll('option:not([value=""])').forEach(o => o.remove());

    for (const b of uniques) {
      const opt = document.createElement('option');
      opt.value = b;
      opt.textContent = b;
      buildingFilter.appendChild(opt);
    }
    updateSelectPlaceholderStyle(buildingFilter);
  }

function renderRows(rows) {
  if (!tableBody) return;
  tableBody.innerHTML = '';
  rows.forEach((s) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${s.idSensor ?? ''}</td>
      <td>${s.deviceType ?? ''}</td>
      <td>${s.commissioningDate ?? ''}</td>
      <td>
        <span class="${s.status ? 'badge badge--ok' : 'badge badge--off'}">
          ${s.status ? 'Active' : 'Inactive'}
        </span>
      </td>
      <td>${s.buildingName ?? ''}</td>
      <td>${s.location ?? ''}</td>
      <td>${s.idGateway ?? ''}</td>
      <td>
        <div class="button-container">
          <a href="/manage-sensors/monitoring/${s.idSensor}">
            <img src="/image/monitoring-data.svg" alt="Monitor">
          </a>
          <a href="/manage-sensors/edit/${s.idSensor}">
            <img src="/image/edit-icon.svg" alt="Edit">
          </a>
          <a href="#" class="openDeletePopup" data-id="${s.idSensor}">
            <img src="/image/delete-icon.svg" alt="Delete">
          </a>
        </div>
      </td>
    `;
    tableBody.appendChild(row);
  });

  if (rows.length === 0) {
    const emptyRow = document.createElement('tr');
    emptyRow.innerHTML = `
      <td colspan="7" style="text-align:center; font-style:italic; padding:16px;">
        No sensors found.
      </td>
    `;
    tableBody.appendChild(emptyRow);
  }

  // Re-bind des boutons Delete
  tableBody.querySelectorAll('.openDeletePopup').forEach(btn => {
    btn.addEventListener('click', e => {
      e.preventDefault();
      const id = btn.getAttribute('data-id');
      const form = document.getElementById('deleteForm');
      form.action = `/manage-sensors/delete/${id}`;
      document.getElementById('deleteSensorPopup').style.display = 'block';
    });
  });
}

  // Pagination
  function renderPagination(totalCount) {
    if (!paginationEl) return;

    const totalPages = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
    currentPage = Math.min(currentPage, totalPages);

    paginationEl.innerHTML = '';
    const btn = (label, page, disabled = false, active = false, ariaLabel) => {
      const b = document.createElement('button');
      b.className = 'page-btn' + (active ? ' active' : '');
      b.textContent = label;
      if (ariaLabel) b.setAttribute('aria-label', ariaLabel);
      if (disabled) b.setAttribute('disabled', 'disabled');
      b.addEventListener('click', () => {
        if (!disabled && currentPage !== page) {
          currentPage = page;
          renderRowsPaginated();
        }
      });
      return b;
    };

    paginationEl.appendChild(btn('«', 1, currentPage === 1, false, 'First page'));
    paginationEl.appendChild(btn('‹', currentPage - 1, currentPage === 1, false, 'Previous page'));

    const totalPagesToShow = 5;
    let startPage = Math.max(1, currentPage - Math.floor(totalPagesToShow / 2));
    let endPage = Math.min(totalPages, startPage + totalPagesToShow - 1);
    if (endPage - startPage + 1 < totalPagesToShow) {
      startPage = Math.max(1, endPage - totalPagesToShow + 1);
    }

    for (let p = startPage; p <= endPage; p++) {
      paginationEl.appendChild(btn(String(p), p, false, p === currentPage));
    }

    paginationEl.appendChild(btn('›', currentPage + 1, currentPage === totalPages, false, 'Next page'));
    paginationEl.appendChild(btn('»', totalPages, currentPage === totalPages, false, 'Last page'));
  }

  // Appliquer les filtres
  function applyFilters() {
    const b = buildingFilter?.value || '';
    const s = statusFilter?.value || '';
    const q = (searchInput?.value || '').trim();
    const d = (dateInput?.value || '').trim();

    let rows = (sensors || []).slice();

    // Filter by building
    if (b) rows = rows.filter(x => (x.buildingName || '') === b);

    // Filter by status (true/false)
    if (s) {
      const boolVal = (s === 'true');
      rows = rows.filter(x => String(x.status) === String(boolVal));
    }

    // Filter by sensorId (like startsWith)
    if (q) {
      rows = rows.filter(x =>
        matchesLikeOrIncludes(x.idSensor, q)
      );
    }

    // Filter by commissioningDate >= selected
    if (d) {
      const dDate = new Date(d);
      rows = rows.filter(x => {
        if (!x.commissioningDate) return false;
        const gDate = new Date(x.commissioningDate);
        return gDate >= dDate;
      });
    }

    filteredRows = rows;
    currentPage = 1;
    renderRowsPaginated();
  }

  // Rendu paginé
  function renderRowsPaginated() {
    const total = filteredRows.length;
    const start = (currentPage - 1) * PAGE_SIZE;
    const pageRows = filteredRows.slice(start, start + PAGE_SIZE);
    renderRows(pageRows);
    renderPagination(total);
  }

  // Écouteurs
  buildingFilter?.addEventListener('change', () => {
    updateSelectPlaceholderStyle(buildingFilter);
    applyFilters();
  });
  statusFilter?.addEventListener('change', () => {
    updateSelectPlaceholderStyle(statusFilter);
    applyFilters();
  });
  searchInput?.addEventListener('input', applyFilters);
  dateInput?.addEventListener('input', applyFilters);

  // Clear boutons
  document.getElementById('clearBuilding')?.addEventListener('click', () => {
    if (!buildingFilter) return;
    buildingFilter.value = '';
    updateSelectPlaceholderStyle(buildingFilter);
    applyFilters();
  });

  document.getElementById('clearStatus')?.addEventListener('click', () => {
    if (!statusFilter) return;
    statusFilter.value = '';
    updateSelectPlaceholderStyle(statusFilter);
    applyFilters();
  });

  document.getElementById('clearSearch')?.addEventListener('click', () => {
    if (!searchInput) return;
    searchInput.value = '';
    applyFilters();
  });

  document.getElementById('clearDate')?.addEventListener('click', () => {
    if (!dateInput) return;
    dateInput.value = '';
    applyFilters();
  });

  // Fermer la popup Delete (bouton)
  if (exitDelete) {
    exitDelete.addEventListener("click", () => {
      const errorDiv = document.querySelector('.error-message-delete');
      if (errorDiv) {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
      }
      document.getElementById('deleteSensorPopup').style.display = 'none';
    });
  }

  // Fermer popup au clic extérieur
  window.addEventListener('click', function(event) {
    const modal = document.getElementById('deleteSensorPopup');
    if (modal && event.target === modal) {
      const errorDiv = document.querySelector('.error-message-delete');
      if (errorDiv) {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
      }
      modal.style.display = 'none';
    }
  });


  // Init
  populateBuildings();
  filteredRows = sensors || [];
  renderRowsPaginated();
});

/* Flatpickr init */
flatpickr(".datepicker", { dateFormat: "Y-m-d" });

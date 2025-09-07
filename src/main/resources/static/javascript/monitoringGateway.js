const ctxRam = document.getElementById('ramChart').getContext('2d');
    const ramChart = new Chart(ctxRam, {
        type: 'line',
        data: {
            labels: Array(30).fill(''),
            datasets: [{
                label: 'RAM Usage (%) : ' + new Date().toISOString().split('T')[0],
                data: Array(30).fill(0),
                borderColor: '#28a745',
                backgroundColor: 'rgba(40, 167, 69, 0.1)',
                fill: true,
                tension: 0.3,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: { display: true, text: '% Usage' }
                },
                x: {
                    title: { display: true, text: 'Heure' }
                }
            },
            plugins: {
                legend: { position: 'top' }
            }
        }
    });

    const ctxCpu = document.getElementById('cpuChart').getContext('2d');
    const cpuChart = new Chart(ctxCpu, {
        type: 'line',
        data: {
            labels: Array(30).fill(''),
            datasets: [{
                label: 'CPU Usage (%) : ' + new Date().toISOString().split('T')[0],
                data: Array(30).fill(0),
                borderColor: '#007bff',
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                fill: true,
                tension: 0.3,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: { display: true, text: '% Usage' }
                },
                x: {
                    title: { display: true, text: 'Heure' }
                }
            },
            plugins: {
                legend: { position: 'top' }
            }
        }
    });

    const ctxHarddrive = document.getElementById('harddriveChart').getContext('2d');
    const harddriveChart = new Chart(ctxHarddrive, {
        type: 'line',
        data: {
            labels: Array(30).fill(''),
            datasets: [{
                label: 'Hard Drive Usage (%) : ' + new Date().toISOString().split('T')[0],
                data: Array(30).fill(0),
                borderColor: '#FFA500',
                backgroundColor: 'rgba(255, 165, 0, 0.1)',
                fill: true,
                tension: 0.3,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: { display: true, text: '% Usage' }
                },
                x: {
                    title: { display: true, text: 'Heure' }
                }
            },
            plugins: {
                legend: { position: 'top' }
            }
        }
    });

const map = L.map('map').setView([0, 0], 13);
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);
let gatewayMarker = L.marker([0, 0]).addTo(map);

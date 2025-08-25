// Lógica de la landing page pública

const colombianCities = [
    'Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena', 'Bucaramanga',
    'Cúcuta', 'Pereira', 'Santa Marta', 'Ibagué', 'Pasto', 'Manizales',
    'Neiva', 'Villavicencio', 'Armenia', 'Montería', 'Valledupar', 'Sincelejo',
    'Riohacha', 'Tunja', 'Florencia', 'Popayán', 'Yopal', 'Leticia',
    'Mocoa', 'San Andrés', 'Quibdó', 'Arauca', 'Mitú', 'Inírida', 'Puerto Carreño'
];

let allFlights = [];
let loginModal;

document.addEventListener('DOMContentLoaded', () => {
    M.AutoInit();
    loginModal = M.Modal.init(document.getElementById('login-modal'));
    initCitySelects();
    loadFlights();
    document.getElementById('search-btn').addEventListener('click', handleSearch);
});

function initCitySelects() {
    const originSelect = document.getElementById('origin-select');
    const destinationSelect = document.getElementById('destination-select');
    colombianCities.forEach(city => {
        const opt1 = document.createElement('option');
        opt1.value = city;
        opt1.textContent = city;
        originSelect.appendChild(opt1);
        const opt2 = document.createElement('option');
        opt2.value = city;
        opt2.textContent = city;
        destinationSelect.appendChild(opt2);
    });
    M.FormSelect.init(document.querySelectorAll('select'));
}

function loadFlights() {
    fetch('/api/vuelos')
        .then(res => res.ok ? res.json() : [])
        .then(data => {
            allFlights = Array.isArray(data) ? data : [];
            renderFlights(allFlights);
        })
        .catch(() => {
            allFlights = [];
            renderFlights(allFlights);
        });
}

function renderFlights(flights) {
    const container = document.getElementById('offers');
    container.innerHTML = '';
    if (flights.length === 0) {
        container.innerHTML = '<div class="col s12"><div class="card-panel grey lighten-4"><span class="grey-text">No se encontraron vuelos</span></div></div>';
        return;
    }
    flights.forEach(f => {
        const col = document.createElement('div');
        col.className = 'col s12 m6 l4';
        col.innerHTML = `
            <div class="card hoverable offer-card">
                <div class="card-content center">
                    <span class="card-title">${f.origen} → ${f.destino}</span>
                    <p>Vuelo ${f.numeroVuelo || f.numero_vuelo || ''}</p>
                    <p class="red-text text-darken-2">COP ${Number(f.precio).toLocaleString()}</p>
                </div>
            </div>`;
        col.querySelector('.card').addEventListener('click', showLoginPrompt);
        container.appendChild(col);
    });
}

function handleSearch() {
    const origin = document.getElementById('origin-select').value;
    const destination = document.getElementById('destination-select').value;
    let filtered = allFlights;
    if (origin) filtered = filtered.filter(f => f.origen === origin);
    if (destination) filtered = filtered.filter(f => f.destino === destination);
    renderFlights(filtered);
}

function showLoginPrompt() {
    loginModal.open();
}

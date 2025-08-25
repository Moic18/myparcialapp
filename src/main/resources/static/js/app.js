// Frontend logic for interacting with backend API

document.addEventListener('DOMContentLoaded', () => {
    M.AutoInit();

    const loginSection = document.getElementById('login-section');
    const appContent = document.getElementById('app-content');
    const logoutBtn = document.getElementById('logout-btn');
    const clienteSection = document.getElementById('cliente-section');
    const adminSection = document.getElementById('admin-section');
    const infoLink = document.getElementById('info-link');
    const infoSection = document.getElementById('info-section');

    let token = localStorage.getItem('jwtToken');
    let currentRole = localStorage.getItem('userRole');
    let selectedFlightId = null;

    function updateUI() {
        if (token) {
            loginSection.style.display = 'none';
            appContent.style.display = 'block';
            logoutBtn.style.display = 'block';
            clienteSection.style.display = currentRole ? 'block' : 'none';
            const adminVisible = currentRole === 'ADMINISTRADOR' || currentRole === 'EMPLEADO';
            adminSection.style.display = adminVisible ? 'block' : 'none';
            infoLink.style.display = infoSection.style.display = adminVisible ? 'block' : 'none';
        } else {
            loginSection.style.display = 'block';
            appContent.style.display = 'none';
            logoutBtn.style.display = 'none';
        }
    }

    function handleError(message) {
        M.toast({ html: message, classes: 'red' });
    }

    function showSuccess(message) {
        M.toast({ html: message, classes: 'green' });
    }

    function showInfo(message) {
        M.toast({ html: message, classes: 'blue' });
    }

    // Función para formatear valores de objetos
    function formatObjectValue(value, depth = 0) {
        if (value === null || value === undefined) return '<span class="grey-text">N/A</span>';

        if (typeof value === 'object') {
            if (Array.isArray(value)) {
                if (value.length === 0) return '<span class="grey-text">[]</span>';
                return value.map(item => formatObjectValue(item, depth + 1)).join('<br>');
            } else {
                let html = '<div class="nested-object">';
                Object.entries(value).forEach(([key, val]) => {
                    const formattedKey = key.split('_').map(word =>
                        word.charAt(0).toUpperCase() + word.slice(1)
                    ).join(' ');

                    if (typeof val === 'object' && val !== null) {
                        html += `<div class="nested-item">
                            <strong>${formattedKey}:</strong>
                            <div class="nested-content">${formatObjectValue(val, depth + 1)}</div>
                        </div>`;
                    } else {
                        html += `<div class="nested-item">
                            <strong>${formattedKey}:</strong> ${formatSimpleValue(val)}
                        </div>`;
                    }
                });
                return html + '</div>';
            }
        }

        return formatSimpleValue(value);
    }

    function formatSimpleValue(value) {
        if (typeof value === 'boolean') {
            return value ?
                '<span class="green-text text-darken-2"><i class="material-icons tiny">check_circle</i> Sí</span>' :
                '<span class="red-text text-darken-2"><i class="material-icons tiny">cancel</i> No</span>';
        }

        if (typeof value === 'string' && value.includes('T') && value.includes('Z')) {
            try {
                const date = new Date(value);
                return `<span class="blue-text text-darken-2">
                    <i class="material-icons tiny">event</i> ${date.toLocaleDateString()} ${date.toLocaleTimeString()}
                </span>`;
            } catch (e) {
                return value;
            }
        }

        if (typeof value === 'number' && value.toString().includes('.')) {
            return `$${value.toFixed(2)}`;
        }

        return value;
    }

    function showOutput(id, data, showToast = true) {
        const container = document.getElementById(id);
        container.innerHTML = '';
        const items = Array.isArray(data) ? data : [data];

        if (items.length === 0) {
            container.innerHTML = `
                <div class="col s12">
                    <div class="card-panel grey lighten-4">
                        <span class="grey-text">No se encontraron resultados</span>
                    </div>
                </div>
            `;
            return;
        }

        items.forEach(item => {
            const col = document.createElement('div');
            col.className = 'col s12 m6 l4';

            let content = '<div class="object-display">';
            Object.entries(item).forEach(([key, value]) => {
                const formattedKey = key.split('_').map(word =>
                    word.charAt(0).toUpperCase() + word.slice(1)
                ).join(' ');

                content += `
                    <div class="object-field">
                        <strong class="field-label">${formattedKey}:</strong>
                        <span class="field-value">${formatObjectValue(value)}</span>
                    </div>
                `;
            });
            content += '</div>';

            col.innerHTML = `
                <div class="card hoverable">
                    <div class="card-content">
                        <span class="card-title blue-text text-darken-2">
                            <i class="material-icons tiny">description</i> Detalles
                        </span>
                        ${content}
                    </div>
                </div>
            `;
            container.appendChild(col);
        });

        if (showToast) showSuccess('Operación exitosa');
    }

    // Función específica para mostrar reservas en formato de tarjetas grid
    function showReservationOutput(id, reservationData) {
        const container = document.getElementById(id);
        container.innerHTML = '';
        const reservations = Array.isArray(reservationData) ? reservationData : [reservationData];

        if (reservations.length === 0) {
            container.innerHTML = `
                <div class="col s12">
                    <div class="card-panel blue lighten-5">
                        <span class="blue-text text-darken-2">
                            <i class="material-icons left">info</i> No se encontraron reservas
                        </span>
                    </div>
                </div>
            `;
            return;
        }

        reservations.forEach(reservation => {
            const col = document.createElement('div');
            col.className = 'col s12 m6 l4';

            // Determinar color según estado
            let statusColor = 'blue';
            let statusIcon = 'info';

            if (reservation.estado === 'CONFIRMADA') {
                statusColor = 'green';
                statusIcon = 'check_circle';
            } else if (reservation.estado === 'CANCELADA') {
                statusColor = 'red';
                statusIcon = 'cancel';
            } else if (reservation.estado === 'COMPLETADA') {
                statusColor = 'purple';
                statusIcon = 'done_all';
            }

            let content = `
                <div class="card reservation-grid-card hoverable">
                    <div class="card-content ${statusColor} darken-2 white-text" style="padding: 15px;">
                        <span class="card-title white-text">
                            <i class="material-icons left">confirmation_number</i>
                            ${reservation.codigo_reserva || 'Sin código'}
                        </span>
                        <p class="white-text">Estado: ${reservation.estado || 'N/A'}</p>
                    </div>

                    <div class="card-content" style="padding: 20px;">
                        <!-- Información básica -->
                        <div class="reservation-basic-info">
                            <p><strong>Total:</strong> $${reservation.total || '0.00'}</p>
                            <p><strong>Pasajeros:</strong> ${reservation.cantidad_pasajeros || '0'}</p>
                            <p><strong>Fecha:</strong> ${reservation.fecha_reserva ? new Date(reservation.fecha_reserva).toLocaleDateString() : 'N/A'}</p>
                            <p><strong>Puede Cancelar:</strong> ${reservation.puede_cancel ?
                                '<span class="green-text"><i class="material-icons tiny">check</i> Sí</span>' :
                                '<span class="red-text"><i class="material-icons tiny">block</i> No</span>'}</p>
                        </div>

                        <div class="divider" style="margin: 15px 0;"></div>

                        <!-- Información del vuelo (expandible) -->
                        <div class="reservation-details">
                            <a class="waves-effect waves-teal btn-flat toggle-details" style="padding: 0; margin-bottom: 10px;">
                                <i class="material-icons left">flight</i> Ver detalles del vuelo
                                <i class="material-icons right">expand_more</i>
                            </a>

                            <div class="flight-details" style="display: none; margin-left: 10px; padding-left: 10px; border-left: 2px solid #2196f3;">
            `;

            // Información del vuelo
            if (reservation.vuelo && typeof reservation.vuelo === 'object') {
                content += `
                    <p><strong>Número:</strong> ${reservation.vuelo.numero_vuelo || 'N/A'}</p>
                    <p><strong>Ruta:</strong> ${reservation.vuelo.origen || 'N/A'} → ${reservation.vuelo.destino || 'N/A'}</p>
                    <p><strong>Salida:</strong> ${reservation.vuelo.fecha_salida ? new Date(reservation.vuelo.fecha_salida).toLocaleString() : 'N/A'}</p>
                    <p><strong>Llegada:</strong> ${reservation.vuelo.fecha_llegada ? new Date(reservation.vuelo.fecha_llegada).toLocaleString() : 'N/A'}</p>
                    <p><strong>Precio:</strong> $${reservation.vuelo.precio || '0.00'}</p>
                `;
            }

            content += `
                            </div>

                            <!-- Información del cliente -->
                            <a class="waves-effect waves-teal btn-flat toggle-details" style="padding: 0; margin: 10px 0;">
                                <i class="material-icons left">person</i> Ver información del cliente
                                <i class="material-icons right">expand_more</i>
                            </a>

                            <div class="client-details" style="display: none; margin-left: 10px; padding-left: 10px; border-left: 2px solid #4caf50;">
            `;

            // Información del cliente
            if (reservation.cliente && typeof reservation.cliente === 'object') {
                content += `
                    <p><strong>Nombre:</strong> ${reservation.cliente.name || reservation.cliente.nombre || 'N/A'}</p>
                    <p><strong>Email:</strong> ${reservation.cliente.email || 'N/A'}</p>
                    <p><strong>Rol:</strong> ${reservation.cliente.role || 'N/A'}</p>
                `;
            }

            content += `
                            </div>

                            <!-- Información de pasajeros -->
                            <a class="waves-effect waves-teal btn-flat toggle-details" style="padding: 0; margin: 10px 0;">
                                <i class="material-icons left">group</i> Ver pasajeros (${reservation.pasajeros?.length || 0})
                                <i class="material-icons right">expand_more</i>
                            </a>

                            <div class="passengers-details" style="display: none;">
            `;

            // Información de pasajeros
            if (reservation.pasajeros && Array.isArray(reservation.pasajeros) && reservation.pasajeros.length > 0) {
                reservation.pasajeros.forEach((pasajero, index) => {
                    content += `
                        <div class="passenger-item" style="margin: 10px 0; padding: 10px; background: #f5f5f5; border-radius: 5px;">
                            <p><strong>Pasajero ${index + 1}:</strong> ${pasajero.nombre || 'N/A'}</p>
                            <p><strong>Documento:</strong> ${pasajero.documento || 'N/A'}</p>
                            <p><strong>Edad:</strong> ${pasajero.edad || 'N/A'}</p>
                        </div>
                    `;
                });
            } else {
                content += `<p class="grey-text">No hay pasajeros registrados</p>`;
            }

            content += `
                            </div>
                        </div>
                    </div>

                    <div class="card-action">
                        <span class="badge ${statusColor} white-text" style="padding: 3px 10px; border-radius: 12px;">
                            <i class="material-icons tiny">${statusIcon}</i> ${reservation.estado}
                        </span>
                    </div>
                </div>
            `;

            col.innerHTML = content;
            container.appendChild(col);
        });

        // Agregar funcionalidad de toggle para los detalles
        setTimeout(() => {
            document.querySelectorAll('.toggle-details').forEach(button => {
                button.addEventListener('click', function(e) {
                    e.preventDefault();
                    const target = this.nextElementSibling;
                    const icon = this.querySelector('.material-icons.right');

                    if (target.style.display === 'none') {
                        target.style.display = 'block';
                        icon.textContent = 'expand_less';
                    } else {
                        target.style.display = 'none';
                        icon.textContent = 'expand_more';
                    }
                });
            });
        }, 100);

        showSuccess(`Mostrando ${reservations.length} reserva${reservations.length !== 1 ? 's' : ''}`);
    }

    function apiRequest(method, url, body) {
        const options = { method, headers: {} };
        if (token) options.headers['Authorization'] = `Bearer ${token}`;
        if (body) {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(body);
        }
        return fetch(url, options).then(async r => {
            const text = await r.text();
            if (!r.ok) {
                let message = 'Error';
                try {
                    const obj = JSON.parse(text);
                    message = `${obj.message || text} (Código ${r.status})`;
                } catch {
                    message = `${text || 'Error'} (Código ${r.status})`;
                }
                throw new Error(message);
            }
            try { return JSON.parse(text); } catch { return text; }
        });
    }

    // Función para validar que el rol esté en mayúsculas
    function validateRole(role) {
        if (role && role !== role.toUpperCase()) {
            showInfo('El rol debe estar en MAYÚSCULAS (ej: CLIENTE, EMPLEADO, ADMINISTRADOR)');
            return false;
        }
        return true;
    }

    // Login/logout
    document.getElementById('login-form').addEventListener('submit', e => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        apiRequest('POST', '/api/auth/login', { email, password })
            .then(data => {
                token = data.token;
                currentRole = data.user.role;
                localStorage.setItem('jwtToken', token);
                localStorage.setItem('userRole', currentRole);
                updateUI();
                showSuccess('Inicio de sesión exitoso');
            })
            .catch(err => handleError(err.message));
    });

    logoutBtn.addEventListener('click', () => {
        token = null;
        currentRole = null;
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        updateUI();
        showInfo('Sesión cerrada');
    });

    // ----- Cliente -----
    document.getElementById('create-reservation-form').addEventListener('submit', e => {
        e.preventDefault();
        if (!selectedFlightId) {
            handleError('Debe seleccionar un vuelo');
            return;
        }
        const payload = {
            vuelo_id: Number(selectedFlightId),
            pasajeros: [
                {
                    nombre: document.getElementById('pasajero_nombre').value,
                    documento: document.getElementById('pasajero_documento').value,
                    edad: Number(document.getElementById('pasajero_edad').value),
                    email: document.getElementById('pasajero_email').value,
                    telefono: document.getElementById('pasajero_telefono').value
                }
            ]
        };
        apiRequest('POST', '/api/reservas', payload)
            .then(data => {
                showSuccess('Reserva creada exitosamente');
                showReservationOutput('my-reservations-output', data);
                e.target.reset();
                selectedFlightId = null;
                document.getElementById('vuelo_id').value = '';
                document.getElementById('selected-flight-info').textContent = 'Ningún vuelo seleccionado';
                document.querySelectorAll('#flights-list .offer-card').forEach(card => card.classList.remove('selected-flight'));
            })
            .catch(err => handleError(err.message));
    });


    document.getElementById('my-reservations-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/reservas/mis-reservas')
            .then(data => showReservationOutput('my-reservations-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('cancel-reservation-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('cancel_reserva_id').value;
        apiRequest('DELETE', `/api/reservas/${id}`)
            .then(data => {
                showSuccess('Reserva cancelada exitosamente');
                showReservationOutput('my-reservations-output', data);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('list-flights-btn').addEventListener('click', () => {
        selectedFlightId = null;
        document.getElementById('vuelo_id').value = '';
        document.getElementById('selected-flight-info').textContent = 'Ningún vuelo seleccionado';
        apiRequest('GET', '/api/vuelos')
            .then(data => {
                const list = document.getElementById('flights-list');
                list.innerHTML = '';

                if (data.length === 0) {
                    list.innerHTML = `
                        <div class="col s12">
                            <div class="card-panel blue lighten-5">
                                <span class="blue-text text-darken-2">
                                    <i class="material-icons left">flight</i> No hay vuelos disponibles
                                </span>
                            </div>
                        </div>
                    `;
                    return;
                }

                data.forEach(f => {
                    const col = document.createElement('div');
                    col.className = 'col s12 m6 l4';
                    col.innerHTML = `
                       <div class="card hoverable offer-card">
                            <div class="card-content center">
                                <span class="card-title">${f.origen} → ${f.destino}</span>
                                <p>Vuelo ${f.numeroVuelo || f.numero_vuelo}</p>
                                <p>Salida: ${new Date(f.fechaSalida || f.fecha_salida).toLocaleString()}</p>
                                <p>Llegada: ${new Date(f.fechaLlegada || f.fecha_llegada).toLocaleString()}</p>
                                <p>Aeronave: ${f.aeronave?.modelo || ''}</p>
                                <p class="red-text text-darken-2">COP ${Number(f.precio).toLocaleString()}</p>
                                <button class="btn select-flight-btn" data-id="${f.id}" data-info="${f.origen} → ${f.destino} (${f.numeroVuelo || f.numero_vuelo})">Seleccionar</button>
                            </div>
                        </div>`;

                    col.querySelector('.select-flight-btn').addEventListener('click', () => {
                        selectedFlightId = f.id;
                        document.getElementById('vuelo_id').value = f.id;
                        document.getElementById('selected-flight-info').textContent = `Vuelo seleccionado: ${f.origen} → ${f.destino} (${f.numeroVuelo || f.numero_vuelo})`;
                        document.querySelectorAll('#flights-list .offer-card').forEach(card => card.classList.remove('selected-flight'));
                        col.querySelector('.offer-card').classList.add('selected-flight');
                        showSuccess('Vuelo seleccionado');
                    });

                    list.appendChild(col);
                });
                showSuccess('Vuelos cargados exitosamente');
            })
            .catch(err => handleError(err.message));
    });

    // ----- Usuarios -----
    document.getElementById('create-user-form').addEventListener('submit', e => {
        e.preventDefault();
        const role = document.getElementById('user_role').value;

        // Validar que el rol esté en mayúsculas
        if (!validateRole(role)) {
            return;
        }

        const payload = {
            name: document.getElementById('user_name').value,
            email: document.getElementById('user_email').value,
            password: document.getElementById('user_password').value,
            role: role.toUpperCase()
        };

        apiRequest('POST', '/api/usuarios', payload)
            .then(data => {
                showSuccess('Usuario creado exitosamente');
                showOutput('users-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('create-employee-form').addEventListener('submit', e => {
        e.preventDefault();
        const payload = {
            name: document.getElementById('emp_name').value,
            email: document.getElementById('emp_email').value,
            password: document.getElementById('emp_password').value
        };
        apiRequest('POST', '/api/usuarios/empleado', payload)
            .then(data => {
                showSuccess('Empleado creado exitosamente');
                showOutput('users-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('update-user-form').addEventListener('submit', e => {
        e.preventDefault();
        const role = document.getElementById('upd_user_role').value;

        // Validar que el rol esté en mayúsculas
        if (role && !validateRole(role)) {
            return;
        }

        const id = document.getElementById('upd_user_id').value;
        const payload = {
            name: document.getElementById('upd_user_name').value || null,
            email: document.getElementById('upd_user_email').value || null,
            role: role ? role.toUpperCase() : null
        };
        apiRequest('PUT', `/api/usuarios/${id}`, payload)
            .then(data => {
                showSuccess('Usuario actualizado exitosamente');
                showOutput('users-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-all-users-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/usuarios')
            .then(data => showOutput('users-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-users-by-role-form').addEventListener('submit', e => {
        e.preventDefault();
        const role = document.getElementById('role_query').value;
        apiRequest('GET', `/api/usuarios/rol/${role}`)
            .then(data => showOutput('users-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-user-by-id-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('user_id_query').value;
        apiRequest('GET', `/api/usuarios/${id}`)
            .then(data => showOutput('users-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('delete-user-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('del_user_id').value;
        apiRequest('DELETE', `/api/usuarios/${id}`)
            .then(data => {
                showSuccess('Usuario eliminado exitosamente');
                showOutput('users-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    // ----- Vuelos -----
    document.getElementById('create-flight-form').addEventListener('submit', e => {
        e.preventDefault();
        const payload = {
            numero_vuelo: document.getElementById('numero_vuelo').value,
            origen: document.getElementById('origen').value,
            destino: document.getElementById('destino').value,
            fecha_salida: new Date(document.getElementById('fecha_salida').value).toISOString(),
            fecha_llegada: new Date(document.getElementById('fecha_llegada').value).toISOString(),
            precio: Number(document.getElementById('precio').value),
            aeronave_id: Number(document.getElementById('aeronave_id').value)
        };
        apiRequest('POST', '/api/vuelos', payload)
            .then(data => {
                showSuccess('Vuelo creado exitosamente');
                showOutput('flights-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('search-flight-form').addEventListener('submit', e => {
        e.preventDefault();
        const payload = {
            origen: document.getElementById('search_origen').value || null,
            destino: document.getElementById('search_destino').value || null,
            fecha: document.getElementById('search_fecha').value || null
        };
        apiRequest('POST', '/api/vuelos/buscar', payload)
            .then(data => showOutput('flights-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('update-flight-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('upd_vuelo_id').value;
        const payload = {
            origen: document.getElementById('upd_origen').value || null,
            destino: document.getElementById('upd_destino').value || null,
            estado: document.getElementById('upd_estado').value || null
        };
        apiRequest('PUT', `/api/vuelos/${id}`, payload)
            .then(data => {
                showSuccess('Vuelo actualizado exitosamente');
                showOutput('flights-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-all-flights-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/vuelos')
            .then(data => showOutput('flights-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-flights-by-status-form').addEventListener('submit', e => {
        e.preventDefault();
        const status = document.getElementById('status_query').value;
        apiRequest('GET', `/api/vuelos/estado/${status}`)
            .then(data => showOutput('flights-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-flight-by-id-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('flight_id_query').value;
        apiRequest('GET', `/api/vuelos/${id}`)
            .then(data => showOutput('flights-output', data))
            .catch(err => handleError(err.message));
    });

    // ----- Pasajeros -----
    document.getElementById('passengers-by-flight-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('p_vuelo_id').value;
        apiRequest('GET', `/api/pasajeros/vuelo/${id}`)
            .then(data => showOutput('passengers-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('passengers-by-reserva-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('p_reserva_id').value;
        apiRequest('GET', `/api/pasajeros/reserva/${id}`)
            .then(data => showOutput('passengers-output', data))
            .catch(err => handleError(err.message));
    });

    // ----- Aeronaves -----
    document.getElementById('create-plane-form').addEventListener('submit', e => {
        e.preventDefault();
        const payload = {
            modelo: document.getElementById('plane_modelo').value,
            capacidad: Number(document.getElementById('plane_capacidad').value),
            codigo: document.getElementById('plane_codigo').value
        };
        apiRequest('POST', '/api/aeronaves', payload)
            .then(data => {
                showSuccess('Aeronave creada exitosamente');
                showOutput('planes-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-all-planes-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/aeronaves')
            .then(data => showOutput('planes-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('update-plane-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('upd_plane_id').value;
        const payload = {
            modelo: document.getElementById('upd_plane_modelo').value || null,
            capacidad: document.getElementById('upd_plane_capacidad').value ? Number(document.getElementById('upd_plane_capacidad').value) : null,
            codigo: document.getElementById('upd_plane_codigo').value || null
        };
        apiRequest('PUT', `/api/aeronaves/${id}`, payload)
            .then(data => {
                showSuccess('Aeronave actualizada exitosamente');
                showOutput('planes-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-plane-by-id-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('plane_id_query').value;
        apiRequest('GET', `/api/aeronaves/${id}`)
            .then(data => showOutput('planes-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('delete-plane-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('del_plane_id').value;
        apiRequest('DELETE', `/api/aeronaves/${id}`)
            .then(data => {
                showSuccess('Aeronave eliminada exitosamente');
                showOutput('planes-output', data, false);
                e.target.reset();
            })
            .catch(err => handleError(err.message));
    });

    // ----- Reservas (admin) -----
    document.getElementById('get-all-reservas-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/reservas')
            .then(data => showReservationOutput('reservas-output', data))
            .catch(err => handleError(err.message));
    });

    document.getElementById('get-reserva-by-id-form').addEventListener('submit', e => {
        e.preventDefault();
        const id = document.getElementById('reserva_id_query').value;
        apiRequest('GET', `/api/reservas/${id}`)
            .then(data => showReservationOutput('reservas-output', data))
            .catch(err => handleError(err.message));
    });

    // ----- Información -----
    document.getElementById('health-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/health')
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });
    document.getElementById('info-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/info')
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });
    document.getElementById('test-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/test')
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });
    document.getElementById('stats-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/reportes/estadisticas-generales')
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });
    document.getElementById('top-flights-btn').addEventListener('click', () => {
        apiRequest('GET', '/api/reportes/vuelos-mas-reservados')
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });
    document.getElementById('income-form').addEventListener('submit', e => {
        e.preventDefault();
        const params = new URLSearchParams({
            inicio: document.getElementById('ingresos_inicio').value,
            fin: document.getElementById('ingresos_fin').value
        });
        apiRequest('GET', `/api/reportes/ingresos?${params.toString()}`)
            .then(data => showOutput('info-output', data))
            .catch(err => handleError(err.message));
    });

    updateUI();
});
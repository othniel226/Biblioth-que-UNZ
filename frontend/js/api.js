// api.js - Intercepte les appels API et utilise les données mockées
// URL de votre backend sur Render
const BACKEND_URL = 'https://biblioth-que-unz-wzlfonrender.com/api';

// Utiliser les mocks ou le vrai backend
const USE_MOCK = false; // Mis à false pour utiliser le vrai backend

function callApi(endpoint, options = {}) {
    if (USE_MOCK) {
        return mockApiCall(endpoint, options);
    } else {
        return fetch(`${BACKEND_URL}/${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('token') 
                    ? `Bearer ${localStorage.getItem('token')}` 
                    : '',
                ...options.headers
            }
        }).then(res => {
            if (!res.ok) {
                throw new Error(`Erreur HTTP: ${res.status}`);
            }
            return res.json();
        }).catch(err => {
            console.error('Erreur API:', err);
            // Si le backend est indisponible on bascule sur les mocks
            return mockApiCall(endpoint, options);
        });
    }
}

function mockApiCall(endpoint, options) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (endpoint === 'livres') {
                resolve(bibliothequeData.ouvrages);
            } else if (endpoint === 'utilisateurs') {
                resolve(bibliothequeData.utilisateurs);
            } else {
                resolve({ message: 'API mockée' });
            }
        }, 300);
    });
}

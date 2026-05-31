// api.js - Intercepte les appels API et utilise les données mockées

// URL de votre backend (si un jour il tourne)
const BACKEND_URL = 'http://localhost:8080/api';

// Utiliser les mocks ou le vrai backend
const USE_MOCK = true; // Mettre à false quand le backend sera prêt

function callApi(endpoint, options = {}) {
    if (USE_MOCK) {
        // Utiliser les données mockées
        return mockApiCall(endpoint, options);
    } else {
        // Utiliser le vrai backend
        return fetch(`${BACKEND_URL}/${endpoint}`, options).then(res => res.json());
    }
}

function mockApiCall(endpoint, options) {
    // Simuler une réponse API
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

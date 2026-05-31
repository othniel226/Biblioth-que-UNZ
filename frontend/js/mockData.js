// mockData.js - Données simulées pour le développement

const bibliothequeData = {
    // Utilisateurs
    utilisateurs: [
        { id: 1, nom: "Jean Dupont", email: "jean@test.com", role: "etudiant", matricule: "ETU001" },
        { id: 2, nom: "Marie Diallo", email: "marie@test.com", role: "bibliothecaire", matricule: "BIB001" },
        { id: 3, nom: "Ousmane Traoré", email: "ousmane@test.com", role: "etudiant", matricule: "ETU002" }
    ],
    
    // Livres/Ouvrages
    ouvrages: [
        { id: 1, titre: "Le Petit Prince", auteur: "Antoine de Saint-Exupéry", isbn: "978-2-07-040850-4", categorie: "Littérature", quantite: 5, disponibles: 3 },
        { id: 2, titre: "1984", auteur: "George Orwell", isbn: "978-2-07-036822-8", categorie: "Roman", quantite: 3, disponibles: 1 },
        { id: 3, titre: "Les Misérables", auteur: "Victor Hugo", isbn: "978-2-01-323841-1", categorie: "Classique", quantite: 4, disponibles: 4 },
        { id: 4, titre: "L'Étranger", auteur: "Albert Camus", isbn: "978-2-07-036002-4", categorie: "Philosophie", quantite: 6, disponibles: 2 }
    ],
    
    // Emprunts
    emprunts: [
        { id: 1, utilisateurId: 1, ouvrageId: 1, dateEmprunt: "2026-05-20", dateRetour: "2026-06-03", statut: "en_cours" },
        { id: 2, utilisateurId: 3, ouvrageId: 2, dateEmprunt: "2026-05-15", dateRetour: "2026-05-29", statut: "en_cours" }
    ]
};

// API simulée
const apiMock = {
    // Récupérer tous les livres
    getLivres: function() {
        return Promise.resolve(bibliothequeData.ouvrages);
    },
    
    // Récupérer un livre par ID
    getLivre: function(id) {
        const livre = bibliothequeData.ouvrages.find(l => l.id === id);
        return Promise.resolve(livre);
    },
    
    // Récupérer tous les utilisateurs
    getUtilisateurs: function() {
        return Promise.resolve(bibliothequeData.utilisateurs);
    },
    
    // Ajouter un emprunt
    addEmprunt: function(emprunt) {
        const newEmprunt = { ...emprunt, id: bibliothequeData.emprunts.length + 1, statut: "en_cours" };
        bibliothequeData.emprunts.push(newEmprunt);
        return Promise.resolve(newEmprunt);
    },
    
    // Authentification
    login: function(email, password) {
        const user = bibliothequeData.utilisateurs.find(u => u.email === email);
        if (user) {
            return Promise.resolve({ token: "fake-jwt-token", user: user });
        }
        return Promise.reject(new Error("Email ou mot de passe incorrect"));
    }
};

// Exporter pour utiliser dans d'autres fichiers
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { bibliothequeData, apiMock };
}

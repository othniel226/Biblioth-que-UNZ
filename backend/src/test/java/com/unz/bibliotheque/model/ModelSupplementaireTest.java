package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.StatutEmprunt;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import com.unz.bibliotheque.model.enums.StatutReservation;
import com.unz.bibliotheque.model.enums.Role;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests supplémentaires pour couvrir les méthodes non testées
 * Cible : Etudiant, Ouvrage, Exemplaire, Reservation, Categorie, Utilisateur
 */
@DisplayName("Modèles supplémentaires — couverture JaCoCo")
class ModelSupplementaireTest {

    // ══════════════════════════════════════════════════════
    // ETUDIANT
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Etudiant — getNombreEmpruntsActifs()")
    class EtudiantTest {

        @Test
        @DisplayName("✅ getNombreEmpruntsActifs() = 2 pour 2 emprunts EN_COURS")
        void getNombreEmpruntsActifs_deuxEnCours() {
            Etudiant etudiant = new Etudiant();

            Emprunt e1 = new Emprunt(); e1.setStatut(StatutEmprunt.EN_COURS);
            Emprunt e2 = new Emprunt(); e2.setStatut(StatutEmprunt.EN_COURS);
            Emprunt e3 = new Emprunt(); e3.setStatut(StatutEmprunt.RENDU);

            etudiant.setEmprunts(List.of(e1, e2, e3));

            assertThat(etudiant.getNombreEmpruntsActifs()).isEqualTo(2L);
        }

        @Test
        @DisplayName("✅ getNombreEmpruntsActifs() = 0 si tous rendus")
        void getNombreEmpruntsActifs_tousRendus() {
            Etudiant etudiant = new Etudiant();
            Emprunt e1 = new Emprunt(); e1.setStatut(StatutEmprunt.RENDU);
            Emprunt e2 = new Emprunt(); e2.setStatut(StatutEmprunt.RENDU);
            etudiant.setEmprunts(List.of(e1, e2));

            assertThat(etudiant.getNombreEmpruntsActifs()).isEqualTo(0L);
        }

        @Test
        @DisplayName("✅ getNombreEmpruntsActifs() = 0 si liste vide")
        void getNombreEmpruntsActifs_listeVide() {
            Etudiant etudiant = new Etudiant();
            etudiant.setEmprunts(List.of());

            assertThat(etudiant.getNombreEmpruntsActifs()).isEqualTo(0L);
        }

        @Test
        @DisplayName("✅ getNombreEmpruntsActifs() compte PROLONGE et EN_RETARD")
        void getNombreEmpruntsActifs_compteProlongeEtRetard() {
            Etudiant etudiant = new Etudiant();
            Emprunt e1 = new Emprunt(); e1.setStatut(StatutEmprunt.PROLONGE);
            Emprunt e2 = new Emprunt(); e2.setStatut(StatutEmprunt.EN_RETARD);
            Emprunt e3 = new Emprunt(); e3.setStatut(StatutEmprunt.RENDU);
            etudiant.setEmprunts(List.of(e1, e2, e3));

            assertThat(etudiant.getNombreEmpruntsActifs()).isEqualTo(2L);
        }

        @Test
        @DisplayName("✅ Etudiant — champs matricule, filiere, niveau")
        void etudiant_champsSetsEtGets() {
            Etudiant etudiant = new Etudiant();
            etudiant.setMatricule("2023INF001");
            etudiant.setFiliere("Informatique");
            etudiant.setNiveau("L3");

            assertThat(etudiant.getMatricule()).isEqualTo("2023INF001");
            assertThat(etudiant.getFiliere()).isEqualTo("Informatique");
            assertThat(etudiant.getNiveau()).isEqualTo("L3");
        }

        @Test
        @DisplayName("✅ Etudiant — liste réservations initialisée vide")
        void etudiant_reservationsVideParDefaut() {
            Etudiant etudiant = new Etudiant();
            assertThat(etudiant.getReservations()).isNotNull();
        }
    }

    // ══════════════════════════════════════════════════════
    // OUVRAGE
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Ouvrage — estDisponible(), getNombreExemplairesDisponibles(), getPremierExemplaireDisponible()")
    class OuvrageTest {

        private Exemplaire exemplaireDisponible() {
            Exemplaire e = new Exemplaire();
            e.setStatut(StatutExemplaire.DISPONIBLE);
            return e;
        }

        private Exemplaire exemplaireEmprunte() {
            Exemplaire e = new Exemplaire();
            e.setStatut(StatutExemplaire.EMPRUNTE);
            return e;
        }

        @Test
        @DisplayName("✅ estDisponible() = true si au moins un exemplaire DISPONIBLE")
        void estDisponible_true() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(exemplaireEmprunte(), exemplaireDisponible()));
            assertThat(o.estDisponible()).isTrue();
        }

        @Test
        @DisplayName("✅ estDisponible() = false si tous EMPRUNTE")
        void estDisponible_false() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(exemplaireEmprunte(), exemplaireEmprunte()));
            assertThat(o.estDisponible()).isFalse();
        }

        @Test
        @DisplayName("✅ estDisponible() = false si liste vide")
        void estDisponible_listeVide() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of());
            assertThat(o.estDisponible()).isFalse();
        }

        @Test
        @DisplayName("✅ getNombreExemplairesDisponibles() = 2")
        void getNombreExemplairesDisponibles_retourne2() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(
                    exemplaireDisponible(), exemplaireDisponible(), exemplaireEmprunte()));
            assertThat(o.getNombreExemplairesDisponibles()).isEqualTo(2L);
        }

        @Test
        @DisplayName("✅ getNombreExemplairesDisponibles() = 0 si aucun dispo")
        void getNombreExemplairesDisponibles_zero() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(exemplaireEmprunte()));
            assertThat(o.getNombreExemplairesDisponibles()).isEqualTo(0L);
        }

        @Test
        @DisplayName("✅ getPremierExemplaireDisponible() retourne le premier DISPONIBLE")
        void getPremierExemplaireDisponible_retournePremier() {
            Exemplaire dispo = exemplaireDisponible();
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(exemplaireEmprunte(), dispo));
            assertThat(o.getPremierExemplaireDisponible()).isEqualTo(dispo);
        }

        @Test
        @DisplayName("✅ getPremierExemplaireDisponible() retourne null si aucun dispo")
        void getPremierExemplaireDisponible_retourneNull() {
            Ouvrage o = new Ouvrage();
            o.setExemplaires(List.of(exemplaireEmprunte()));
            assertThat(o.getPremierExemplaireDisponible()).isNull();
        }

        @Test
        @DisplayName("✅ Ouvrage — archive = false par défaut")
        void ouvrage_archiveFalseParDefaut() {
            Ouvrage o = new Ouvrage();
            assertThat(o.getArchive()).isFalse();
        }

        @Test
        @DisplayName("✅ Ouvrage — champs titre, auteur, isbn")
        void ouvrage_champsBasiques() {
            Ouvrage o = new Ouvrage();
            o.setTitre("Clean Code");
            o.setAuteur("Robert C. Martin");
            o.setIsbn("978-0-13-235088-4");
            assertThat(o.getTitre()).isEqualTo("Clean Code");
            assertThat(o.getAuteur()).isEqualTo("Robert C. Martin");
        }
    }

    // ══════════════════════════════════════════════════════
    // EXEMPLAIRE
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Exemplaire — marquerEmprunte(), marquerDisponible(), marquerReserve()")
    class ExemplaireTest {

        @Test
        @DisplayName("✅ statut DISPONIBLE par défaut")
        void exemplaire_disponibleParDefaut() {
            Exemplaire e = new Exemplaire();
            assertThat(e.getStatut()).isEqualTo(StatutExemplaire.DISPONIBLE);
        }

        @Test
        @DisplayName("✅ marquerEmprunte() → statut = EMPRUNTE")
        void marquerEmprunte_statutEmprunte() {
            Exemplaire e = new Exemplaire();
            e.marquerEmprunte();
            assertThat(e.getStatut()).isEqualTo(StatutExemplaire.EMPRUNTE);
        }

        @Test
        @DisplayName("✅ marquerDisponible() → statut = DISPONIBLE")
        void marquerDisponible_statutDisponible() {
            Exemplaire e = new Exemplaire();
            e.marquerEmprunte();
            e.marquerDisponible();
            assertThat(e.getStatut()).isEqualTo(StatutExemplaire.DISPONIBLE);
        }

        @Test
        @DisplayName("✅ marquerReserve() → statut = RESERVE")
        void marquerReserve_statutReserve() {
            Exemplaire e = new Exemplaire();
            e.marquerReserve();
            assertThat(e.getStatut()).isEqualTo(StatutExemplaire.RESERVE);
        }

        @Test
        @DisplayName("✅ Exemplaire — codeBarres et notes")
        void exemplaire_champsBasiques() {
            Exemplaire e = new Exemplaire();
            e.setCodeBarres("EX-001-A");
            e.setNotes("Bon état");
            assertThat(e.getCodeBarres()).isEqualTo("EX-001-A");
            assertThat(e.getNotes()).isEqualTo("Bon état");
        }
    }

    // ══════════════════════════════════════════════════════
    // RESERVATION
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Reservation — confirmer(), annuler()")
    class ReservationTest {

        @Test
        @DisplayName("✅ statut EN_ATTENTE par défaut")
        void reservation_enAttenteParDefaut() {
            Reservation r = new Reservation();
            assertThat(r.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE);
        }

        @Test
        @DisplayName("✅ confirmer(48) → statut CONFIRMEE, dateExpiration dans 48h")
        void confirmer_statutConfirmee() {
            Reservation r = new Reservation();
            r.confirmer(48);
            assertThat(r.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
            assertThat(r.getDateConfirmation()).isNotNull();
            assertThat(r.getDateExpiration()).isAfter(LocalDateTime.now().plusHours(47));
        }

        @Test
        @DisplayName("✅ annuler() → statut ANNULEE")
        void annuler_statutAnnulee() {
            Reservation r = new Reservation();
            r.annuler();
            assertThat(r.getStatut()).isEqualTo(StatutReservation.ANNULEE);
        }

        @Test
        @DisplayName("✅ dateReservation initialisée à la construction")
        void reservation_dateReservationInitialisee() {
            Reservation r = new Reservation();
            assertThat(r.getDateReservation()).isNotNull();
            assertThat(r.getDateReservation()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("✅ positionFile peut être défini")
        void reservation_positionFile() {
            Reservation r = new Reservation();
            r.setPositionFile(3);
            assertThat(r.getPositionFile()).isEqualTo(3);
        }
    }

    // ══════════════════════════════════════════════════════
    // CATEGORIE
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Categorie — champs et valeurs par défaut")
    class CategorieTest {

        @Test
        @DisplayName("✅ couleur = #1a237e par défaut")
        void categorie_couleurParDefaut() {
            Categorie c = new Categorie();
            assertThat(c.getCouleur()).isEqualTo("#1a237e");
        }

        @Test
        @DisplayName("✅ Categorie — nom et description")
        void categorie_champsBasiques() {
            Categorie c = new Categorie();
            c.setNom("Informatique");
            c.setDescription("Livres d'informatique et programmation");
            assertThat(c.getNom()).isEqualTo("Informatique");
            assertThat(c.getDescription()).contains("informatique");
        }

        @Test
        @DisplayName("✅ Categorie — liste ouvrages initialisée vide")
        void categorie_ouvragesVideParDefaut() {
            Categorie c = new Categorie();
            assertThat(c.getOuvrages()).isNotNull();
        }

        @Test
        @DisplayName("✅ Categorie — couleur personnalisable")
        void categorie_couleurPersonnalisable() {
            Categorie c = new Categorie();
            c.setCouleur("#ff5722");
            assertThat(c.getCouleur()).isEqualTo("#ff5722");
        }
    }

    // ══════════════════════════════════════════════════════
    // UTILISATEUR
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("Utilisateur — getNomComplet(), héritage")
    class UtilisateurTest {

        @Test
        @DisplayName("✅ getNomComplet() retourne prenom + nom")
        void getNomComplet_retournePrenomNom() {
            Etudiant u = new Etudiant();
            u.setPrenom("Moussa");
            u.setNom("Ouedraogo");
            assertThat(u.getNomComplet()).isEqualTo("Moussa Ouedraogo");
        }

        @Test
        @DisplayName("✅ actif = true par défaut")
        void utilisateur_actifParDefaut() {
            Etudiant u = new Etudiant();
            assertThat(u.getActif()).isTrue();
        }

        @Test
        @DisplayName("✅ role peut être défini")
        void utilisateur_roleDefini() {
            Etudiant u = new Etudiant();
            u.setRole(Role.ETUDIANT);
            assertThat(u.getRole()).isEqualTo(Role.ETUDIANT);
        }

        @Test
        @DisplayName("✅ email et motDePasse peuvent être définis")
        void utilisateur_emailEtMotDePasse() {
            Etudiant u = new Etudiant();
            u.setEmail("moussa@etud.unz.bf");
            u.setMotDePasse("$2a$10$encoded");
            assertThat(u.getEmail()).isEqualTo("moussa@etud.unz.bf");
            assertThat(u.getMotDePasse()).isEqualTo("$2a$10$encoded");
        }

        @Test
        @DisplayName("✅ Bibliothecaire est une instance de Utilisateur")
        void bibliothecaire_estUtilisateur() {
            Bibliothecaire b = new Bibliothecaire();
            b.setPrenom("Alice");
            b.setNom("Kabore");
            assertThat(b.getNomComplet()).isEqualTo("Alice Kabore");
            assertThat(b).isInstanceOf(Utilisateur.class);
        }

        @Test
        @DisplayName("✅ Administrateur est une instance de Utilisateur")
        void administrateur_estUtilisateur() {
            Administrateur a = new Administrateur();
            a.setPrenom("Super");
            a.setNom("Admin");
            assertThat(a.getNomComplet()).isEqualTo("Super Admin");
            assertThat(a).isInstanceOf(Utilisateur.class);
        }
    }
}

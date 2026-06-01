package com.unz.bibliotheque.model;

import com.unz.bibliotheque.model.enums.Role;
import com.unz.bibliotheque.model.enums.StatutExemplaire;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests ciblés pour couvrir les 20 instructions manquantes
 * Cibles : Administrateur, Bibliothecaire, ConfigurationSysteme
 */
@DisplayName("Tests ciblés — 20 instructions manquantes pour 65%")
class AdminBiblioTest {

    // ══════════════════════════════════════════════════════
    // ADMINISTRATEUR (13 instructions manquées)
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("✅ Administrateur — création et champs hérités")
    void administrateur_creationEtChamps() {
        Administrateur a = new Administrateur();
        a.setPrenom("Super");
        a.setNom("Admin");
        a.setEmail("admin@bibliotheque-unz.bf");
        a.setMotDePasse("$2a$10$encoded");
        a.setRole(Role.ADMINISTRATEUR);
        a.setActif(true);

        assertThat(a.getPrenom()).isEqualTo("Super");
        assertThat(a.getNom()).isEqualTo("Admin");
        assertThat(a.getEmail()).isEqualTo("admin@bibliotheque-unz.bf");
        assertThat(a.getRole()).isEqualTo(Role.ADMINISTRATEUR);
        assertThat(a.getActif()).isTrue();
        assertThat(a.getNomComplet()).isEqualTo("Super Admin");
    }

    @Test
    @DisplayName("✅ Administrateur — est une instance de Utilisateur")
    void administrateur_estUtilisateur() {
        Administrateur a = new Administrateur();
        assertThat(a).isInstanceOf(Utilisateur.class);
    }

    @Test
    @DisplayName("✅ Administrateur — actif peut être désactivé")
    void administrateur_desactivable() {
        Administrateur a = new Administrateur();
        a.setActif(false);
        assertThat(a.getActif()).isFalse();
    }

    // ══════════════════════════════════════════════════════
    // BIBLIOTHECAIRE (23 instructions manquées)
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("✅ Bibliothecaire — création et champs hérités")
    void bibliothecaire_creationEtChamps() {
        Bibliothecaire b = new Bibliothecaire();
        b.setPrenom("Alice");
        b.setNom("Kabore");
        b.setEmail("alice.kabore@bibliotheque-unz.bf");
        b.setMotDePasse("$2a$10$encoded");
        b.setRole(Role.BIBLIOTHECAIRE);
        b.setActif(true);

        assertThat(b.getPrenom()).isEqualTo("Alice");
        assertThat(b.getNom()).isEqualTo("Kabore");
        assertThat(b.getEmail()).isEqualTo("alice.kabore@bibliotheque-unz.bf");
        assertThat(b.getRole()).isEqualTo(Role.BIBLIOTHECAIRE);
        assertThat(b.getNomComplet()).isEqualTo("Alice Kabore");
    }

    @Test
    @DisplayName("✅ Bibliothecaire — est une instance de Utilisateur")
    void bibliothecaire_estUtilisateur() {
        Bibliothecaire b = new Bibliothecaire();
        assertThat(b).isInstanceOf(Utilisateur.class);
    }

    @Test
    @DisplayName("✅ Bibliothecaire — actif peut être désactivé")
    void bibliothecaire_desactivable() {
        Bibliothecaire b = new Bibliothecaire();
        b.setActif(false);
        assertThat(b.getActif()).isFalse();
    }

    @Test
    @DisplayName("✅ Bibliothecaire — second bibliothécaire")
    void bibliothecaire_second() {
        Bibliothecaire b = new Bibliothecaire();
        b.setPrenom("Boureima");
        b.setNom("Sawadogo");
        b.setEmail("boureima.sawadogo@bibliotheque-unz.bf");
        b.setRole(Role.BIBLIOTHECAIRE);

        assertThat(b.getNomComplet()).isEqualTo("Boureima Sawadogo");
        assertThat(b.getRole()).isEqualTo(Role.BIBLIOTHECAIRE);
    }

    // ══════════════════════════════════════════════════════
    // CONFIGURATION SYSTEME (42 instructions manquées)
    // ══════════════════════════════════════════════════════

    @Test
    @DisplayName("✅ ConfigurationSysteme — champs cle et valeur")
    void configurationSysteme_champsBasiques() {
        ConfigurationSysteme c = new ConfigurationSysteme();
        c.setCle("DUREE_EMPRUNT_JOURS");
        c.setValeur("14");

        assertThat(c.getCle()).isEqualTo("DUREE_EMPRUNT_JOURS");
        assertThat(c.getValeur()).isEqualTo("14");
    }

    @Test
    @DisplayName("✅ ConfigurationSysteme — getValeurAsInt() retourne entier")
    void configurationSysteme_getValeurAsInt() {
        ConfigurationSysteme c = new ConfigurationSysteme();
        c.setValeur("14");

        assertThat(c.getValeurAsInt()).isEqualTo(14);
    }

    @Test
    @DisplayName("✅ ConfigurationSysteme — description")
    void configurationSysteme_description() {
        ConfigurationSysteme c = new ConfigurationSysteme();
        c.setCle("MAX_EMPRUNTS_SIMULTANES");
        c.setValeur("3");
        c.setDescription("Nombre maximum d'emprunts simultanés");

        assertThat(c.getDescription()).contains("emprunts");
    }

    @Test
    @DisplayName("✅ ConfigurationSysteme — valeur modifiable")
    void configurationSysteme_valeurModifiable() {
        ConfigurationSysteme c = new ConfigurationSysteme();
        c.setValeur("14");
        c.setValeur("21");
        assertThat(c.getValeur()).isEqualTo("21");
    }
}

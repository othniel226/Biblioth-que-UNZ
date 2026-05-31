// ============================================================
// PATCH — Méthodes à AJOUTER dans AdminController.java
// Copier ces méthodes dans la classe AdminController existante
// ============================================================

/*
    // ── Ajouter cet import en haut de AdminController.java ──
    import com.unz.bibliotheque.model.Etudiant;
    import com.unz.bibliotheque.model.Bibliothecaire;
    import org.springframework.security.crypto.password.PasswordEncoder;

    // ── Ajouter ce champ dans la classe AdminController ──
    private final PasswordEncoder passwordEncoder;

    // ── Ajouter ces méthodes dans la classe AdminController ──

    // Afficher le formulaire de création d'utilisateur
    @GetMapping("/utilisateurs/creer")
    public String afficherFormCreerUtilisateur() {
        return "admin/creer-utilisateur";
    }

    // Traiter la création d'un utilisateur
    @PostMapping("/utilisateurs/creer")
    public String creerUtilisateur(
        @RequestParam String prenom,
        @RequestParam String nom,
        @RequestParam String email,
        @RequestParam String motDePasse,
        @RequestParam String role,
        @RequestParam(required = false) String matricule,
        @RequestParam(required = false) String filiere,
        @RequestParam(required = false) String niveau,
        @RequestParam(required = false) String badgeNumero,
        @RequestParam(required = false) String service,
        RedirectAttributes ra
    ) {
        if (utilisateurRepo.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Cet email est déjà utilisé.");
            return "redirect:/admin/utilisateurs/creer";
        }

        Role roleEnum = Role.valueOf(role);
        Utilisateur utilisateur;

        switch (roleEnum) {
            case ETUDIANT -> {
                Etudiant e = new Etudiant();
                e.setMatricule(matricule != null ? matricule.toUpperCase() : "");
                e.setFiliere(filiere != null ? filiere : "");
                e.setNiveau(niveau != null ? niveau : "L1");
                utilisateur = e;
            }
            case BIBLIOTHECAIRE -> {
                Bibliothecaire b = new Bibliothecaire();
                b.setBadgeNumero(badgeNumero != null ? badgeNumero : "BIB-000");
                b.setService(service);
                utilisateur = b;
            }
            default -> utilisateur = new Administrateur();
        }

        utilisateur.setPrenom(prenom.trim());
        utilisateur.setNom(nom.trim());
        utilisateur.setEmail(email.trim().toLowerCase());
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setRole(roleEnum);
        utilisateur.setActif(true);

        utilisateurRepo.save(utilisateur);
        ra.addFlashAttribute("success", "Utilisateur " + prenom + " " + nom + " créé avec succès !");
        return "redirect:/admin/utilisateurs";
    }
*/

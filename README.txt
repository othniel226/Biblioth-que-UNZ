============================================================
  BIBLIOTHÈQUE UNZ — Application de Gestion de Bibliothèque
  Université Norbert Zongo | Koudougou, Burkina Faso
  Génie Logiciel L3 — Semestre 5 | Mai 2026
  Enseignant : Dr OUEDRAOGO Moïse
  Soumission : moisewedra@gmail.com
============================================================

DÉMARRAGE EN 3 ÉTAPES
────────────────────────────────────────────────────────────
ÉTAPE 1 : Configurer Java 17
  set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
  set PATH=%JAVA_HOME%\bin;%PATH%

ÉTAPE 2 : Créer la base de données
  mysql -u root -p
  (dans MySQL) source chemin/sql/bibliotheque_unz_PRODUCTION.sql

ÉTAPE 3 : Lancer le backend
  cd backend
  mvn spring-boot:run -DskipTests

OUVRIR DANS LE NAVIGATEUR
  Application  : http://localhost:8081
  Swagger UI   : http://localhost:8081/swagger-ui.html
  Frontend     : double-clic sur frontend/index.html

COMPTES DE TEST (mot de passe : password)
────────────────────────────────────────────────────────────
  admin@bibliotheque-unz.bf          → Administrateur
  alice.kabore@bibliotheque-unz.bf   → Bibliothécaire
  moussa.ouedraogo@etud.unz.bf       → Étudiant
  aliou.barry@etud.unz.bf            → Étudiant (retards)
  kadiatou.coulibaly@etud.unz.bf     → Étudiant (réservation)

STRUCTURE DU PROJET
────────────────────────────────────────────────────────────
  backend/    → Spring Boot 3.2 + Java 17 (53 fichiers Java)
  frontend/   → Pages HTML statiques avec Bootstrap 5.3
  sql/        → Script SQL complet (tables + données de test)

TECHNOLOGIES
────────────────────────────────────────────────────────────
  Java 17 | Spring Boot 3.2 | Thymeleaf | Bootstrap 5.3
  MySQL 8.0 | JPA/Hibernate | Spring Security | JWT
  Pattern Strategy + Observer + Singleton | JUnit 5
============================================================

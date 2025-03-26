-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mer. 26 mars 2025 à 08:11
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bibliodb`
--

-- --------------------------------------------------------

--
-- Structure de la table `emprunt`
--

DROP TABLE IF EXISTS `emprunt`;
CREATE TABLE IF NOT EXISTS `emprunt` (
  `id_emprunt` int NOT NULL AUTO_INCREMENT,
  `date_emprunt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_retour_prevue` datetime DEFAULT NULL,
  `date_retour_effectif` datetime DEFAULT NULL,
  `statut` enum('en cours','retard','rendu','perdu') COLLATE utf8mb4_unicode_ci DEFAULT 'en cours',
  `id_lecteur` int NOT NULL,
  `numero_exemplaire` int NOT NULL,
  `id_responsable` int DEFAULT NULL,
  PRIMARY KEY (`id_emprunt`),
  KEY `id_responsable` (`id_responsable`),
  KEY `idx_emprunt_lecteur` (`id_lecteur`),
  KEY `idx_emprunt_exemplaire` (`numero_exemplaire`),
  KEY `idx_emprunt_statut` (`statut`),
  KEY `idx_emprunt_dates` (`date_emprunt`,`date_retour_prevue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déclencheurs `emprunt`
--
DROP TRIGGER IF EXISTS `after_emprunt_insert`;
DELIMITER $$
CREATE TRIGGER `after_emprunt_insert` AFTER INSERT ON `emprunt` FOR EACH ROW BEGIN
    UPDATE EXEMPLAIRE
    SET disponible = FALSE
    WHERE numero_exemplaire = NEW.numero_exemplaire;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `after_emprunt_update`;
DELIMITER $$
CREATE TRIGGER `after_emprunt_update` AFTER UPDATE ON `emprunt` FOR EACH ROW BEGIN
    IF NEW.date_retour_effectif IS NOT NULL AND OLD.date_retour_effectif IS NULL THEN
    UPDATE EXEMPLAIRE
    SET disponible = TRUE
    WHERE numero_exemplaire = NEW.numero_exemplaire;
END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `exemplaire`
--

DROP TABLE IF EXISTS `exemplaire`;
CREATE TABLE IF NOT EXISTS `exemplaire` (
  `numero_exemplaire` int NOT NULL AUTO_INCREMENT,
  `etat` enum('neuf','bon','moyen','mauvais') COLLATE utf8mb4_unicode_ci DEFAULT 'bon',
  `disponible` tinyint(1) DEFAULT '1',
  `date_creation` date DEFAULT NULL,
  `isbn` int NOT NULL,
  PRIMARY KEY (`numero_exemplaire`),
  KEY `idx_exemplaire_disponible` (`disponible`),
  KEY `idx_exemplaire_isbn` (`isbn`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `exemplaire`
--

INSERT INTO `exemplaire` (`numero_exemplaire`, `etat`, `disponible`, `date_creation`, `isbn`) VALUES
(1, 'bon', 1, '2025-03-26', 2147483647);

--
-- Déclencheurs `exemplaire`
--
DROP TRIGGER IF EXISTS `after_exemplaire_delete`;
DELIMITER $$
CREATE TRIGGER `after_exemplaire_delete` AFTER DELETE ON `exemplaire` FOR EACH ROW BEGIN
    UPDATE LIVRE
    SET nombre_exemplaires = nombre_exemplaires - 1
    WHERE isbn = OLD.isbn;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `after_exemplaire_insert`;
DELIMITER $$
CREATE TRIGGER `after_exemplaire_insert` AFTER INSERT ON `exemplaire` FOR EACH ROW BEGIN
    UPDATE LIVRE
    SET nombre_exemplaires = nombre_exemplaires + 1
    WHERE isbn = NEW.isbn;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `lecteur`
--

DROP TABLE IF EXISTS `lecteur`;
CREATE TABLE IF NOT EXISTS `lecteur` (
  `id_lecteur` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adresse` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_inscription` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_lecteur`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_lecteur_nom` (`nom`),
  KEY `idx_lecteur_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `lecteur`
--

INSERT INTO `lecteur` (`id_lecteur`, `nom`, `contact`, `email`, `adresse`, `date_inscription`) VALUES
(1, 'Jean Dupont', '0612345678', 'jean.dupont@email.com', '12 Rue de la Paix, Paris', '2025-03-26 05:16:35');

-- --------------------------------------------------------

--
-- Structure de la table `livre`
--

DROP TABLE IF EXISTS `livre`;
CREATE TABLE IF NOT EXISTS `livre` (
  `isbn` int NOT NULL,
  `titre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `auteur` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `categorie` enum('Roman','Science-Fiction','Fantasy','Policier','Historique','Biographie','Poésie','Théâtre','Essai','Documentaire','Jeunesse','BD-Manga','Autre') COLLATE utf8mb4_unicode_ci DEFAULT 'Roman',
  `nombre_pages` int DEFAULT NULL,
  `nombre_exemplaires` int DEFAULT '0',
  `date_creation` date DEFAULT NULL,
  PRIMARY KEY (`isbn`),
  KEY `idx_livre_titre` (`titre`),
  KEY `idx_livre_auteur` (`auteur`),
  KEY `idx_livre_categorie` (`categorie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `livre`
--

INSERT INTO `livre` (`isbn`, `titre`, `auteur`, `categorie`, `nombre_pages`, `nombre_exemplaires`, `date_creation`) VALUES
(2147483647, 'L\'Étranger', 'Albert Camus', 'Roman', 123, 1, '1942-01-01');

-- --------------------------------------------------------

--
-- Structure de la table `responsable`
--

DROP TABLE IF EXISTS `responsable`;
CREATE TABLE IF NOT EXISTS `responsable` (
  `id_responsable` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mot_de_passe` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('admin','gestionnaire') COLLATE utf8mb4_unicode_ci DEFAULT 'gestionnaire',
  `date_creation` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_responsable`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `idx_responsable_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `responsable`
--

INSERT INTO `responsable` (`id_responsable`, `nom`, `contact`, `email`, `mot_de_passe`, `role`, `date_creation`) VALUES
(1, 'Administrateur', NULL, 'admin@bibliotheque.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin', '2025-03-26 05:16:33');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `emprunt`
--
ALTER TABLE `emprunt`
  ADD CONSTRAINT `emprunt_ibfk_1` FOREIGN KEY (`id_lecteur`) REFERENCES `lecteur` (`id_lecteur`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `emprunt_ibfk_2` FOREIGN KEY (`numero_exemplaire`) REFERENCES `exemplaire` (`numero_exemplaire`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `emprunt_ibfk_3` FOREIGN KEY (`id_responsable`) REFERENCES `responsable` (`id_responsable`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Contraintes pour la table `exemplaire`
--
ALTER TABLE `exemplaire`
  ADD CONSTRAINT `exemplaire_ibfk_1` FOREIGN KEY (`isbn`) REFERENCES `livre` (`isbn`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

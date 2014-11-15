-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Sam 15 Novembre 2014 à 00:45
-- Version du serveur: 5.6.12-log
-- Version de PHP: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `polymtl_mamytas`
--
CREATE DATABASE IF NOT EXISTS `polymtl_mamytas` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `polymtl_mamytas`;

-- --------------------------------------------------------

--
-- Structure de la table `projets`
--

DROP TABLE IF EXISTS `projets`;
CREATE TABLE IF NOT EXISTS `projets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) DEFAULT NULL,
  `create_datetime` datetime DEFAULT NULL,
  `update_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `projets`
--

INSERT INTO `projets` (`id`, `nom`, `create_datetime`, `update_datetime`) VALUES
(1, 'TP2 INF4410', '2014-10-14 12:45:00', '2014-11-10 15:48:00'),
(2, 'Projet INF6405', '2014-10-28 19:00:00', '2014-11-11 19:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  `create_datetime` datetime DEFAULT NULL,
  `project_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`project_id`),
  KEY `fk_taches_projets1_idx` (`project_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Contenu de la table `tasks`
--

INSERT INTO `tasks` (`id`, `nom`, `create_datetime`, `project_id`) VALUES
(1, 'Coder application', '2014-10-14 12:46:00', 1),
(2, 'Rédiger Rapport', '2014-10-14 12:47:00', 1),
(3, 'Rédiger Rapport', '2014-10-28 19:01:00', 2),
(4, 'Coder app Android', '2014-10-28 19:02:00', 2),
(5, 'Coder Serveur REST JSON', '2014-10-28 19:03:00', 2);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `token_expiration_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Contenu de la table `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `token`, `token_expiration_datetime`) VALUES
(1, 'antoine.giraud@polymtl.ca', '2d64fc8596f4f849e40c91b244c00dcd', NULL, NULL),
(2, 'paul.berthier@polymtl.ca', 'ab4f63f9ac65152575886860dde480a1', NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `users_has_projects`
--

DROP TABLE IF EXISTS `users_has_projects`;
CREATE TABLE IF NOT EXISTS `users_has_projects` (
  `user_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `datetime_joined_projet` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`,`project_id`),
  KEY `fk_users_has_projets_projets1_idx` (`project_id`),
  KEY `fk_users_has_projets_users_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `users_has_projects`
--

INSERT INTO `users_has_projects` (`user_id`, `project_id`, `datetime_joined_projet`) VALUES
(1, 1, '2014-10-14 12:45:00'),
(1, 2, '2014-10-28 19:00:00'),
(2, 2, '2014-10-28 19:00:00');

-- --------------------------------------------------------

-- -----------------------------------------------------
-- Table `mydb`.`users_works_on_tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `users_works_on_tasks` ;

CREATE TABLE IF NOT EXISTS `users_works_on_tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NULL,
  `task_id` INT NOT NULL,
  `create_datetime` DATETIME NULL,
  `time_spent` INT NULL DEFAULT 30,
  `is_finished` TINYINT(1) NULL,
  PRIMARY KEY (`id`, `task_id`),
  INDEX `fk_users_has_taches_taches1_idx` (`task_id` ASC),
  INDEX `fk_users_has_taches_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_users_has_taches_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_taches_taches1`
    FOREIGN KEY (`task_id`)
    REFERENCES `tasks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


--
-- Contenu de la table `users_works_on_tasks`
--

INSERT INTO `users_works_on_tasks` (`id`, `user_id`, `task_id`, `create_datetime`, `time_spent`, `is_finished`) VALUES
(1, 1, 1, '2014-11-09 15:00:00', 1320, 1),
(1, null, 1, '2014-11-09 15:00:00', 160, 1),
(2, 1, 2, '2014-11-09 18:00:00', 120, 1),
(3, 1, 5, '2014-11-12 13:00:00', 120, 0),
(4, 2, 4, '2014-11-09 00:00:00', 600, 1),
(5, 1, 1, '2014-11-09 15:00:00', 1320, 1);

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `fk_taches_projets1` FOREIGN KEY (`project_id`) REFERENCES `projets` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `users_has_projects`
--
ALTER TABLE `users_has_projects`
  ADD CONSTRAINT `fk_users_has_projets_projets1` FOREIGN KEY (`project_id`) REFERENCES `projets` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_users_has_projets_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`users` ;

CREATE TABLE IF NOT EXISTS `mydb`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NULL,
  `password` VARCHAR(255) NULL,
  `token` VARCHAR(255) NULL,
  `token_expiration_datetime` DATETIME NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`projets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`projets` ;

CREATE TABLE IF NOT EXISTS `mydb`.`projets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(45) NULL,
  `create_datetime` DATETIME NULL,
  `update_datetime` DATETIME NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`tasks` ;

CREATE TABLE IF NOT EXISTS `mydb`.`tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(255) NULL,
  `create_datetime` DATETIME NULL,
  `project_id` INT NOT NULL,
  PRIMARY KEY (`id`, `project_id`),
  INDEX `fk_taches_projets1_idx` (`project_id` ASC),
  CONSTRAINT `fk_taches_projets1`
    FOREIGN KEY (`project_id`)
    REFERENCES `mydb`.`projets` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`users_has_projects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`users_has_projects` ;

CREATE TABLE IF NOT EXISTS `mydb`.`users_has_projects` (
  `user_id` INT NOT NULL,
  `project_id` INT NOT NULL,
  `datetime_joined_projet` DATETIME NULL,
  PRIMARY KEY (`user_id`, `project_id`),
  INDEX `fk_users_has_projets_projets1_idx` (`project_id` ASC),
  INDEX `fk_users_has_projets_users_idx` (`user_id` ASC),
  CONSTRAINT `fk_users_has_projets_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_projets_projets1`
    FOREIGN KEY (`project_id`)
    REFERENCES `mydb`.`projets` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`users_works_on_tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`users_works_on_tasks` ;

CREATE TABLE IF NOT EXISTS `mydb`.`users_works_on_tasks` (
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
    REFERENCES `mydb`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_taches_taches1`
    FOREIGN KEY (`task_id`)
    REFERENCES `mydb`.`tasks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

# --- !Ups
CREATE TABLE `to_do_category` (
                                  `id`         BIGINT(20)   unsigned     NOT NULL AUTO_INCREMENT,
                                  `name`       VARCHAR(255)              NOT NULL,
                                  `slug`       VARCHAR(64) CHARSET ascii NOT NULL,
                                  `color`      TINYINT UNSIGNED          NOT NULL,
                                  `updated_at` timestamp                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `created_at` timestamp                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# --- !Downs
DROP TABLE `to_do_category`;
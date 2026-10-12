-- Optional: Spring Boot creates this automatically (ddl-auto=update). Run it yourself if you prefer.
CREATE DATABASE IF NOT EXISTS food_wastage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_wastage;

CREATE TABLE IF NOT EXISTS food (
  id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `date`    DATE         NOT NULL,
  name      VARCHAR(80)  NOT NULL,
  category  VARCHAR(40)  NOT NULL DEFAULT 'General',
  prepared  DOUBLE       NOT NULL,
  served    DOUBLE       NOT NULL,
  donated   DOUBLE       NOT NULL DEFAULT 0,
  INDEX idx_food_date (`date`),
  INDEX idx_food_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

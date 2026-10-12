-- Optional demo rows for the TT session. Run after schema.sql (or after the backend has started once).
USE food_wastage;
INSERT INTO food (`date`, name, category, prepared, served, donated) VALUES
 (CURDATE() - INTERVAL 6 DAY, 'Rice',    'Main course', 50, 40, 2),
 (CURDATE() - INTERVAL 5 DAY, 'Rice',    'Main course', 48, 41, 1),
 (CURDATE() - INTERVAL 4 DAY, 'Rice',    'Main course', 52, 39, 0),
 (CURDATE() - INTERVAL 3 DAY, 'Rice',    'Main course', 50, 42, 2),
 (CURDATE() - INTERVAL 2 DAY, 'Rice',    'Main course', 51, 38, 3),
 (CURDATE() - INTERVAL 1 DAY, 'Rice',    'Main course', 49, 40, 1),
 (CURDATE() - INTERVAL 6 DAY, 'Dal',     'Main course', 20, 18, 0),
 (CURDATE() - INTERVAL 3 DAY, 'Dal',     'Main course', 21, 19, 1),
 (CURDATE() - INTERVAL 1 DAY, 'Dal',     'Main course', 20, 17, 1),
 (CURDATE() - INTERVAL 5 DAY, 'Curry',   'Side dish',   15, 10, 2),
 (CURDATE() - INTERVAL 2 DAY, 'Curry',   'Side dish',   15, 11, 1),
 (CURDATE() - INTERVAL 4 DAY, 'Chapati', 'Bread',       30, 27, 0),
 (CURDATE() - INTERVAL 1 DAY, 'Chapati', 'Bread',       30, 26, 2),
 (CURDATE() - INTERVAL 2 DAY, 'Samosa',  'Snacks',      15, 12, 0);

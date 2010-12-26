CREATE PROCEDURE GetQuotes(
	IN symbol VARCHAR(20)
)

READS SQL DATA
COMMENT 'Selects all quotes for given symbol'

BEGIN
	SET @s = CONCAT('SELECT * FROM ', symbol);
	PREPARE stmt FROM @s;
	EXECUTE stmt;
END

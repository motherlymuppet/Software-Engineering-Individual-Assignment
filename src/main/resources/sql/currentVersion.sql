-- This returns the most current version
SELECT (value)
FROM constants
WHERE _key = 'version';
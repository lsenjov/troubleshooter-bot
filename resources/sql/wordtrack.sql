-- For getting words out

-- :name get-words*
-- :doc Get top said words from the last daycycle
SELECT word, count(word) AS wc
FROM wordtrack
GROUP BY word
ORDER BY wc desc
LIMIT :num;

# java-filmorate
![](../../../Downloads/Untitled.png)

*Примеры запросов:* за основу взяты фильмы, для пользователей будет очень похоже
1. Все фильмы SELECT * FROM Films;
2. Найти фильм по id SELECT * FROM Films WHERE film_id = id;
3. Добавить фильм INSERT INTO Films(...) VALUES (...);
4. Обновить фильм UPDATE Films SET ... WHERE ...;
5. Добавить like INSERT INTO Likes (...) VALUES (...);
6. Удалить like DELETE FROM Likes WHERE ...;
7. Получить топ фильмов SELECT * FROM Films ORDER BY rate DESC LIMIT count;

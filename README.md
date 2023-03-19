# java-filmorate
Template repository for Filmorate project.
![DB_scheme](https://user-images.githubusercontent.com/114878995/226187909-591fd788-316b-4be2-b4ca-37cafb37ab5c.png)

*Комментарий:* в данной реализации не очень нравится что Friendship, Likes и Film_Genres по сути помойки.

*Примеры запросов:* за основу взяты фильмы, для пользователей будет очень похоже
1. Все фильмы SELECT * FROM Films;
2. Найти фильм по id SELECT * FROM Films WHERE film_id = id;
3. Добавить фильм INSERT INTO Films(...) VALUES (...);
4. Обновить фильм UPDATE Films SET ... WHERE ...;
5. Добавить like INSERT INTO Likes (...) VALUES (...);
6. Удалить like DELETE FROM Likes WHERE ...;
7. Получить топ фильмов SELECT * FROM Films ORDER BY rate DESC LIMIT count;

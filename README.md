# java-filmorate

Схема базы данных:

![](https://github.com/kkn365/java-filmorate/blob/main/practicum%20-%20edu.png)

Операция выборки общих друзей:
```sql
select user_id, email, login, name, birthday from edu.user  
where user_id in ( 
	select friend_id 
	from edu.friendship f1
	where f1.user_id = 2
	intersect 
	select friend_id 
	from edu.friendship f1
	where f1.user_id = 3
	);
```

Опрерация вывода списка фильмов по убыванию количества лайков:
```sql
select f.name, f.description, f.release_date, f.duration, rt.name from edu.film f 
right join ( select film_id, count(t.film_id) as likes_count
				from ( select distinct user_id, film_id 
				       from edu.likes ) as t
				group by t.film_id
				order by likes_count desc ) as r on r.film_id = f.film_id
left join edu.rating as rt on f.rating_id = rt.rating_id;
```

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.event.storage.EventStorage;
import ru.yandex.practicum.filmorate.dal.film.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dal.review.marks.ReviewMarksStorage;
import ru.yandex.practicum.filmorate.dal.review.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.user.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.assistance.EventType;
import ru.yandex.practicum.filmorate.model.assistance.Operation;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewMarksStorage reviewMarksStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewByFilm(Long filmId, Integer count) {
        return reviewStorage.getReviewsByFilm(filmId, count);
    }

    public Review addReview(Review review) {
        checkUserFilmId(review);
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        checkUserFilmId(review);
        Review currentReview = getReviewById(review.getReviewId());
        eventStorage.save(currentReview.getUserId(), currentReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        Review currentReview = getReviewById(reviewId);
        eventStorage.save(currentReview.getUserId(), reviewId, EventType.REVIEW, Operation.REMOVE);
        reviewStorage.deleteReview(reviewId);
    }

    public Review putLike(Long reviewId, Long userId) {
        return reviewMarksStorage.like(reviewId, userId);
    }

    public Review putDislike(Long reviewId, Long userId) {
        return reviewMarksStorage.dislike(reviewId, userId);
    }

    public Review removeLike(Long reviewId, Long userId) {
        return reviewMarksStorage.removeLike(reviewId, userId);
    }

    public Review removeDislike(Long reviewId, Long userId) {
        return reviewMarksStorage.removeDislike(reviewId, userId);
    }

    private void checkUserFilmId(Review review) {
        User user = userStorage.getUserById(review.getUserId());
        if (user == null) {
            throw new NotFoundException("Пользователь не найден в базе данных");
        }

        Film film = filmStorage.getFilmById(review.getFilmId());
        if (film == null) {
            throw new NotFoundException("Фильм не найден в базе данных");
        }
    }
}

package ru.yandex.practicum.filmorate.dal.review.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    Review getReviewById(Long reviewId);

    List<Review> getReviewsByFilm(Long filmId, Integer count);

}

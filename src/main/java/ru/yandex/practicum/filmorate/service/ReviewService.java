package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.reviewMarks.ReviewMarksStorage;
import ru.yandex.practicum.filmorate.dal.review.reviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewMarksStorage reviewMarksStorage;

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewByFilm(Long filmId, Integer count) {
        return reviewStorage.getReviewsByFilm(filmId, count);
    }

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
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
}

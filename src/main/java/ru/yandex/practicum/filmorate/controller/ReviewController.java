package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviewsByFilm(
            @RequestParam(name = "filmId", required = false, defaultValue = "0") Long filmId,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        return reviewService.getReviewByFilm(filmId, count);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review likeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewService.putLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review dislikeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewService.putDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Review removeReviewLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public Review removeReviewDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        return reviewService.removeDislike(reviewId, userId);
    }


}

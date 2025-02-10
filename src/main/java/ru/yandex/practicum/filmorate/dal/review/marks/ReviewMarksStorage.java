package ru.yandex.practicum.filmorate.dal.review.marks;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewMarksStorage {
    Review like(Long reviewId, Long userId);

    Review dislike(Long reviewId, Long userId);

    Review removeLike(Long reviewId, Long userId);

    Review removeDislike(Long reviewId, Long userId);
}

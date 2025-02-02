package ru.yandex.practicum.filmorate.dal.review.reviewMarks;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.review.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

@RequiredArgsConstructor
@Repository
public class ReviewMarksDbStorage implements ReviewMarksStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final ReviewMapper reviewMapper;
    private static final String GET_REVIEW_BY_ID = """
            SELECT *
            FROM reviews
            WHERE review_id = :review_id
            """;
    private static final String PUT_A_MARK = """
            MERGE INTO review_marks
             (review_id, user_id, is_useful)
              KEY (review_id, user_id)
               VALUES (:review_id, :user_id, :is_useful)
            """;
    private static final String DELETE_A_MARK = """
            DELETE
            FROM review_marks
            WHERE user_id = :user_id AND review_id = :review_id
            """;
    private static final String UPDATE_REVIEW_MARK = """
            UPDATE reviews
            SET useful = (
                SELECT
                COUNT(CASE WHEN is_useful = TRUE THEN 1 END)-
                COUNT(CASE WHEN is_useful = FALSE THEN 1 END) AS diff
                FROM review_marks
                WHERE review_id = :review_id
            )
            """;

    @Override
    public Review like(Long reviewId, Long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId)
                .addValue("is_useful", true);
        jdbc.update(PUT_A_MARK, parameterSource);
        return getReviewById(reviewId);
    }

    @Override
    public Review dislike(Long reviewId, Long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId)
                .addValue("is_useful", false);
        jdbc.update(PUT_A_MARK, parameterSource);
        return getReviewById(reviewId);
    }

    @Override
    public Review removeLike(Long reviewId, Long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        jdbc.update(DELETE_A_MARK, parameterSource);
        return getReviewById(reviewId);
    }

    @Override
    public Review removeDislike(Long reviewId, Long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        jdbc.update(DELETE_A_MARK, parameterSource);
        return getReviewById(reviewId);
    }

    private Review getReviewById(Long reviewId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        updateReviewRating(reviewId);
        return jdbc.query(GET_REVIEW_BY_ID, parameterSource, reviewMapper).getFirst();
    }

    private void updateReviewRating(Long reviewId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        int updatedReviews = jdbc.update(UPDATE_REVIEW_MARK, parameterSource);
        if (updatedReviews < 1) {
            throw new NotFoundException("Отзыв не найден в базе данных");
        }
    }
}

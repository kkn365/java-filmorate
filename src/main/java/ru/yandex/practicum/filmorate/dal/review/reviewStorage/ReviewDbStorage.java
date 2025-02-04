package ru.yandex.practicum.filmorate.dal.review.reviewStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.event.eventStorage.EventStorage;
import ru.yandex.practicum.filmorate.dal.review.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;

import java.sql.PreparedStatement;
import java.util.List;


@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final EventStorage eventStorage;
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    private static final String INSERT_INTO_REVIEWS = """
            INSERT INTO reviews(content, is_positive, user_id, film_id)
            VALUES (?,?,?,?)
            """;
    private static final String UPDATE_REVIEWS = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE review_id = ?
            """;
    private static final String DELETE_REVIEW = """
            DELETE FROM reviews
            WHERE review_id = ?
            """;
    private static final String GET_REVIEW_BY_ID = """
            SELECT *
            FROM reviews
            WHERE review_id = ?
            """;
    private static final String GET_REVIEW_BY_FILM = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            ORDER BY USEFUL DESC
            LIMIT ?
            """;
    private static final String GET_ALL_REVIEWS = """
            SELECT *
            FROM reviews
            ORDER BY USEFUL DESC
            LIMIT ?
            """;

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO_REVIEWS, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        eventStorage.save(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        int updatedCount = jdbcTemplate.update(UPDATE_REVIEWS, review.getContent(), review.getIsPositive(), review.getReviewId());
        if (updatedCount < 1) {
            throw new NotFoundException("Отзыв не найден в базе данных");
        }
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Long reviewId) {
        int deletedCount = jdbcTemplate.update(DELETE_REVIEW, reviewId);
        if (deletedCount < 1) {
            throw new NotFoundException("Отзыв не найден в базе данных");
        }
    }

    @Override
    public Review getReviewById(Long reviewId) {
        List<Review> review = jdbcTemplate.query(GET_REVIEW_BY_ID, reviewMapper, reviewId);
        if (review.isEmpty()) {
            throw new NotFoundException("Отзыв не найден в базе данных");
        }
        return review.getFirst();
    }

    @Override
    public List<Review> getReviewsByFilm(Long filmId, Integer count) {
        if (filmId == 0) {
            List<Review> allReviews = jdbcTemplate.query(GET_ALL_REVIEWS, reviewMapper, count);
            return allReviews;
        }
        List<Review> filmReviews = jdbcTemplate.query(GET_REVIEW_BY_FILM, reviewMapper, filmId, count);
        return filmReviews;
    }
}

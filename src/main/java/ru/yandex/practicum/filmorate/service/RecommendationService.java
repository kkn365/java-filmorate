package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserService userService;
    private final FilmService filmService;

    @Value("${filmorate.default.film-mark}")
    private Double defaultMark;
    @Value("${filmorate.default.minimum-mark-for-recommendation}")
    private Double minimumMark;

    private static Map<Long, HashMap<Long, Double>> filmsDiff;
    private static Map<Long, HashMap<Long, Integer>> filmsFreq;
    private static Map<Long, HashMap<Long, Double>> outputData;

    public Collection<FilmDto> getRecommendedFilmsList(Long userId) {

        if (userService.getUserById(userId) == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final Collection<Like> likeDataField = filmService.getDataField(userId);
        Collection<FilmDto> recommendedFilms = new ArrayList<>();
        if (likeDataField.isEmpty()) {
            return recommendedFilms;
        }

        Map<Long, HashMap<Long, Double>> inputData = new HashMap<>();
        filmsDiff = new HashMap<>();
        filmsFreq = new HashMap<>();
        outputData = new HashMap<>();

        for (Like like : likeDataField) {
            if (!inputData.containsKey(like.getUserId())) {
                HashMap<Long, Double> rate = new HashMap<>();
                rate.put(like.getFilmId(), defaultMark);
                inputData.put(like.getUserId(), rate);
            } else {
                inputData.get(like.getUserId()).put(like.getFilmId(), defaultMark);
            }
        }

        buildDifferencesMatrix(inputData);
        predict(inputData, likeDataField);

        final Collection<FilmDto> allFilms = filmService.getFilms();
        Collection<Long> recommendedFilmIds = new ArrayList<>();

        for (Map.Entry<Long, Double> filmId : outputData.get(userId).entrySet()) {
            if (!inputData.get(userId).containsKey(filmId.getKey()) && filmId.getValue() >= minimumMark) {
                recommendedFilmIds.add(filmId.getKey());
            }
        }
        Arrays.sort(recommendedFilmIds.toArray());

        for (FilmDto film : allFilms) {
            if (recommendedFilmIds.contains(film.getId())) {
                recommendedFilms.add(film);
            }
        }

        return recommendedFilms;
    }

    private static void buildDifferencesMatrix(Map<Long, HashMap<Long, Double>> data) {
        for (HashMap<Long, Double> user : data.values()) {
            for (Map.Entry<Long, Double> e : user.entrySet()) {
                if (!filmsDiff.containsKey(e.getKey())) {
                    filmsDiff.put(e.getKey(), new HashMap<>());
                    filmsFreq.put(e.getKey(), new HashMap<>());
                }
                for (Map.Entry<Long, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (filmsFreq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = filmsFreq.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0.0;
                    if (filmsDiff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = filmsDiff.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    filmsFreq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    filmsDiff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Long j : filmsDiff.keySet()) {
            for (Long i : filmsDiff.get(j).keySet()) {
                double oldValue = filmsDiff.get(j).get(i);
                int count = filmsFreq.get(j).get(i);
                filmsDiff.get(j).put(i, oldValue / count);
            }
        }
    }

    private static void predict(Map<Long, HashMap<Long, Double>> data, Collection<Like> likes) {
        HashMap<Long, Double> uPred = new HashMap<>();
        HashMap<Long, Integer> uFreq = new HashMap<>();
        final List<Long> filmsIds = likes.stream().map(Like::getFilmId).distinct().toList();
        for (Long j : filmsDiff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Map.Entry<Long, HashMap<Long, Double>> e : data.entrySet()) {
            for (Long j : e.getValue().keySet()) {
                for (Long k : filmsDiff.keySet()) {
                    try {
                        double predictedValue = filmsDiff.get(k).get(j) + e.getValue().get(j);
                        double finalValue = predictedValue * filmsFreq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + filmsFreq.get(k).get(j));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
            HashMap<Long, Double> clean = new HashMap<>();
            for (Long j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }
            for (Long j : filmsIds) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }

}

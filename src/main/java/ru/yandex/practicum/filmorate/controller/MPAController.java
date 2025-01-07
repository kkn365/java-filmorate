package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MPAController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> getAll() {
        log.info("Получен запрос 'GET /mpa'");
        Collection<MpaDto> allMPAs = mpaService.getAllMPAs()
                .stream()
                .map(MPAMapper::mapToMpaDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'GET /mpa' с телом: {}", allMPAs);
        return allMPAs;
    }

    @GetMapping("/{id}")
    public MpaDto getMPAById(@PathVariable int id) {
        log.info("Получен запрос 'GET /mpa/{}'", id);
        MpaDto mpa = MPAMapper.mapToMpaDto(mpaService.getMPAbyId(id));
        log.info("Отправлен ответ на запрос 'GET /mpa/{}' с телом: {}", id, mpa);
        return mpa;
    }

}

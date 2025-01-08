package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPAController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> getAll() {
        return mpaService.getAllMPAs();
    }

    @GetMapping("/{id}")
    public MpaDto getMPAById(@PathVariable int id) {
        return mpaService.getMPAbyId(id);
    }

}

package com.example.campains.town;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/towns/v1")
public class TownController {

    private final TownService townService;

    public TownController(TownService townService) {
        this.townService = townService;
    }

    @GetMapping
    public List<TownDto> getTowns() {
        return townService.getTowns();
    }


}

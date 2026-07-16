package com.example.campains.town;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TownService {


    private final TownRepository townRepository;

    public TownService(TownRepository townRepository) {
        this.townRepository = townRepository;
    }

    public List<TownDto> getTowns() {
        return townRepository.findAll().stream().map(t -> new TownDto(t.getName())).toList();
    }
}

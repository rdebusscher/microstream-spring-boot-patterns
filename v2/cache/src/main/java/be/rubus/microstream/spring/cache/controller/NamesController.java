package be.rubus.microstream.spring.cache.controller;

import be.rubus.microstream.spring.cache.service.NamesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NamesController {

    private final NamesService namesService;

    public NamesController(NamesService namesService) {
        this.namesService = namesService;
    }

    @GetMapping("/data")
    public List<String> getData() {
        return namesService.getNames();
    }

    @PostMapping("/data/{name}")
    public void updateData(@PathVariable String name) {
        namesService.addName(name);
    }

    @DeleteMapping("/data/{name}")
    public void deleteData(@PathVariable String name) {
        namesService.deleteName(name);
    }
}

package be.rubus.microstream.spring.example.controller;

import be.rubus.microstream.spring.example.database.Names;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class NamesController {

    private final Names greenRoot;

    public NamesController(Names greenRoot) {
        this.greenRoot = greenRoot;
    }

    @GetMapping("/names")
    public Collection<String> getAll() {
        return greenRoot.getNames();
    }

    @PostMapping("/names/{name}")
    public void addName(@PathVariable String name) {
        greenRoot.addName(name);
    }

    @DeleteMapping("/names/{name}")
    public void removeName(@PathVariable String name) {
        greenRoot.deleteName(name);
    }


}

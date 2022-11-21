package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController (AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Animal> getAnimalInfo(@PathVariable Long id) {
        Animal animal = animalService.findAnimal(id);
        if (animal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(animal);
    }

    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        return animalService.addAnimal(animal);
    }

    @PutMapping
    public ResponseEntity<Animal> editAnimal(@RequestBody Animal animal) {
        Animal animal1 = animalService.editAnimal(animal);
        if (animal1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(animal1);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.ok().build();
    }
}

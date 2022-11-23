package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation (
            summary = "get information about the animal",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "information about the animal from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                            )
                    )
            },
            tags = "Animals"
    )

    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalInfo(@PathVariable Long id) {
        Animal animal = animalService.findAnimal(id);
        if (animal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(animal);
    }

    @Operation (
            summary = "animal creation",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "return created animals",
                    content = @Content(
                            mediaType  = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                    )
            )
    },
            tags = "Animals"
)
    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {

        return animalService.addAnimal(animal);

    }
    @Operation (
            summary = "editing animal parameters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "returns an animal with modified parameters",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                            )
                    )
            },
            tags = "Animals"
    )

    @PutMapping
    public ResponseEntity<Animal> editAnimal(@RequestBody Animal animal) {
        Animal animal1 = animalService.editAnimal(animal);
        if (animal1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(animal1);
    }

    @Operation (
            summary = "deleting an animal from the db",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                            )
                    )
            },
            tags = "Animals"
    )


    @DeleteMapping("/{id}")
    public ResponseEntity deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.ok().build();
    }
}

package dev.pro.animalshelterbot.controller;

import dev.pro.animalshelterbot.constants.AnimalKind;
import dev.pro.animalshelterbot.exception.AnimalNotFoundException;
import dev.pro.animalshelterbot.model.Animal;
import dev.pro.animalshelterbot.model.DailyReport;
import dev.pro.animalshelterbot.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController (AnimalService animalService) {
        this.animalService = animalService;
    }

    private final Logger logger = LoggerFactory.getLogger(AnimalController.class);


    @Operation (
            summary = "get information about the animal by id",
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
    ResponseEntity<Animal> getAnimalById(@Parameter(description = "animal id", example = "123")@PathVariable Long id) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        return animal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation (
            summary = "get daily reports regarding the animal by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "daily reports regarding the animal from the db",
                            content = @Content(
                                    mediaType  = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DailyReport.class))

                            )
                    )
            },
            tags = "Animals"
    )
    @GetMapping("/{id}/reports")
    ResponseEntity<List<DailyReport>> getReportsByAnimalId(@Parameter(description = "animal id", example = "123")@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getDailyReports(id));
    }

    @Operation (
            summary = "get information about the animal by substrings in name, kind, breed and color",
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
    @GetMapping
    public ResponseEntity <List<Animal>> getAnimal(@Parameter(description = "animal name", example = "Matroskin")   @RequestParam(required = false) String name,
                                                   @Parameter(description = "animal kind", example = "1")           @RequestParam(required = false) Integer kind,
                                                   @Parameter(description = "animal breed", example = "Maine Coon") @RequestParam(required = false) String breed,
                                                   @Parameter(description = "animal color", example = "Gray")       @RequestParam(required = false) String color) {
        boolean nameIsNotEmpty = name != null && !name.isBlank();
        boolean breedIsNotEmpty = breed != null && !breed.isBlank();
        boolean colorIsNotEmpty = color != null && !color.isBlank();
        boolean kindIsNotEmpty = kind != null;

        if(!nameIsNotEmpty && !breedIsNotEmpty && !colorIsNotEmpty && !kindIsNotEmpty) {
            return ResponseEntity.notFound().build();
        }
        if(kind != null) {
            boolean isFound = false;
            for (AnimalKind a : AnimalKind.values()) {
                if (kind == a.ordinal()) {
                    isFound = true;
                }
            }
            if (!isFound) {
                throw new AnimalNotFoundException("Animal with such kind index was not find in the database");
            }
            return ResponseEntity.ok(animalService.getAnimalBySubstringsAndKind(name, kind, breed, color));
        } else {
            return ResponseEntity.ok(animalService.getAnimalBySubstrings(name, breed, color));
        }
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
    public void deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
    }

    @Operation (
            summary = "animal avatar uploading",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return animal with avatar",
                            content = @Content(
                                    mediaType  = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                            )
                    )
            },
            tags = "Animals"
    )
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity <String> uploadAvatar(@PathVariable Long id,
                                                @RequestParam MultipartFile avatar) {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        try {
            animalService.uploadAvatar(id, avatar);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.notFound().build();
        }
    }

    @Operation (
            summary = "get animal avatar",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "return avatar animal",
                            content = @Content(
                                    mediaType  = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))

                            )
                    )
            },
            tags = "Animals"
    )
    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity <byte[]> downloadAvatar(@PathVariable Long id) {

        Animal animal = animalService.getAnimalById(id).orElseThrow(
                () -> new AnimalNotFoundException("Animal with such id was not found in the database"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(animal.getMediaType()));
        headers.setContentLength(animal.getAvatarPreview().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(animal.getAvatarPreview());
    }
}

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
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

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

    @GetMapping
    public ResponseEntity <Collection<Animal>> getAnimal(
                                                         @Parameter(description = "name animal", example = "Motroskin")  @RequestParam  (required = false) String name,
                                                         @Parameter(description = "kind animal", example = "cat")        @RequestParam  (required = false) String kind,
                                                         @Parameter(description = "breed animal", example = "Maine Coon")@RequestParam  (required = false) String breed) {

        if (name !=null && !name.isBlank()) {
            return ResponseEntity.ok(animalService.getName(name));
        }
        if (kind !=null && !kind.isBlank()) {
            return ResponseEntity.ok(animalService.getKind(kind));
        }
        if (breed !=null && !breed.isBlank()) {
            return ResponseEntity.ok(animalService.getBreed(breed));
        }

        return ResponseEntity.ok(animalService.getAllAnimal());
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

    @Operation (
            summary = "animal avatar creation",
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
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity <String> uploadAvatar(@PathVariable Long id,
                                                @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        animalService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
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

        Animal animal = animalService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(animal.getMediaType()));
        headers.setContentLength(animal.getAvatarPicture().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(animal.getAvatarPicture());
    }
}

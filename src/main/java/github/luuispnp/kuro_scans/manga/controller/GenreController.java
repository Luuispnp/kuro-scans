package github.luuispnp.kuro_scans.manga.controller;

import github.luuispnp.kuro_scans.manga.dto.request.GenreRequest;
import github.luuispnp.kuro_scans.manga.dto.response.GenreResponse;
import github.luuispnp.kuro_scans.manga.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        List<GenreResponse> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable UUID id) {
        GenreResponse genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(@RequestBody @Valid GenreRequest request) {
        GenreResponse genre = genreService.createGenre(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(genre);
    }

    @PutMapping("{id}")
    public ResponseEntity<GenreResponse> updateGenreById(@PathVariable UUID id, @RequestBody @Valid GenreRequest request) {
        GenreResponse genre = genreService.updateGenreById(id, request);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
        genreService.deleteGenreById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}

package github.luuispnp.kuro_scans.manga.controller;

import github.luuispnp.kuro_scans.manga.dto.request.MangaRequest;
import github.luuispnp.kuro_scans.manga.dto.response.MangaResponse;
import github.luuispnp.kuro_scans.manga.service.MangaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mangas")
@RequiredArgsConstructor
public class MangaController {

    private final MangaService mangaService;

    @GetMapping
    public ResponseEntity<Page<MangaResponse>> getAllMangas(Pageable pageable) {
        Page<MangaResponse> mangas = mangaService.getAllMangas(pageable);
        return ResponseEntity.ok(mangas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> getMangaById(@PathVariable UUID id) {
        MangaResponse manga = mangaService.getMangaById(id);
        return ResponseEntity.ok(manga);
    }

    @PostMapping
    public ResponseEntity<MangaResponse> createManga(@RequestBody @Valid MangaRequest request) {
        MangaResponse manga = mangaService.createManga(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(manga);
    }

    @PutMapping("{id}")
    public ResponseEntity<MangaResponse> updateMangaById(@PathVariable UUID id ,@RequestBody @Valid MangaRequest request) {
        MangaResponse manga = mangaService.updateMangaById(id, request);
        return ResponseEntity.ok(manga);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteMangaById(@PathVariable UUID id) {
        mangaService.deleteMangaById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}

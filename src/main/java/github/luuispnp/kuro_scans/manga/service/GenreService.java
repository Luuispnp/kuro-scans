package github.luuispnp.kuro_scans.manga.service;

import github.luuispnp.kuro_scans.common.exception.ResourceNotFoundException;
import github.luuispnp.kuro_scans.manga.dto.request.GenreRequest;
import github.luuispnp.kuro_scans.manga.dto.response.GenreResponse;
import github.luuispnp.kuro_scans.manga.entity.Genre;
import github.luuispnp.kuro_scans.manga.mapper.GenreMapper;
import github.luuispnp.kuro_scans.manga.repository.GenreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genreMapper.toGenreResponseList(genres);
    }

    public GenreResponse getGenreById(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com id: " + id));
        return genreMapper.toGenreResponse(genre);
    }

    @Transactional
    public GenreResponse createGenre(@Valid GenreRequest request) {
        Genre genre = genreMapper.genreRequestToGenre(request);
        Genre genreSalvo = genreRepository.save(genre);
        return genreMapper.toGenreResponse(genreSalvo);
    }

    @Transactional
    public GenreResponse updateGenreById(UUID id, @Valid GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com id: " + id));
        genreMapper.updateGenre(request, genre);
        Genre genreAtualizado = genreRepository.save(genre);
        return genreMapper.toGenreResponse(genreAtualizado);
    }

    @Transactional
    public void deleteGenreById(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado com id: " + id));
        genreRepository.delete(genre);
    }
}

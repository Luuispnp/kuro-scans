package github.luuispnp.kuro_scans.manga.service;

import github.luuispnp.kuro_scans.common.exception.ResourceNotFoundException;
import github.luuispnp.kuro_scans.manga.dto.request.MangaRequest;
import github.luuispnp.kuro_scans.manga.dto.response.MangaResponse;
import github.luuispnp.kuro_scans.manga.entity.Genre;
import github.luuispnp.kuro_scans.manga.entity.Manga;
import github.luuispnp.kuro_scans.manga.mapper.MangaMapper;
import github.luuispnp.kuro_scans.manga.repository.GenreRepository;
import github.luuispnp.kuro_scans.manga.repository.MangaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MangaService {

    private final MangaRepository mangaRepository;

    private final GenreRepository genreRepository;

    private final MangaMapper mangaMapper;

    public Page<MangaResponse> getAllMangas(Pageable pageable) {
        Page<Manga> mangaPage = mangaRepository.findAll(pageable);
        return mangaPage.map(manga -> mangaMapper.toMangaResponse(manga));
    }

    public MangaResponse getMangaById(UUID id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mangá não encontrado com o id: " + id));
        return mangaMapper.toMangaResponse(manga);
    }

    @Transactional
    public MangaResponse createManga(@Valid MangaRequest request) {
        Set<Genre> genres = resolveGenres(request.genreIds());
        Manga manga = mangaMapper.mangaRequestToManga(request);
        manga.setGenres(genres);
        Manga mangaSalvo = mangaRepository.save(manga);
        return mangaMapper.toMangaResponse(mangaSalvo);
    }

    @Transactional
    public MangaResponse updateMangaById(UUID id, @Valid MangaRequest request) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mangá não encontrado com o id: " + id));
        Set<Genre> genres = resolveGenres(request.genreIds());
        mangaMapper.updateManga(request, manga);
        manga.setGenres(genres);
        Manga mangaAtualizado = mangaRepository.save(manga);
        return mangaMapper.toMangaResponse(mangaAtualizado);
    }

    @Transactional
    public void deleteMangaById(UUID id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mangá não encontrado com o id: " + id));
        mangaRepository.delete(manga);
    }

    private Set<Genre> resolveGenres(Set<UUID> genreIds) {
        List<Genre> genresEncontrados = genreRepository.findAllById(genreIds);

        if (genresEncontrados.size() != genreIds.size()) {
            Set<UUID> idsEncontrados = genresEncontrados.stream()
                    .map(genre -> genre.getId())
                    .collect(Collectors.toSet());

            Set<UUID> idsNaoEncontrados = new HashSet<>(genreIds);
            idsNaoEncontrados.removeAll(idsEncontrados);

            throw new ResourceNotFoundException(
                    "Gêneros não encontrados com ids: " + idsEncontrados);
        }

        return new HashSet<>(genresEncontrados);
    }
}

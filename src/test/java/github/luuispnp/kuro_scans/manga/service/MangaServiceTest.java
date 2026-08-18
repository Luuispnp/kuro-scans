package github.luuispnp.kuro_scans.manga.service;

import github.luuispnp.kuro_scans.common.exception.ResourceNotFoundException;
import github.luuispnp.kuro_scans.manga.dto.request.MangaRequest;
import github.luuispnp.kuro_scans.manga.dto.response.GenreResponse;
import github.luuispnp.kuro_scans.manga.dto.response.MangaResponse;
import github.luuispnp.kuro_scans.manga.entity.Genre;
import github.luuispnp.kuro_scans.manga.entity.Manga;
import github.luuispnp.kuro_scans.manga.mapper.MangaMapper;
import github.luuispnp.kuro_scans.manga.repository.GenreRepository;
import github.luuispnp.kuro_scans.manga.repository.MangaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MangaServiceTest {

    @Mock
    private MangaRepository mangaRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MangaMapper mangaMapper;

    @InjectMocks
    private MangaService mangaService;

    @Test
    void deveCriarMangaComSucesso() {
        // Arrange
        UUID genreId1 = UUID.randomUUID();
        UUID genreId2 = UUID.randomUUID();
        Set<UUID> genreIds = Set.of(genreId1, genreId2);

        MangaRequest request = new MangaRequest(
                "Manga1", "Sinopse1", "Status1", "Autor1", "Artista1", "Imagem1", genreIds
        );

        Genre genre1 = new Genre(genreId1, "Gênero1", new HashSet<>());
        Genre genre2 = new Genre(genreId2, "Gênero2", new HashSet<>());
        List<Genre> genresEncontrados = List.of(genre1, genre2);

        Manga mangaMapeado = new Manga();
        mangaMapeado.setTitle("Manga1");
        mangaMapeado.setSynopsis("Sinopse1");
        mangaMapeado.setStatus("Status1");
        mangaMapeado.setAuthor("Autor1");
        mangaMapeado.setArtist("Artista1");
        mangaMapeado.setCoverImageKey("Imagem1");

        Manga mangaSalvo = new Manga();
        mangaSalvo.setId(UUID.randomUUID());
        mangaSalvo.setTitle(mangaMapeado.getTitle());
        mangaSalvo.setSynopsis(mangaMapeado.getSynopsis());
        mangaSalvo.setStatus(mangaMapeado.getStatus());
        mangaSalvo.setAuthor(mangaMapeado.getAuthor());
        mangaSalvo.setArtist(mangaMapeado.getArtist());
        mangaSalvo.setCoverImageKey(mangaMapeado.getCoverImageKey());
        mangaSalvo.setCreatedAt(LocalDateTime.now());
        mangaSalvo.setGenres(Set.of(genre1, genre2));

        Set<GenreResponse> genresResponse = Set.of(
                new GenreResponse(genreId1, "Gênero1"),
                new GenreResponse(genreId2, "Gênero2")
        );

        MangaResponse expectedResponse = new MangaResponse(
                mangaSalvo.getId(), mangaSalvo.getTitle(), mangaSalvo.getSynopsis(), mangaSalvo.getStatus(),
                mangaSalvo.getAuthor(), mangaSalvo.getArtist(), mangaSalvo.getCoverImageKey(),
                mangaSalvo.getCreatedAt(), genresResponse
        );

        when(genreRepository.findAllById(genreIds)).thenReturn(genresEncontrados);
        when(mangaMapper.mangaRequestToManga(request)).thenReturn(mangaMapeado);
        when(mangaRepository.save(mangaMapeado)).thenReturn(mangaSalvo);
        when(mangaMapper.toMangaResponse(mangaSalvo)).thenReturn(expectedResponse);

        // Act
        MangaResponse response = mangaService.createManga(request);

        // Assert
        assertThat(response).isEqualTo(expectedResponse);
        verify(mangaRepository).save(mangaMapeado);
    }

    @Test
    void deveLancarExcecaoQuandoGeneroNaoExisteAoCriarManga() {
        // Arrange
        UUID genreIdValido = UUID.randomUUID();
        UUID genreIdInexistente = UUID.randomUUID();
        Set<UUID> genreIds = Set.of(genreIdValido, genreIdInexistente);

        MangaRequest request = new MangaRequest(
                "Manga1", "Sinopse1", "Status1", "Autor1", "Artista1", "Imagem1", genreIds
        );

        Genre genreValido = new Genre(genreIdValido, "Gênero1", new HashSet<>());

        when(genreRepository.findAllById(genreIds)).thenReturn(List.of(genreValido));

        // Act & Assert
        assertThatThrownBy(() -> mangaService.createManga(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(mangaRepository, never()).save(any(Manga.class));
        verify(mangaMapper, never()).mangaRequestToManga(any(MangaRequest.class));
    }

    @Test
    void deveRetornarMangaPeloId() {
        // Arrange
        Manga manga = criarMangaDeTeste("Manga1");

        Set<GenreResponse> genresResponse = manga.getGenres().stream()
                .map(genre -> new GenreResponse(genre.getId(), genre.getName()))
                .collect(Collectors.toSet());

        MangaResponse expectedResponse = new MangaResponse(
                manga.getId(), manga.getTitle(), manga.getSynopsis(), manga.getStatus(),
                manga.getAuthor(), manga.getArtist(), manga.getCoverImageKey(),
                manga.getCreatedAt(), genresResponse
        );

        when(mangaRepository.findById(manga.getId())).thenReturn(Optional.of(manga));
        when(mangaMapper.toMangaResponse(manga)).thenReturn(expectedResponse);

        // Act
        MangaResponse response = mangaService.getMangaById(manga.getId());

        // Assert
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void deveLancarExcecaoQuandoMangaNaoExisteAoBuscar() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mangaService.getMangaById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(mangaMapper, never()).toMangaResponse(any(Manga.class));
    }

    @Test
    void deveListarTodosOsMangasPaginado() {
        // Arrange
        Manga manga1 = criarMangaDeTeste("Manga1");
        Manga manga2 = criarMangaDeTeste("Manga2");

        MangaResponse response1 = paraResponse(manga1);
        MangaResponse response2 = paraResponse(manga2);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Manga> mangaPage = new PageImpl<>(List.of(manga1, manga2), pageable, 2);

        when(mangaRepository.findAll(pageable)).thenReturn(mangaPage);
        when(mangaMapper.toMangaResponse(manga1)).thenReturn(response1);
        when(mangaMapper.toMangaResponse(manga2)).thenReturn(response2);

        // Act
        Page<MangaResponse> result = mangaService.getAllMangas(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(response1, response2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void deveAtualizarMangaComSucesso() {
        // Arrange
        UUID genreId = UUID.randomUUID();
        Set<UUID> genreIds = Set.of(genreId);
        MangaRequest request = new MangaRequest(
                "Manga Atualizado", "Nova sinopse", "COMPLETED", "Autor1", "Artista1", "Imagem1", genreIds
        );

        Manga mangaExistente = criarMangaDeTeste("Manga1");
        Genre genre = new Genre(genreId, "Gênero1", new HashSet<>());

        Manga mangaAtualizado = criarMangaDeTeste("Manga Atualizado");
        mangaAtualizado.setId(mangaExistente.getId());

        MangaResponse expectedResponse = paraResponse(mangaAtualizado);

        when(mangaRepository.findById(mangaExistente.getId())).thenReturn(Optional.of(mangaExistente));
        when(genreRepository.findAllById(genreIds)).thenReturn(List.of(genre));
        when(mangaRepository.save(mangaExistente)).thenReturn(mangaAtualizado);
        when(mangaMapper.toMangaResponse(mangaAtualizado)).thenReturn(expectedResponse);

        // Act
        MangaResponse response = mangaService.updateMangaById(mangaExistente.getId(), request);

        // Assert
        assertThat(response).isEqualTo(expectedResponse);
        verify(mangaMapper).updateManga(request, mangaExistente);
        verify(mangaRepository).save(mangaExistente);
    }

    @Test
    void deveLancarExcecaoQuandoMangaNaoExisteAoAtualizar() {
        // Arrange
        UUID id = UUID.randomUUID();
        MangaRequest request = new MangaRequest(
                "Manga1", "Sinopse1", "Status1", "Autor1", "Artista1", "Imagem1", Set.of(UUID.randomUUID())
        );

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mangaService.updateMangaById(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(mangaRepository, never()).save(any(Manga.class));
        verify(genreRepository, never()).findAllById(any());
    }

    @Test
    void deveDeletarMangaComSucesso() {
        // Arrange
        Manga manga = criarMangaDeTeste("Manga1");
        when(mangaRepository.findById(manga.getId())).thenReturn(Optional.of(manga));

        // Act
        mangaService.deleteMangaById(manga.getId());

        // Assert
        verify(mangaRepository).delete(manga);
    }

    @Test
    void deveLancarExcecaoQuandoMangaNaoExisteAoDeletar() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> mangaService.deleteMangaById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(mangaRepository, never()).delete(any(Manga.class));
    }

    // ---- helpers ----

    private Manga criarMangaDeTeste(String title) {
        Manga manga = new Manga();
        manga.setId(UUID.randomUUID());
        manga.setTitle(title);
        manga.setSynopsis("Sinopse de " + title);
        manga.setStatus("ONGOING");
        manga.setAuthor("Autor");
        manga.setArtist("Artista");
        manga.setCoverImageKey("Imagem");
        manga.setCreatedAt(LocalDateTime.now());
        manga.setGenres(Set.of(
                new Genre(UUID.randomUUID(), "Gênero A", new HashSet<>()),
                new Genre(UUID.randomUUID(), "Gênero B", new HashSet<>())
        ));
        return manga;
    }

    private MangaResponse paraResponse(Manga manga) {
        Set<GenreResponse> genres = manga.getGenres().stream()
                .map(genre -> new GenreResponse(genre.getId(), genre.getName()))
                .collect(Collectors.toSet());

        return new MangaResponse(
                manga.getId(), manga.getTitle(), manga.getSynopsis(), manga.getStatus(),
                manga.getAuthor(), manga.getArtist(), manga.getCoverImageKey(),
                manga.getCreatedAt(), genres
        );
    }

}

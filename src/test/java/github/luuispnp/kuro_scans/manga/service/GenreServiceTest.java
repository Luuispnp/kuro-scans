package github.luuispnp.kuro_scans.manga.service;

import github.luuispnp.kuro_scans.common.exception.ResourceNotFoundException;
import github.luuispnp.kuro_scans.manga.dto.request.GenreRequest;
import github.luuispnp.kuro_scans.manga.dto.response.GenreResponse;
import github.luuispnp.kuro_scans.manga.entity.Genre;
import github.luuispnp.kuro_scans.manga.mapper.GenreMapper;
import github.luuispnp.kuro_scans.manga.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreService genreService;

    @Test
    void deveCriarGeneroComSucesso() {
        // Arrange
        GenreRequest request = new GenreRequest("Ação");

        Genre genreMapeado = new Genre();
        genreMapeado.setName("Ação");

        Genre genreSalvo = new Genre(UUID.randomUUID(), "Ação", new HashSet<>());

        GenreResponse expectedResponse = new GenreResponse(genreSalvo.getId(), "Ação");

        when(genreMapper.genreRequestToGenre(request)).thenReturn(genreMapeado);
        when(genreRepository.save(genreMapeado)).thenReturn(genreSalvo);
        when(genreMapper.toGenreResponse(genreSalvo)).thenReturn(expectedResponse);

        // Act
        GenreResponse result = genreService.createGenre(request);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);
        verify(genreRepository).save(genreMapeado);
    }

    @Test
    void deveBuscarGeneroPorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Genre genre = new Genre(id, "Fantasia", new HashSet<>());
        GenreResponse expectedResponse = new GenreResponse(id, "Fantasia");

        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        when(genreMapper.toGenreResponse(genre)).thenReturn(expectedResponse);

        // Act
        GenreResponse result = genreService.getGenreById(id);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void deveLancarExcecaoQuandoGeneroNaoExisteAoBuscar() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.getGenreById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(genreMapper, never()).toGenreResponse(any(Genre.class));
    }

    @Test
    void deveListarTodosOsGeneros() {
        // Arrange
        Genre genre1 = new Genre(UUID.randomUUID(), "Ação", new HashSet<>());
        Genre genre2 = new Genre(UUID.randomUUID(), "Romance", new HashSet<>());
        List<Genre> genres = List.of(genre1, genre2);

        List<GenreResponse> expectedResponses = List.of(
                new GenreResponse(genre1.getId(), "Ação"),
                new GenreResponse(genre2.getId(), "Romance")
        );

        when(genreRepository.findAll()).thenReturn(genres);
        when(genreMapper.toGenreResponseList(genres)).thenReturn(expectedResponses);

        // Act
        List<GenreResponse> result = genreService.getAllGenres();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedResponses);
    }

    @Test
    void deveAtualizarGeneroComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        GenreRequest request = new GenreRequest("Ação e Aventura");
        Genre genreExistente = new Genre(id, "Ação", new HashSet<>());
        Genre genreAtualizado = new Genre(id, "Ação e Aventura", new HashSet<>());
        GenreResponse expectedResponse = new GenreResponse(id, "Ação e Aventura");

        when(genreRepository.findById(id)).thenReturn(Optional.of(genreExistente));
        when(genreRepository.save(genreExistente)).thenReturn(genreAtualizado);
        when(genreMapper.toGenreResponse(genreAtualizado)).thenReturn(expectedResponse);

        // Act
        GenreResponse result = genreService.updateGenreById(id, request);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);
        verify(genreMapper).updateGenre(request, genreExistente);
        verify(genreRepository).save(genreExistente);
    }

    @Test
    void deveLancarExcecaoQuandoGeneroNaoExisteAoAtualizar() {
        // Arrange
        UUID id = UUID.randomUUID();
        GenreRequest request = new GenreRequest("Ação e Aventura");
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.updateGenreById(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(genreRepository, never()).save(any(Genre.class));
        verify(genreMapper, never()).updateGenre(any(GenreRequest.class), any(Genre.class));
    }

    @Test
    void deveDeletarGeneroComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Genre genre = new Genre(id, "Ação", new HashSet<>());
        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

        // Act
        genreService.deleteGenreById(id);

        // Assert
        verify(genreRepository).delete(genre);
    }

    @Test
    void deveLancarExcecaoQuandoGeneroNaoExisteAoDeletar() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> genreService.deleteGenreById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(genreRepository, never()).delete(any(Genre.class));
    }

}

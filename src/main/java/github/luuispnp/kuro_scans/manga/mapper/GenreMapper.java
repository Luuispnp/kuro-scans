package github.luuispnp.kuro_scans.manga.mapper;

import github.luuispnp.kuro_scans.manga.dto.request.GenreRequest;
import github.luuispnp.kuro_scans.manga.dto.response.GenreResponse;
import github.luuispnp.kuro_scans.manga.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    List<GenreResponse> toGenreResponseList(List<Genre> genres);

    GenreResponse toGenreResponse(Genre genre);

    @Mapping(target = "id", ignore = true)
    Genre genreRequestToGenre(GenreRequest request);

    @Mapping(target = "id", ignore = true)
    void updateGenre(GenreRequest request, @MappingTarget Genre genre);

}

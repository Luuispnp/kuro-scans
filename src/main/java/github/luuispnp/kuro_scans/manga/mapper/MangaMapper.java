package github.luuispnp.kuro_scans.manga.mapper;

import github.luuispnp.kuro_scans.manga.dto.request.MangaRequest;
import github.luuispnp.kuro_scans.manga.dto.response.MangaResponse;
import github.luuispnp.kuro_scans.manga.entity.Manga;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MangaMapper {

    MangaResponse toMangaResponse(Manga manga);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    Manga mangaRequestToManga(MangaRequest mangaRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    void updateManga(MangaRequest mangaRequest, @MappingTarget Manga manga);

}

package github.luuispnp.kuro_scans.manga.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MangaResponse(
        UUID id,
        String title,
        String synopsis,
        String status,
        String author,
        String artist,
        String coverImageKey,
        LocalDateTime createdAt,
        Set<GenreResponse> genres
) {
}

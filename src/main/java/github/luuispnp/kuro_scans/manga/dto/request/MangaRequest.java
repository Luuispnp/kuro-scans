package github.luuispnp.kuro_scans.manga.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record MangaRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        String synopsis,

        @Size(max = 20)
        String status,

        @Size(max = 100)
        String author,

        @Size(max = 100)
        String artist,

        @Size(max = 255)
        String coverImageKey,

        @NotEmpty
        Set<UUID> genreIds

) {
}

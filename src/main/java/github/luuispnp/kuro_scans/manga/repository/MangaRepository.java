package github.luuispnp.kuro_scans.manga.repository;

import github.luuispnp.kuro_scans.manga.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MangaRepository extends JpaRepository<Manga, UUID> {



}

package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Long> {
    Optional<Genre> findGenreByGenreIdx(int genreIdx);
}

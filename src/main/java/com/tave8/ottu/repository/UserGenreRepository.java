package com.tave8.ottu.repository;

import com.tave8.ottu.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre,Long> {

}

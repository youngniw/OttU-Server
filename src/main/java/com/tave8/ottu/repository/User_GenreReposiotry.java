package com.tave8.ottu.repository;

import com.tave8.ottu.entity.User_Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_GenreReposiotry extends JpaRepository<User_Genre,Long> {

}

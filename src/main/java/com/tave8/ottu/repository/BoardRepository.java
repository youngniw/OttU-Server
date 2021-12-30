package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Board;
import com.tave8.ottu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


}

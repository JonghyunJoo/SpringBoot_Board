package com.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.board.domain.Board;


public interface BoardRepository extends JpaRepository<Board, Long>{
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);
    Page<Board> findByContentContaining(String keyword, Pageable pageable);
    Page<Board> findByWriterContaining(String keyword, Pageable pageable);
    
    long countByTitleContaining(String title);
    long countByContentContaining(String content);
    long countByWriterContaining(String writer);
}

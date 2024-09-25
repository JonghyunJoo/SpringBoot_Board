package com.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.board.domain.Board;
import com.board.dto.BoardDto;
import com.board.repository.BoardRepository;
import com.board.util.Header;
import com.board.util.Pagination;
import com.board.util.Search;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
	
	@Mock
    private BoardRepository boardRepository;
	
    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("게시글 리스트 조회 테스트")
    void getBoardListTest() {
        // given
        List<Board> boardList = Arrays.asList(
            new Board(1L, "title", "writer", "content"),
            new Board(2L, "title2", "writer2", "content2")
        );
        Page<Board> page = new PageImpl<>(boardList);
        when(boardRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);
        when(boardRepository.count()).thenReturn(2L);

        // when
        Header<List<BoardDto>> result = boardService.getBoardList(1, 2);

        // then
        assertEquals(2, result.getData().size());
        assertEquals("title", result.getData().get(0).getTitle());
        assertEquals("writer", result.getData().get(0).getWriter());
        assertEquals("content", result.getData().get(0).getContent());
    }
    
    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void getPostTest() {
        // Given
        Board board = new Board(1L, "title", "writer", "content");
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // When
        Header<BoardDto> result = boardService.getPost(1L);

        // Then
        assertEquals("title", result.getData().getTitle());
        assertEquals("writer", result.getData().getWriter());
        assertEquals("content", result.getData().getContent());
    }
    
    @Test
    @DisplayName("게시글 작성 테스트")
    void savePostTest() {
        // Given
        BoardDto boardDto = new BoardDto(null, "title", "writer", "content", LocalDateTime.now(), LocalDateTime.now());
        when(boardRepository.save(Mockito.any(Board.class))).thenReturn(boardDto.toEntity());

        // When
        Header<String> result = boardService.savePost(boardDto);

        // Then
        assertEquals("게시글이 성공적으로 작성되었습니다.", result.getData());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePostTest() {
        // Given
        Long postId = 1L;

        // When
        Header<String> result = boardService.deletePost(postId);

        // Then
        assertEquals("게시글이 성공적으로 삭제되었습니다.", result.getData());
    }

    @Test
    @DisplayName("게시글 제목으로 검색 테스트")
    void searchPostsByTitleTest() {
        // Given
        Search search = new Search("title", "searchTitle", 1, 10);

        List<Board> mockBoards = List.of(new Board(1L, "searchTitle", "Content", "Writer"));
        Page<Board> mockPage = new PageImpl<>(mockBoards);
        
        when(boardRepository.findByTitleContaining(
                eq("searchTitle"), 
                Mockito.any(PageRequest.class)
        )).thenReturn(mockPage);
        when(boardRepository.countByTitleContaining(Mockito.anyString())).thenReturn(1L);

        // When
        Header<List<BoardDto>> response = boardService.searchPosts(search);
        
        // Then
        Header<List<BoardDto>> expected = Header.OK(mockBoards.stream().map(BoardDto::fromEntity).toList(), new Pagination(1, 10));
        assertEquals(expected.getData(), response.getData());
        assertEquals(expected.getPagination(), response.getPagination());

    }

    @Test
    @DisplayName("게시글 내용으로 검색 테스트")
    void searchPostsByContentTest() {
        // Given
        Search search = new Search("content", "searchContent", 1, 10);

        List<Board> mockBoards = List.of(new Board(1L, "Title", "searchContent", "Writer"));
        Page<Board> mockPage = new PageImpl<>(mockBoards);
        
        when(boardRepository.findByContentContaining(
                eq("searchContent"), 
                Mockito.any(PageRequest.class)
        )).thenReturn(mockPage);
        when(boardRepository.countByContentContaining(Mockito.anyString())).thenReturn(1L);

        Header<List<BoardDto>> response = boardService.searchPosts(search);
        
        // Then
        Header<List<BoardDto>> expected = Header.OK(mockBoards.stream().map(BoardDto::fromEntity).toList(), new Pagination(1, 10));
        assertEquals(expected.getData(), response.getData());
        assertEquals(expected.getPagination(), response.getPagination());

    }

    @Test
    @DisplayName("게시글 작성자로 검색 테스트")
    void searchPostsByWriterTest() {
        // Given
        Search search = new Search("writer", "searchWriter", 1, 10);

        List<Board> mockBoards = List.of(new Board(1L, "Title", "Content", "searchWriter"));
        Page<Board> mockPage = new PageImpl<>(mockBoards);
        
        when(boardRepository.findByWriterContaining(
                eq("searchWriter"), 
                Mockito.any(PageRequest.class)
        )).thenReturn(mockPage);
        when(boardRepository.countByWriterContaining(Mockito.anyString())).thenReturn(1L);

        // When
        Header<List<BoardDto>> response = boardService.searchPosts(search);
        
        // Then
        Header<List<BoardDto>> expected = Header.OK(mockBoards.stream().map(BoardDto::fromEntity).toList(), new Pagination(1, 10));
        assertEquals(expected.getData(), response.getData());
        assertEquals(expected.getPagination(), response.getPagination());

    }

}

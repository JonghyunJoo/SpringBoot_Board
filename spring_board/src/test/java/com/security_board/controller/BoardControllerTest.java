package com.security_board.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.security_board.board.dto.BoardDto;
import com.security_board.board.service.BoardService;
import com.security_board.board.util.Header;
import com.security_board.board.util.Search;

@WebMvcTest
public class BoardControllerTest {
	@MockBean
	private BoardService boardService;
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET 게시글 리스트 조회 로직 확인")
    public void GETBoardListTest() throws Exception {
        // given
        List<BoardDto> boardList = Arrays
                .asList(new BoardDto(1L, "title", "writer", "content", LocalDateTime.now(), LocalDateTime.now()));
        Header<List<BoardDto>> header = Header.OK(boardList);
        when(boardService.getBoardList(anyInt(), anyInt())).thenReturn(header);

        // when
        mockMvc.perform(get("/board/list").param("page", "1").param("size", "1"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("title"));
    }

    @Test
    @DisplayName("GET 게시글 조회 테스트")
    public void GETBoardDetailTest() throws Exception {
        // given
        BoardDto boardDto = new BoardDto(1L, "title", "writer", "content", LocalDateTime.now(), LocalDateTime.now());
        Header<BoardDto> header = Header.OK(boardDto);
        when(boardService.getPost(anyLong())).thenReturn(header);

        // when
        mockMvc.perform(get("/board/post/1"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.writer").value("writer"))
                .andExpect(jsonPath("$.data.content").value("content"));
    }

    @Test
    @DisplayName("POST 게시글 작성 테스트")
    public void POSTBoardWriteTest() throws Exception {
        // given
        BoardDto boardDto = new BoardDto(null, "title", "writer", "content", LocalDateTime.now(), LocalDateTime.now());
        Header<String> response = Header.OK("게시글이 성공적으로 작성되었습니다.");
        when(boardService.savePost(Mockito.any(BoardDto.class))).thenReturn(response);

        String boardDtoJson = objectMapper.writeValueAsString(boardDto);

        // when
        mockMvc.perform(post("/board/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(boardDtoJson))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("게시글이 성공적으로 작성되었습니다."));
    }

    @Test
    @DisplayName("PUT 게시글 수정 테스트")
    public void PUTBoardUpdateTest() throws Exception {
        // given
        BoardDto boardDto = new BoardDto(1L, "newTitle", "writer", "newContent", LocalDateTime.now(), LocalDateTime.now());
        Header<String> response = Header.OK("게시글이 성공적으로 작성되었습니다.");
        when(boardService.savePost(Mockito.any(BoardDto.class))).thenReturn(response);

        String boardDtoJson = objectMapper.writeValueAsString(boardDto);

        // when
        mockMvc.perform(put("/board/post/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(boardDtoJson))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("게시글이 성공적으로 작성되었습니다."));
    }

    @Test
    @DisplayName("DELETE 게시글 삭제 테스트")
    public void testDelete() throws Exception {
        // given
        Header<String> response = Header.OK("게시글이 성공적으로 삭제되었습니다.");
        when(boardService.deletePost(anyLong())).thenReturn(response);

        // when
        mockMvc.perform(delete("/board/post/1"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("게시글이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("게시글 검색 테스트")
    public void testSearch() throws Exception {
        // given
        List<BoardDto> searchResults = Arrays.asList(new BoardDto(1L, "searchTitle", "searchWriter", "searchContent", LocalDateTime.now(), LocalDateTime.now()));
        Header<List<BoardDto>> header = Header.OK(searchResults);
        
        when(boardService.searchPosts(Mockito.any(Search.class))).thenReturn(header);
        
        // when
        mockMvc.perform(get("/board/search")
                .param("type", "title")
                .param("keyword", "search")
                .param("page", "1")
                .param("size", "10"))

        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("searchTitle"))
                .andExpect(jsonPath("$.data[0].writer").value("searchWriter"))
                .andExpect(jsonPath("$.data[0].content").value("searchContent"));
    }
}

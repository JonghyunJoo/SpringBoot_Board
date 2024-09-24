package com.board.controller;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.board.dto.BoardDto;
import com.board.service.BoardService;
import com.board.util.Header;
import com.board.util.Search;

@RestController
@AllArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 리스트 조회
    @GetMapping({"", "/list"})
    public Header<List<BoardDto>> list(@RequestParam(value = "page", defaultValue = "1") Integer pageNum, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return boardService.getBoardList(pageNum, size);
    }
    
    // 게시글 조회
    @GetMapping("/post/{no}")
    public Header<BoardDto> detail(@PathVariable("no") Long no) {
    	return boardService.getPost(no);
    }

    // 게시글 작성
    @PostMapping("/post")
    public Header<String> write(@RequestBody BoardDto boardDto) {
        return boardService.savePost(boardDto);
    }

    // 게시글 수정
    @PutMapping("/post/edit/{no}")
    public Header<String> update(@PathVariable("no") Long no, @RequestBody BoardDto boardDto) {
        boardDto.setId(no);
        return boardService.savePost(boardDto);
    }

    // 게시글 삭제
    @DeleteMapping("/post/{no}")
    public Header<String> delete(@PathVariable("no") Long no) {
        return boardService.deletePost(no);
    }

    // 게시글 검색
    @GetMapping("/search")
    public Header<List<BoardDto>> search(Search search) {
        
        return boardService.searchPosts(search);
    }
}

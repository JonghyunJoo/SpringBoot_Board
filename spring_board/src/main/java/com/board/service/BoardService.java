package com.board.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.board.domain.Board;
import com.board.dto.BoardDto;
import com.board.repository.BoardRepository;
import com.board.util.Header;
import com.board.util.Pagination;
import com.board.util.Search;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BoardService {
	//BoardRepository 객체 생성
    private final BoardRepository boardRepository;
    private static final Integer BLOCK_SIZE = 5; 

    @Transactional
    public Header<List<BoardDto>> getBoardList(Integer pageNum, Integer boardSize) {
        Page<Board> page = boardRepository.findAll(
            PageRequest.of(pageNum - 1, boardSize, Sort.by(Sort.Direction.DESC, "id"))
        );
        List<BoardDto> boardDtoList = page.getContent()
                                          .stream()
                                          .map(BoardDto::fromEntity)
                                          .toList();

        Pagination pagination = createPagination(getBoardCount(), boardSize);
        return Header.OK(boardDtoList, pagination);
    }

    @Transactional
    public Header<BoardDto> getPost(Long id) {
        return boardRepository.findById(id)
            .map(BoardDto::fromEntity)
            .map(Header::OK)
            .orElseThrow(() -> new IllegalArgumentException("Board not found"));
    }

    @Transactional
    public Header<String> savePost(BoardDto boardDto) {
        boardRepository.save(boardDto.toEntity());
        return Header.OK("게시글이 성공적으로 작성되었습니다.");
    }

    @Transactional
    public Header<String> deletePost(Long id) {
        boardRepository.deleteById(id);
        return Header.OK("게시글이 성공적으로 삭제되었습니다.");
    }

    @Transactional
    public Header<List<BoardDto>> searchPosts(Search search) {
        String type = search.getType();
        String keyword = search.getKeyword();
        Integer pageNum = search.getPage();
        Integer boardSize = search.getSize();

        Page<Board> page;
        long totalSearchCount;

        // 검색 타입에 따라 적절한 검색 메서드 호출
        switch (type) {
            case "title":
                page = boardRepository.findByTitleContaining(
                    keyword, PageRequest.of(pageNum - 1, boardSize, Sort.by(Sort.Direction.DESC, "id"))
                );
                totalSearchCount = boardRepository.countByTitleContaining(keyword);
                break;
            case "content":
                page = boardRepository.findByContentContaining(
                    keyword, PageRequest.of(pageNum - 1, boardSize, Sort.by(Sort.Direction.DESC, "id"))
                );
                totalSearchCount = boardRepository.countByContentContaining(keyword);
                break;
            case "writer":
                page = boardRepository.findByWriterContaining(
                    keyword, PageRequest.of(pageNum - 1, boardSize, Sort.by(Sort.Direction.DESC, "id"))
                );
                totalSearchCount = boardRepository.countByWriterContaining(keyword);
                break;
            default:
                return Header.ERROR("Invalid search type");
        }

        List<BoardDto> boardDtoList = page.getContent()
                                          .stream()
                                          .map(BoardDto::fromEntity)
                                          .toList();

        Pagination pagination = createPagination((int) totalSearchCount, boardSize);
        return Header.OK(boardDtoList, pagination);
    }


    @Transactional
    public Integer getBoardCount() {
        return (int) boardRepository.count();
    }

    private Pagination createPagination(int totalListCnt, int pageSize) {
        return new Pagination(totalListCnt, pageSize);
    }
}

package com.security_board.board.dto;

import java.time.LocalDateTime;

import com.security_board.board.domain.Board;

import lombok.*;

//DTO : 데이터 전달 목적
//데이터를 캡슐화한 데이터 전달 객체

@Getter
@Setter
@ToString // 객체가 가지고 있는 정보나 값들을 문자열로 만들어 리턴하는 메서드
@NoArgsConstructor // 인자 없이 객체 생성 가능
public class BoardDto {
	private Long id;
	private String title;
	private String writer;
	private String content;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;

	public static BoardDto fromEntity(Board board) {
		return BoardDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.writer(board.getWriter())
				.content(board.getContent())
				.build();
	}

	public Board toEntity() {
		Board board = Board.builder()
				.id(id)
				.title(title)
				.writer(writer)
				.content(content)
				.build();
		return board;
	}

	@Builder
	public BoardDto(Long id, String title, String writer, String content, LocalDateTime createDate,
			LocalDateTime modifiedDate) {
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.content = content;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
	}
}

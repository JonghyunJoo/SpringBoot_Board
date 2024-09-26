package com.security_board.board.domain;

import org.springframework.util.Assert;
import jakarta.persistence.*;
import lombok.*;

//Board : 실제 DB와 매핑될 클래스 (Entity Class)

// JPA에서는 프록시 생성을 위해 기본 생성자를 반드시 하나 생성해야 한다.
// 생성자 자동 생성 : NoArgsConstructor, AllArgsConstructor
// NoArgsConstructor : 객체 생성 시 초기 인자 없이 객체를 생성할 수 있다.

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name= "board")
public class Board extends Time{

	@Id  //PK Field
	@GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성규칙
	private Long id;
	
	@Column(length = 100, nullable = false)
	private String title;
	
	@Column(length = 10, nullable = false)
	private String writer;
	
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;
	
	@Builder
	public Board(Long id, String title, String writer, String content) {
		
		//Assert 구문으로 예외사항 체크
		Assert.hasText(title, "제목은 필수 입력사항입니다");
		Assert.hasText(writer, "작성자는 필수 입력사항입니다");
		Assert.hasText(content, "내용은 필수 입력사항입니다");
		
		this.id = id;
		this.title = title;
		this.writer = writer;
		this.content = content;
	}
}

package com.security_board.board.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Search {
    private String type;  //search key
    private String keyword;  //search value
    private Integer page;
    private Integer size;
}

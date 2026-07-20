package com.honda.managerlmsmini.dto;

import java.util.List;
import lombok.*;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

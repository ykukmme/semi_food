package com.semi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredExportRequest {
    private List<Long> orderIds;
    private String searchTerm;
}

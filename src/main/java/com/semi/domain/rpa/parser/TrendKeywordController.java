package com.semi.domain.rpa.parser;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TrendKeywordController {

    private final TrendKeywordService trendKeywordService;

    @GetMapping("/api/keywords") // http://localhost:8080/api/keywords
    public List<TrendKeywordResponse.RankItem> keywordsFetch() {
            List<TrendKeywordResponse.RankItem> result = trendKeywordService.getNaverKeywords();
        return result;
    }

    @GetMapping("/api/keywords/saveWithSequentialId") // http://localhost:8080/api/keywords/saveWithSequentialId
    public List<TrendKeyword> saveWithSequentialId() {
            List<TrendKeywordResponse.RankItem> trendList = trendKeywordService.getNaverKeywords();
            List<TrendKeyword> result = trendKeywordService.saveWithSequentialId(trendList);

        return result;
    }
}

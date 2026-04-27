package com.semi.domain.rpa.parser;

import com.semi.domain.rpa.parser.response.TrendKeywordResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TrendKeywordController {

    private final TrendKeywordService trendKeywordService;
// http://localhost:8080/api/keywords
    @GetMapping("/api/keywords")
    public List<TrendKeywordResponse.RankItem> fetch() {
            List<TrendKeywordResponse.RankItem> result = trendKeywordService.getNaverKeywords();
        return result;
    }
}

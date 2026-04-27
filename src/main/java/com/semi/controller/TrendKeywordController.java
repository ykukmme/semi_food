package com.semi.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/trend")
@RequiredArgsConstructor
public class TrendKeywordController {

    private final TrendKeywordService trendKeywordService;

    @GetMapping("/keywords")    
    public String getTrendKeywords(Model model){
        List<TrendKeyword> keywords = trendKeywordService.getKeywords();
        // ID 필드를 기준으로 비교하는 규칙(Comparator) 생성
        Comparator<TrendKeyword> idComparator = Comparator.comparing(TrendKeyword::getId);
        model.addAttribute("idComparator", idComparator);
        model.addAttribute("keywords", keywords);
        return "dashboard";
    }
}

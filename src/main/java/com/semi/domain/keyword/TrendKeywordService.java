package com.semi.domain.keyword;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrendKeywordService {

    private final TrendKeywordRepository trendKeywordRepository;

    @Transactional
    public List<TrendKeyword> getKeywords(){
        return trendKeywordRepository.findByIsActiveTrueOrderByRankAsc();
    }
    
}

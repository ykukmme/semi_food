package com.semi.domain.keyword;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
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

    @Transactional(readOnly = true)
    public List<TrendKeyword> getKeywordsOrderById() {
        return trendKeywordRepository.findByIsActiveTrueOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public List<TrendKeyword> getKeywordsCollectedOnOrderById(LocalDate collectedDate) {
        return trendKeywordRepository.findTop20KeywordsCollectedBetweenOrderById(
                collectedDate.atStartOfDay(),
                collectedDate.plusDays(1).atStartOfDay()
        );
    }

    /** DB에 적재된 가장 최근 수집일자의 키워드 Top20 (없으면 빈 리스트) */
    @Transactional(readOnly = true)
    public List<TrendKeyword> getKeywordsCollectedOnLatestDate() {
        LocalDateTime latest = trendKeywordRepository.findMaxCollectedAt();
        if (latest == null) {
            return Collections.emptyList();
        }
        return getKeywordsCollectedOnOrderById(latest.toLocalDate());
    }

}

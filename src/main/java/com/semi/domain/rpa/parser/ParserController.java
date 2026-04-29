package com.semi.domain.rpa.parser;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
시퀀스{
    1. 트랜드 데이터를 저장 함. size = 20
    2. DB에 저장된 드랜드 데이터가져오거나, List에 따로 저장해뒀다가 바로 제품+공급자를 받아오기.
        기존에는 DB를 그대로 유지시키기 위해 넣지 않고 있었으나, 파싱을 원활하게 하려면 필수 같음.
    3. 트랜드 순위에 따른 제품 및 공급자를 저장함 size = 10
        각 트랜드(20개)마다 약 10개씩 반복 하면서 저장.
    }
*/

@RestController
@RequiredArgsConstructor
public class ParserController {
    // TrendKeyord
    private final TrendKeywordService trendKeywordService;
    
    @GetMapping("/api/TrandKeywords") // http://localhost:8080/api/keywords
    public List<TrendKeywordResponse.TrendKeywordItem> keywordsFetch() {
            List<TrendKeywordResponse.TrendKeywordItem> result = trendKeywordService.getNaverKeywords();
        return result;
    }

    @GetMapping("/api/TrandKeywords/saveWithSequentialId") // http://localhost:8080/api/keywords/saveWithSequentialId
    public List<TrendKeyword> saveWithSequentialId() {
            List<TrendKeywordResponse.TrendKeywordItem> trendList = trendKeywordService.getNaverKeywords();
            List<TrendKeyword> result = trendKeywordService.saveWithSequentialId(trendList);

        return result;
    }



    // Supplier


    // Product



    // jsop 테스트 코드
    private final JsoupTestService jsoupTestService;

    /**
     * 엔드포인트: http://localhost:8080/api/resolve-url?linkUrl=https://ader.naver.com/v1/Nkes2ua21RrfgCrAkVdOmlIaZxdQYluqoX2QqxIFALimCP7508fx8LfDlPtd0XGBWFtFBd-kWt0RnZjphtrLcYlHS1VLvwME1SdniesspcQcAgE6RL7Ufmaqb05Ui_kq7bDtG9QdwgQb_nADLqtjk0CeOQYMRdJ_c9zUDoSn3HqVS9HlX_X6M29-M6nqb-wGPNI51AU6vhAwB1X6S7lNchwnD11h95qC4f5FRYowlnyUQuBUU00cBHVaEyUGWjmwe6gqzJBjVv7eC0ZUJ6Bi5kKlJnNR93ax6ES-msolNhM_njomLgbiKuhZMhpXUANJ?c=m.nplusstore.best.keyword.popular&NaPm=1
     */
    @GetMapping("/api/resolve-url")
    public String resolveUrl(@RequestParam String linkUrl) {
        // 서비스 호출 후 최종 URL 반환
        return jsoupTestService.getFinalDestinationUrl(linkUrl);
    }

        /**
     * 엔드포인트: http://localhost:8080/api/saveWithSequentialId_resolve_url?linkUrl=https://ader.naver.com/v1/Nkes2ua21RrfgCrAkVdOmlIaZxdQYluqoX2QqxIFALimCP7508fx8LfDlPtd0XGBWFtFBd-kWt0RnZjphtrLcYlHS1VLvwME1SdniesspcQcAgE6RL7Ufmaqb05Ui_kq7bDtG9QdwgQb_nADLqtjk0CeOQYMRdJ_c9zUDoSn3HqVS9HlX_X6M29-M6nqb-wGPNI51AU6vhAwB1X6S7lNchwnD11h95qC4f5FRYowlnyUQuBUU00cBHVaEyUGWjmwe6gqzJBjVv7eC0ZUJ6Bi5kKlJnNR93ax6ES-msolNhM_njomLgbiKuhZMhpXUANJ?c=m.nplusstore.best.keyword.popular&NaPm=1
     */
    @GetMapping("/api/saveWithSequentialId_resolve_url")
    public Map<String, Object> saveWithSequentialId_resolve_url(@RequestParam String linkUrl) {
        // 서비스 호출 후 최종 URL 반환
        Map<String, Object> result = jsoupTestService.getAllAvailableInfo(linkUrl);
        return result;
    }


        /**
     * 엔드포인트: http://localhost:8080/api/getJsonFromHtml?linkUrl=https://snxbest.naver.com/keyword/best?categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY&ageType=ALL
     */
    @GetMapping("/api/getJsonFromHtml")
    public String getJsonFromHtml(@RequestParam String linkUrl) {
        // 서비스 호출 후 최종 URL 반환
        String result = jsoupTestService.getJsonFromHtml(linkUrl);
        return result;
    }



}

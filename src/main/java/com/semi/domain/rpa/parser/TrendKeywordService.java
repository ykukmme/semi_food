package com.semi.domain.rpa.parser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse.RankItem;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

@Service
public class TrendKeywordService {

    private final RestClient restClient = RestClient.create();

    public List<TrendKeywordResponse.RankItem> getNaverKeywords() {
        String targetSiteUrl= "https://snxbest.naver.com/keyword/best?categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY&ageType=ALL&activeRankId=2165824835&syncDate=20260423" ;
        String dataUrl = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank?ageType=ALL&categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY" ;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36" ;
        
        String rawJson = restClient.get()
            .uri(dataUrl)
            .header("User-Agent", userAgent)
            .header("Accept", "application/json")
            .retrieve()
            .body(String.class);

    System.out.println("네이버 응답 원문: " + rawJson);
        
        List<TrendKeywordResponse.RankItem> response = restClient.get()
                .uri(dataUrl)
                // 핵심: JSON과 XML을 모두 수용한다고 헤더를 설정합니다.
                // 브라우저인 것처럼 속이기 위해 User-Agent 추가 (가장 중요)
                .header("User-Agent", userAgent)
                // Accept 헤더 설정
                .header("Accept", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)
                // 추가적인 탐지 방지를 위해 언어 및 참조 정보 추가 (선택사항이지만 권장)
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Referer", targetSiteUrl)
                // .header("Referer", "https://snxbest.naver.com/")
                // .header("Referer", "https://naver.com")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TrendKeywordResponse.RankItem>>() {});  // 여기서 Jackson MessageConverter가 자동 동작함
                // .body(TrendKeywordResponse.class); // 여기서 Jackson MessageConverter가 자동 동작함

                    System.out.println("수신된 데이터: " + response);
                    return response;
    }
}

// user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
// options.add_argument(f'user-agent={user_agent}')
package com.semi.domain.rpa.parser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordRepository;
import com.semi.domain.rpa.parser.mapper.TrendKeywordMapper;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class TrendKeywordService {

    private final RestClient restClient = RestClient.create();
    private final TrendKeywordRepository repository;
    private final TrendKeywordMapper trendKeywordMapper; // 매퍼 주입

    public List<TrendKeywordResponse.TrendKeywordItem> getNaverKeywords() {

        // api url 의 경우 RankId와 syncDate 를 조합해서 제품 url api에서 처리가 간헐적으로 가능
        // [ ]TODO 기존 로직을 일부 수정하기
        // [x]TODO TrendKeyword에 RankId와 syncDate를 추가
        // [ ]TODO 기존 TrendKeywordService 클래스의 내용도 SupplierAndProductService 처럼 수정하기
        
        String targetSiteUrl= "https://snxbest.naver.com/keyword/best?categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY&ageType=ALL&activeRankId=2165824835&syncDate=20260423" ;
        String dataUrl = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank?ageType=ALL&categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY" ;

        String rawJson = restClient.get()
            .uri(dataUrl)
            .header("User-Agent", Constants.Http.USER_AGENT)
            .header("Accept", Constants.Http.CONTENT_TYPE_JSON)
            .retrieve()
            .body(String.class);

        System.out.println("네이버 응답 원문: " + rawJson);
        
        // data가 json/xml 둘 다 올 수 있기 때문에, Jackson이 자동으로 파싱하도록 설정
        List<TrendKeywordResponse.TrendKeywordItem> response = restClient.get()
                .uri(dataUrl)
                .header("User-Agent", Constants.Http.USER_AGENT)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)
                .header("Accept-Language", Constants.Http.ACCEPT_LANGUAGE)
                .header("Referer", targetSiteUrl)
                // .header("Referer", "https://snxbest.naver.com/")
                // .header("Referer", "https://naver.com")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TrendKeywordResponse.TrendKeywordItem>>() {});  // 여기서 Jackson MessageConverter가 자동 동작함
                // .body(TrendKeywordResponse.class); // 여기서 Jackson MessageConverter가 자동 동작함

                    System.out.println("수신된 데이터: " + response);
                    return response;
    }

@Transactional
public List<TrendKeyword> saveWithSequentialId(List<TrendKeywordResponse.TrendKeywordItem> items) {
    
    // 1. 현재 DB에서 가장 큰 ID 가져오기 (데이터 없으면 0으로 시작)
    Long maxId = repository.findMaxId();
    long nextId = (maxId == null) ? 0L : maxId;
    TrendKeyword lastRecord = repository.findFirstByOrderByIdDesc();
    // 2. DTO -> VO 변환 (MapStruct 사용)
    List<TrendKeyword> newVOList = new ArrayList<>();
    List<TrendKeyword> parsedVOList = trendKeywordMapper.toVoList(items); // 파싱된 데이터

    // 파싱한 데이터가 기존 데이터 보다 최신이 아니라면, tempStr List 반환
if ( parsedVOList.get(0).getCollectedAt().isAfter(lastRecord.getCollectedAt())){
    String tempStr = "추가로 저장된 값이 없습니다. 추가 Data CollectedAt:" + parsedVOList.get(0).getCollectedAt()+", 기존 Data CollectedAt:" + lastRecord.getCollectedAt() ;
    newVOList.add(new TrendKeyword(0L, tempStr, 0, 0, LocalDateTime.now(), false, null, null)); ;
    return newVOList ;            
}
// if ( repository.findFirstByOrderByIdDesc().getCollectedAt().isAfter(parsedVOList.get(0).getCollectedAt())){ }
    // 3. 중복 확인 후 새 ID 부여하여 리스트 구성
    
    for (TrendKeyword parsedVO : parsedVOList) {
        // 이미 저장된 날짜와 키워드인지 확인
        if (!repository.existsByCollectedAtAndKeyword(parsedVO.getCollectedAt(), parsedVO.getKeyword())) {
            continue;
        }
        // 수동으로 ID를 부여하기 위해 새로운 객체 생성 (기존 필드는 유지)
        // @Builder
        TrendKeyword tempVO = TrendKeyword.builder()
                .id(++nextId) // 1 증가시킨 값을 ID로 부여
                .keyword(parsedVO.getKeyword())
                .rank(parsedVO.getRank())
                .frequency(0)
                .collectedAt(parsedVO.getCollectedAt())
                .isActive(true)
                .build();
        newVOList.add(tempVO);

    }
    // 4. 최종 저장
    if (!newVOList.isEmpty()) {
        repository.saveAll(newVOList);
        System.out.println(newVOList.size() + "건 저장 완료 (마지막 ID: " + nextId + ")");
    }
    return newVOList;
}


    @Transactional
    public List<TrendKeyword> saveTrendKeywords(List<TrendKeywordResponse.TrendKeywordItem> items) {
        // 매퍼로 리스트 전체 변환
        List<TrendKeyword> voList = trendKeywordMapper.toVoList(items);
        repository.saveAll(voList);
        return voList;
    }
}

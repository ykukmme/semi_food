package com.semi.domain.rpa.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// [ ]TODO 아래와 같은 형식으로 클래서를 나누고, ParserConstants 내부에서 Http, SNXBEST, DB 같은 식으로 클래스를 나누고, 위치를 파서 패키지 쪽으로 옮길 것

// 상속과 인스턴스화를 방지하기 위해 final 클래스와 private 생성자 사용
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Http {
    /*
    예시
    .header("User-Agent", Constants.Http.USER_AGENT)
    .header("Accept", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)
    .header("Accept-Language", Constants.Http.ACCEPT_LANGUAGE)
    .header("Referer", targetSiteUrl)
    .header("Accept-Language",  "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7") 
    */
    public static final String HEADER_AUTH = "X-Parser-Auth";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String ACCEPT_LANGUAGE = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";

    }

    public static final class Db{
        public static final String UNKNOWN = "unknown";
    }

    public static final class Snxbest {
        // 네이버에서 RankId, syncDate 를 모두 문자열로 처리하고 있음. 

        // 크롤링 대상 페이지 , api를 받기전 Referer로 사용됨, 변수를 넣을 수는 있으나, 계정정보가 있거나 일부 값들은 무시당함.
        public static final String TARGET_SITE_URL_TEMPLATE = "https://snxbest.naver.com/keyword/best?categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY&ageType=ALL&activeRankId=%d&syncDate=%s"; //categoryId= 50000006 = 식품, %d = RankId , %s = syncDate 
        
        // 랭킹 데이터 API, categoryId는 식품고정
        public static final String API_V1_FOOD_RANK_DATA_URL_DAILY = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank?ageType=ALL&categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY"; 
        
        // 공급자/상품 데이터 API 랭킹, 동기데이터(ymd,syncDate) 둘 다 필요
        public static final String API_V1_FOOD_PRODUCT_DATA_URL_TEMPLATE = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank/%d?showAd=true&channel=m.nplusstore.best.keyword.popular&stmsId=100410832&areaCode=bkeypop&ymd=%s"; //  %d = ranking_id = 2177000359, %s = ymd = syncDate
    }

}

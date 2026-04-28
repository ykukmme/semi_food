package com.semi.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// [ ]TODO 아래와 같은 형식으로 클래서를 나누고, ParserConstants 내부에서 Http, SNXBEST, DB 같은 식으로 클래스를 나누고, 위치를 파서 패키지 쪽으로 옮길 것
// public static final class Http {
//     public static final String HEADER_AUTH = "X-Parser-Auth";
// }

// 상속과 인스턴스화를 방지하기 위해 final 클래스와 private 생성자 사용
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParserHttpConstants {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String SAP_UNKNOWN_SUPPLIER_NAME = "알 수 없음";
    public static final String SAP_UNKNOWN_SUPPLIER_URL = "알 수 없음";

    // 네이버에서 RankId, syncDate 를 모두 문자열로 처리하고 있음. 
    public static final String SNXBEST_TARGET_SITE_URL_TEMPLATE = "https://snxbest.naver.com/keyword/best?categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY&ageType=ALL&activeRankId=%d&syncDate=%s"; //categoryId= 50000006 = 식품, %d = RankId , %s = syncDate 
    public static final String SNXBEST_API_V1_FOOD_RANK_DATA_URL_DAILY = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank?ageType=ALL&categoryId=50000006&sortType=KEYWORD_POPULAR&periodType=DAILY"; 
    public static final String SNXBEST_API_V1_FOOD_PRODUCT_DATA_URL_TEMPLATE = "https://snxbest.naver.com/api/v1/snxbest/keyword/rank/%d?showAd=true&channel=m.nplusstore.best.keyword.popular&stmsId=100410832&areaCode=bkeypop&ymd=%s"; // %d = 2177000359, %s = ymd = syncDate

}

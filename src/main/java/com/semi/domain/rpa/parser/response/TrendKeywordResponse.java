package com.semi.domain.rpa.parser.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 멤버 변수명을 외부데이터 기준으로 작성 (예: title, rank, rankId, syncDate 등) -> 매퍼에서 TrendKeyword의 필드명과 매핑해주는 방식으로 처리
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrendKeywordResponse {

    @JsonProperty("rankList")
    @JacksonXmlProperty(localName = "rankList")
    @JacksonXmlElementWrapper(useWrapping = false) 
    private List<TrendKeywordItem> rankList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrendKeywordItem {
        
        @JsonProperty("rank")
        @JacksonXmlProperty(localName = "rank")
        private int rank;

        @JsonProperty("status")
        @JacksonXmlProperty(localName = "status")
        private String status; // "UP", "DOWN", "NEW" 등

        @JsonProperty("rankFluctuation")
        @JacksonXmlProperty(localName = "rankFluctuation")
        private int rankFluctuation; // 변화 수치

        @JsonProperty("title")
        @JacksonXmlProperty(localName = "title")
        private String title; // 실제 키워드 (예: 버터떡)

        @JsonProperty("subTitle")
        @JacksonXmlProperty(localName = "subTitle")
        private String subTitle;

        @JsonProperty("rankId")
        @JacksonXmlProperty(localName = "rankId")
        private Long rankId;

        @JsonProperty("syncDate")
        @JacksonXmlProperty(localName = "syncDate")
        // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        private String syncDate;

        @JsonProperty("catNm1")
        @JacksonXmlProperty(localName = "catNm1")
        private String catNm1; // 카테고리1 (식품)

        @JsonProperty("catNm2")
        @JacksonXmlProperty(localName = "catNm2")
        private String catNm2; // 카테고리2 (떡류)

        @JsonProperty("catNm3")
        @JacksonXmlProperty(localName = "catNm3")
        private String catNm3; // 카테고리3 (기타떡)
        
        @JsonProperty("categoryId")
        @JacksonXmlProperty(localName = "categoryId")
        private String categoryId;
        
    }


}
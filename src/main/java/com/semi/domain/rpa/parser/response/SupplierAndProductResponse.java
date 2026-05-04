package com.semi.domain.rpa.parser.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//하나의 응답에 제품과 공급자가 모두 들어있음으로 동시에 처리, 단 공급자ID때문에 공급자를 먼저 처리

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "KeywordRankDetailResult") // XML 최상위 요소명과 매핑, JSON에서는 무시됨
// @JsonRootName("") // JSON 최상위 요소명과 매핑, XML에서는 무시됨
public class SupplierAndProductResponse {

    @JsonProperty("products")
    @JacksonXmlProperty(localName = "products")
    @JacksonXmlElementWrapper(localName = "products")
    private List<ProductItem> productList;

    @JsonProperty("query")
    @JacksonXmlProperty(localName = "query")
    private String query;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class SupplierItem {
        // 공급자
        @JsonProperty("mallNm")
        @JacksonXmlProperty(localName = "mallNm")
        private String mallNm; // 공급자
        
        @JsonProperty("mallLinkUrl")
        @JacksonXmlProperty(localName = "mallLinkUrl")
        private String mallLinkUrl; // 공급자 url, smartstore는 파라메터가 없을시 무조건 챕챠를 띄움
        
        @JsonProperty("syncDate")
        @JacksonXmlProperty(localName = "syncDate")
        private String syncDate; // 수집 날짜 (예: 20260427)
    } 


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductItem {

        // 제품
        @JsonProperty("rank")
        @JacksonXmlProperty(localName = "rank")
        private int rank;

        @JsonProperty("title")
        @JacksonXmlProperty(localName = "title")
        private String title; // 실제 키워드 (예: 버터떡)

        @JsonProperty("imageUrl")
        @JacksonXmlProperty(localName = "imageUrl")
        private String imageUrl;

        @JsonProperty("linkUrl")
        @JacksonXmlProperty(localName = "linkUrl")
        private String linkUrl;

        @JsonProperty("mallNm")
        @JacksonXmlProperty(localName = "mallNm")
        private String mallNm; // 공급자

        @JsonProperty("mallLinkUrl")
        @JacksonXmlProperty(localName = "mallLinkUrl")
        private String mallLinkUrl; // 공급자 url

        @JsonProperty("priceValue")
        @JacksonXmlProperty(localName = "priceValue")
        private Integer priceValue; // 제품가격

        @JsonProperty("syncDate")
        @JacksonXmlProperty(localName = "syncDate")
        private String syncDate; // 수집 날짜 (예: 20260427)

    }


}

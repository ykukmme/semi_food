package com.semi.domain.rpa.parser.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import jakarta.persistence.Column;
import lombok.Data;
import java.util.List;



@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    @JsonProperty("productList")
    @JacksonXmlProperty(localName = "productList")
    @JacksonXmlElementWrapper(useWrapping = false) 
    private List<ProductItem> productList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductItem {
        
        @JsonProperty("rank")
        @JacksonXmlProperty(localName = "rank")
        private int rank;

        @JsonProperty("productId")
        @JacksonXmlProperty(localName = "productId")
        private String productId; // "UP", "DOWN", "NEW" 등

        @JsonProperty("title")
        @JacksonXmlProperty(localName = "title")
        private String title; // 실제 키워드 (예: 버터떡)

        @JsonProperty("imageUrl")
        @JacksonXmlProperty(localName = "imageUrl")
        private String imageUrl;

        @JsonProperty("linkUrl")
        @JacksonXmlProperty(localName = "linkUrl")
        private String linkUrl;

        @JsonProperty("priceValue")
        @JacksonXmlProperty(localName = "priceValue")
        private Integer priceValue; // 제품가격

        @JsonProperty("syncDate")
        @JacksonXmlProperty(localName = "syncDate")
        private LocalDateTime syncDate; 

        @JsonProperty("mallNm")
        @JacksonXmlProperty(localName = "mallNm")
        private String mallNm; // 공급자
        
        @JsonProperty("mallLinkUrl")
        @JacksonXmlProperty(localName = "mallLinkUrl")
        private String mallLinkUrl; // 공급자 url, smartstore는 파라메터가 없을시 무조건 챕챠를 띄움
        
    }


}
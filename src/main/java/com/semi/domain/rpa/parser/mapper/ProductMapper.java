package com.semi.domain.rpa.parser.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.semi.domain.product.Product;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse.ProductItem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;



// []TODO Jsoup 관련 테스트부분 파일들 제거하기, 아마도 남길만한 파일은 없는 것으로 추정됨.
    // []TODO gradle에서 jsoup 관련 의존성 제거하기
    // []TODO ParserController에서 jsoup 테스트 부분 제거하기
    // []TODO JsoupTestService 제거하기


// [x]TODO 기존 트랜드 부분도 이름에  Parser 추가하기 -> ParserController로 통합

// [x]TODO !주의 파싱해야 할 데이 제품데이터 안에 Supplier도 같이 들어 있어있음
    // [x]TODO ProductParserMapper 완성하기
    // [x]TODO SupplierParserMapper 완성하기

// [x]TODO ProductParserResponse 완성하기
// [x]TODO SupplierParserResponse 완성하기

// [ ]TODO ParserControler 완성하기
    // [x]TODO trendKeyword Service 완성하기
    // [ ]TODO product Service 완성하기
    // [ ]TODO supplier Service 완성하기

// [x]TODO ParserRepository 완성하기
    // [x]TODO trendKeyword Repository 완성하기
    // [x]TODO 1차 product Repository 완성하기
    // [x]TODO 1차 supplier Repository 완성하기


@Mapper(componentModel = "spring" , imports = {LocalDate.class, DateTimeFormatter.class}) //스프링 빈으로 등록 , 클래스 임포트 추가) 
public interface ProductMapper {

    // 이름이 다른 필드들을 서로 연결해주는 설정 타겟= Product
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keyword", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(source = "title", target = "name")      
    @Mapping(target = "description", ignore = true)
    @Mapping(source = "priceValue", target = "price")          
    @Mapping(source = "imageUrl", target = "imageUrl")          
    @Mapping(source = "linkUrl", target = "productUrl")           
    @Mapping(target = "autoOrder", ignore = true)

    // 날짜로 변환 후 시분초0000 추가
    @Mapping(expression = "java(LocalDate.parse(item.getSyncDate(), DateTimeFormatter.ofPattern(\"yyyyMMdd\")).atStartOfDay())", target = "crawledAt") 
    // @Mapping(source = "isActive", target = "isActive") 

    // 리스트 변환도 메서드 한 줄 선언으로 해결 (toVo를 내부적으로 반복 호출함)
    Product toVo(ProductItem item);
    List<Product> toVoList(List<ProductItem> items);
}

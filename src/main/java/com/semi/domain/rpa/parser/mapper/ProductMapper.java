package com.semi.domain.rpa.parser.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.semi.domain.product.Product;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse.ProductItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;


@Mapper(componentModel = "spring" , imports = {LocalDate.class, DateTimeFormatter.class, LocalDateTime.class}) //스프링 빈으로 등록 , 클래스 임포트 추가) 
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

    @Mapping(target = "crawledAt" , expression = "java(LocalDateTime.now())")  
    @Mapping(target = "syncDate", expression = "java(LocalDate.parse(item.getSyncDate(), DateTimeFormatter.ofPattern(\"yyyyMMdd\")).atStartOfDay())") 
    // @Mapping(source = "isActive", target = "isActive") 

    // 리스트 변환도 메서드 한 줄 선언으로 해결 (toVo를 내부적으로 반복 호출함)
    Product toVo(ProductItem item);
    List<Product> toVoList(List<ProductItem> items);
}

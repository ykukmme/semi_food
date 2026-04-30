package com.semi.domain.rpa.parser.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.semi.domain.rpa.parser.response.SupplierAndProductResponse.SupplierItem;
import com.semi.domain.supplier.Supplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;


@Mapper(componentModel = "spring" , imports = {LocalDate.class, DateTimeFormatter.class, LocalDateTime.class}) //스프링 빈으로 등록 , 클래스 임포트 추가) 
public interface SupplierMapper {
    // 이름이 다른 필드들을 서로 연결해주는 설정 타겟= Supplier

    // 광고가 아니면 mall(공급자)관련 필드가 누락됨.
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "mallNm", target = "name",  defaultValue = "알 수 없음")
    @Mapping(source = "mallLinkUrl", target = "url",  defaultValue = "알 수 없음")       

    @Mapping( target = "createdAt" , expression = "java(LocalDateTime.now())" )
    // @Mapping(source = "isActive", target = "isActive") 

    // 리스트 변환도 메서드 한 줄 선언으로 해결 (toVo를 내부적으로 반복 호출함)
    Supplier toVo(SupplierItem item);
    List<Supplier> toVoList(List<SupplierItem> items);
}

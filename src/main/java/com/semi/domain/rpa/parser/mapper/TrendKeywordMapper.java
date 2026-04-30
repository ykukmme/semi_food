package com.semi.domain.rpa.parser.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse.TrendKeywordItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

// syncDate의 경우 naver의 내부데이터 인지, 날짜정보가 불규칙함. 단순 오늘 날짜로 대체 해야 할 수 있음

@Mapper(componentModel = "spring" , imports = {LocalDate.class, DateTimeFormatter.class, LocalDateTime.class}) //스프링 빈으로 등록 , 클래스 임포트 추가) 
public interface TrendKeywordMapper {

    // 이름이 다른 필드들을 서로 연결해주는 설정
    @Mapping(source = "title", target = "keyword")      
    @Mapping(source = "rank", target = "rank")          
    // @Mapping(source = "frequency", target = "frequency") 

    @Mapping(source = "rankId", target = "rankingId")

    @Mapping(target = "collectedAt", expression = "java(LocalDateTime.now())")  
    // @Mapping(target = "syncDate", expression = "java(item.getSyncDate() != null ? item.getSyncDate().toLocalDate().atStartOfDay() : null)")
    @Mapping(target = "syncDate", expression = "java(LocalDate.parse(item.getSyncDate(), DateTimeFormatter.ofPattern(\"yyyyMMdd\")).atStartOfDay())") 
    
    // @Mapping(source = "isActive", target = "isActive") 

    // 리스트 변환도 메서드 한 줄 선언으로 해결 (toVo를 내부적으로 반복 호출함)
    TrendKeyword toVo(TrendKeywordItem item);
    List<TrendKeyword> toVoList(List<TrendKeywordItem> items);
}

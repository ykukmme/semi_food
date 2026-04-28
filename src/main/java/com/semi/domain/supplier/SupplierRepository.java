package com.semi.domain.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.semi.domain.keyword.TrendKeyword;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /** 이름으로 조회 (RPA 크롤링 시 중복 생성 방지용) */
    Optional<Supplier> findByName(String name);

    // 서버에서 ID를 수동처리하기 위한 코드
    @Query("SELECT MAX(t.id) FROM Supplier t")
    Long findMaxId();
    // 서버에서 ID를 수동처리하기 위한 코드
    // 중복 체크용 (데이터가 이미 있는지 확인)
    boolean existsByCreatedAtAndName(LocalDateTime createdAt, String name);
    
    Supplier findFirstByOrderByIdDesc();

}

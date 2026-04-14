package com.semi.domain.supplier;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /** 이름으로 조회 (RPA 크롤링 시 중복 생성 방지용) */
    Optional<Supplier> findByName(String name);
}

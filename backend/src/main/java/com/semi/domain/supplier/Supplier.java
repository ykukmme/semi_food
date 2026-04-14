package com.semi.domain.supplier;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공급업체 엔티티
 * RPA 크롤링 시 판매자 정보로 자동 생성
 */
@Entity
@Table(name = "supplier")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;  // 구매처명 (발주서에 기재)

    @Column(length = 500)
    private String url;  // 구매처 홈 링크

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Supplier(String name, String url) {
        this.name      = name;
        this.url       = url;
        this.createdAt = LocalDateTime.now();
    }
}

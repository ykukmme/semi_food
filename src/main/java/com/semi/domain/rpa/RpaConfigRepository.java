package com.semi.domain.rpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpaConfigRepository extends JpaRepository<RpaConfig, Long> {
}

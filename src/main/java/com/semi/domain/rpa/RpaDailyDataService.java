package com.semi.domain.rpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordRepository;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import com.semi.domain.rpa.response.RpaDashboardDataResponse;
import com.semi.domain.rpa.response.RpaDailyDataSummary;
import com.semi.domain.rpa.response.RpaDailyDeleteResponse;
import com.semi.domain.rpa.response.RpaLogRow;
import com.semi.domain.rpa.response.RpaProductRow;
import com.semi.domain.rpa.response.RpaSupplierRow;
import com.semi.domain.rpa.response.RpaTrendKeywordRow;
import com.semi.domain.supplier.Supplier;
import com.semi.domain.supplier.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RpaDailyDataService {

    private final TrendKeywordRepository trendKeywordRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final RpaCrudSafetyService rpaCrudSafetyService;
    private final RpaLogRepository rpaLogRepository;

    @Transactional(readOnly = true)
    public RpaDailyDataSummary summarizeDailyData(LocalDate targetDate) {
        LocalDateTime start = targetDate.atStartOfDay();
        List<TrendKeyword> keywords = trendKeywordRepository.findAllByCollectedAtGreaterThanEqualOrderByCollectedAtDesc(start);
        List<Product> products = productRepository.findAllByCrawledAtGreaterThanEqualOrderByCrawledAtDesc(start);
        List<Supplier> suppliers = supplierRepository.findAllByCreatedAtGreaterThanEqualOrderByCreatedAtDesc(start);
        List<TrendKeyword> deletableKeywords = trendKeywordRepository.findRpaDeletableKeywords(start);
        List<Product> deletableProducts = productRepository.findRpaDeletableProducts(start);
        List<Supplier> deletableSuppliers = supplierRepository.findRpaDeletableSuppliers(start);

        return new RpaDailyDataSummary(
            targetDate,
            keywords.size(),
            products.size(),
            suppliers.size(),
            deletableKeywords.size(),
            deletableProducts.size(),
            deletableSuppliers.size(),
            rpaCrudSafetyService.isRpaRunning(),
            "당일 RPA 데이터 요약"
        );
    }

    @Transactional(readOnly = true)
    public RpaDashboardDataResponse getDailyDashboardData(LocalDate targetDate) {
        LocalDateTime start = targetDate.atStartOfDay();
        RpaDailyDataSummary summary = summarizeDailyData(targetDate);
        List<RpaTrendKeywordRow> keywordRows = trendKeywordRepository.findAllByCollectedAtGreaterThanEqualOrderByCollectedAtDesc(start)
            .stream()
            .map(keyword -> new RpaTrendKeywordRow(
                keyword.getId(),
                keyword.getKeyword(),
                keyword.getRank(),
                keyword.getFrequency(),
                keyword.getCollectedAt(),
                keyword.getIsActive(),
                keyword.getRankingId(),
                keyword.getSyncDate()
            ))
            .toList();
        List<RpaSupplierRow> supplierRows = supplierRepository.findAllByCreatedAtGreaterThanEqualOrderByCreatedAtDesc(start)
            .stream()
            .map(supplier -> new RpaSupplierRow(
                supplier.getId(),
                supplier.getName(),
                supplier.getUrl(),
                supplier.getCreatedAt()
            ))
            .toList();
        List<RpaProductRow> productRows = productRepository.findAllByCrawledAtGreaterThanEqualOrderByCrawledAtDesc(start)
            .stream()
            .map(product -> new RpaProductRow(
                product.getId(),
                product.getKeyword().getId(),
                product.getKeyword().getKeyword(),
                product.getSupplier().getId(),
                product.getSupplier().getName(),
                product.getName(),
                product.getPrice(),
                product.getAutoOrder(),
                product.getCrawledAt(),
                product.getSyncDate()
            ))
            .toList();
        List<RpaLogRow> logRows = rpaLogRepository.findAllByStartedAtGreaterThanEqualOrderByStartedAtDesc(start)
            .stream()
            .map(log -> new RpaLogRow(
                log.getId(),
                log.getStatus().name(),
                log.getStartedAt(),
                log.getEndedAt(),
                log.getKeywordCount(),
                log.getProductCount(),
                log.getMessage()
            ))
            .toList();

        return new RpaDashboardDataResponse(
            targetDate,
            summary,
            keywordRows,
            supplierRows,
            productRows,
            logRows
        );
    }

    @Transactional
    public RpaDailyDeleteResponse deleteDailyData(LocalDate targetDate) {
        rpaCrudSafetyService.assertRpaNotRunning();

        LocalDateTime start = targetDate.atStartOfDay();
        List<Product> deletableProducts = productRepository.findRpaDeletableProducts(start);
        productRepository.deleteAllInBatch(deletableProducts);

        List<Supplier> deletableSuppliers = supplierRepository.findRpaDeletableSuppliers(start);
        supplierRepository.deleteAllInBatch(deletableSuppliers);

        List<TrendKeyword> deletableKeywords = trendKeywordRepository.findRpaDeletableKeywords(start);
        trendKeywordRepository.deleteAllInBatch(deletableKeywords);

        return new RpaDailyDeleteResponse(
            targetDate,
            deletableProducts.size(),
            deletableSuppliers.size(),
            deletableKeywords.size(),
            "RPA 당일 데이터 삭제 완료"
        );
    }
}

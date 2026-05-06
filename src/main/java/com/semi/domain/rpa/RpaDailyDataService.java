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
import com.semi.domain.rpa.response.RpaViewDeleteResponse;
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
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        List<TrendKeyword> keywords = trendKeywordRepository.findAllByCollectedAtGreaterThanEqualAndCollectedAtLessThanOrderByCollectedAtDesc(start, end);
        List<Product> products = productRepository.findAllByCrawledAtGreaterThanEqualAndCrawledAtLessThanOrderByCrawledAtDesc(start, end);
        List<Supplier> suppliers = supplierRepository.findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(start, end);
        List<TrendKeyword> deletableKeywords = trendKeywordRepository.findRpaDeletableKeywords(start, end);
        List<Product> deletableProducts = productRepository.findRpaDeletableProducts(start, end);
        List<Supplier> deletableSuppliers = supplierRepository.findRpaDeletableSuppliers(start, end);

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
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        RpaDailyDataSummary summary = summarizeDailyData(targetDate);
        List<RpaTrendKeywordRow> keywordRows = trendKeywordRepository.findAllByCollectedAtGreaterThanEqualAndCollectedAtLessThanOrderByCollectedAtDesc(start, end)
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
        List<RpaSupplierRow> supplierRows = supplierRepository.findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(start, end)
            .stream()
            .map(supplier -> new RpaSupplierRow(
                supplier.getId(),
                supplier.getName(),
                supplier.getUrl(),
                supplier.getCreatedAt()
            ))
            .toList();
        List<RpaProductRow> productRows = productRepository.findAllByCrawledAtGreaterThanEqualAndCrawledAtLessThanOrderByCrawledAtDesc(start, end)
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
        List<RpaLogRow> logRows = rpaLogRepository.findAllByStartedAtGreaterThanEqualAndStartedAtLessThanOrderByStartedAtDesc(start, end)
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
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        List<Product> deletableProducts = productRepository.findRpaDeletableProducts(start, end);
        productRepository.deleteAllInBatch(deletableProducts);

        List<Supplier> deletableSuppliers = supplierRepository.findRpaDeletableSuppliers(start, end);
        supplierRepository.deleteAllInBatch(deletableSuppliers);

        List<TrendKeyword> deletableKeywords = trendKeywordRepository.findRpaDeletableKeywords(start, end);
        trendKeywordRepository.deleteAllInBatch(deletableKeywords);

        return new RpaDailyDeleteResponse(
            targetDate,
            deletableProducts.size(),
            deletableSuppliers.size(),
            deletableKeywords.size(),
            "RPA 당일 데이터 삭제 완료"
        );
    }

    @Transactional
    public RpaViewDeleteResponse deleteDailyViewData(LocalDate targetDate, String viewName) {
        rpaCrudSafetyService.assertRpaNotRunning();

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        String normalizedViewName = viewName == null ? "" : viewName.trim().toLowerCase();

        return switch (normalizedViewName) {
            case "product" -> deleteDailyProducts(targetDate, start, end);
            case "supplier" -> deleteDailySuppliers(targetDate, start, end);
            case "trend_keyword" -> deleteDailyTrendKeywords(targetDate, start, end);
            case "rpa_log" -> deleteDailyRpaLogs(targetDate, start, end);
            default -> throw new IllegalArgumentException("지원하지 않는 RPA view입니다. view=" + viewName);
        };
    }

    private RpaViewDeleteResponse deleteDailyProducts(LocalDate targetDate, LocalDateTime start, LocalDateTime end) {
        List<Product> deletableProducts = productRepository.findRpaDeletableProducts(start, end);
        productRepository.deleteAllInBatch(deletableProducts);
        return new RpaViewDeleteResponse(targetDate, "product", deletableProducts.size(), "상품 view 삭제 완료");
    }

    private RpaViewDeleteResponse deleteDailySuppliers(LocalDate targetDate, LocalDateTime start, LocalDateTime end) {
        List<Supplier> deletableSuppliers = supplierRepository.findRpaDeletableSuppliers(start, end);
        supplierRepository.deleteAllInBatch(deletableSuppliers);
        return new RpaViewDeleteResponse(targetDate, "supplier", deletableSuppliers.size(), "공급자 view 삭제 완료");
    }

    private RpaViewDeleteResponse deleteDailyTrendKeywords(LocalDate targetDate, LocalDateTime start, LocalDateTime end) {
        List<TrendKeyword> deletableKeywords = trendKeywordRepository.findRpaDeletableKeywords(start, end);
        trendKeywordRepository.deleteAllInBatch(deletableKeywords);
        return new RpaViewDeleteResponse(targetDate, "trend_keyword", deletableKeywords.size(), "트렌드 키워드 view 삭제 완료");
    }

    private RpaViewDeleteResponse deleteDailyRpaLogs(LocalDate targetDate, LocalDateTime start, LocalDateTime end) {
        List<RpaLog> deletableLogs = rpaLogRepository.findAllByStartedAtGreaterThanEqualAndStartedAtLessThanAndStatusNotOrderByStartedAtDesc(
            start,
            end,
            RpaStatus.RUNNING
        );
        rpaLogRepository.deleteAllInBatch(deletableLogs);
        return new RpaViewDeleteResponse(targetDate, "rpa_log", deletableLogs.size(), "RPA 로그 view 삭제 완료");
    }
}

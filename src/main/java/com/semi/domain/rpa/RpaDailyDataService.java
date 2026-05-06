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
import com.semi.domain.rpa.response.RpaDailyDataSummary;
import com.semi.domain.rpa.response.RpaDailyDeleteResponse;
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

package com.semi.domain.rpa.parser;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordRepository;
import com.semi.domain.product.Product;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse;
import com.semi.domain.rpa.parser.response.TrendKeywordResponse;
import com.semi.domain.supplier.Supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
시퀀스{
    1. 트랜드 데이터를 저장 함. size = 20
    2. DB에 저장된 드랜드 데이터가져오거나, List에 따로 저장해뒀다가 바로 제품+공급자를 받아오기.
        기존에는 DB를 그대로 유지시키기 위해 넣지 않고 있었으나, 파싱을 원활하게 하려면 필수 같음.
    3. 트랜드 순위에 따른 제품 및 공급자를 저장함 size = 10
        각 트랜드(20개)마다 약 10개씩 반복 하면서 저장.
    }
*/

@RestController
@RequiredArgsConstructor
@Validated // filterChain 설정시 filterChain에서 추가설정 필요, 동작안함
public class ParserController {
    // TrendKeyord
    private final TrendKeywordService trendKeywordService;
    private final TrendKeywordRepository trendKeywordRepository;
    private final SupplierAndProductService supplierAndProductService;
    
    @GetMapping("/api/TrandKeywords") // http://localhost:8080/api/TrandKeywords
    public List<TrendKeywordResponse.TrendKeywordItem> keywordsFetch() {
            List<TrendKeywordResponse.TrendKeywordItem> result = trendKeywordService.getNaverKeywords();
        return result;
    }

    @GetMapping("/api/TrandKeywords/saveWithSequentialId") // http://localhost:8080/api/TrandKeywords/saveWithSequentialId
    public List<TrendKeyword> saveWithSequentialId() {
            List<TrendKeywordResponse.TrendKeywordItem> trendList = trendKeywordService.getNaverKeywords();
            List<TrendKeyword> result = trendKeywordService.saveWithSequentialId(trendList);

        return result;
    }


    @GetMapping("/api/Suppliers") // http://localhost:8080/api/Suppliers?rankId=2182837573&syncDate=20260429
    public SupplierAndProductResponse suppliersFetch(
        @RequestParam @NotBlank(message = "rankId는 필수 입력값입니다.") Long rankId,
        @RequestParam @NotBlank(message = "syncDate는 필수 입력값입니다.") String syncDate
    ) {
        SupplierAndProductResponse result = supplierAndProductService.getNaverSuppliers(rankId, syncDate);

        return result;
    }    

    @GetMapping("/api/Suppliers/saveWithSequentialId") // http://localhost:8080/api/Suppliers/saveWithSequentialId?rankId=2182837573&syncDate=20260429
    public List<Supplier> suppliersSaveWithSequentialId(
        @RequestParam @NotBlank(message = "rankId는 필수 입력값입니다.") Long rankId,
        @RequestParam @NotBlank(message = "syncDate는 필수 입력값입니다.") String syncDate
    ) {
        SupplierAndProductResponse supplierAndProductResponse = supplierAndProductService.getSupplierAndProducts(rankId, syncDate);
        List<Supplier> result = supplierAndProductService.saveSuppliersWithSequentialId(supplierAndProductResponse);

        return result;
    }


    // Product
    // Reason: Product parser test data contains products under the supplier/product response root.
    // Behavior: Fetch the Naver detail response and return only the product list for parser verification.
    // Source: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html
    @GetMapping("/api/Products") // http://localhost:8080/api/Products?rankId=2182837573&syncDate=20260429
    public List<SupplierAndProductResponse.ProductItem> productsFetch(
        @RequestParam @NotNull(message = "rankId는 필수 입력값입니다.") Long rankId,
        @RequestParam @NotBlank(message = "syncDate는 필수 입력값입니다.") String syncDate
    ) {
        SupplierAndProductResponse supplierAndProductResponse = supplierAndProductService.getSupplierAndProducts(rankId, syncDate);
        List<SupplierAndProductResponse.ProductItem> result = supplierAndProductResponse.getProductList();

        if (result == null) {
            return List.of();
        }

        return result;
    }

    // Reason: Product needs an existing TrendKeyword foreign key before it can be stored.
    // Behavior: Load TrendKeyword by keywordId, fetch parser products, save related suppliers first, then save products.
    // Source: https://docs.spring.io/spring-data/jpa/reference/repositories/core-concepts.html
    // http://localhost:8080/api/Products/saveWithSequentialId?keywordId=160&rankId=2179193963&syncDate=20260428
    @GetMapping("/api/Products/saveWithSequentialId") // http://localhost:8080/api/Products/saveWithSequentialId?keywordId=160&rankId=2179193963&syncDate=20260428
    public List<Product> productsSaveWithSequentialId(
        @RequestParam @NotNull(message = "keywordId는 필수 입력값입니다.") Long keywordId,
        @RequestParam @NotNull(message = "rankId는 필수 입력값입니다.") Long rankId,
        @RequestParam @NotBlank(message = "syncDate는 필수 입력값입니다.") String syncDate
    ) {
        TrendKeyword trendKeyword = trendKeywordRepository.findById(keywordId)
            .orElseThrow(() -> new IllegalArgumentException("keywordId에 해당하는 TrendKeyword가 없습니다. keywordId=" + keywordId));
        SupplierAndProductResponse supplierAndProductResponse = supplierAndProductService.getSupplierAndProducts(rankId, syncDate);
        List<Product> result = supplierAndProductService.saveProductsWithSequentialId(trendKeyword, supplierAndProductResponse);

        return result;
    }



}

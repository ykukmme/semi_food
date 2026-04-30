package com.semi.domain.rpa.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import com.semi.domain.rpa.parser.mapper.ProductMapper;
import com.semi.domain.rpa.parser.mapper.SupplierMapper;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse.ProductItem;
import com.semi.domain.rpa.parser.response.SupplierAndProductResponse.SupplierItem;
import com.semi.domain.supplier.Supplier;
import com.semi.domain.supplier.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierAndProductService {

    private final RestClient restClient = RestClient.create();
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierMapper supplierMapper;
    private final ProductMapper productMapper;

    public SupplierAndProductResponse getSupplierAndProducts(Long sapRankId, String sapSyncDate) {
        String sapTargetSiteUrl = String.format(Constants.Snxbest.TARGET_SITE_URL_TEMPLATE, sapRankId, sapSyncDate);
        String sapDataUrl = String.format(Constants.Snxbest.API_V1_FOOD_PRODUCT_DATA_URL_TEMPLATE , sapRankId, sapSyncDate);

        SupplierAndProductResponse sapResponse = restClient.get()
            .uri(sapDataUrl)
            .header("User-Agent", Constants.Http.USER_AGENT)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)
            .header("Accept-Language",  Constants.Http.ACCEPT_LANGUAGE)
            .header("Referer", sapTargetSiteUrl)
            .retrieve()
            .body(SupplierAndProductResponse.class);

        System.out.println("수신된 공급자/상품 데이터: " + sapResponse);
        return sapResponse;
    }

    @Transactional
    public List<Supplier> saveSuppliersWithSequentialId(SupplierAndProductResponse sapResponse) {
        List<ProductItem> sapProductItems = sapGetProductItems(sapResponse);
        return new ArrayList<>(sapBuildSupplierMap(sapProductItems).values());
    }

    @Transactional
    public List<Product> saveProductsWithSequentialId(TrendKeyword sapKeyword, SupplierAndProductResponse sapResponse) {
        if (sapKeyword == null) {
            throw new IllegalArgumentException("TrendKeyword 정보 없이 Product를 저장할 수 없습니다.");
        }

        List<ProductItem> sapProductItems = sapGetProductItems(sapResponse);
        if (sapProductItems.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Supplier> sapSupplierMap = sapBuildSupplierMap(sapProductItems);
        Long sapMaxId = productRepository.findMaxId();
        long sapNextId = (sapMaxId == null) ? 0L : sapMaxId;

        List<Product> sapMappedProducts = productMapper.toVoList(sapProductItems);
        List<Product> sapNewProducts = new ArrayList<>();

        for (int sapIndex = 0; sapIndex < sapMappedProducts.size(); sapIndex++) {
            Product sapMappedProduct = sapMappedProducts.get(sapIndex);
            ProductItem sapSourceItem = sapProductItems.get(sapIndex);

            if (productRepository.existsByCrawledAtAndName(sapMappedProduct.getCrawledAt(), sapMappedProduct.getName())) {
                continue;
            }

            String sapSupplierName = sapNormalizeText(sapSourceItem.getMallNm(), Constants.Db.UNKNOWN);
            Supplier sapSupplier = sapSupplierMap.get(sapSupplierName);

            Product sapProduct = Product.builder()
                .id(++sapNextId)
                .keyword(sapKeyword)
                .supplier(sapSupplier)
                .name(sapMappedProduct.getName())
                .description(null)
                .price(sapMappedProduct.getPrice())
                .imageUrl(sapMappedProduct.getImageUrl())
                .productUrl(sapMappedProduct.getProductUrl())
                .autoOrder(false)
                .crawledAt(sapMappedProduct.getCrawledAt())
                .build();
            sapNewProducts.add(sapProduct);
        }

        if (!sapNewProducts.isEmpty()) {
            productRepository.saveAll(sapNewProducts);
            System.out.println(sapNewProducts.size() + "건의 Product 저장 완료 (마지막 ID: " + sapNextId + ")");
        }

        return sapNewProducts;
    }

    private Map<String, Supplier> sapBuildSupplierMap(List<ProductItem> sapProductItems) {
        Map<String, Supplier> sapSupplierMap = new LinkedHashMap<>();
        List<SupplierItem> sapSupplierItems = sapExtractSupplierItems(sapProductItems);

        if (sapSupplierItems.isEmpty()) {
            return sapSupplierMap;
        }

        Long sapMaxId = supplierRepository.findMaxId();
        long sapNextId = (sapMaxId == null) ? 0L : sapMaxId;
        List<Supplier> sapNewSuppliers = new ArrayList<>();

        for (SupplierItem sapSupplierItem : sapSupplierItems) {
            Supplier sapMappedSupplier = supplierMapper.toVo(sapSupplierItem);
            String sapSupplierName = sapNormalizeText(sapMappedSupplier.getName(), Constants.Db.UNKNOWN);
            String sapSupplierUrl = sapNormalizeText(sapMappedSupplier.getUrl(), Constants.Db.UNKNOWN);

            Supplier sapExistingSupplier = supplierRepository.findByName(sapSupplierName).orElse(null);
            if (sapExistingSupplier != null) {
                sapSupplierMap.put(sapSupplierName, sapExistingSupplier);
                continue;
            }

            if (supplierRepository.existsByCreatedAtAndName(sapMappedSupplier.getCreatedAt(), sapSupplierName)) {
                Supplier sapDuplicatedSupplier = supplierRepository.findByName(sapSupplierName).orElse(null);
                if (sapDuplicatedSupplier != null) {
                    sapSupplierMap.put(sapSupplierName, sapDuplicatedSupplier);
                }
                continue;
            }

            Supplier sapNewSupplier = Supplier.builder()
                    .id(++sapNextId)
                    .name(sapSupplierName)
                    .url(sapSupplierUrl)
                    .createdAt(sapMappedSupplier.getCreatedAt())
                    .build();
            sapNewSuppliers.add(sapNewSupplier);
            sapSupplierMap.put(sapSupplierName, sapNewSupplier);
        }

        if (!sapNewSuppliers.isEmpty()) {
            supplierRepository.saveAll(sapNewSuppliers);
            System.out.println(sapNewSuppliers.size() + "건의 Supplier 저장 완료 (마지막 ID: " + sapNextId + ")");
        }

        return sapSupplierMap;
    }

    private List<SupplierItem> sapExtractSupplierItems(List<ProductItem> sapProductItems) {
        Map<String, SupplierItem> sapSupplierItemMap = new LinkedHashMap<>();

        for (ProductItem sapProductItem : sapProductItems) {
            SupplierItem sapSupplierItem = new SupplierItem();
            String sapSupplierName = sapNormalizeText(sapProductItem.getMallNm(), Constants.Db.UNKNOWN);
            String sapSupplierUrl = sapNormalizeText(sapProductItem.getMallLinkUrl(), Constants.Db.UNKNOWN);

            sapSupplierItem.setMallNm(sapSupplierName);
            sapSupplierItem.setMallLinkUrl(sapSupplierUrl);
            sapSupplierItem.setSyncDate(sapProductItem.getSyncDate());

            sapSupplierItemMap.putIfAbsent(sapSupplierName, sapSupplierItem);
        }

        return new ArrayList<>(sapSupplierItemMap.values());
    }

    private List<ProductItem> sapGetProductItems(SupplierAndProductResponse sapResponse) {
        if (sapResponse == null || sapResponse.getProductList() == null) {
            return new ArrayList<>();
        }
        return sapResponse.getProductList();
    }

    private String sapNormalizeText(String sapValue, String sapDefaultValue) {
        if (sapValue == null || sapValue.isBlank()) {
            return sapDefaultValue;
        }
        return sapValue.trim();
    }
}

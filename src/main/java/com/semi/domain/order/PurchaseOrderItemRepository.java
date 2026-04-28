package com.semi.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    @Query("select coalesce(sum(item.quantity), 0) from PurchaseOrderItem item")
    long sumOrderedQuantity();

    @Query("select count(distinct item.product.id) from PurchaseOrderItem item")
    long countDistinctOrderedProducts();

    @Query(value = """
            select count(*)
            from (
                select product_id
                from purchase_order_item
                group by product_id
                having count(*) > 1
            ) repeated_products
            """, nativeQuery = true)
    long countRepeatOrderedProducts();
}

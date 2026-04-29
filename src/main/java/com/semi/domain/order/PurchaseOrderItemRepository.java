package com.semi.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
            select item
            from PurchaseOrderItem item
            join fetch item.purchaseOrder po
            left join fetch item.product product
            where po.member.id = :memberId
              and (
                :query = ''
                or lower(item.productName) like lower(concat('%', :query, '%'))
                or lower(product.name) like lower(concat('%', :query, '%'))
                or lower(coalesce(product.description, '')) like lower(concat('%', :query, '%'))
                or lower(po.orderNumber) like lower(concat('%', :query, '%'))
              )
            order by po.orderedAt desc, item.id desc
            """)
    List<PurchaseOrderItem> searchMemberOrderItems(
            @Param("memberId") Long memberId,
            @Param("query") String query
    );
}

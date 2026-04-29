package com.semi.domain.product;

public enum ProductCategory {
    AGRICULTURAL("agricultural"),
    MARINE("marine"),
    PROCESSED("processed"),
    GIFT("gift");

    private final String tabKey;

    ProductCategory(String tabKey) {
        this.tabKey = tabKey;
    }

    public String getTabKey() {
        return tabKey;
    }
}

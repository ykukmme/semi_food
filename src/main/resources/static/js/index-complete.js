(() => {
    const CART_STORAGE_KEY = "namhae_cart_v1";

    function loadCartFromStorage() {
        try {
            const raw = localStorage.getItem(CART_STORAGE_KEY);
            if (!raw) {
                return [];
            }

            const parsed = JSON.parse(raw);
            return Array.isArray(parsed) ? parsed : [];
        } catch {
            return [];
        }
    }

    function updateCartBadge() {
        const badge = document.getElementById("cart-count-badge");
        if (!badge) {
            return;
        }

        const totalCount = loadCartFromStorage().reduce((sum, item) => {
            const quantity = Number(item && item.quantity) || 0;
            return sum + quantity;
        }, 0);

        if (totalCount > 0) {
            badge.textContent = totalCount > 99 ? "99+" : String(totalCount);
            badge.classList.remove("is-hidden");
            return;
        }

        badge.classList.add("is-hidden");
    }

    function updateHeaderBackground() {
        const header = document.querySelector(".site-header");
        if (!header) {
            return;
        }

        if (window.innerHeight < 100) {
            header.style.backgroundColor = "transparent";
            header.style.backdropFilter = "none";
            header.style.borderBottomColor = "transparent";
            header.style.opacity = "0";
            header.style.pointerEvents = "none";
            return;
        }

        header.style.opacity = "1";
        header.style.pointerEvents = "auto";

        if (window.scrollY === 0) {
            header.style.backgroundColor = "transparent";
            header.style.backdropFilter = "none";
            header.style.borderBottomColor = "transparent";
            return;
        }

        header.style.backgroundColor = "rgba(255, 255, 255, 0.1)";
        header.style.backdropFilter = "blur(12px)";
        header.style.borderBottomColor = "rgba(153, 194, 255, 0.2)";
    }

    function setupProductListControls() {
        const initialVisibleCount = 28;
        const additionalVisibleCount = 20;
        const tabButtons = document.querySelectorAll(".tab-btn");
        const productItems = document.querySelectorAll("#products-grid > [data-category]");
        const emptyMessage = document.getElementById("products-empty-message");
        const form = document.getElementById("nav-search-form");
        const input = document.getElementById("nav-search-input");
        const moreButton = document.getElementById("product-more-btn");
        let visibleLimit = initialVisibleCount;

        if (!productItems.length) {
            if (moreButton) {
                moreButton.disabled = true;
                moreButton.setAttribute("aria-disabled", "true");
                moreButton.textContent = "더 이상 상품이 없습니다";
            }
            return;
        }

        function getActiveCategory() {
            return document.querySelector(".tab-btn.is-active")?.dataset.category || "all";
        }

        function getSearchQuery() {
            return input ? input.value.trim().toLowerCase() : "";
        }

        function filterProducts() {
            const category = getActiveCategory();
            const query = getSearchQuery();
            let matchedCount = 0;
            let visibleCount = 0;

            productItems.forEach((item) => {
                const productName = item.querySelector(".product-card__name")?.textContent.trim().toLowerCase() || "";
                const matchesCategory = category === "all" || item.dataset.category === category;
                const matchesQuery = !query || productName.includes(query);
                const isMatched = matchesCategory && matchesQuery;
                const isVisible = isMatched && matchedCount < visibleLimit;

                item.classList.toggle("is-hidden", !isVisible);

                if (isMatched) {
                    matchedCount += 1;
                }

                if (isVisible) {
                    visibleCount += 1;
                }
            });

            if (emptyMessage) {
                emptyMessage.classList.toggle("is-hidden", visibleCount > 0);
            }

            if (moreButton) {
                const hasMoreProducts = matchedCount > visibleLimit;
                moreButton.disabled = !hasMoreProducts;
                moreButton.setAttribute("aria-disabled", String(!hasMoreProducts));
                moreButton.textContent = hasMoreProducts ? "상품 더 보기" : "더 이상 상품이 없습니다";
            }
        }

        tabButtons.forEach((button) => {
            button.addEventListener("click", () => {
                visibleLimit = initialVisibleCount;
                tabButtons.forEach((btn) => btn.classList.remove("is-active"));
                button.classList.add("is-active");
                filterProducts();
            });
        });

        if (form) {
            form.addEventListener("submit", (event) => {
                event.preventDefault();
                filterProducts();
            });
        }

        if (input) {
            input.addEventListener("input", () => {
                visibleLimit = initialVisibleCount;
                filterProducts();
            });
        }

        if (moreButton) {
            moreButton.addEventListener("click", () => {
                visibleLimit += additionalVisibleCount;
                filterProducts();
            });
        }

        filterProducts();
    }

    function setupScrollTopButton() {
        const button = document.getElementById("scroll-top-btn");
        if (!button) {
            return;
        }

        function updateVisibility() {
            button.classList.toggle("is-visible", window.scrollY > 360);
        }

        button.addEventListener("click", () => {
            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });

        updateVisibility();
        window.addEventListener("scroll", updateVisibility, { passive: true });
    }

    function init() {
        updateCartBadge();
        setupProductListControls();
        setupScrollTopButton();
        updateHeaderBackground();

        window.addEventListener("scroll", updateHeaderBackground, { passive: true });
        window.addEventListener("resize", updateHeaderBackground);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();

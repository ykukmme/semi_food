<<<<<<< HEAD
// index.html Complete JavaScript
const NAMHAE_CART_STORAGE_KEY = 'namhae_cart_v1';


function loadCartFromStorage() {
    try {
        const raw = localStorage.getItem(NAMHAE_CART_STORAGE_KEY);
        if (!raw) return [];
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch {
        return [];
    }
}

function updateCartBadge() {
    const badge = document.getElementById('cart-count-badge');
    if (!badge) return;

    const totalCount = loadCartFromStorage().reduce((sum, item) => {
        const quantity = Number(item?.quantity) || 0;
        return sum + quantity;
    }, 0);

    if (totalCount > 0) {
        badge.textContent = totalCount > 99 ? '99+' : String(totalCount);
        badge.classList.remove('hidden');
    } else {
        badge.classList.add('hidden');
    }
}

// 1. Header scroll effect
const header = document.querySelector('.glass-nav');
if (header) {
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });
}

// 2. Category tab switching logic
const tabButtons = document.querySelectorAll('.tab-btn');
const productsGrid = document.getElementById('products-grid');

// State to track if showing all products
let isShowingAll = false;
let lastActiveCategory = 'agricultural';

// Initial products: Best 1-3 and Gift product
const initialProducts = [
    { title: '남해 흑마늘 팩', desc: '60일 숙성', price: '₩45,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBvD8uIdVNYEyiWGHi8yFmVhG6kbg5-KXyFssfxmkrmP-JrJbYqvVD6RtXPpMp9e3q0oJT9JD1rgMsLvnW_N7Guc2UpVX3lrboH_s0l84ZsmqcHHbcMeGGSxCPSUrWw4kZobqPVHoybx0lTymFYoGlw4m66hm8W5HgmeTzDzx4u5Cm14ARuzsuwU-FgzJ9cvl2OMTwesvvWrLgPV0wO-VE5w86RDbjiJXooh7j6sTkPGKfugkB4AmON8LapwmBn12YwXpz-0x63ew4', tag: 'BEST 01' },
    { title: '죽방렴 멸치', desc: '프리미엄 등급', price: '₩32,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCtnOlqwxH7qQy3ChR7FmuXsVSSaxhYvOnns0ob-uiDx4zOr71NBx6Q94Wz385GUyjkNNQ5N5JxcRDKfABqITkNJgwySp4LsPMI-vQcuQuJwFqzjv8KCLkeQXUb2tis5ggjGam2O8DACKgg-cvn9SEBhXqCmnefnl0zlnctaNRUCXO92vpIkuO8xAeWFh6b5wDdgld1a9JrVKnBCD__TPAa3BKTgPblzey3Iu4AWPf0VZzS9B_Z7rYdGhncgkQMULAuYf5QSiJ20tM', tag: 'BEST 02' },
    { title: '유자 청꿀', desc: '오래된 과수원에서 수확', price: '₩18,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA6h2K_MVUBoM6e2g9pPgi1msYnPU-LqTPmHqzCmHfsvP_BBUDLS8stEczldwRW7Escn4_-JCFeGGjjyBtGHDmvzMEGEGzBojUCZqNvDtWyL-Jo2aVNs-7gLGycXcdjZL_FigVsA6YTvm5dy2Ze7AGvew5LlLXtPg4U6B9nTX5rrLUWH_b3tvv7BG4W0mZQCabL3COnRI52h3TOxTODA5LcENrvt5Lqj7OiQqdrw-dqt2bpXQuzWWqCWeN1X7WAsqMeGl46bGUZcjw', tag: 'BEST 03' },
    { title: '대수확 박스', desc: '계절별 선물', price: '₩85,000', img: 'https://picsum.photos/seed/harvest/400/500.jpg', tag: 'GIFT' }
];

// Category products for tab switching
const mockProducts = {
    agricultural: [
        { title: '남해 흑마늘 팩', desc: '60일 숙성', price: '₩45,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBvD8uIdVNYEyiWGHi8yFmVhG6kbg5-KXyFssfxmkrmP-JrJbYqvVD6RtXPpMp9e3q0oJT9JD1rgMsLvnW_N7Guc2UpVX3lrboH_s0l84ZsmqcHHbcMeGGSxCPSUrWw4kZobqPVHoybx0lTymFYoGlw4m66hm8W5HgmeTzDzx4u5Cm14ARuzsuwU-FgzJ9cvl2OMTwesvvWrLgPV0wO-VE5w86RDbjiJXooh7j6sTkPGKfugkB4AmON8LapwmBn12YwXpz-0x63ew4', tag: 'BEST 01' },
        { title: '바닷바람 황금쌀', desc: '2024년 햅쌀', price: '₩68,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBczyhcl-myvmsy97jzfGBOYvdcKPxAJBIwW-DN3Am5aqlg831CLfC3PUSo0sYcilP7G3PF57aahgUVvY6aK3EkSlrvGRSwgrIM5QtrTIP2MYFPDS4_WTv3M2dIN0uH4XLbljElwmXdT_b1KLtqq9wf5xlhJrLMuUKvrH4psKSO-h23HKyWP-GR9_zTer6zwtBGnvuYGz3F-_pqokzSF0tnYfdXbqkSe7GUawF3eSXJ9F1DsMuzxKnCXE_lgrLEwvRYNXCMiel9Izc', tag: 'BEST 04' },
        { title: '남해 유자청', desc: '신선한 유자', price: '₩38,000', img: 'https://picsum.photos/seed/yuja/400/500.jpg', tag: 'BEST 03' },
        { title: '유기농 시금치', desc: '봄철 특선', price: '₩12,000', img: 'https://picsum.photos/seed/spinach/400/500.jpg', tag: '' }
    ],
    marine: [
        { title: '죽방렴 멸치', desc: '갓 잡은 신선도', price: '₩25,000', img: 'https://picsum.photos/seed/anchovy/400/500.jpg', tag: 'BEST 02' },
        { title: '황금 고등어', desc: '남해 명물', price: '₩22,000', img: 'https://picsum.photos/seed/mackerel/400/500.jpg', tag: '' },
        { title: '갓 구운 오징어', desc: '말린 해산물', price: '₩35,000', img: 'https://picsum.photos/seed/squid/400/500.jpg', tag: '' },
        { title: '김치 양념젓갈', desc: '전통 방식', price: '₩18,000', img: 'https://picsum.photos/seed/jeotgal/400/500.jpg', tag: '' }
    ],
    processed: [
        { title: '들꽃 숲꿀', desc: '설천면 특산품', price: '₩55,000', img: 'https://picsum.photos/seed/honey/400/500.jpg', tag: 'NEW' },
        { title: '천일염 소금', desc: '3년 숙성', price: '₩8,000', img: 'https://picsum.photos/seed/salt/400/500.jpg', tag: '' },
        { title: '반찬 세트', desc: '전통 맛집', price: '₩42,000', img: 'https://picsum.photos/seed/banchan/400/500.jpg', tag: '' },
        { title: '김 선물세트', desc: '프리미엄 김', price: '₩28,000', img: 'https://picsum.photos/seed/gim/400/500.jpg', tag: '' }
    ],
    gift: [
        { title: '대수확 박스', desc: '계절별 선물', price: '₩85,000', img: 'https://picsum.photos/seed/harvest/400/500.jpg', tag: 'GIFT' },
        { title: '헤리티지 세트', desc: '명절 선물', price: '₩120,000', img: 'https://picsum.photos/seed/heritage/400/500.jpg', tag: '' },
        { title: '남해 특산품', desc: '지역 대표', price: '₩65,000', img: 'https://picsum.photos/seed/special/400/500.jpg', tag: '' },
        { title: '건강 선물세트', desc: '웰빙 선물', price: '₩95,000', img: 'https://picsum.photos/seed/health/400/500.jpg', tag: '' }
    ]
};

function renderProducts(category) {
    if (!productsGrid) return;
    
    const products = mockProducts[category] || [];
    
    productsGrid.innerHTML = products.map(product => `
        <div class="group cursor-pointer product-card" onclick="window.location.href='product.html'">
            <div class="relative aspect-[4/5] rounded-xl overflow-hidden mb-3 bg-surface-container-low">
                <img src="${product.img}" alt="${product.title}" class="w-full h-full object-cover">
                ${product.tag ? `<span class="absolute top-4 right-4 px-3 py-1 bg-secondary text-on-secondary text-[10px] font-bold rounded-full font-label">${product.tag}</span>` : ''}
                <div class="absolute bottom-4 left-4 right-4 p-4 translate-y-12 group-hover:translate-y-0 opacity-0 group-hover:opacity-100 transition-all duration-300">
                    <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">상품 보기</button>
                </div>
            </div>
            <h3 class="font-bold text-on-surface mb-1">${product.title}</h3>
            <p class="text-sm text-on-surface-variant mb-2">${product.desc}</p>
            <p class="text-primary font-bold">${product.price}</p>
        </div>
    `).join('');
}

if (tabButtons.length > 0 && productsGrid) {
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const category = button.dataset.category;
            
            // Check if this category is already active
            const isActive = button.classList.contains('bg-primary');
            
            if (isActive) {
                // If already active, show initial products (Best 1-3 + Gift)
                productsGrid.innerHTML = initialProducts.map(product => `
                    <div class="group cursor-pointer product-card" onclick="window.location.href='product.html'">
                        <div class="relative aspect-[4/5] rounded-xl overflow-hidden mb-3 bg-surface-container-low">
                            <img src="${product.img}" alt="${product.title}" class="w-full h-full object-cover">
                            ${product.tag ? `<span class="absolute top-4 right-4 px-3 py-1 bg-secondary text-on-secondary text-[10px] font-bold rounded-full font-label">${product.tag}</span>` : ''}
                            <div class="absolute bottom-4 left-4 right-4 p-4 translate-y-12 group-hover:translate-y-0 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">상품 보기</button>
                            </div>
                        </div>
                        <h3 class="font-bold text-on-surface mb-1">${product.title}</h3>
                        <p class="text-sm text-on-surface-variant mb-2">${product.desc}</p>
                        <p class="text-primary font-bold">${product.price}</p>
                    </div>
                `).join('');
                
                // Reset all tabs
                tabButtons.forEach(btn => {
                    btn.classList.remove('bg-primary', 'text-white');
                    btn.classList.add('bg-surface-container-lowest', 'text-on-surface-variant');
                });
                
                                
                isShowingAll = true;
            } else {
                // Update active state
                tabButtons.forEach(btn => {
                    btn.classList.remove('bg-primary', 'text-white');
                    btn.classList.add('bg-surface-container-lowest', 'text-on-surface-variant');
                });
                button.classList.remove('bg-surface-container-lowest', 'text-on-surface-variant');
                button.classList.add('bg-primary', 'text-white');
                
                                
                // Render products for this category (max 4)
                renderProducts(category);
                
                // Update state
                isShowingAll = false;
                lastActiveCategory = category;
            }
        });
    });
    
    // Initialize with initial products (Best 1-3 + Gift)
    if (productsGrid) {
        productsGrid.innerHTML = initialProducts.map(product => `
            <div class="group cursor-pointer product-card" onclick="window.location.href='product.html'">
                <div class="relative aspect-[4/5] rounded-xl overflow-hidden mb-3 bg-surface-container-low">
                    <img src="${product.img}" alt="${product.title}" class="w-full h-full object-cover">
                    ${product.tag ? `<span class="absolute top-4 right-4 px-3 py-1 bg-secondary text-on-secondary text-[10px] font-bold rounded-full font-label">${product.tag}</span>` : ''}
                    <div class="absolute bottom-4 left-4 right-4 p-4 translate-y-12 group-hover:translate-y-0 opacity-0 group-hover:opacity-100 transition-all duration-300">
                        <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">상품 보기</button>
                    </div>
                </div>
                <h3 class="font-bold text-on-surface mb-1">${product.title}</h3>
                <p class="text-sm text-on-surface-variant mb-2">${product.desc}</p>
                <p class="text-primary font-bold">${product.price}</p>
            </div>
        `).join('');
    }
}

// 3. "Show all products" button functionality
const showAllBtn = document.getElementById('show-all-btn');
if (showAllBtn) {
    showAllBtn.addEventListener('click', () => {
        if (!isShowingAll) {
            // Show all products from all categories
            const allProducts = [
                ...mockProducts.agricultural,
                ...mockProducts.marine,
                ...mockProducts.processed
            ];
            
            if (productsGrid) {
                productsGrid.innerHTML = allProducts.map(product => `
                    <div class="group cursor-pointer product-card" onclick="window.location.href='product.html'">
                        <div class="relative aspect-[4/5] rounded-xl overflow-hidden mb-3 bg-surface-container-low">
                            <img src="${product.img}" alt="${product.title}" class="w-full h-full object-cover">
                            ${product.tag ? `<span class="absolute top-4 right-4 px-3 py-1 bg-secondary text-on-secondary text-[10px] font-bold rounded-full font-label">${product.tag}</span>` : ''}
                            <div class="absolute bottom-4 left-4 right-4 p-4 translate-y-12 group-hover:translate-y-0 opacity-0 group-hover:opacity-100 transition-all duration-300">
                                <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">상품 보기</button>
                            </div>
                        </div>
                        <h3 class="font-bold text-on-surface mb-1">${product.title}</h3>
                        <p class="text-sm text-on-surface-variant mb-2">${product.desc}</p>
                        <p class="text-primary font-bold">${product.price}</p>
                    </div>
                `).join('');
            }
            
            showAllBtn.textContent = '카테고리별 보기';
            showAllBtn.classList.remove('bg-outline', 'text-on-surface-variant');
            showAllBtn.classList.add('bg-primary', 'text-white');
            isShowingAll = true;
        } else {
            // Return to last active category
            const activeTab = document.querySelector(`[data-category="${lastActiveCategory}"]`);
            if (activeTab) {
                activeTab.click();
            }
            showAllBtn.textContent = '전체 상품 보기';
            showAllBtn.classList.remove('bg-primary', 'text-white');
            showAllBtn.classList.add('bg-outline', 'text-on-surface-variant');
            isShowingAll = false;
        }
    });
}

// 4. Search functionality
document.getElementById('nav-search-form')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const raw = document.getElementById('nav-search-input').value || '';
    const q = raw.trim();
    window.location.href = 'search_result.html?q=' + encodeURIComponent(q);
});

document.addEventListener('DOMContentLoaded', () => {
    updateCartBadge();
});


=======
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
        const header = document.querySelector("header");
        if (!header) {
            return;
        }

        const isAtTop = window.scrollY === 0;
        const isSmallWindow = window.innerHeight < 100;
        const navItems = header.querySelectorAll("a, .material-symbols-outlined");

        if (isSmallWindow) {
            header.style.backgroundColor = "transparent";
            header.style.backdropFilter = "none";
            header.style.borderBottomColor = "transparent";
            header.style.opacity = "0";
            header.style.pointerEvents = "none";
            navItems.forEach((el) => {
                el.style.color = "white";
            });
            return;
        }

        if (isAtTop) {
            header.style.backgroundColor = "transparent";
            header.style.backdropFilter = "none";
            header.style.borderBottomColor = "transparent";
            header.style.opacity = "1";
            header.style.pointerEvents = "auto";
            navItems.forEach((el) => {
                el.style.color = "white";
            });
            return;
        }

        header.style.backgroundColor = "rgba(255, 255, 255, 0.1)";
        header.style.backdropFilter = "blur(12px)";
        header.style.borderBottomColor = "rgba(153, 194, 255, 0.2)";
        header.style.opacity = "1";
        header.style.pointerEvents = "auto";
        navItems.forEach((el) => {
            el.style.color = "white";
        });
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
            let visibleCount = 0;
            let matchedCount = 0;

            productItems.forEach((item) => {
                const productName = item.querySelector("h3")?.textContent.trim().toLowerCase() || "";
                const matchesCategory = category === "all" || item.dataset.category === category;
                const matchesQuery = !query || productName.includes(query);
                const isMatched = matchesCategory && matchesQuery;
                const isWithinLimit = matchedCount < visibleLimit;
                const isVisible = isMatched && isWithinLimit;

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
                tabButtons.forEach((btn) => {
                    btn.classList.remove("is-active");
                });

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

        window.addEventListener("scroll", updateHeaderBackground);
        window.addEventListener("resize", updateHeaderBackground);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)

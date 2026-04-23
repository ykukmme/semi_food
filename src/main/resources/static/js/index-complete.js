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

// Simple mock data for category switching to show it's functional
const mockProducts = {
    agricultural: [
        { title: '남해 흑마늘 팩', desc: '60일 숙성', price: '₩45,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBvD8uIdVNYEyiWGHi8yFmVhG6kbg5-KXyFssfxmkrmP-JrJbYqvVD6RtXPpMp9e3q0oJT9JD1rgMsLvnW_N7Guc2UpVX3lrboH_s0l84ZsmqcHHbcMeGGSxCPSUrWw4kZobqPVHoybx0lTymFYoGlw4m66hm8W5HgmeTzDzx4u5Cm14ARuzsuwU-FgzJ9cvl2OMTwesvvWrLgPV0wO-VE5w86RDbjiJXooh7j6sTkPGKfugkB4AmON8LapwmBn12YwXpz-0x63ew4', tag: 'BEST 01' },
        { title: '바닷바람 황금쌀', desc: '2024년 햅쌀', price: '₩68,000', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBczyhcl-myvmsy97jzfGBOYvdcKPxAJBIwW-DN3Am5aqlg831CLfC3PUSo0sYcilP7G3PF57aahgUVvY6aK3EkSlrvGRSwgrIM5QtrTIP2MYFPDS4_WTv3M2dIN0uH4XLbljElwmXdT_b1KLtqq9wf5xlhJrLMuUKvrH4psKSO-h23HKyWP-GR9_zTer6zwtBGnvuYGz3F-_pqokzSF0tnYfdXbqkSe7GUawF3eSXJ9F1DsMuzxKnCXE_lgrLEwvRYNXCMiel9Izc', tag: 'BEST 04' }
    ],
    marine: [
        { title: '죽방렴 멸치', desc: '갓 잡은 신선도', price: '₩25,000', img: 'https://picsum.photos/seed/anchovy/400/500.jpg', tag: 'BEST 02' },
        { title: '황금 고등어', desc: '남해 명물', price: '₩22,000', img: 'https://picsum.photos/seed/mackerel/400/500.jpg', tag: '' }
    ],
    processed: [
        { title: '들꽃 숲꿀', desc: '설천면 특산품', price: '₩55,000', img: 'https://picsum.photos/seed/honey/400/500.jpg', tag: 'NEW' },
        { title: '천일염 소금', desc: '3년 숙성', price: '₩8,000', img: 'https://picsum.photos/seed/salt/400/500.jpg', tag: '' }
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
                    <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">바로 추가</button>
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
            
            // Update active state
            tabButtons.forEach(btn => {
                btn.classList.remove('bg-primary', 'text-white');
                btn.classList.add('bg-surface-container-lowest', 'text-on-surface-variant');
            });
            button.classList.remove('bg-surface-container-lowest', 'text-on-surface-variant');
            button.classList.add('bg-primary', 'text-white');
            
            // Render products
            renderProducts(category);
            
            // Update state
            isShowingAll = false;
            lastActiveCategory = category;
        });
    });
    
    // Initialize with agricultural products
    renderProducts('agricultural');
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
                                <button class="w-full bg-white text-on-surface py-3 rounded-lg font-bold text-sm shadow-xl hover:bg-primary hover:text-white transition-colors">바로 추가</button>
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

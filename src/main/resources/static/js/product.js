// product.html JavaScript
const NAMHAE_CART_STORAGE_KEY = 'namhae_cart_v1';

function loadCartFromStorage() {
    try {
        const raw = localStorage.getItem(NAMHAE_CART_STORAGE_KEY);
        if (!raw) return [];
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch { return []; }
}

function saveCartToStorage(items) {
    localStorage.setItem(NAMHAE_CART_STORAGE_KEY, JSON.stringify(items));
}

function updateCartBadge() {
    const badge = document.getElementById('cart-count-badge');
    if (!badge) return;

    const totalCount = loadCartFromStorage().reduce((sum, item) => {
        const itemQuantity = Number(item?.quantity) || 0;
        return sum + itemQuantity;
    }, 0);

    if (totalCount > 0) {
        badge.textContent = totalCount > 99 ? '99+' : String(totalCount);
        badge.classList.remove('hidden');
    } else {
        badge.textContent = '0';
        badge.classList.add('hidden');
    }
}

// ── 메인 이미지 높이를 장바구니 버튼 bottom에 맞춤 ──
function syncImageHeight() {
    const mainImage = document.getElementById('main-product-image');
    const thumbnails = document.querySelectorAll('.thumbnail-container');
    const addToCartBtn = document.getElementById('btn-add-to-cart');
    const buyNowBtn = document.getElementById('btn-buy-now');

    if (!mainImage || !addToCartBtn || !buyNowBtn) return;

    // 장바구니 버튼들의 bottom 위치 계산
    const addToCartBottom = addToCartBtn.getBoundingClientRect().bottom;
    const buyNowBottom = buyNowBtn.getBoundingClientRect().bottom;
    const maxBtnBottom = Math.max(addToCartBottom, buyNowBottom);
    const viewportBottom = window.innerHeight;
    const safeBottom = viewportBottom - 40; // 하단 여백

    // 이미지 높이 조정
    const targetHeight = Math.min(maxBtnBottom, safeBottom);
    const currentImageRect = mainImage.getBoundingClientRect();
    const imageTop = currentImageRect.top;
    const availableHeight = targetHeight - imageTop;

    if (availableHeight > 200) { // 최소 높이 보장
        mainImage.style.height = `${availableHeight}px`;
    }
}

// 폰트·이미지 로드 후 정확한 레이아웃 기준으로 측정
requestAnimationFrame(() => requestAnimationFrame(syncImageHeight));
window.addEventListener('resize', syncImageHeight);

// 썸네일 클릭 시 메인 이미지 변경
const mainImage = document.getElementById('main-product-image');
const thumbnails = document.querySelectorAll('.thumbnail-container');

if (mainImage && thumbnails.length > 0) {
    thumbnails.forEach((thumb, index) => {
        thumb.addEventListener('click', () => {
            const thumbImg = thumb.querySelector('img');
            if (thumbImg) {
                mainImage.src = thumbImg.src;
                
                // 활성화된 썸네일 시각적 표시
                thumbnails.forEach(t => t.classList.remove('active'));
                thumb.classList.add('active');
            }
        });
    });
}

// 가격 및 수량 관련 변수
let currentBasePrice = 15000;
let currentOriginalPrice = 20000;
let quantity = 100;
let minQty = 100;
let selectedSizeLabel = '소';

const displayPrice = document.getElementById('display-price');
const displayOriginalPrice = document.getElementById('display-original-price');
const qtyValue = document.getElementById('qty-value');
const sizeOptions = document.querySelectorAll('.size-option');

const updatePrices = () => {
    displayPrice.textContent = `${(currentBasePrice * quantity).toLocaleString()}원`;
    displayOriginalPrice.textContent = `${(currentOriginalPrice * quantity).toLocaleString()}원`;
    qtyValue.textContent = quantity;
};

sizeOptions.forEach(option => {
    option.addEventListener('click', () => {
        currentBasePrice = parseInt(option.dataset.price) || currentBasePrice;
        currentOriginalPrice = parseInt(option.dataset.original) || currentOriginalPrice;
        minQty = parseInt(option.dataset.minQty, 10) || 1;
        quantity = minQty;
        selectedSizeLabel = option.dataset.size || option.textContent.trim();

        sizeOptions.forEach(opt => {
            opt.className = 'size-option px-6 py-3 bg-gray-100 text-gray-700 rounded-xl font-medium text-sm hover:bg-gray-200 transition-all';
        });
        option.className = 'size-option px-6 py-3 bg-primary text-white rounded-xl font-bold text-sm shadow-lg shadow-primary/20 transition-all';
        updatePrices();
    });
});

const qtyStep = 10;
document.getElementById('qty-plus')?.addEventListener('click', () => { quantity += qtyStep; updatePrices(); });
document.getElementById('qty-minus')?.addEventListener('click', () => {
    if (quantity > minQty) { quantity = Math.max(minQty, quantity - qtyStep); updatePrices(); }
});

// 장바구니 추가 로직
function addCurrentProductToCart() {
    const titleEl = document.getElementById('product-title');
    const productTitle = titleEl ? titleEl.textContent.trim() : '제품 이름';
    const imageSrc = mainImage ? mainImage.src : '';
    const lineId = `heritage-demo-${selectedSizeLabel}`;
    const cart = loadCartFromStorage();
    const line = { id: lineId, name: `${productTitle} (${selectedSizeLabel})`, collection: 'Heritage Namhae', price: currentBasePrice, quantity, image: imageSrc, minQty };
    const idx = cart.findIndex(row => row.id === lineId);
    if (idx >= 0) {
        cart[idx].quantity += quantity;
    } else {
        cart.push(line);
    }
    saveCartToStorage(cart);
}

document.getElementById('btn-add-to-cart')?.addEventListener('click', () => {
    addCurrentProductToCart();
    updateCartBadge();
    alert('상품이 장바구니에 추가되었습니다.');
});

document.getElementById('btn-buy-now')?.addEventListener('click', () => {
    addCurrentProductToCart();
    updateCartBadge();
    window.location.href = 'cart.html';
});

document.addEventListener('DOMContentLoaded', () => {
    updateCartBadge();
    syncImageHeight();
});

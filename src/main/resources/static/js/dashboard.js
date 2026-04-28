function updateDashboardHeaderBackground() {
    const header = document.querySelector('.glass-nav');
    if (!header) {
        return;
    }

    if (window.innerHeight < 100) {
        header.style.backgroundColor = 'transparent';
        header.style.backdropFilter = 'none';
        header.style.webkitBackdropFilter = 'none';
        header.style.borderBottomColor = 'transparent';
        header.style.opacity = '0';
        header.style.pointerEvents = 'none';
        return;
    }

    header.style.opacity = '1';
    header.style.pointerEvents = 'auto';

    if (window.scrollY === 0) {
        header.style.backgroundColor = 'transparent';
        header.style.backdropFilter = 'none';
        header.style.webkitBackdropFilter = 'none';
        header.style.borderBottomColor = 'transparent';
        return;
    }

    header.style.backgroundColor = 'rgba(255, 255, 255, 0.55)';
    header.style.backdropFilter = 'blur(12px)';
    header.style.webkitBackdropFilter = 'blur(12px)';
    header.style.borderBottomColor = 'rgba(153, 194, 255, 0.2)';
}

function loadRecentCartItems() {
    try {
        const raw = localStorage.getItem('namhae_cart_v1');
        const items = raw ? JSON.parse(raw) : [];
        return Array.isArray(items) ? items.slice(-5).reverse() : [];
    } catch {
        return [];
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function renderRecentCartItems() {
    const homeContent = document.getElementById('dashboardHomeContent');
    if (!homeContent || document.getElementById('recent-cart-section')) {
        return;
    }

    const items = loadRecentCartItems();
    const section = document.createElement('section');
    section.id = 'recent-cart-section';
    section.className = 'panel rounded-[28px] p-6';

    const cards = items.length > 0
        ? items.map((item) => {
            const price = Number(item.price) || 0;
            const quantity = Number(item.quantity) || 1;
            const productId = encodeURIComponent(item.productId || item.id || '');
            const image = escapeHtml(item.image || item.productImage || 'https://placehold.co/300x300?text=Product');
            const name = escapeHtml(item.name || '상품');
            const href = productId ? `/product/view?id=${productId}` : '/';

            return `
                <article class="rounded-2xl bg-surface-container-low p-4 border border-outline-variant/10 transition hover:border-primary/40">
                    <a class="block" href="${href}">
                        <div class="aspect-square rounded-xl bg-surface-container overflow-hidden mb-4">
                            <img class="w-full h-full object-cover" src="${image}" alt="${name}">
                        </div>
                        <h3 class="text-sm font-bold text-on-surface leading-tight line-clamp-2">${name}</h3>
                        <div class="mt-3 flex items-center justify-between gap-3 text-xs">
                            <span class="font-bold text-primary">수량 ${quantity}</span>
                            <span class="font-bold text-on-surface">${price.toLocaleString('ko-KR')}원</span>
                        </div>
                    </a>
                </article>`;
        }).join('')
        : '<div class="rounded-2xl bg-surface-container-low p-6 text-center text-on-surface-variant font-semibold">최근 담은 장바구니 상품이 없습니다.</div>';

    section.innerHTML = `
        <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-6">
            <div>
                <p class="text-xs font-bold uppercase tracking-[0.24em] text-tertiary">대시보드</p>
                <h2 class="mt-2 text-2xl font-headline font-bold text-on-surface">최근 담은 장바구니 상품</h2>
            </div>
            <a href="/cart/view" class="text-sm font-bold text-primary hover:opacity-80 transition-opacity">장바구니 보기</a>
        </div>
        <div class="${items.length > 0 ? 'grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-4' : ''}">
            ${cards}
        </div>`;

    homeContent.prepend(section);
}

function setupKeywordMoreButton() {
    const button = document.getElementById('keyword-more-button');
    if (!button) {
        return;
    }

    button.addEventListener('click', () => {
        const extraKeywords = document.querySelectorAll('.js-extra-keyword');
        const isExpanded = button.dataset.expanded === 'true';

        extraKeywords.forEach((item) => {
            item.classList.toggle('hidden', isExpanded);
        });
        button.dataset.expanded = String(!isExpanded);
        button.textContent = isExpanded ? '\uB354\uBCF4\uAE30' : '\uB2EB\uAE30';
    });
}

document.getElementById('dashboard-search-form')?.addEventListener('submit', (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const raw = document.getElementById('dashboard-search-input')?.value || '';
    const q = raw.trim();
    const action = form.getAttribute('action') || '/dashboard_search_result';
    window.location.href = action + '?q=' + encodeURIComponent(q);
});

updateDashboardHeaderBackground();
renderRecentCartItems();
setupKeywordMoreButton();
window.addEventListener('scroll', updateDashboardHeaderBackground, { passive: true });
window.addEventListener('resize', updateDashboardHeaderBackground);

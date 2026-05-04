const TOKEN_KEY = 'accessToken';
const CART_STORAGE_KEY = 'namhae_cart_v1';

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
    document.cookie = `${TOKEN_KEY}=${encodeURIComponent(token)}; path=/; max-age=86400; SameSite=Lax`;
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('role');
    document.cookie = `${TOKEN_KEY}=; path=/; max-age=0; SameSite=Lax`;
}

function readCartItems() {
    try {
        const raw = localStorage.getItem(CART_STORAGE_KEY);
        const parsed = raw ? JSON.parse(raw) : [];
        return Array.isArray(parsed) ? parsed : [];
    } catch {
        return [];
    }
}

function getCartItemsCount() {
    return readCartItems().reduce((sum, item) => {
        const quantity = Number(item && item.quantity) || 0;
        return sum + quantity;
    }, 0);
}

function ensureCartBadgeStyles() {
    if (document.getElementById('cart-badge-styles')) {
        return;
    }

    const style = document.createElement('style');
    style.id = 'cart-badge-styles';
    style.textContent = `
        .cart-link-with-badge {
            position: relative;
        }

        .cart-count-badge {
            position: absolute;
            top: -0.5rem;
            right: -0.75rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 1.35rem;
            height: 1.25rem;
            padding: 0 0.25rem;
            border: 1px solid #fff !important;
            border-radius: 999px;
            background: #dc2626 !important;
            color: #fff !important;
            -webkit-text-fill-color: #fff !important;
            font-size: 0.625rem;
            font-weight: 900;
            line-height: 1;
            text-align: center;
            box-shadow: 0 6px 16px rgba(220, 38, 38, 0.35) !important;
            pointer-events: none;
        }

        .cart-count-badge.hidden,
        .cart-count-badge.is-hidden {
            display: none !important;
        }
    `;
    document.head.appendChild(style);
}

function ensureCartBadgeElements() {
    document.querySelectorAll('a[href*="/cart/view"]').forEach((link) => {
        if (link.matches('#btn-go-to-cart, [data-cart-badge="false"]')) {
            return;
        }

        const hasCartIcon = Array.from(link.querySelectorAll('.material-symbols-outlined'))
            .some((icon) => /shopping_(bag|cart)/.test(icon.textContent.trim()));

        if (!hasCartIcon) {
            return;
        }

        link.classList.add('cart-link-with-badge');

        if (link.querySelector('.cart-count-badge')) {
            return;
        }

        const badge = document.createElement('span');
        badge.className = 'cart-count-badge is-hidden';
        badge.textContent = '0';
        link.appendChild(badge);
    });
}

function updateCartBadges() {
    ensureCartBadgeStyles();
    ensureCartBadgeElements();

    const totalCount = getCartItemsCount();
    document.querySelectorAll('.cart-count-badge').forEach((badge) => {
        badge.textContent = totalCount > 99 ? '99+' : String(totalCount);
        badge.classList.toggle('hidden', totalCount <= 0);
        badge.classList.toggle('is-hidden', totalCount <= 0);
    });
}

window.updateCartBadges = updateCartBadges;

function ensureAuthToastStyles() {
    if (document.getElementById('auth-toast-styles')) {
        return;
    }

    const style = document.createElement('style');
    style.id = 'auth-toast-styles';
    style.textContent = `
        .product-toast {
            position: fixed;
            top: 5.5rem;
            right: 1.25rem;
            z-index: 1000;
            padding: 0.85rem 1.15rem;
            border-radius: 8px;
            background: #0066cc;
            color: #fff;
            font-weight: 800;
            box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
            transform: translateX(calc(100% + 2rem));
            transition: transform 0.25s ease;
        }

        .product-toast.is-visible {
            transform: translateX(0);
        }
    `;
    document.head.appendChild(style);
}

function showAuthNotification(message, onComplete) {
    ensureAuthToastStyles();

    const notification = document.createElement('div');
    notification.className = 'product-toast';
    notification.textContent = message;
    document.body.appendChild(notification);

    requestAnimationFrame(() => notification.classList.add('is-visible'));

    window.setTimeout(() => {
        notification.classList.remove('is-visible');
        window.setTimeout(() => {
            notification.remove();
            if (typeof onComplete === 'function') {
                onComplete();
            }
        }, 300);
    }, 2000);
}

async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        clearToken();
        window.location.href = '/login.html';
        return;
    }

    return response;
}

function redirectIfNoToken(redirectUrl = 'index.html') {
    if (!getToken()) {
        window.location.href = redirectUrl;
    }
}

function showGuestMenu() {
    const guestMenu = document.getElementById('guestMenu');
    const memberMenu = document.getElementById('memberMenu');

    if (guestMenu) {
        guestMenu.style.display = 'flex';
    }

    if (memberMenu) {
        memberMenu.style.display = 'none';
    }
}

function showMemberMenu() {
    const guestMenu = document.getElementById('guestMenu');
    const memberMenu = document.getElementById('memberMenu');

    if (guestMenu) {
        guestMenu.style.display = 'none';
    }

    if (memberMenu) {
        memberMenu.style.display = 'flex';
    }
}

async function renderLoginMenu() {
    const token = getToken();
    const guestMenu = document.getElementById('guestMenu');
    const memberMenu = document.getElementById('memberMenu');

    if (!guestMenu && !memberMenu) {
        return;
    }

    if (!token) {
        showGuestMenu();
        return;
    }

    try {
        const response = await fetch('/api/auth/me', {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        if (!response.ok) {
            clearToken();
            showGuestMenu();
            return;
        }

        const member = await response.json();
        if (member.memberId) {
            showMemberMenu();
            return;
        }

        showGuestMenu();
    } catch (error) {
        showGuestMenu();
    }
}

function handleLogout(event) {
    event.preventDefault();
    event.stopImmediatePropagation();
    clearToken();
    showAuthNotification('로그아웃되었습니다.', () => {
        location.href = '/';
    });
}

function setupLogoutButton() {
    document.querySelectorAll('#logoutBtn, [data-logout-button]').forEach((button) => {
        if (button.dataset.logoutBound === 'true') {
            return;
        }

        button.dataset.logoutBound = 'true';
        button.addEventListener('click', handleLogout);
    });

    if (document.body?.dataset.logoutDelegated === 'true') {
        return;
    }

    document.body.dataset.logoutDelegated = 'true';
    document.body.addEventListener('click', (event) => {
        const logoutButton = event.target.closest('#logoutBtn, [data-logout-button]');
        if (!logoutButton) {
            return;
        }

        handleLogout(event);
    });
}

function openMemberDashboard(token = getToken()) {
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    window.location.href = '/member';
}

function setupMemberDashboardLink() {
    document.getElementById('member-dashboard-link')?.addEventListener('click', async (event) => {
        event.preventDefault();

        const token = getToken();
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        try {
            const meResponse = await fetch('/api/auth/me', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!meResponse.ok) {
                clearToken();
                window.location.href = '/login.html';
                return;
            }

            openMemberDashboard(token);
        } catch (error) {
            window.location.href = '/login.html';
        }
    });
}

function initAuthMenu() {
    renderLoginMenu();
    setupLogoutButton();
    setupMemberDashboardLink();
    updateCartBadges();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuthMenu);
} else {
    initAuthMenu();
}

window.addEventListener('storage', (event) => {
    if (event.key === CART_STORAGE_KEY) {
        updateCartBadges();
    }
});

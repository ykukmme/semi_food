const TOKEN_KEY = 'accessToken';

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
            z-index: 10000;
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
    }, 900);
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
    showAuthNotification('\uB85C\uADF8\uC544\uC6C3\uB418\uC5C8\uC2B5\uB2C8\uB2E4.', () => {
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

async function openMemberDashboard(memberId, token = getToken()) {
    if (!token || !memberId) {
        window.location.href = '/login.html';
        return;
    }

    const dashboardResponse = await fetch('/member', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            Authorization: `Bearer ${token}`,
        },
        body: new URLSearchParams({
            memberId,
        }),
    });

    if (!dashboardResponse.ok || dashboardResponse.url.includes('/login.html')) {
        window.location.href = '/login.html';
        return;
    }

    const html = await dashboardResponse.text();
    window.history.pushState({}, '', '/member');
    document.open();
    document.write(html);
    document.close();
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

            const member = await meResponse.json();
            await openMemberDashboard(member.memberId, token);
        } catch (error) {
            window.location.href = '/login.html';
        }
    });
}

function initAuthMenu() {
    renderLoginMenu();
    setupLogoutButton();
    setupMemberDashboardLink();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuthMenu);
} else {
    initAuthMenu();
}

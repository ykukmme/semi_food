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
    event.stopPropagation();
    clearToken();
    location.href = '/';
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

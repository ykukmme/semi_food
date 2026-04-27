// 인증 토큰 유틸 — 모든 페이지에서 공통 사용

const TOKEN_KEY = 'accessToken';

/** localStorage에서 토큰 조회 */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/** localStorage에 토큰 저장 */
function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

/** localStorage에서 토큰 삭제 (로그아웃) */
function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('role');
}

/**
 * Authorization 헤더를 자동으로 포함하는 fetch 래퍼
 * 401 응답 시 자동으로 로그인 페이지로 리다이렉트
 */
async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    };

    const response = await fetch(url, { ...options, headers });

    // 토큰 만료 또는 미인증 → 로그아웃 후 로그인 페이지로
    if (response.status === 401) {
        clearToken();
        window.location.href = 'login.html';
        return;
    }

    return response;
}

/**
 * 토큰이 없으면 지정 경로로 리다이렉트
 * 인증이 필요한 페이지 상단에서 호출
 */
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

function showMemberMenu(member) {
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

    if(!token){
        showGuestMenu();
        return;
    }

    try {
        const res = await fetch('/api/auth/me', {
            headers: {
                Authorization:`Bearer ${token}`
            }
        });

        if(!res.ok){
            clearToken();
            showGuestMenu();
            return;
        }

        const member = await res.json();

        if(member.memberId){
            showMemberMenu(member);
            return;
        }

        showGuestMenu();
    } catch (error) {
        showGuestMenu();
    }
}

function setupLogoutButton() {
    document.getElementById('logoutBtn')?.addEventListener('click', ()=>{
        clearToken();
        location.href="/";
    });
}

function initAuthMenu() {
    renderLoginMenu();
    setupLogoutButton();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuthMenu);
} else {
    initAuthMenu();
}

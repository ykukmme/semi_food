// 로그인 API 요청 및 폼 유효성 검사
// API_BASE는 api.js에서 환경에 따라 자동 주입됨 (localhost → 8080 직접, EC2 → Nginx 상대경로)

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearErrors();

    const data = {
        memberId: document.getElementById('memberId').value.trim(),
        password: document.getElementById('password').value,
    };

    const submitBtn = document.querySelector('.btn-submit');
    submitBtn.disabled = true;
    submitBtn.textContent = '처리 중...';

    try {
        const res = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const json = await res.json();

        if (res.status === 200) {
            // 로그인 성공 — 토큰 저장 후 이동
            setToken(json.accessToken);
            localStorage.setItem('role', json.role);
            window.location.href = '/';
        } else if (res.status === 400 && json.errors) {
            // 입력값 검증 실패
            Object.entries(json.errors).forEach(([field, msg]) => {
                showFieldError(field, msg);
            });
        } else {
            // 401 등 인증 실패
            showGlobalError(json.message || '로그인 중 오류가 발생했습니다.');
        }
    } catch (err) {
        showGlobalError('서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '로그인';
    }
});

function showFieldError(field, message) {
    const input = document.getElementById(field);
    const errEl = document.getElementById(`${field}Error`);
    if (input) input.classList.add('error');
    if (errEl) errEl.textContent = message;
}

function showGlobalError(message) {
    const el = document.getElementById('globalError');
    el.textContent = message;
    el.classList.remove('hidden');
}

function clearErrors() {
    document.querySelectorAll('.error-msg').forEach(el => el.textContent = '');
    document.querySelectorAll('input.error').forEach(el => el.classList.remove('error'));
    const global = document.getElementById('globalError');
    global.textContent = '';
    global.classList.add('hidden');
}

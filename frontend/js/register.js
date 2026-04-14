// 회원가입 API 요청 및 폼 유효성 검사

const API_BASE = 'http://localhost:8080';

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearErrors();

    const memberIdVal = document.getElementById('memberId').value.trim();

    // 아이디 형식 검사: 영문, 숫자, 밑줄만 허용
    if (memberIdVal && !/^[a-zA-Z0-9_]+$/.test(memberIdVal)) {
        showFieldError('memberId', '아이디는 영문, 숫자, 밑줄(_)만 사용 가능합니다.');
        return;
    }

    const phoneRaw = document.getElementById('phone').value.trim();

    // 전화번호 입력 시 숫자(또는 하이픈)만 허용
    if (phoneRaw && !/^[\d\-]+$/.test(phoneRaw)) {
        showFieldError('phone', '전화번호는 숫자만 입력해주세요.');
        return;
    }

    const phoneDigits = phoneRaw.replace(/-/g, ''); // 하이픈 제거 후 숫자만
    if (phoneDigits && !/^\d{10,11}$/.test(phoneDigits)) {
        showFieldError('phone', '전화번호는 10~11자리 숫자로 입력해주세요.');
        return;
    }

    const data = {
        memberId: document.getElementById('memberId').value.trim(),
        password: document.getElementById('password').value,
        email:    document.getElementById('email').value.trim(),
        phone:    phoneDigits || null,
        name:     document.getElementById('name').value.trim(),
    };

    const submitBtn = document.querySelector('.btn-submit');
    submitBtn.disabled = true;
    submitBtn.textContent = '처리 중...';

    try {
        const res = await fetch(`${API_BASE}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const json = await res.json();

        if (res.status === 201) {
            // 가입 성공 — 로그인 페이지로 이동
            alert(`환영합니다, ${json.name}님! 가입이 완료됐습니다.`);
            window.location.href = 'login.html';
        } else if (res.status === 400 && json.errors) {
            // 유효성 검사 실패 — 필드별 오류 표시
            Object.entries(json.errors).forEach(([field, msg]) => {
                showFieldError(field, msg);
            });
        } else {
            // 중복 등 기타 오류
            showGlobalError(json.message || '가입 중 오류가 발생했습니다.');
        }
    } catch (err) {
        showGlobalError('서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '가입하기';
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

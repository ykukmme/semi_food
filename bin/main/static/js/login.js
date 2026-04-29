if (localStorage.getItem('accessToken')) {
    window.location.replace('/');
}

document.getElementById('loginForm')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    clearErrors();

    const data = {
        memberId: document.getElementById('memberId').value.trim(),
        password: document.getElementById('password').value,
    };

    const submitBtn = document.querySelector('.btn-submit');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Processing...';

    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const body = await response.json();

        if (response.ok) {
            setToken(body.accessToken);
            localStorage.setItem('role', body.role);
            
            // 권한에 따라 리디렉션
            if (body.role === 'ADMIN') {
                window.location.href = '/admin';
            } else {
                window.location.href = '/';
            }
            return;
        }

        if (response.status === 400 && body.errors) {
            Object.entries(body.errors).forEach(([field, message]) => {
                showFieldError(field, message);
            });
            return;
        }

        showGlobalError(body.message || 'Login failed.');
    } catch (error) {
        showGlobalError('Could not connect to the server.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Login';
    }
});

function showFieldError(field, message) {
    const input = document.getElementById(field);
    const errorElement = document.getElementById(`${field}Error`);
    if (input) {
        input.classList.add('error');
    }
    if (errorElement) {
        errorElement.textContent = message;
    }
}

function showGlobalError(message) {
    const element = document.getElementById('globalError');
    element.textContent = message;
    element.classList.remove('hidden');
}

function clearErrors() {
    document.querySelectorAll('.error-msg').forEach((element) => {
        element.textContent = '';
    });
    document.querySelectorAll('input.error').forEach((element) => {
        element.classList.remove('error');
    });

    const global = document.getElementById('globalError');
    global.textContent = '';
    global.classList.add('hidden');
}

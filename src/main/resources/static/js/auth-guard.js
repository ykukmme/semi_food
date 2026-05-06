(function () {
    const TOKEN_KEY = "accessToken";

    function getStoredToken() {
        try {
            const localToken = localStorage.getItem(TOKEN_KEY);
            if (localToken) {
                return localToken;
            }
        } catch (error) {
            // Fall back to the cookie below.
        }

        const cookieToken = document.cookie
            .split("; ")
            .find((row) => row.startsWith(`${TOKEN_KEY}=`))
            ?.split("=")[1];
        return cookieToken ? decodeURIComponent(cookieToken) : null;
    }

    function clearStoredToken() {
        try {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem("role");
        } catch (error) {
            // Ignore storage access errors and still expire the cookie.
        }
        document.cookie = `${TOKEN_KEY}=; path=/; max-age=0; SameSite=Lax`;
    }

    function redirectToLogin() {
        if (window.location.pathname !== "/login.html") {
            window.location.replace("/login.html");
        }
    }

    async function requireAuthenticatedPage() {
        const token = getStoredToken();
        if (!token) {
            redirectToLogin();
            return;
        }

        try {
            const response = await fetch("/api/auth/me", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                clearStoredToken();
                redirectToLogin();
            }
        } catch (error) {
            clearStoredToken();
            redirectToLogin();
        }
    }

    requireAuthenticatedPage();
})();

// dashboard.html JavaScript
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

document.getElementById('dashboard-search-form')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const raw = document.getElementById('dashboard-search-input')?.value || '';
    const q = raw.trim();
    window.location.href = 'dashboard_search_result.html?q=' + encodeURIComponent(q);
});

updateDashboardHeaderBackground();
window.addEventListener('scroll', updateDashboardHeaderBackground, { passive: true });
window.addEventListener('resize', updateDashboardHeaderBackground);

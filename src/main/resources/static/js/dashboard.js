// dashboard.html JavaScript
document.getElementById('dashboard-search-form')?.addEventListener('submit', (e) => {
    e.preventDefault();
    const raw = document.getElementById('dashboard-search-input')?.value || '';
    const q = raw.trim();
    window.location.href = 'dashboard_search_result.html?q=' + encodeURIComponent(q);
});

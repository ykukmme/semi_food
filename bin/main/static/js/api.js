/**
 * API 베이스 URL 자동 감지
 *
 * - 개발 (localhost): Spring Boot 직접 호출 (http://localhost:8080)
 * - 운영 (EC2 / 도메인): 상대경로 사용 → Nginx가 /api/ → localhost:8080으로 프록시
 *
 * 이 파일을 모든 HTML의 첫 번째 <script>로 로드해야 합니다.
 */
const API_BASE = (() => {
    const host = window.location.hostname;
    // 로컬 개발 환경
    if (host === 'localhost' || host === '127.0.0.1') {
        return 'http://localhost:8080';
    }
    // 운영 환경 — Nginx 리버스 프록시 사용 (상대경로)
    return '';
})();

#!/bin/bash
# ============================================================
# 배포 스크립트 — 스마트 식품 이커머스 플랫폼
#
# 사용법 (프로젝트 루트에서 실행):
#   chmod +x deploy/deploy.sh
#   ./deploy/deploy.sh
#
# 사전 조건:
#   - EC2_HOST 환경변수 설정 (또는 스크립트 내 직접 입력)
#   - ~/.ssh/semi-key.pem (EC2 키페어)
#   - EC2 서버에 Java 21, Nginx 설치 완료
#   - EC2 서버 /opt/semi/.env 파일 생성 완료
#
# 구조: Spring Boot가 src/main/resources/static/ 정적 파일 직접 서빙
#       → JAR 하나로 프론트 + 백엔드 모두 배포
# ============================================================

set -e  # 에러 발생 시 즉시 종료

# ── 설정 ────────────────────────────────────────────────────
EC2_HOST="${EC2_HOST:-your-ec2-ip-or-domain}"   # EC2 퍼블릭 IP 또는 도메인
EC2_USER="ec2-user"                              # Amazon Linux 기본 사용자
SSH_KEY="~/.ssh/semi-key.pem"                    # EC2 키페어 경로
DEPLOY_DIR="/opt/semi"

echo "======================================================"
echo "  배포 시작 → $EC2_HOST"
echo "======================================================"

# ── 1. 빌드 (프론트 포함 JAR) ──────────────────────────────
echo "[1/4] 빌드 중... (정적 파일 포함)"
./gradlew clean bootJar -x test
JAR_FILE=$(ls build/libs/*.jar | grep -v plain)
echo "  빌드 완료: $JAR_FILE"

# ── 2. 배포 디렉토리 준비 (최초 1회) ───────────────────────
echo "[2/4] EC2 디렉토리 준비..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo mkdir -p $DEPLOY_DIR
    sudo chown -R $EC2_USER:$EC2_USER $DEPLOY_DIR
"

# ── 3. 파일 전송 ────────────────────────────────────────────
echo "[3/4] 파일 전송 중..."

# JAR 전송 (프론트엔드 정적 파일 포함)
scp -i "$SSH_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:$DEPLOY_DIR/backend.jar"

# Nginx 설정 전송
scp -i "$SSH_KEY" deploy/nginx.conf "$EC2_USER@$EC2_HOST:/tmp/semi.conf"

echo "  전송 완료"

# ── 4. Nginx 설정 적용 + 서비스 재시작 ─────────────────────
echo "[4/4] 서비스 재시작..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo cp /tmp/semi.conf /etc/nginx/conf.d/semi.conf
    sudo nginx -t && sudo systemctl reload nginx

    sudo systemctl daemon-reload
    sudo systemctl restart semi-backend
    sleep 3
    sudo systemctl status semi-backend --no-pager
"

echo ""
echo "======================================================"
echo "  배포 완료!"
echo "  접속 주소: http://$EC2_HOST/login.html"
echo "======================================================"

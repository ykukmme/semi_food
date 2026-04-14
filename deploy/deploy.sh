#!/bin/bash
# ============================================================
# 배포 스크립트 — 스마트 식품 이커머스 플랫폼
#
# 사용법 (로컬 개발 PC에서 실행):
#   chmod +x deploy/deploy.sh
#   ./deploy/deploy.sh
#
# 사전 조건:
#   - EC2_HOST 환경변수 설정 (또는 스크립트 내 직접 입력)
#   - ~/.ssh/semi-key.pem (EC2 키페어)
#   - EC2 서버에 Java 21, Nginx 설치 완료
#   - EC2 서버 /opt/semi/.env 파일 생성 완료
# ============================================================

set -e  # 에러 발생 시 즉시 종료

# ── 설정 ────────────────────────────────────────────────────
EC2_HOST="${EC2_HOST:-your-ec2-ip-or-domain}"   # EC2 퍼블릭 IP 또는 도메인
EC2_USER="ec2-user"                              # Amazon Linux 기본 사용자
SSH_KEY="~/.ssh/semi-key.pem"                    # EC2 키페어 경로
DEPLOY_DIR="/opt/semi"
FRONTEND_DIR="/var/www/semi/frontend"

echo "======================================================"
echo "  배포 시작 → $EC2_HOST"
echo "======================================================"

# ── 1. 백엔드 빌드 ──────────────────────────────────────────
echo "[1/5] 백엔드 빌드 중..."
cd backend
./gradlew clean bootJar -x test
JAR_FILE=$(ls build/libs/*.jar | grep -v plain)
echo "  빌드 완료: $JAR_FILE"
cd ..

# ── 2. 배포 디렉토리 준비 (최초 1회) ───────────────────────
echo "[2/5] EC2 디렉토리 준비..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo mkdir -p $DEPLOY_DIR
    sudo mkdir -p $FRONTEND_DIR
    sudo chown -R $EC2_USER:$EC2_USER $DEPLOY_DIR
    sudo chown -R $EC2_USER:$EC2_USER $FRONTEND_DIR
"

# ── 3. 파일 전송 ────────────────────────────────────────────
echo "[3/5] 파일 전송 중..."

# JAR 전송
scp -i "$SSH_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:$DEPLOY_DIR/backend.jar"

# 프론트엔드 파일 전송
scp -i "$SSH_KEY" -r frontend/* "$EC2_USER@$EC2_HOST:$FRONTEND_DIR/"

# Nginx 설정 전송
scp -i "$SSH_KEY" deploy/nginx.conf "$EC2_USER@$EC2_HOST:/tmp/semi.conf"

echo "  전송 완료"

# ── 4. Nginx 설정 적용 ──────────────────────────────────────
echo "[4/5] Nginx 설정 적용..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo cp /tmp/semi.conf /etc/nginx/conf.d/semi.conf
    sudo nginx -t && sudo systemctl reload nginx
"
echo "  Nginx 재로드 완료"

# ── 5. 백엔드 서비스 재시작 ─────────────────────────────────
echo "[5/5] 백엔드 서비스 재시작..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    # systemd 서비스 파일이 없으면 복사
    if [ ! -f /etc/systemd/system/semi-backend.service ]; then
        echo '  서비스 파일 최초 등록...'
    fi
    sudo systemctl daemon-reload
    sudo systemctl restart semi-backend
    sleep 3
    sudo systemctl status semi-backend --no-pager
"

echo ""
echo "======================================================"
echo "  배포 완료!"
echo "  접속 주소: http://$EC2_HOST"
echo "======================================================"

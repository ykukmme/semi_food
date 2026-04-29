# 1단계: 빌드
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .

# gradlew 파일에 실행 권한 부여 (추가)
RUN chmod +x gradlew

# 윈도우 줄바꿈 문자(\r)를 리눅스용으로 변환 (핵심 추가!)
RUN sed -i 's/\r$//' gradlew

# gradlew 대신 시스템 설치된 gradle 명령어로 빌드 (Wrapper 파일 필요 없음)
# render.com 의 메모리 문제라고 하여 명령어 수정
# test하지 않음, 데몬 생성 X, 빌드정보출력, jvm 메모리 384m제한
RUN ./gradlew build -x test --no-daemon --info -Dorg.gradle.jvmargs="-Xmx384m" 

# 2단계: 실행
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# build/libs 폴더 안의 jar 파일을 가져옴
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

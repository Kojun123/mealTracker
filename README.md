# JunMealTracker

https://junmeal.com

간단한 입력만으로 음식 기록을 남기고, AI 기반 파싱을 통해 칼로리·단백질을 자동 분석하는 개인 식단 트래커입니다. 

실사용을 전제로 UX 개선에 집중하였습니다.

---

## 프로젝트 목표

자연어 기반 음식 입력 자동 분석

반복 입력 최소화를 위한 토큰 중심 UX 설계

실제 운영 환경에서의 배포 및 서버 관리 경험 축적

---

## 핵심 기능

* **1. AI 기반 빠른 기록**
  * 음식명을 자연어로 입력하면 OpenAI API를 통해 파싱 및 성분 분석
  * 모호한 표현도 추정 처리 후 저장
  * 저장 전 영양 정보 직접 수정 가능

* **2. 토큰 기반 입력 UI**
  * 입력창 내부 토큰 구조로 즉시 수정 가능
  * 동일 음식 재선택 시 수량 증가(xN)
  * 클릭 기반 입력으로 반복 타이핑 최소화
  * x 버튼으로 즉시 제거

* **3. 즐겨찾기 / 최근 기록**
  * 자주 먹는 음식 빠른 추가
  * 최근 기록 한 줄 노출로 재입력 최소화

* **4. 대시보드**
  * 일일 칼로리/단백질 요약
  * 날짜별 조회
  * 기록 수정 및 삭제

* **5. 인증**
  * OAuth2 기반 소셜 로그인
  * JWT 기반 무상태 인증 처리

---

## 기술 스택

### Frontend
* React (Vite)

### Backend
* Spring Boot (REST API)
* MyBatis
* MySQL

### Infra
* Docker 기반 컨테이너 운영
* Jenkins CI/CD 자동 배포 파이프라인 구축
* AWS EC2 (m7i-flex.large, 2vCPU, 8GB RAM)

---

## UX 포인트

* 입력창 내부 **토큰 UI**로 오입력 즉시 수정 가능
* 토큰 수량 증가로 반복 입력 최소화
* 즐겨찾기 바텀시트에서 관리
* 다크 테마 기준 가독성 최적화


---

## 대략적인 화면 소개

<img width="1772" height="1272" alt="image" src="https://github.com/user-attachments/assets/3a7ca604-58eb-4b48-8314-20ca8f92d97e" />

<img width="1780" height="922" alt="image" src="https://github.com/user-attachments/assets/2dad08f0-ca69-4f0c-95a7-b9eb5db7cc8f" />

---

## 배포 프로세스

GitHub Push
→ Jenkins Webhook 트리거
→ 빌드
→ Docker 이미지 생성
→ 기존 컨테이너 교체
→ 서비스 재기동

무중단에 가까운 자동 배포 환경을 구성했습니다.

---

## 개선 경험 (요약)

JWT 만료 시 무한 요청 반복 문제 해결

배포 후 프론트 API 경로 꼬임 문제 수정

CORS 설정 충돌 이슈 해결

HTTPS 및 리버스 프록시 구성 경험

---

## 실행 환경

* Java 21
* MySQL
* Docker

### 환경 변수
* `JWT_SECRET`
* `DB_USERNAME`
* `DB_PASSWORD`
* `GOOGLE_CLIENT_ID`
* `GOOGLE_CLIENT_SECRET`
* `OPENAI_API_KEY`


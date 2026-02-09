# Meal Tracker

https://juntodo.site

간단한 입력으로 오늘 먹은 음식을 기록하고 칼로리·단백질을 관리하는 개인용 식단 트래커입니다. 즐겨찾기와 빠른 입력 UX를 중심으로 설계되었습니다.
개인 프로젝트로 본인의 실사용을 중심으로 개발되었으며 지속적으로 개선 중인 프로젝트입니다.

---

## 핵심 기능

* **빠른 기록**
  * 먹은 거 대충 던지면 AI가 파싱하고 분석해서 결과 저장
  * 음식이 모호해도 알아서 추정하는 것이 핵심 포인트
  * 기록된 음식 성분을 직접 수정 가능

* **즐겨찾기(Favorites)**
  * 자주 먹는 음식 저장
  * 칼로리·단백질 입력
  * 클릭 시 입력창 토큰으로 추가
  * 동일 음식 클릭 시 **수량 증가(xN)**
  * x 버튼으로 즉시 제거

* **최근 먹은 것**
  * 오늘 기록 기준 최근 항목 한 줄 노출
  * 클릭 시 토큰으로 추가

* **대시보드**
  * 오늘 섭취 요약(칼로리/단백질)
  * 날짜 선택
  * 기록 수정/삭제

* **기타**
  * OAuth2 기반 소셜 로그인

---

## 기술 스택

### Frontend
* React (Vite)

### Backend
* Spring Boot
* MyBatis
* MySQL

### Infra
* Docker
* Jenkins
* AWS EC2

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

## 배포 절차

GitHub push
→ Jenkins 트리거
→ 코드 체크아웃
→ 빌드
→ Docker 이미지 생성
→ 기존 컨테이너 중지
→ 새 컨테이너 실행
→ 서비스 정상 기동

---

## Backend 실행

### 요구사항
* Java 21
* MySQL
* OpenAI API 키

### 환경 변수
* `JWT_SECRET`
* `DB_USERNAME`
* `DB_PASSWORD`
* `GOOGLE_CLIENT_ID`
* `GOOGLE_CLIENT_SECRET`
* `OPENAI_API_KEY`


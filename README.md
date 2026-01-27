# CrossFit 예약 애플리케이션

## 개요
크로스핏 박스를 위한 예약/출석/기록/공지/회원권 관리를 제공하는 플랫폼입니다. 관리자/코치/회원의 역할 기반 권한과 JWT 인증을 사용합니다.

## 빠른 시작
```bash
./gradlew bootRun
```

기본 시드 계정은 아래와 같습니다.
- 관리자: `admin@crossfit.local` / `admin123`
- 코치: `coach@crossfit.local` / `coach123`
- 회원: `member@crossfit.local` / `member123`

H2 콘솔은 `http://localhost:8080/h2-console`에서 확인할 수 있습니다.

## 주요 기능
- **회원 인증**: 이메일+비밀번호 로그인, JWT 발급, 역할(Role)=ADMIN, COACH, MEMBER.
- **수업 예약/조회**:
  - 고정 타임슬롯 제공: `09:00`, `10:30`, `17:30`, `19:00`, `20:30`.
  - 정원/대기열, 마감시간, 취소 제한(수업 1시간 전) 적용.
- **출석 캘린더**: 출석일 표시, 월간 출석률(=출석일/월). 평일 하루 운동 횟수 제한 없음.
- **WOD/기록**: 당일 WOD(관리자/코치 등록), 개인 기록(타임/RM/라운드).
- **공지**: 관리자만 작성/수정, 회원은 읽기 전용.
- **회원권**: 시작일/종료일 기반 잔여일수 계산. 종류는 기간제/횟수제 중 하나만 지원(우선).
- **Admin/Coach 전용 설정**:
  - 공지 작성
  - 회원 관리(관리자/코치 권한이 있는 계정만 모든 페이지 열람 가능)
  - 수업 정원 관리(기본 제한 없음)
  - 사진 업로드

## 화면 흐름
1. 로그인 → 홈(오늘 WOD/공지/내 회원권 카드)
2. 예약 탭: 날짜 선택 → 세션 카드(정원/예약자수/내 상태) → 예약/취소
3. 캘린더 탭: 출석일 하이라이트·월간 출석률
4. 기록 탭: 오늘 WOD 기반 기록 입력/히스토리
5. 공지 탭: 목록/상세(회원은 읽기만)
6. 프로필: 회원권 잔여일/설정(푸시, 약관)

## 기술 스택
- **백엔드**: Spring Boot (Java 21 또는 Kotlin), Spring Security(JWT), Spring Data JPA, MySQL, Flyway, Gradle, Docker
- **안드로이드**: Kotlin, Jetpack Compose, MVVM, Coroutines/Flow, Hilt, Retrofit

## 도메인 모델(초안)
- **User**: id, email, passwordHash, role, status, createdAt
- **Session**: id, date, timeSlot, capacity, cutoffAt
- **Reservation**: id, userId, sessionId, status(RESERVED/WAITLIST/CANCELED), createdAt
- **Attendance**: id, userId, sessionId, attendedAt
- **Wod**: id, date, title, description, createdBy
- **Record**: id, userId, wodId, type(TIME/RM/ROUND), value, createdAt
- **Announcement**: id, title, body, createdBy, createdAt, updatedAt
- **Membership**: id, userId, type(PERIOD/COUNT), startDate, endDate, remainingCount

## API 설계(초안)
### 인증
- `POST /api/auth/login` → JWT 발급

### 예약/조회
- `GET /api/sessions?date=YYYY-MM-DD` → 특정 날짜 세션 목록
- `POST /api/sessions/{id}/reservations` → 예약
- `DELETE /api/sessions/reservations/{id}` → 취소(수업 1시간 전 제한)
- `PATCH /api/sessions/{id}/capacity` → 정원 변경(ADMIN/COACH)

### 출석
- `GET /api/attendance?month=YYYY-MM` → 출석일/월간 출석률
- `POST /api/attendance` → 출석 체크(관리자/코치)

### WOD/기록
- `GET /api/wod/today` → 오늘 WOD
- `POST /api/wod` → WOD 등록(관리자/코치)
- `POST /api/records` → 개인 기록 등록
- `GET /api/records?month=YYYY-MM` → 히스토리

### 공지
- `GET /api/announcements` → 목록
- `GET /api/announcements/{id}` → 상세
- `POST /api/announcements` → 작성(ADMIN)
- `PUT /api/announcements/{id}` → 수정(ADMIN)

### 회원권
- `GET /api/memberships/me` → 내 회원권
- `POST /api/memberships` → 회원권 등록(ADMIN/COACH)

## 비즈니스 규칙(초안)
- 예약 취소는 수업 시작 1시간 전까지만 가능.
- 정원이 설정되지 않은 세션은 무제한(기본값).
- 대기열은 정원 초과 시 `WAITLIST` 상태로 생성.
- 출석률은 월 기준으로 계산(월간 출석일/해당 월).

## 2차 로드맵
- 사진 업로드, 푸시 알림, 결제, 복수 지점, 코치 배정, 리더보드

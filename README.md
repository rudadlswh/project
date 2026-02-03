# CrossFit 예약 애플리케이션

## 개요
크로스핏 박스를 위한 예약/출석/기록/공지/회원권 관리를 제공하는 플랫폼입니다. 관리자/코치/회원의 역할 기반 권한과 JWT 인증을 사용합니다.

## 빠른 시작
```bash
./gradlew bootRun
```

## 백엔드/안드로이드 개발 실행
### 백엔드 실행(포트 8081)
```bash
cd /Users/chogyeongmin/develop/crossfit
./gradlew bootRun
```

8081 포트가 이미 사용 중이면 아래로 확인 후 종료하세요.
```bash
lsof -nP -iTCP:8081 -sTCP:LISTEN
kill <PID>
```

Windows라면:
```bat
taskkill /PID <PID> /F
```

### 안드로이드 재설치
```bash
cd /Users/chogyeongmin/develop/crossfit/android
./gradlew installDebug
```

에뮬레이터 기준 API 주소는 `http://10.0.2.2:8081/`입니다.
기본 시드 계정은 아래와 같습니다.
- 관리자: `admin@crossfit.local` / `admin123`
- 코치: `coach@crossfit.local` / `coach123`
- 회원: `member@crossfit.local` / `member123`

H2 콘솔은 `http://localhost:8081/h2-console`에서 확인할 수 있습니다.

## 주요 기능
- **회원 인증**: 이메일+비밀번호 로그인, JWT 발급, 역할(Role)=ADMIN, COACH, MEMBER.
- **수업 예약/조회**:
  - 고정 타임슬롯 제공: `09:00`, `10:30`, `17:30`, `19:00`, `20:30`.
  - 정원/대기열, 마감시간, 취소 제한(수업 1시간 전) 적용.
  - 대기열 자동 승격(취소/정원 변경 시).
  - 회원권 유효성 검증(기간/횟수) 및 COUNT 소진/환불 처리.
- **출석 캘린더**: 출석일 표시, 월간 출석률(=출석일/월). 회원 전용 화면(관리자/코치는 숨김).
- **WOD/기록**: 당일 WOD(관리자/코치 등록), 개인 기록(타임/RM/라운드), 이미지 업로드.
- **공지**: 관리자만 작성/수정, 회원은 읽기 전용.
- **회원권**: 시작일/종료일 기반 잔여일수 계산(관리자/코치 홈에는 미표시).
- **관리자/코치 전용 기능**:
  - 예약자 수/명단 확인(슬롯 클릭 시 목록 표시)
  - 회원 기록 일괄 등록
  - WOD 수정, 공지 작성
  - 운동 이미지 업로드(갤러리 선택)
  - 관리자 도구: 코치 등록, 회원권 기간 연장

## 화면 흐름
1. 로그인 → 홈(오늘 WOD/공지/회원권 카드)
2. 예약 탭: 날짜 선택 → 세션 카드(예약/예약자수) → 예약/취소 또는 명단 확인
3. 캘린더 탭: 출석일 하이라이트·월간 출석률(회원 전용)
4. 기록 탭: 오늘 WOD 기반 기록 입력/히스토리, 관리자/코치 일괄 등록
5. 공지 탭: 목록/상세(관리자 작성 가능)
6. 프로필: 설정(약관 등)

## 기술 스택
- **백엔드**: Spring Boot (Java 21 또는 Kotlin), Spring Security(JWT), Spring Data JPA, MySQL, Flyway, Gradle, Docker
- **안드로이드**: Kotlin, Jetpack Compose, MVVM, Coroutines/Flow, Hilt, Retrofit

## 도메인 모델(요약)
- **User**: id, email, passwordHash, displayName, role, active, createdAt
- **Session**: id, date, timeSlot, capacity, cutoffAt
- **Reservation**: id, userId, sessionId, status(RESERVED/WAITLIST/CANCELED), createdAt
- **Attendance**: id, userId, attendedDate
- **Wod**: id, date, title, type, description, createdBy
- **Record**: id, userId, wodId, type(TIME/RM/ROUND), value, imageUrl, recordDate, createdAt
- **Announcement**: id, title, body, createdBy, createdAt, updatedAt
- **Membership**: id, userId, type(PERIOD/COUNT), startDate, endDate, remainingCount

## API (현재)
기본은 `/api` 프리픽스 기준입니다.
### 인증
- `POST /api/auth/login` → JWT 발급
- `POST /api/auth/register` → 회원가입/로그인 응답

### 예약/조회
- `GET /api/sessions?date=YYYY-MM-DD` → 특정 날짜 세션 목록
- `GET /api/sessions/{id}/reservations` → 예약자 명단(ADMIN/COACH)
- `PATCH /api/sessions/{id}/capacity` → 정원 변경(ADMIN/COACH)
- `POST /api/reservations` → 예약
- `DELETE /api/reservations?date=YYYY-MM-DD&timeSlot=HH:mm` → 취소
- `GET /api/reservations/me` → 내 예약 목록

### 출석
- `GET /api/attendance/monthly?month=YYYY-MM` → 출석일/월간 출석률
- `POST /api/attendance` → 출석 체크(관리자/코치)

### WOD/기록
- `GET /api/wod?date=YYYY-MM-DD` → 특정 날짜 WOD
- `POST /api/wod` → WOD 등록(관리자/코치)
- `DELETE /api/wod?date=YYYY-MM-DD` → WOD 삭제(관리자/코치)
- `POST /api/records` → 개인 기록 등록(이미지 URL 포함 가능)
- `POST /api/records/bulk` → 기록 일괄 등록(ADMIN/COACH)
- `GET /api/records?month=YYYY-MM` → 히스토리
- `GET /api/records/my` → 내 기록 전체

### 공지
- `GET /api/notices` → 목록
- `GET /api/notices/{id}` → 상세
- `POST /api/notices` → 작성(ADMIN)

### 회원권
- `GET /api/memberships/me` → 내 회원권
- `POST /api/memberships` → 회원권 등록(ADMIN/COACH)
- `POST /api/admin/memberships/extend` → 회원권 연장(ADMIN)

### 관리자
- `POST /api/admin/coaches` → 코치 등록(ADMIN)

### 업로드
- `POST /api/uploads/images` → 이미지 업로드(multipart, field=`image`)
- `GET /uploads/{filename}` → 업로드 이미지 정적 제공

## 비즈니스 규칙(핵심)
- 예약 취소는 수업 시작 1시간 전까지만 가능.
- 정원이 설정되지 않은 세션은 무제한(기본값).
- 대기열은 정원 초과 시 `WAITLIST` 상태로 생성.
- 대기열은 취소/정원 변경 시 자동 승격.
- 회원권이 없는 회원은 예약 불가. COUNT는 예약 시 차감, 취소 시 환불.
- 출석/예약은 중복 생성 불가(유니크 제약).

## 2차 로드맵
- 사진 업로드, 푸시 알림, 결제, 복수 지점, 코치 배정, 리더보드

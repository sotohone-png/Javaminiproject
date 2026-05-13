🐾 D-Care: 스마트 다견 관리 시스템

D-Care는 당뇨 관리가 필요한 반려견들을 위한 스마트 인슐린 투여 관리 솔루션입니다. 다견 가정에서도 효율적으로 반려견별 투여 기록을 관리하고 통계를 확인할 수 있도록 설계되었습니다.




🚀 프로젝트 개요

이 프로젝트는 반려견의 몸무게와 인슐린 종류에 따른 최적의 투여량을 계산하고, 투여 시간을 기록하여 체계적인 건강 관리를 돕습니다. GUI(Swing)와 CLI(Console) 환경을 모두 지원하여 사용자의 편의성을 높였습니다.




🛠 기술 스택

•
Language: Java 11+

•
UI Framework: Java Swing (GUI), Console (CLI)

•
Data Storage: Text-based File System (dogs.txt, history.txt)

•
Architecture: Service-Oriented Architecture (SOA) 스타일의 계층 구조




📑 시스템 주요 기능

1. 반려견 관리

•
반려견 등록, 삭제 및 목록 조회

•
중복 이름 방지를 위한 자동 넘버링 기능

•
몸무게 변동에 따른 데이터 동기화

2. 인슐린 투여 관리

•
지속성 인슐린(캐닌슐린): 12시간 간격, 0.5 units/kg 기준 투여량 계산

•
속효성 인슐린(휴멀린): 6시간 간격, 0.3 units/kg 기준 투여량 계산

•
실시간 투여 시간 기록 및 다음 투여 예정 시간 안내

3. 데이터 분석 및 통계

•
반려견별 누적 투여 횟수 및 총 투여량 리포트 생성

•
과거 기록(삭제된 반려견 포함)에 대한 상세 로그 조회 및 관리

4. 데이터 지속성

•
프로그램 종료 시 자동/선택적 데이터 저장

•
실행 시 기존 데이터 로드 및 정규화(Normalization)




🏗 클래스 구조 (Class Architecture)

프로젝트는 역할에 따라 다음과 같이 구분됩니다.

구분
클래스명
역할 설명
Domain
Dog
반려견의 이름, 몸무게, 최근 투여 시간을 관리하는 엔티티
Logic
Insulin, MedicalAlert
인슐린 투여량 계산 및 스케줄링을 위한 추상 클래스 및 인터페이스
Service
DcareService, HistoryService
비즈니스 로직 처리 및 통계 데이터 가공
Persistence
DataManager
파일 시스템 기반의 데이터 로드 및 저장
Presentation
DcareApp, DcareSystem
각각 GUI(Swing)와 CLI 환경의 사용자 인터페이스 제공
Exception
MedicalDangerException
의료적 위험 상황 발생 시 처리를 위한 사용자 정의 예외







🖥 실행 방법



---
# 클래스 다이어그램
<img width="1825" height="1711" alt="제목 없는 다이어그램" src="https://github.com/user-attachments/assets/693970eb-7b36-41f3-9ff1-661f48447451" />


---



# 1.기본 작동 화면

<img width="586" height="647" alt="Honeycam 2026-05-12 11-59-51" src="https://github.com/user-attachments/assets/7f8a253a-9cd2-4de3-9ea0-1db655672ab1" />


# 2.오류 발생 화면
### 2. 오류 발생 화면

| 1. 인슐린 종류 선택 오류 | 2. 몸무게 입력 예외 처리 | 3. 주사 시간 예외 처리 |
| :---: | :---: | :---: |
| <img width="355" height="158" alt="Honeycam 2026-05-12 11-54-59" src="https://github.com/user-attachments/assets/f91d50a9-cacf-4bf9-a7ec-46cbe4a9346c" /> | <img width="359" height="184" alt="Honeycam 2026-05-12 11-57-20" src="https://github.com/user-attachments/assets/342fe21b-29bc-42fb-84f7-cb8fc5ce2805" /> | <img width="391" height="199" alt="Honeycam 2026-05-12 11-58-23" src="https://github.com/user-attachments/assets/9dd1b270-60d2-49f9-a975-590eea287737" /> |

---

# 수정 내용

### 🕒 주사 시간 입력 로직의 진화 (UX 개선)

### 1. Before: 엄격한 수동 입력 방식

사용자가 반드시 시계나 휴대폰을 확인하고, 정해진 형식(`HH:mm`)에 맞춰 직접 타이핑해야 했습니다.

- **동작 방식:** 오직 `LocalTime.parse()`만 수행.
- **사용자 입력:** `08:30` (직접 시계 확인 후 입력)
- **문제점:** `:` 기호를 빠뜨리거나 한 자릿수만 입력하면 프로그램이 예외를 발생시키며 멈추거나 재입력을 요구함.

---

### 2. After: 'now' 키워드를 활용한 지능형 입력

현재 시각을 시스템에서 즉시 가져오는 기능을 추가하여 편의성을 극대화했습니다.

- **동작 방식:** 입력값이 `now`인지 먼저 체크 후, 맞으면 `LocalTime.now()` 실행.
- **사용자 입력:** `now` (타이핑 3번으로 끝)
- **장점:** 주사 직후 바로 기록하는 사용자의 번거로움을 90% 이상 줄여주며, 오타로 인한 예외 발생 확률이 낮아짐.

```java
// [Before] 오직 형식에 맞는 문자열만 가능
System.out.print("▶ 마지막 주사 시간: ");
String input = sc.nextLine();
LocalTime lastTime = LocalTime.parse(input); // 형식 틀리면 바로 Exception!

// --------------------------------------------------

// [After] 키워드 기반 유연한 입력 처리
System.out.print("▶ 마지막 주사 시간 (HH:mm 또는 'now'): ");
String input = sc.nextLine();

LocalTime lastTime;
if (input.equalsIgnoreCase("now")) {
    lastTime = LocalTime.now(); // 시스템 현재 시간 자동 할당
    System.out.println("현재 시간(" + lastTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")으로 설정되었습니다.");
} else {
    lastTime = LocalTime.parse(input); // 기존처럼 수동 입력도 지원
}
```
---
🔮 향후 개선 아이디어

 인슐린 종류 추가 (혼합형 등)
 혈당 수치 입력 및 분석 기능
 투여 히스토리 파일 저장 (history.txt)
 GUI 버전 (JavaFX)

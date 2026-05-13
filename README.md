# 🐾 D-Care: 스마트 다견 관리 시스템

D-Care는 당뇨 관리가 필요한 반려견들을 위한 스마트 인슐린 투여 관리 솔루션입니다. 다견 가정에서도 효율적으로 반려견별 투여 기록을 관리하고 통계를 확인할 수 있도록 설계되었습니다.


🌟 프로젝트 동기 (Motivation)

반려견의 당뇨 관리는 정확한 용량과 엄격한 시간 엄수가 생명입니다. 특히 여러 마리의 강아지를 키우는 가정에서는 투여 기록이 섞이거나 용량 계산에 실수가 생길 위험이 큽니다. 이러한 실수를 방지하고, 과거 기록을 바탕으로 건강 상태를 한눈에 파악하고자 이 프로젝트를 시작하게 되었습니다.


# 🚀 프로젝트 개요

이 프로젝트는 반려견의 몸무게와 인슐린 종류에 따른 최적의 투여량을 계산하고, 투여 시간을 기록하여 체계적인 건강 관리를 돕습니다. JavaFX 기반의 현대적인 GUI 환경을 제공하여 사용자의 편의성을 극대화했습니다.

---

# 🛠 기술 스택

- **Language**: Java 11+

- **UI Framework**: JavaFX (Modern GUI)

- **Data Storage**: Text-based File System (dogs.txt, history.txt)

- **Architecture**: Service-Oriented Architecture (SOA) 스타일의 계층 구조 분리

---

📑 시스템 주요 기능

**1. 반려견 관리**

- 반려견 등록 및 삭제: 이름과 몸무게를 기반으로 간편하게 관리

- 중복 이름 방지: 동일 이름 등록 시 자동으로 넘버링(예: 초코 2) 부여

- 실시간 데이터 동기화: 몸무게 변동 시 즉각적인 투여량 계산 반영

**2. 인슐린 투여 관리**

- 지속성 인슐린(캐닌슐린): 12시간 간격, 0.5 units/kg 기준 자동 계산

- 속효성 인슐린(휴멀린): 6시간 간격, 0.3 units/kg 기준 자동 계산

- 스마트 기록: 현재 시간 자동 입력 및 맞춤형 투여 시간 설정 가능

**3. 데이터 분석 및 통계**

- 누적 리포트: 반려견별 총 투여 횟수 및 누적 투여량 시각화 (Table View)

- 이력 보존: 관리 목록에서 삭제된 반려견의 데이터도 🕒이전기록으로 안전하게 보존

**4. 데이터 지속성 및 안전성**

- 자동 저장: 데이터 변경 시 실시간 파일 시스템 저장

- 데이터 정규화: 실행 시 기존 텍스트 데이터의 오류를 검사하고 정규화(Normalization) 수행

---


# 🏗 클래스 구조 (Class Architecture)

프로젝트는 역할에 따라 다음과 같이 구분됩니다.

| 구분 | 클래스명 | 역할 설명 |
| --- | --- | --- |
| **Domain** | `Dog` | 반려견의 이름, 몸무게, 최근 투여 시간을 관리하는 엔티티 |
| **Logic** | `Insulin`, `MedicalAlert` | 인슐린 투여량 계산 및 스케줄링을 위한 추상 클래스 및 인터페이스 |
| **Service** | `DcareService`, `HistoryService` | 비즈니스 로직 처리 및 통계 데이터 가공 |
| **Persistence** | `DataManager` | 파일 시스템 기반의 데이터 로드 및 저장 |
| **Presentation** | `DcareApp`, `DcareSystem` | 각각 GUI(Swing)와 CLI 환경의 사용자 인터페이스 제공 |
| **Exception** | `MedicalDangerException` | 의료적 위험 상황 발생 시 처리를 위한 사용자 정의 예외 |



---

🏗️ 시스템 아키텍처 (Code Structure)

이 프로젝트는 유지보수와 확장성을 고려하여 관심사의 분리(SoC) 원칙을 철저히 따릅니다.

1. Domain Model (캡슐화 & 상속)

Dog.java: 반려견의 기본 정보와 최근 투여 상태를 관리하는 객체.

Insulin.java (Abstract): 인슐린의 공통 속성을 정의.

LongActingInsulin / ShortActingInsulin: 인슐린 종류별로 다른 계산 로직과 안전 수치를 구현 (다형성 활용).

2. Business Logic (서비스 계층)

DcareService.java: 전체 시스템의 '두뇌'. 중복 이름 자동 넘버링, 데이터 동기화, 통계 계산 등을 수행합니다.

MedicalAlert.java (Interface): 모든 인슐린이 반드시 갖춰야 할 안전 규칙(최대 용량, 최소 간격)을 강제합니다.

3. Data Persistence (데이터 지속성)

DataManager.java: 파일 I/O를 담당합니다. 파이프(|) 구분자를 활용하여 데이터의 파싱 오류를 최소화하였습니다.

---

🛡️ 핵심 차별점 (Key Highlights)

✅ 의료적 안전 장치 (Medical Guardrails)

과다 투여 방지: 인슐린별로 설정된 getMaxDose()를 초과할 경우 MedicalDangerException을 발생시켜 사용자에게 경고합니다.

최소 투여 간격 체크: 너무 짧은 시간 내에 재투여가 시도되면 경고 메시지를 출력하여 저혈당 쇼크를 방지합니다.

✅ 데이터 무결성 보장 (Data Self-Healing)

프로그램 실행 시, 텍스트 파일의 데이터를 전수 조사하여 이름 중복을 해결하고, 변경된 이름에 맞춰 과거 기록들까지 실시간으로 업데이트합니다.

---

🖼️ UI/UX 설계

Swing Custom Design: 기본 Swing의 투박함을 벗어나기 위해 현대적인 컬러 팔레트(#1a5fa8, #e6f1fb 등)와 여백(Padding) 시스템을 도입하여 사용자 친화적인 인터페이스를 구현했습니다.

---
🛠️ 실행 방법 (Installation)

저장소를 클론합니다.

Java 11 이상의 환경에서 DcareLauncher.java를 실행합니다.

(JavaFX 설정이 필요할 수 있습니다.)

# 실행 예시
java -cp . dcare.DcareLauncher

---







# 클래스 다이어그램
<img width="1825" height="1711" alt="제목 없는 다이어그램" src="https://github.com/user-attachments/assets/693970eb-7b36-41f3-9ff1-661f48447451" />


---



# 1.기본 작동 화면

<img width="700" height="643" alt="Honeycam 2026-05-13 16-02-38" src="https://github.com/user-attachments/assets/a4cd9eee-67ca-4bb5-8374-4031b9d87edb" />

<img width="700" height="643" alt="Honeycam 2026-05-13 16-03-17" src="https://github.com/user-attachments/assets/307a2d5a-54c7-4f57-aae0-94ac4bccb62b" />




# 2.오류 발생 화면

 <img width="700" height="643" alt="Honeycam 2026-05-13 16-06-48" src="https://github.com/user-attachments/assets/5c164387-d001-411f-aa55-a9acdd4abf29" /> 

 <img width="702" height="649" alt="Honeycam 2026-05-13 16-12-12" src="https://github.com/user-attachments/assets/d30e2e89-9a20-487d-be46-a5ba21d1255d" /> 

 <img width="702" height="649" alt="Honeycam 2026-05-13 16-12-41" src="https://github.com/user-attachments/assets/6736a208-90ae-433c-a129-9147816a2024" /> 

---

# 수정 내용




---


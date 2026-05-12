## 강아지 인슐린 투여 시간 관리 프로그램

## 📑 프로젝트 기획 의도: D-Care (Diabetes Dog Care)

### 1. 문제 정의 (Problem Statement)

"반려동물의 당뇨병은 철저한 시간 관리와 정확한 용량 투여가 생명입니다. 하지만 일반적인 보호자가 매일 두 번, 정해진 시간에 복잡한 단위(Unit)를 계산하여 주사하는 과정에서 **기록 누락이나 계산 착오**라는 인적 오류(Human Error)가 발생할 위험이 큽니다. 이러한 실수는 반려동물의 저혈당 쇼크 등 치명적인 결과로 이어질 수 있습니다."

### 2. 기획 배경 및 동기 (Motivation)

"저는 기술이 가장 가치 있게 쓰이는 곳은 '생명을 보호하는 영역'이라고 생각했습니다. 당뇨견을 키우는 보호자들이 겪는 심리적 부담감(시간 망각에 대한 불안, 투여량 계산의 번거로움)을 자바 프로그램을 통해 해결하고자 본 프로젝트를 시작하게 되었습니다."

### 3. 해결 방안 (Technical Solution)

"단순한 계산기를 넘어, **자바의 객체 지향 원리**를 활용하여 다음과 같은 안전장치를 마련했습니다."

- **정확성(Accuracy):** 인슐린 종류에 따른 자동 계산 로직으로 오차 없는 투여량 산출.
- **안전성(Safety):** 사용자 정의 예외 처리(`MedicalDangerException`)를 통해 위험 수치 투여를 시스템적으로 차단.
- **지속성(Persistence):** 투여 히스토리 관리 기능을 통해 보호자가 과거 기록을 직관적으로 확인 가능.

### 4. 최종 목표 (Vision)

"보호자의 실수를 줄여 반려동물의 건강 수명을 연장하고, 보호자에게는 관리의 편리함을 제공하는 '신뢰할 수 있는 디지털 케어 가이드'를 만드는 것이 본 프로젝트의 목표입니다."

---

## 🚀 D-Care 시스템 주요 기능 요약

### 1. 동적 객체 생성 및 관리 (다형성 활용)

- **기능:** 사용자가 선택한 인슐린 종류(지속성/속효성)에 따라 실시간으로 적절한 객체를 생성합니다.
- **기술 포인트:** `Insulin` 부모 클래스 타입을 사용하여, 서로 다른 계산 공식(Override)을 가진 자식 객체들을 하나의 로직에서 유연하게 관리합니다.

### 2. 정밀 용량 계산 엔진 (Medical Calculation)

- **기능:** 입력된 강아지의 몸무게와 선택된 인슐린의 계수를 결합하여 최적의 투여 유닛(Unit)을 산출합니다.
- **기술 포인트:** 인터페이스(`MedicalAlert`)에 정의된 추상 메서드를 각 클래스 특성에 맞게 구현하여 데이터의 일관성을 확보했습니다.

### 3. 지능형 스케줄링 가이드 (Time Tracking)

- **기능:** 마지막 투여 시간을 기준으로 차기 주사 권장 시간을 자동 계산하여 안내합니다.
- **기술 포인트:** `java.time.LocalTime` API를 활용하여 24시간 체계 내의 시간 연산을 정확하게 수행하며, `now` 키워드를 통한 현재 시간 자동 매핑(Mapping) 기능을 제공합니다.

### 4. 2중 안전 예외 처리 시스템 (Double Safeguard)

- **기능:** 시스템 다운 방지 및 의료 사고 예방을 위한 단계별 예외 처리를 수행합니다.
    - **1단계 (형식 검증):** 문자 입력, 시간 형식 오류 등 데이터 타입 불일치를 `try-catch`로 차단.
    - **2단계 (논리 검증):** 몸무게가 0이하인 경우나, 계산된 용량이 치사량을 초과하는 경우 사용자 정의 예외(`MedicalDangerException`)를 발생시켜 처방 중단.

### 5. 투여 이력 로그 관리 (History Logging)

- **기능:** 프로그램 실행 중 발생한 모든 계산 결과를 리스트에 누적 저장합니다.
- **기술 포인트:** `ArrayList` 컬렉션을 활용하여 가변적인 데이터를 수집하고, 프로그램 종료 시 일괄적으로 히스토리를 출력하여 보호자의 복약 기록 관리를 돕습니다.

---

### 🛠 D-Care 프로젝트 기술 스택 (Tech Stack)

### 1. Core Language & Runtime

- **Java SE 8+**: 최신 날짜/시간 API와 컬렉션 프레임워크를 활용하기 위해 자바 표준 에디션을 사용했습니다.
- **Object-Oriented Programming (OOP)**: 프로그램을 단순히 순차적으로 실행하는 것이 아니라, 객체 간의 협력 구조로 설계했습니다.

### 2. 핵심 적용 기술 (Core Java Skills)

- **Interface & Abstract Class**:
    - `MedicalAlert` 인터페이스로 서비스 규격을 정의하고, `Insulin` 추상 클래스로 공통 속성을 캡슐화했습니다.
- **Polymorphism (다형성)**:
    - 부모 타입 참조 변수를 통해 `LongActing`, `ShortActing` 객체를 동적으로 제어하여 확장성을 확보했습니다.
- **Exception Handling (예외 처리)**:
    - `try-catch-finally` 구조와 `throw` 키워드를 사용한 사용자 정의 예외(`MedicalDangerException`) 처리로 시스템 안정성을 강화했습니다.
- **Java Collection Framework**:
    - `ArrayList`를 활용하여 정해지지 않은 수의 투여 데이터를 메모리에 저장하고 관리하는 로직을 구현했습니다.

### 3. Standard Library (표준 라이브러리)

- **java.time (Date/Time API)**: `LocalTime`, `DateTimeFormatter`를 사용하여 정밀한 시간 연산 및 포맷팅을 수행했습니다.
- **java.util (Scanner & Collections)**: 사용자 입출력 제어 및 데이터 구조 관리를 위해 사용했습니다.

### 4. Development Environment (IDE)

- **Eclipse / IntelliJ**: 자바 개발 환경에서 코드 작성, 디버깅 및 컴파일을 수행했습니다.

---

# Class 
```mermaid
classDiagram
    class MedicalAlert {
        <<interface>>
        +getSchedule(LocalTime lastShotTime) String
        +calculateDose(double weight) double
    }

    class Insulin {
        <<abstract>>
        #String brandName
        #int intervalHours
        +getBrandName() String
    }

    class LongActingInsulin {
        +LongActingInsulin()
        +getSchedule(LocalTime) String
        +calculateDose(double) double
    }

    class ShortActingInsulin {
        +ShortActingInsulin()
        +getSchedule(LocalTime) String
        +calculateDose(double) double
    }

    class MedicalDangerException {
        +MedicalDangerException(String msg)
    }

    class DcareSystem {
        -List~String~ history
        +main(String[] args) void
    }

    MedicalAlert <|.. Insulin : implements
    Insulin <|-- LongActingInsulin : extends
    Insulin <|-- ShortActingInsulin : extends
    Exception <|-- MedicalDangerException : extends
    DcareSystem ..> Insulin : uses
    DcareSystem ..> MedicalDangerException : throws
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

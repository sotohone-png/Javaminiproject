[제목 없는 다이어그램.drawio (1).html](https://github.com/user-attachments/files/27694917/drawio.1.html)## 강아지 인슐린 투여 시간 관리 프로그램

## 📌 프로젝트 개요

당뇨를 앓고 있는 반려견의 인슐린 투여를 체계적으로 관리하기 위한 Java 콘솔 애플리케이션입니다.  
인슐린 종류(지속성 / 속효성)를 선택하면 몸무게 기반 권장 용량과 다음 투여 시간을 자동 계산하고,  
위험 수치 초과 시 예외를 발생시켜 보호자에게 즉시 경고합니다.

**학습 목표**: Java OOP 3대 핵심 개념을 실제 도메인에 적용

| 개념 | 적용 위치 |
|------|-----------|
| Interface | `MedicalAlert` — 모든 의료 알림의 공통 규격 정의 |
| Inheritance | `Insulin` (추상) → `LongActingInsulin` / `ShortActingInsulin` |
| Exception | `MedicalDangerException` — 고용량 투여 위험 감지 |


---
## 🏗️ 클래스 구조

```
MedicalAlert (interface)
│   + getSchedule(LocalTime) : String
│   + calculateDose(double) : double
│
└── Insulin (abstract) ── implements MedicalAlert
        # brandName : String
        # intervalHours : int
        + getBrandName() : String
        │
        ├── LongActingInsulin   — 캐닌슐린, 12h 간격, dose = weight × 0.5
        └── ShortActingInsulin  — 휴멀린,   6h 간격, dose = weight × 0.3

MedicalDangerException (extends Exception)
└── dose > 8.0 units 시 throw

DcareSystem (main)
└── Scanner 입력 → Insulin 다형성 객체 생성 → 계산 → history 저장
```

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
# 클래스 다이어그램
[Uploadin<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>draw.io</title>
<meta http-equiv="refresh" content="0;URL='https://app.diagrams.net/#G1VL6CFDP_tUONB8Y2NmX43rgwmPd5tWbj'"/>
<meta charset="utf-8"/>
</head>
<body>
<div class="mxgraph" style="max-width:100%;border:1px solid transparent;" data-mxgraph="{&quot;highlight&quot;:&quot;#0000ff&quot;,&quot;nav&quot;:true,&quot;resize&quot;:true,&quot;xml&quot;:&quot;&lt;mxfile host=\&quot;app.diagrams.net\&quot;&gt;&lt;diagram name=\&quot;페이지-1\&quot; id=\&quot;Oh0K9LosG_j7E_v2uYSh\&quot;&gt;&lt;mxGraphModel dx=\&quot;2828\&quot; dy=\&quot;1570\&quot; grid=\&quot;0\&quot; gridSize=\&quot;10\&quot; guides=\&quot;1\&quot; tooltips=\&quot;1\&quot; connect=\&quot;1\&quot; arrows=\&quot;1\&quot; fold=\&quot;1\&quot; page=\&quot;1\&quot; pageScale=\&quot;1\&quot; pageWidth=\&quot;4681\&quot; pageHeight=\&quot;3300\&quot; math=\&quot;0\&quot; shadow=\&quot;0\&quot;&gt;&lt;root&gt;&lt;mxCell id=\&quot;0\&quot;/&gt;&lt;mxCell id=\&quot;1\&quot; parent=\&quot;0\&quot;/&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-1\&quot; parent=\&quot;1\&quot; style=\&quot;swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=26;fillColor=#FFFFFF;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;gradientColor=none;\&quot; value=\&quot;DcareLauncher\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;60\&quot; width=\&quot;140\&quot; x=\&quot;2504\&quot; y=\&quot;110\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-4\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-1\&quot; style=\&quot;text;strokeColor=none;fillColor=#FFFFFF;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;&amp;lt;div&amp;gt;+main(args:String[]):void&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;34\&quot; width=\&quot;140\&quot; y=\&quot;26\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-5\&quot; parent=\&quot;1\&quot; style=\&quot;html=1;whiteSpace=wrap;\&quot; value=\&quot;«external»&amp;lt;br&amp;gt;&amp;lt;b&amp;gt;JFrame&amp;lt;/b&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;50\&quot; width=\&quot;110\&quot; x=\&quot;2286\&quot; y=\&quot;110\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-7\&quot; parent=\&quot;1\&quot; style=\&quot;swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=26;fillColor=none;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;\&quot; value=\&quot;DcareApp\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;674\&quot; width=\&quot;377\&quot; x=\&quot;2277\&quot; y=\&quot;284\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-8\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-7\&quot; style=\&quot;text;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;strokeColor=default;\&quot; value=\&quot;-service:DcareService&amp;lt;div&amp;gt;-headerCountLabel: JLable&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-weightField : JTextField&amp;lt;div&amp;gt;--timeField : JTextField&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-longBtn : JRadioButton&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-shortBtn : JRadioButton&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-dosePreviewLabel : JLabel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-selectedDog : Dog&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-injPanel : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-statsPanel : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-histPanel : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-managePanel : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/div&amp;gt;&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;185\&quot; width=\&quot;377\&quot; y=\&quot;26\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-10\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-7\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;&amp;lt;div&amp;gt;+main(arg:String[]): void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+DcareApp()&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-bulidHeader() :JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-makeBoxPanel() : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-scroll(p : JPanel) : JScrollPane&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;- refreshPanel(idx : int) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;&amp;lt;div&amp;gt;-refreshAll() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-buildInj() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-buildStats() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-buildHist() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-buildManage() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-makeDogCard(dog : Dog) : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-updateDosePreview() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-handleSave() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-showAddDogDialog() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-sectionLbl(t : String) : JLabel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-alertBar(t : String) : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-statCard(title : String, value : String, sub : String) : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-badgeLbl(text : String, bg : Color, fg : Color) : JLabel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-smallBtn(text : String, fg : Color, bg : Color, border : Color) : JButton&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-styledField(text : String) : JTextField&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-inputLbl(t : String) : JLabel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-styleRadioBtn(btn : JRadioButton) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-divider() : JSeparator&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-vgap(h : int) : Component&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-fixWidth(c : JComponent) : JComponent&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-emptyState(t : String, s : String) : JPanel&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-refresh(p : JPanel) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-showAlert(msg : String) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-showInfo(msg : String) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-showConfirm(msg : String) : boolean&amp;lt;/div&amp;gt;&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;463\&quot; width=\&quot;377\&quot; y=\&quot;211\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-11\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-7\&quot; style=\&quot;endArrow=block;endSize=16;endFill=0;html=1;rounded=0;exitX=0.25;exitY=0;exitDx=0;exitDy=0;entryX=0.5;entryY=1;entryDx=0;entryDy=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-5\&quot; value=\&quot;Extends\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2307\&quot; y=\&quot;361\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2467\&quot; y=\&quot;361\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-12\&quot; parent=\&quot;1\&quot; style=\&quot;swimlane;fontStyle=1;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=26;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;\&quot; value=\&quot;DcareService\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;280\&quot; width=\&quot;403\&quot; x=\&quot;2260\&quot; y=\&quot;1061\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-13\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-12\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;-dogList : List&amp;amp;lt;Dog&amp;amp;gt;&amp;lt;div&amp;gt;-history : List &amp;amp;lt;String&amp;amp;gt;&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;41\&quot; width=\&quot;403\&quot; y=\&quot;26\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-14\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-12\&quot; style=\&quot;line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;strokeColor=inherit;\&quot; value=\&quot;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;8\&quot; width=\&quot;403\&quot; y=\&quot;67\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-15\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-12\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;+DcareService()&amp;lt;div&amp;gt;+refreshData() : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+addDog(name : String, weight : double) : void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+addInjection(dog:Dog,insulin:Insulin,weight:double,timeStr:String):Dog&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+fixDuplicateNameAndSyncHistory():void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-updateHistoryNameInHistory(oldNmae:String,newName:String):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+getStateData():List&amp;amp;lt;Object[]&amp;amp;gt;&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+save():void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+getDogList():List&amp;amp;lt;Dog&amp;amp;gt;&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+getHistory():List&amp;amp;lt;String&amp;amp;gt;&amp;lt;br&amp;gt;+removeHistory(index:int):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+removeDog(index:int):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;205\&quot; width=\&quot;403\&quot; y=\&quot;75\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-21\&quot; parent=\&quot;1\&quot; style=\&quot;swimlane;fontStyle=1;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=26;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;\&quot; value=\&quot;DcareSystem\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;210\&quot; width=\&quot;276\&quot; x=\&quot;2729\&quot; y=\&quot;1061\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-22\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-21\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;-dogList : List&amp;amp;lt;Dog&amp;amp;gt;&amp;lt;div&amp;gt;-history : List&amp;amp;lt;String&amp;amp;gt;&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-isDataChanged : boolean&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;58\&quot; width=\&quot;276\&quot; y=\&quot;26\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-23\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-21\&quot; style=\&quot;line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;strokeColor=inherit;\&quot; value=\&quot;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;8\&quot; width=\&quot;276\&quot; y=\&quot;84\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-24\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-21\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;+ main(args:String[]) : void&amp;lt;div&amp;gt;- fixDuplicateNameAndSyncHistory():void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-processInjection(sc :Scanner,menu:String):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-createNewDog(sc:Scanner):Dog&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-manageDogs(sc:Scanner):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;-handleExit(sc:Scanner):void&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+main(args:String[]):void&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;118\&quot; width=\&quot;276\&quot; y=\&quot;92\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-29\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;i&amp;gt;&amp;amp;lt;&amp;amp;lt;Interface&amp;amp;gt;&amp;amp;gt;&amp;lt;/i&amp;gt;&amp;lt;br&amp;gt;&amp;lt;b&amp;gt;MedicalAlert&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+getSchedule(lastShotTime:LocalTime):String&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+calculateDose(weight:double):double&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+getDescription():String&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;133\&quot; width=\&quot;260\&quot; x=\&quot;1826\&quot; y=\&quot;1164\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;i&amp;gt;&amp;amp;lt;&amp;amp;lt;abstract&amp;amp;gt;&amp;amp;gt;&amp;lt;/i&amp;gt;&amp;lt;br&amp;gt;&amp;lt;b&amp;gt;Insulin&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;#brandName:String&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;#intervalHours:int&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+Insulin(brandName:String,intervalHours:int)&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+getBrandName():String&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;138\&quot; width=\&quot;246\&quot; x=\&quot;2284\&quot; y=\&quot;1452\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-32\&quot; parent=\&quot;1\&quot; style=\&quot;swimlane;fontStyle=1;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;startSize=26;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;\&quot; value=\&quot;ShortActingInsulin\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;147\&quot; width=\&quot;265\&quot; x=\&quot;2359\&quot; y=\&quot;1670\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-34\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-32\&quot; style=\&quot;line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;strokeColor=inherit;\&quot; value=\&quot;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;8\&quot; width=\&quot;265\&quot; y=\&quot;26\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-35\&quot; parent=\&quot;fo5RpwrUIlXIoiz2kUDH-32\&quot; style=\&quot;text;strokeColor=none;fillColor=none;align=left;verticalAlign=top;spacingLeft=4;spacingRight=4;overflow=hidden;rotatable=0;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;whiteSpace=wrap;html=1;\&quot; value=\&quot;+ShortActingInsulin()&amp;lt;div&amp;gt;+getSchedule(lastShotTime : LocalTime):String&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+calculateDose(weight : double) : double&amp;lt;/div&amp;gt;&amp;lt;div&amp;gt;+getDescription():String&amp;lt;br&amp;gt;&amp;lt;div&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/div&amp;gt;&amp;lt;/div&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;113\&quot; width=\&quot;265\&quot; y=\&quot;34\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-36\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;DataManager&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;-DOG_FILE:String&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;-HIST_FILE:String&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+saveData(dogList:List&amp;amp;lt;Dog&amp;amp;gt;,history:List&amp;amp;lt;String&amp;amp;gt;):void&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+loadData(dogList:List&amp;amp;lt;Dog&amp;amp;gt;,history:List&amp;amp;lt;String&amp;amp;gt;):void&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;119\&quot; width=\&quot;311\&quot; x=\&quot;2974\&quot; y=\&quot;1455\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-38\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;Dog&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+ name:String&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+weight:double&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+lastInjectionTime:LocalTime&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+Dog(name:String,weight:double)&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+toString():String&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;144\&quot; width=\&quot;196\&quot; x=\&quot;2630\&quot; y=\&quot;1452\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-40\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;HistoryService&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+showDetailedState(history:List&amp;amp;lt;String&amp;amp;gt;,dogList:List&amp;amp;lt;Dog&amp;amp;gt;):void&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+manageHistoryMenu(sc:Scanner,history:List&amp;amp;lt;String&amp;amp;gt;,dogList:List&amp;amp;lt;Dog&amp;amp;gt;):boolean&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;105\&quot; width=\&quot;484\&quot; x=\&quot;3165\&quot; y=\&quot;1208\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-60\&quot; parent=\&quot;1\&quot; style=\&quot;verticalAlign=top;align=left;overflow=fill;html=1;whiteSpace=wrap;\&quot; value=\&quot;&amp;lt;p style=&amp;quot;margin:0px;margin-top:4px;text-align:center;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;LongActingInsulin&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;&amp;lt;br&amp;gt;&amp;lt;/p&amp;gt;&amp;lt;hr size=&amp;quot;1&amp;quot; style=&amp;quot;border-style:solid;&amp;quot;&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+LongActingInsulin&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+getSchedule(lastShotTime:LocalTime):String&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+calculateDose(weight:double):double&amp;lt;/p&amp;gt;&amp;lt;p style=&amp;quot;margin:0px;margin-left:4px;&amp;quot;&amp;gt;+getDescription():String&amp;lt;/p&amp;gt;\&quot; vertex=\&quot;1\&quot;&gt;&lt;mxGeometry height=\&quot;129\&quot; width=\&quot;303\&quot; x=\&quot;1941\&quot; y=\&quot;1670\&quot; as=\&quot;geometry\&quot;/&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-64\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-4\&quot; style=\&quot;endArrow=classic;endSize=16;endFill=1;html=1;rounded=0;exitX=0.5;exitY=1.029;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.782;entryY=0.001;entryDx=0;entryDy=0;entryPerimeter=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-7\&quot; value=\&quot;launches\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; x=\&quot;0.0026\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint as=\&quot;offset\&quot;/&gt;&lt;mxPoint x=\&quot;2474\&quot; y=\&quot;400\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2634\&quot; y=\&quot;400\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-65\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-10\&quot; style=\&quot;endArrow=classic;endSize=16;endFill=1;html=1;rounded=0;exitX=0.467;exitY=0.996;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.481;entryY=0.004;entryDx=0;entryDy=0;entryPerimeter=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-12\&quot; value=\&quot;uses\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2419\&quot; y=\&quot;1067\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2579\&quot; y=\&quot;1067\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-67\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-38\&quot; style=\&quot;endArrow=diamondThin;endSize=16;endFill=0;html=1;rounded=0;exitX=0.25;exitY=0;exitDx=0;exitDy=0;entryX=0.801;entryY=0.995;entryDx=0;entryDy=0;entryPerimeter=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-15\&quot; value=\&quot;aggregates\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2766\&quot; y=\&quot;1654\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2926\&quot; y=\&quot;1654\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-68\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-38\&quot; style=\&quot;endArrow=diamondThin;endSize=16;endFill=0;html=1;rounded=0;exitX=0.648;exitY=0.021;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.312;entryY=0.992;entryDx=0;entryDy=0;entryPerimeter=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-24\&quot; value=\&quot;aggregates\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2766\&quot; y=\&quot;1454\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2926\&quot; y=\&quot;1454\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-69\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-24\&quot; style=\&quot;endArrow=open;endSize=12;dashed=1;html=1;rounded=0;exitX=0.964;exitY=1.034;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.25;entryY=0;entryDx=0;entryDy=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-36\&quot; value=\&quot;uses\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2679\&quot; y=\&quot;1484\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2839\&quot; y=\&quot;1484\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-70\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-24\&quot; style=\&quot;endArrow=open;endSize=12;dashed=1;html=1;rounded=0;exitX=0.996;exitY=0.093;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0;entryY=0.25;entryDx=0;entryDy=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-40\&quot; value=\&quot;uses\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2933\&quot; y=\&quot;1356\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;3093\&quot; y=\&quot;1356\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-71\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-24\&quot; style=\&quot;endArrow=classic;endSize=12;dashed=1;html=1;rounded=0;entryX=0.711;entryY=0.007;entryDx=0;entryDy=0;entryPerimeter=0;exitX=-0.004;exitY=0.347;exitDx=0;exitDy=0;exitPerimeter=0;endFill=1;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; value=\&quot;uses\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2746\&quot; y=\&quot;1274\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2425.998\&quot; y=\&quot;1450.9679999999998\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-72\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-15\&quot; style=\&quot;endArrow=classic;endSize=12;dashed=1;html=1;rounded=0;exitX=0.543;exitY=0.995;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.431;entryY=0.014;entryDx=0;entryDy=0;entryPerimeter=0;endFill=1;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; value=\&quot;uses\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2784\&quot; y=\&quot;1440\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2944\&quot; y=\&quot;1440\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-74\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; style=\&quot;endArrow=block;endSize=12;dashed=1;html=1;rounded=0;exitX=0.007;exitY=0.629;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.577;entryY=0.987;entryDx=0;entryDy=0;entryPerimeter=0;endFill=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-29\&quot; value=\&quot;implements\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2066\&quot; y=\&quot;1436\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2226\&quot; y=\&quot;1436\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-75\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-60\&quot; style=\&quot;endArrow=block;endSize=16;endFill=0;html=1;rounded=0;exitX=0.443;exitY=-0.006;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0;entryY=0.75;entryDx=0;entryDy=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; value=\&quot;extends\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2258\&quot; y=\&quot;1706\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2418\&quot; y=\&quot;1706\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;mxCell id=\&quot;fo5RpwrUIlXIoiz2kUDH-76\&quot; edge=\&quot;1\&quot; parent=\&quot;1\&quot; source=\&quot;fo5RpwrUIlXIoiz2kUDH-32\&quot; style=\&quot;endArrow=block;endSize=16;endFill=0;html=1;rounded=0;exitX=0.478;exitY=0;exitDx=0;exitDy=0;exitPerimeter=0;entryX=0.675;entryY=0.981;entryDx=0;entryDy=0;entryPerimeter=0;\&quot; target=\&quot;fo5RpwrUIlXIoiz2kUDH-30\&quot; value=\&quot;extends\&quot;&gt;&lt;mxGeometry relative=\&quot;1\&quot; width=\&quot;160\&quot; as=\&quot;geometry\&quot;&gt;&lt;mxPoint x=\&quot;2258\&quot; y=\&quot;1706\&quot; as=\&quot;sourcePoint\&quot;/&gt;&lt;mxPoint x=\&quot;2418\&quot; y=\&quot;1706\&quot; as=\&quot;targetPoint\&quot;/&gt;&lt;/mxGeometry&gt;&lt;/mxCell&gt;&lt;/root&gt;&lt;/mxGraphModel&gt;&lt;/diagram&gt;&lt;/mxfile&gt;&quot;,&quot;toolbar&quot;:&quot;pages zoom layers lightbox&quot;,&quot;page&quot;:0}"></div>
<a style="position:absolute;top:50%;left:50%;margin-top:-128px;margin-left:-64px;" href="https://app.diagrams.net/#G1VL6CFDP_tUONB8Y2NmX43rgwmPd5tWbj" target="_blank"><img border="0" src="https://app.diagrams.net/images/drawlogo128.png"/></a>
</body>
</html>
g 제목 없는 다이어그램.drawio (1).html…]()

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

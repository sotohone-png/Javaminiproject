import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// 1. [Interface] 모든 의료 알림의 규격
interface MedicalAlert {
    String getSchedule(LocalTime lastShotTime);
    double calculateDose(double weight);
}

// 2. [Inheritance] 인슐린 공통 속성
abstract class Insulin implements MedicalAlert {
    protected String brandName;
    protected int intervalHours;

    public Insulin(String brandName, int intervalHours) {
        this.brandName = brandName;
        this.intervalHours = intervalHours;
    }
    public String getBrandName() { return brandName; }
}

// [다형성 활용] 지속성 인슐린
class LongActingInsulin extends Insulin {
    public LongActingInsulin() { super("캐닌슐린(지속성)", 12); }
    
    @Override
    public String getSchedule(LocalTime lastShotTime) {
        return lastShotTime.plusHours(intervalHours).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    @Override
    public double calculateDose(double weight) { return weight * 0.5; }
}

// [다형성 활용] 속효성 인슐린 (추가됨)
class ShortActingInsulin extends Insulin {
    public ShortActingInsulin() { super("휴멀린(속효성)", 6); }
    
    @Override
    public String getSchedule(LocalTime lastShotTime) {
        return lastShotTime.plusHours(intervalHours).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    @Override
    public double calculateDose(double weight) { return weight * 0.3; } // 속효성은 보통 용량이 다름
}

// 3. [Exception] 사용자 정의 예외
class MedicalDangerException extends Exception {
    public MedicalDangerException(String message) { super(message); }
}

public class DcareSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> history = new ArrayList<>(); // 투여 기록 저장용 리스트
        
        System.out.println("========================================");
        System.out.println("   🐾 D-Care: 스마트 당뇨견 관리 시스템   ");
        System.out.println("========================================");

        while (true) {
            try {
                System.out.println("\n[ 1.지속성(12h) | 2.속효성(6h) | exit.종료 ]");
                System.out.print("▶ 인슐린 종류를 선택하세요: ");
                String menu = sc.nextLine();

                if (menu.equalsIgnoreCase("exit")) break;

                Insulin selectedInsulin;
                if (menu.equals("1")) selectedInsulin = new LongActingInsulin();
                else if (menu.equals("2")) selectedInsulin = new ShortActingInsulin();
                else throw new IllegalArgumentException("잘못된 메뉴 선택입니다.");

                System.out.print("▶ 강아지 몸무게(kg): ");
                double weight = Double.parseDouble(sc.nextLine());
                if (weight <= 0) throw new IllegalArgumentException("몸무게는 0보다 커야 합니다.");

                System.out.print("▶ 마지막 주사 시간 (HH:mm 또는 'now'): ");
                String timeInput = sc.nextLine();
                
                LocalTime lastTime;
                if (timeInput.equalsIgnoreCase("now")) {
                    lastTime = LocalTime.now();
                } else {
                    lastTime = LocalTime.parse(timeInput);
                }

                double dose = selectedInsulin.calculateDose(weight);

                // 위험 수치 예외 발생
                if (dose > 8.0) {
                    throw new MedicalDangerException("고용량 투여 위험! (" + dose + " units) 수의사 상담 필수.");
                }

                String nextTime = selectedInsulin.getSchedule(lastTime);
                
                // 결과 출력 및 리스트 저장
                System.out.println("\n----------------------------------------");
                System.out.println("✅ 계산 완료: " + selectedInsulin.getBrandName());
                System.out.printf("권장 용량: %.1f units | 다음 시간: %s\n", dose, nextTime);
                System.out.println("----------------------------------------");
                
                history.add(String.format("[%s] %s 투여: %.1f units (차기: %s)", 
                            lastTime.format(DateTimeFormatter.ofPattern("HH:mm")), 
                            selectedInsulin.getBrandName(), dose, nextTime));

            } catch (NumberFormatException e) {
                System.out.println("⚠️ 오류: 숫자를 정확히 입력해주세요.");
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ 오류: 시간 형식을 맞춰주세요 (예: 09:30).");
            } catch (MedicalDangerException e) {
                System.out.println("⚠️ 위험: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ 안내: " + e.getMessage());
            }
        }

        // 종료 시 기록 출력
        System.out.println("\n========================================");
        System.out.println("   📊 오늘 기록된 투여 히스토리");
        if (history.isEmpty()) {
            System.out.println("기록된 데이터가 없습니다.");
        } else {
            for (String log : history) System.out.println(log);
        }
        System.out.println("========================================");
        System.out.println("프로그램을 종료합니다. 건강하세요!");
        sc.close();
    }
}
package dcare;

import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class HistoryService {

    /**
     * 투여 기록을 분석하여 반려견별 통계를 출력합니다.
     * 이름에 공백이 포함된 경우(예: 초코 2)도 정확하게 파싱합니다.
     */
    public static void showDetailedStats(List<String> history, List<Dog> dogList) {
        if (history.isEmpty()) {
            System.out.println("\n⚠️ 분석할 투여 기록이 없습니다.");
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        Map<String, Double> totalDoses = new HashMap<>();

        for (String record : history) {
            try {
                // 이름 추출: "] "(시간 뒤) 부터 ":"(데이터 앞) 까지 추출
                int nameStart = record.indexOf("] ") + 2;
                int nameEnd = record.indexOf(":", nameStart);
                if (nameStart < 2 || nameEnd == -1) continue;
                
                String namePart = record.substring(nameStart, nameEnd).trim();

                // 투여량 추출: ": " 부터 " units" 전까지 추출
                int doseStart = record.indexOf(": ") + 2;
                int doseEnd = record.indexOf(" units");
                double dose = Double.parseDouble(record.substring(doseStart, doseEnd));

                counts.put(namePart, counts.getOrDefault(namePart, 0) + 1);
                totalDoses.put(namePart, totalDoses.getOrDefault(namePart, 0.0) + dose);
            } catch (Exception e) {
                // 파싱 실패 시 해당 라인은 건너뜀
            }
        }

        System.out.println("\n📊 [ 반려견별 누적 투여 리포트 ]");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-15s | %-6s | %-12s\n", "반려견 이름", "횟수", "총 투여량");
        System.out.println("--------------------------------------------------");

        // 1. 현재 관리 중인 반려견 출력
        for (Dog dog : dogList) {
            int count = counts.getOrDefault(dog.name, 0);
            double total = totalDoses.getOrDefault(dog.name, 0.0);
            System.out.printf("%-15s | %-6d | %-10.1f units\n", dog.name, count, total);
        }

        // 2. 관리 목록에는 없으나 기록에는 남은(삭제된) 반려견 출력
        for (String name : counts.keySet()) {
            boolean isCurrent = false;
            for (Dog d : dogList) {
                if (d.name.equals(name)) {
                    isCurrent = true;
                    break;
                }
            }

            if (!isCurrent) {
                System.out.printf("%-15s | %-6d | %-10.1f units (🕒이전 기록)\n", 
                                  name, counts.get(name), totalDoses.get(name));
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.println("💡 팁: 이전 기록은 목록에서 삭제된 반려견의 데이터입니다.");
    }

    public static boolean manageHistoryMenu(Scanner sc, List<String> history, List<Dog> dogList) {
        while (true) {
            System.out.println("\n--- 📂 투여 기록 조회 및 관리 (총 " + history.size() + "건) ---");
            if (history.isEmpty()) {
                System.out.println("조회할 기록이 없습니다.");
                return false;
            }

            // 기록 목록 출력
            for (int i = 0; i < history.size(); i++) {
                System.out.println((i + 1) + ". " + history.get(i));
            }

            System.out.println("\n[ 1. 개별 삭제 | 2. 전체 초기화 | 0. 돌아가기 ]");
            System.out.print("▶ 선택: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("▶ 삭제할 번호: ");
                try {
                    int idx = Integer.parseInt(sc.nextLine()) - 1;
                    if (idx >= 0 && idx < history.size()) {
                        history.remove(idx);
                        System.out.println("✅ 삭제되었습니다.");
                        return true; // 변경 사항 발생 알림
                    } else {
                        System.out.println("⚠️ 잘못된 번호입니다.");
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ 숫자만 입력해 주세요.");
                }
            } else if (choice.equals("2")) {
                System.out.print("⚠️ 정말 모든 기록을 지울까요? (1.예 / 0.아니오): ");
                if (sc.nextLine().equals("1")) {
                    history.clear();
                    System.out.println("✅ 모든 기록이 초기화되었습니다.");
                    return true;
                }
            } else if (choice.equals("0")) {
                return false;
            } else {
                System.out.println("⚠️ 올바른 메뉴를 선택하세요.");
            }
        }
    }
}
package dcare;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class DcareSystem {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static List<Dog> dogList = new ArrayList<>();
    private static List<String> history = new ArrayList<>();
    private static boolean isDataChanged = false;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DataManager.loadData(dogList, history);

        // 실행 시 기존 데이터의 중복 이름을 체크하고 기록까지 동기화
        fixDuplicateNamesAndSyncHistory();

        System.out.println("========================================");
        System.out.println("   🐾 D-Care: 스마트 다견 관리 시스템 v4.8  ");
        System.out.println("   [현재 등록된 반려견: " + dogList.size() + "마리]");
        System.out.println("========================================");
        
        boolean running = true;
        while (running) {
            try {
                System.out.println("\n┌────────── [ 메인 메뉴 ] ──────────┐");
                System.out.println("  1. 인슐린 투여 (지속성)");
                System.out.println("  2. 인슐린 투여 (속효성)");
                System.out.println("  3. 투여 통계 리포트 조회");
                System.out.println("  4. 투여 기록 조회 및 관리");
                System.out.println("  5. 반려견 목록 관리 (추가/삭제)");
                System.out.println("  6. 전체 저장 | 0. 종료");
                System.out.println("└──────────────────────────────────┘");
                System.out.print("▶ 선택: ");
                String menu = sc.nextLine().trim();

                switch (menu) {
                    case "1": case "2":
                        processInjection(sc, menu);
                        break;
                    case "3":
                        HistoryService.showDetailedStats(history, dogList);
                        break;
                    case "4":
                        if (HistoryService.manageHistoryMenu(sc, history, dogList)) {
                            isDataChanged = true;
                            DataManager.saveData(dogList, history);
                        }
                        break;
                    case "5":
                        manageDogs(sc);
                        break;
                    case "6":
                        DataManager.saveData(dogList, history);
                        isDataChanged = false;
                        break;
                    case "0":
                        handleExit(sc);
                        running = false;
                        break;
                    default:
                        System.out.println("⚠️ 잘못된 입력입니다.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ 오류가 발생했습니다: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void fixDuplicateNamesAndSyncHistory() {
        Set<String> nameSet = new HashSet<>();
        boolean fixed = false;
        
        for (Dog dog : dogList) {
            String originalName = dog.name;
            if (nameSet.contains(dog.name)) {
                int count = 2;
                while (nameSet.contains(originalName + " " + count)) { count++; }
                String newName = originalName + " " + count;

                // 과거 기록(history) 내의 이름도 찾아 치환
                String oldPattern = "] " + originalName + ":";
                String newPattern = "] " + newName + ":";
                
                for (int i = 0; i < history.size(); i++) {
                    if (history.get(i).contains(oldPattern)) {
                        history.set(i, history.get(i).replace(oldPattern, newPattern));
                    }
                }
                dog.name = newName;
                fixed = true;
            }
            nameSet.add(dog.name);
        }
        
        if (fixed) {
            System.out.println("💡 시스템: 중복 데이터가 자동으로 정리되었습니다.");
            DataManager.saveData(dogList, history);
        }
    }

    private static void processInjection(Scanner sc, String menu) {
        Insulin insulin = (menu.equals("1")) ? new LongActingInsulin() : new ShortActingInsulin();
        Dog target = null;
        double weight = 0;
        LocalTime now = null;

        if (dogList.isEmpty()) {
            System.out.println("\n⚠️ 등록된 반려견이 없습니다.");
            target = createNewDog(sc);
            if (target == null) return;
            weight = target.weight;
        } else {
            System.out.println("\n[ 투여 대상 선택 ]");
            for (int i = 0; i < dogList.size(); i++) System.out.println((i + 1) + ". " + dogList.get(i).name);
            System.out.println((dogList.size() + 1) + ". 🆕 새 반려견 추가");
            System.out.print("▶ 선택 (0:취소): ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 0) return;
                if (choice == dogList.size() + 1) {
                    target = createNewDog(sc);
                    if (target == null) return;
                } else {
                    target = dogList.get(choice - 1);
                }
                weight = target.weight;
            } catch (Exception e) { 
                System.out.println("⚠️ 숫자를 입력하세요.");
                return; 
            }
        }

        System.out.print("▶ 몸무게(" + weight + "kg) 확인 (Enter:유지 / 숫자:수정): ");
        String wInput = sc.nextLine();
        if (!wInput.isEmpty()) weight = Double.parseDouble(wInput);

        System.out.print("▶ 시간 (HH:mm / 'now'): ");
        String tInput = sc.nextLine();
        try {
            now = tInput.equalsIgnoreCase("now") ? LocalTime.now() : LocalTime.parse(tInput);
        } catch (Exception e) { now = LocalTime.now(); }

        target.weight = weight;
        double dose = insulin.calculateDose(weight);
        history.add(String.format("[%s] %s: %.1f units (%s)", now.format(TIME_FMT), target.name, dose, insulin.getBrandName()));
        isDataChanged = true;
        DataManager.saveData(dogList, history);
        System.out.println("✅ 투여 기록이 완료되었습니다.");
    }

    private static Dog createNewDog(Scanner sc) {
        System.out.print("\n▶ 이름: ");
        String name = sc.nextLine().trim();
        System.out.print("▶ 몸무게(kg): ");
        double weight = Double.parseDouble(sc.nextLine());
        Dog newDog = new Dog(name, weight);
        dogList.add(newDog);
        isDataChanged = true;
        System.out.println("✅ 등록되었습니다.");
        return newDog;
    }

    private static void manageDogs(Scanner sc) {
        System.out.println("\n[ 1.조회 | 2.추가 | 3.삭제 | 0.이전 ]");
        System.out.print("▶ 선택: ");
        String c = sc.nextLine();
        if (c.equals("1")) dogList.forEach(System.out::println);
        else if (c.equals("2")) createNewDog(sc);
        else if (c.equals("3")) {
            for (int i = 0; i < dogList.size(); i++) System.out.println((i+1) + ". " + dogList.get(i).name);
            System.out.print("▶ 삭제할 번호: ");
            try {
                int idx = Integer.parseInt(sc.nextLine()) - 1;
                if (idx >= 0 && idx < dogList.size()) {
                    dogList.remove(idx);
                    isDataChanged = true;
                    DataManager.saveData(dogList, history);
                    System.out.println("✅ 삭제되었습니다.");
                }
            } catch (Exception e) { System.out.println("⚠️ 올바른 번호를 입력하세요."); }
        }
    }

    private static void handleExit(Scanner sc) {
        if (isDataChanged) {
            System.out.print("\n💾 변경사항을 저장할까요? (1:예 / 0:아니오): ");
            if (sc.nextLine().equals("1")) DataManager.saveData(dogList, history);
        }
        System.out.println("🐾 종료합니다.");
    }
}
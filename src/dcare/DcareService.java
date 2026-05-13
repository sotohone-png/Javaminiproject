package dcare;

import java.time.LocalTime;
import java.util.*;

public class DcareService {
    private List<Dog> dogList = new ArrayList<>();
    private List<String> history = new ArrayList<>();

    public DcareService() {
        refreshData();
    }

    public void refreshData() {
        DataManager.loadData(dogList, history);
        // 앱 시작 시 파일에 있는 기존 중복 데이터도 번호를 붙여 정리
        fixDuplicateNamesAndSyncHistory(); 
    }

    public void addDog(String name, double weight) {
        String baseName = name.trim();
        String currentName = baseName;
        
        // 람다 제약을 피하기 위해 일반적인 반복문 사용
        int count = 2;
        while (true) {
            boolean isDuplicate = false;
            for (Dog d : dogList) {
                if (d.name.equalsIgnoreCase(currentName)) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                break; // 중복이 없으면 루프 탈출
            }
            
            // 중복이 있으면 이름 뒤에 번호 추가 (예: 초코 -> 초코 2)
            currentName = baseName + " " + count;
            count++;
        }
        
        // 최종 결정된 이름으로 강아지 추가
        dogList.add(new Dog(currentName, weight));
        save();
    }

    public Dog addInjection(Dog dog, Insulin insulin, double weight, String timeStr) {
        Dog target = dog;

        // 몸무게가 다르면 → 새 강아지로 등록 (이름 뒤 숫자 자동 부여)
        if (dog.weight != weight) {
            // addDog()이 중복 이름 처리(숫자 부여)를 해주므로 그대로 활용
            addDog(dog.name, weight);
            // 방금 추가된 강아지(마지막 항목)를 target으로 설정
            target = dogList.get(dogList.size() - 1);
        }

        double dose = insulin.calculateDose(target.weight);
        String record = String.format("[%s] %s: %.1f units (%s)",
                        timeStr, target.name, dose, insulin.getBrandName());
        history.add(record);
        save();
        return target; // 실제 투여된 강아지 반환 (UI에서 이름 확인용)
    }

    public void fixDuplicateNamesAndSyncHistory() {
        Set<String> nameSet = new HashSet<>();
        boolean fixed = false;
        
        for (Dog dog : dogList) {
            String originalName = dog.name;
            if (nameSet.contains(dog.name)) {
                int count = 2;
                while (nameSet.contains(originalName + " " + count)) count++;
                String newName = originalName + " " + count;
                
                // 과거 기록(history)의 이름도 함께 치환
                updateHistoryNameInHistory(originalName, newName);
                dog.name = newName;
                fixed = true;
            }
            nameSet.add(dog.name);
        }
        if (fixed) save();
    }

    private void updateHistoryNameInHistory(String oldName, String newName) {
        String oldP = "] " + oldName + ":";
        String newP = "] " + newName + ":";
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).contains(oldP)) {
                history.set(i, history.get(i).replace(oldP, newP));
            }
        }
    }

    public List<Object[]> getStatsData() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Double> doses = new HashMap<>();

        for (String record : history) {
            try {
                int nameStart = record.indexOf("] ") + 2;
                int nameEnd = record.indexOf(":", nameStart);
                String name = record.substring(nameStart, nameEnd).trim();

                int doseStart = record.indexOf(": ") + 2;
                int doseEnd = record.indexOf(" units");
                double dose = Double.parseDouble(record.substring(doseStart, doseEnd));

                counts.put(name, counts.getOrDefault(name, 0) + 1);
                doses.put(name, doses.getOrDefault(name, 0.0) + dose);
            } catch (Exception e) {}
        }

        List<Object[]> rows = new ArrayList<>();
        for (Dog dog : dogList) {
            rows.add(new Object[]{dog.name, counts.getOrDefault(dog.name, 0), doses.getOrDefault(dog.name, 0.0), "관리 중"});
        }
        return rows;
    }

    public void save() { DataManager.saveData(dogList, history); }
    public List<Dog> getDogList() { return dogList; }
    public List<String> getHistory() { return history; }
    public void removeHistory(int index) { history.remove(index); save(); }
    public void removeDog(int index) { dogList.remove(index); save(); }
}
package dcare;

import java.io.*;
import java.util.List;
import java.time.LocalTime;

public class DataManager {
    private static final String DOG_FILE = "dogs.txt";
    private static final String HIST_FILE = "history.txt";

    public static void saveData(List<Dog> dogList, List<String> history) {
        try {
            // 1. 강아지 목록 저장
            try (PrintWriter out = new PrintWriter(new FileWriter(DOG_FILE))) {
                for (Dog d : dogList) {
                    String timeStr = (d.lastInjectionTime != null) ? d.lastInjectionTime.toString() : "null";
                    out.println(d.name + "|" + d.weight + "|" + timeStr);
                }
            }

            // 2. 투여 기록 저장
            try (PrintWriter out = new PrintWriter(new FileWriter(HIST_FILE))) {
                for (String line : history) {
                    out.println(line);
                }
            }
            
            // 사용자 요청: 저장 메시지만 심플하게
            System.out.println("💾 데이터 저장 완료.");

        } catch (IOException e) {
            System.out.println("⚠️ 저장 중 오류가 발생했습니다.");
        }
    }

    public static void loadData(List<Dog> dogList, List<String> history) {
        dogList.clear();
        history.clear();

        File dFile = new File(DOG_FILE);
        if (dFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        Dog d = new Dog(parts[0], Double.parseDouble(parts[1]));
                        if (parts.length > 2 && !parts[2].equals("null")) {
                            d.lastInjectionTime = LocalTime.parse(parts[2]);
                        }
                        dogList.add(d);
                    }
                }
            } catch (Exception e) { }
        }

        File hFile = new File(HIST_FILE);
        if (hFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(hFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.add(line);
                }
            } catch (Exception e) { }
        }
    }
}
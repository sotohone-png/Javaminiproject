package dcare; // 본인의 패키지명에 맞게 수정

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Dog {
    String name;
    double weight;
    LocalTime lastInjectionTime;

    public Dog(String name, double weight) {
        this.name = name;
        this.weight = weight;
        this.lastInjectionTime = null;
    }

    @Override
    public String toString() {
        return name + " (" + weight + "kg)" + 
               (lastInjectionTime != null ? " [최근 주사: " + lastInjectionTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "]" : " [기록 없음]");
    }
}
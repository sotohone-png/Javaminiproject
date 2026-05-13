package dcare;
import java.time.LocalTime;

public interface MedicalAlert {
    String getSchedule(LocalTime lastShotTime);
    double calculateDose(double weight);
    String getDescription();

    /** 해당 인슐린의 1회 최대 허용 용량 (units) */
    double getMaxDose();

    /** 최소 투여 간격 (시간) — 이 시간이 지나기 전에 재투여하면 경고 */
    int getMinIntervalHours();
}
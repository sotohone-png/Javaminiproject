package dcare;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LongActingInsulin extends Insulin {
    public LongActingInsulin() { super("지속성 인슐린(캐닌슐린)", 12); }
    @Override
    public String getSchedule(LocalTime lastShotTime) {
        return lastShotTime.plusHours(intervalHours).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    @Override
    public double calculateDose(double weight) { return weight * 0.5; }
    @Override
    public String getDescription() {
        return "ℹ️ 지속성 인슐린: 12시간 간격 권장 (하루 2회 투여용)";
    }
    @Override
    public double getMaxDose() { return 10.0; } // 최대 10 units
    @Override
    public int getMinIntervalHours() { return 10; } // 10시간 이내 재투여 경고
}
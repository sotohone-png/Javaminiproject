package dcare;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class ShortActingInsulin extends Insulin {
    public ShortActingInsulin() { super("속효성 인슐린(휴멀린)", 6); }
    @Override
    public String getSchedule(LocalTime lastShotTime) {
        return lastShotTime.plusHours(intervalHours).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    @Override
    public double calculateDose(double weight) { return weight * 0.3; }
    @Override
    public String getDescription() {
        return "ℹ️ 속효성 인슐린: 식후 혈당 조절용 (작용 시간이 빠름)";
    }
    @Override
    public double getMaxDose() { return 6.0; } // 최대 6 units
    @Override
    public int getMinIntervalHours() { return 4; } // 4시간 이내 재투여 경고
}
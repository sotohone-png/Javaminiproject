package dcare;
import java.time.LocalTime;

public interface MedicalAlert {
    String getSchedule(LocalTime lastShotTime);
    double calculateDose(double weight);
    String getDescription(); 
}
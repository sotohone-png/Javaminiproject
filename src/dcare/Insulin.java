package dcare;

public abstract class Insulin implements MedicalAlert {
    protected String brandName;
    protected int intervalHours;

    public Insulin(String brandName, int intervalHours) {
        this.brandName = brandName;
        this.intervalHours = intervalHours;
    }
    public String getBrandName() { return brandName; }
}
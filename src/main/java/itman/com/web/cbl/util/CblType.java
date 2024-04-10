package itman.com.web.cbl.util;

public enum CblType {
    MID610(6, 10), MID46(4, 6), MID810(8, 10);

    private final int selectDay;
    private final int baseDay;

    CblType(int selectDay, int baseDay) {
        this.selectDay = selectDay;
        this.baseDay = baseDay;
    }

    public int getSelectDay() {
        return selectDay;
    }

    public int getBaseDay() {
        return baseDay;
    }
}

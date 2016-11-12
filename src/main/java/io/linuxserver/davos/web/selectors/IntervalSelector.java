package io.linuxserver.davos.web.selectors;

public enum IntervalSelector {

    MINS_15(15, "Every 15 minutes"), 
    MINS_30(30, "Every 30 minutes"), 
    EVERY_HOUR(60, "Every hour"),
    EVERY_2_HOURS(120, "Every two hours"), 
    TWICE_A_DAY(720, "Twice a day"), 
    EVERY_DAY(1440, "Every day");
    
    public static final IntervalSelector[] ALL = { MINS_15, MINS_30, EVERY_HOUR, EVERY_2_HOURS, TWICE_A_DAY, EVERY_DAY};
    
    private IntervalSelector(int minutes, String text) {
        this.minutes = minutes;
        this.text = text;
    }
    
    private final int minutes;
    private final String text;
    
    public int getMinutes() {
        return minutes;
    }
    
    public String getText() {
        return text;
    }
}


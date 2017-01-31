package ua.com.mcsim.gpstracker.forms;

import java.util.Map;

public class Targets {
    public Targets(Map target) {
        this.target = target;
    }

    public Targets() {
        // Default constructor required for calls to DataSnapshot.getValue(Targets.class)
    }

    private Map<String,String> target;

}

package org.greenblitz.debug.logger;

import edu.wpi.first.networktables.NetworkTable;
import org.greenblitz.debug.DebugTables;

import java.util.HashMap;
import java.util.Map;

public class LoggerManager {

    private static Map<String, LogObject> targets = new HashMap<>();

    public static LogObject getLog(String name) {
        if (!targets.containsKey(name)) {
            targets.put(name, new LogObject(name));
        }
        return targets.get(name);
    }

    public static final NetworkTable TXT_LOGGER = DebugTables.DEBUG.getSubTable("textlogger");


}

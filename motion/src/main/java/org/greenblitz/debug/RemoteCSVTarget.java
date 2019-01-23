package org.greenblitz.debug;

import edu.wpi.first.networktables.NetworkTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoteCSVTarget {

    private static Map<String, RemoteCSVTarget> targets = new HashMap<>();

    public static void initTarget(String fileName, String... names) {
        if (!targets.containsKey(fileName)) {
            targets.put(fileName, new RemoteCSVTarget(fileName, names));
        }
    }

    public static RemoteCSVTarget getTarget(String fileName) {
        return targets.getOrDefault(fileName, null);
    }

    public static final NetworkTable CSV_LOGGER = DebugTables.DEBUG.getSubTable("csvlogger");

    private static final NetworkTable NAMES = CSV_LOGGER.getSubTable("names");
    private static final NetworkTable VALUES = CSV_LOGGER.getSubTable("values");

    private final String m_remoteFileName;
    private final String[] m_ntNames;

    private RemoteCSVTarget(String remoteFileName, String[] ntNames) {
        m_remoteFileName = remoteFileName;
        m_ntNames = ntNames;

        NAMES.getEntry(remoteFileName).setStringArray(ntNames);
    }

    public void report(double... record) {
        if (record.length != m_ntNames.length) {
            System.err.println("Warning unexpected record length");
        }

        VALUES.getEntry(m_remoteFileName).setDoubleArray(record);
    }
}

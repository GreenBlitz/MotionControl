package org.greenblitz.debug;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

public class RemoteGuydeBugger {
    public static final NetworkTable GUYDE_BUGGER = DebugTables.DEBUG.getSubTable("guydebugger");

    private static final NetworkTableEntry X = GUYDE_BUGGER.getEntry("x");
    private static final NetworkTableEntry Y = GUYDE_BUGGER.getEntry("y");
    private static final NetworkTableEntry HEADING = GUYDE_BUGGER.getEntry("heading");
    private static final NetworkTableEntry UPDATE = GUYDE_BUGGER.getEntry("isUpdated");

    public static void report(double x, double y, double heading) {
        X.setDouble(x);
        Y.setDouble(y);
        HEADING.setDouble(heading);
        UPDATE.setBoolean(true);
    }
}

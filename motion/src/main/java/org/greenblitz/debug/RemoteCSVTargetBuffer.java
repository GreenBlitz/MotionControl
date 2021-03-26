package org.greenblitz.debug;

import java.util.ArrayList;

public class RemoteCSVTargetBuffer {
    RemoteCSVTarget target;
    ArrayList<double[]> buffer;

    public RemoteCSVTargetBuffer(String fileName, String... names){
        target = RemoteCSVTarget.initTarget(fileName, names);
        buffer = new ArrayList<double[]>();
    }

    public void report(double... record){
            buffer.add(record);
    }

    public void passToCSV(){
        for (int i = 0; i < buffer.size(); i++) {
            target.report(buffer.get(i));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

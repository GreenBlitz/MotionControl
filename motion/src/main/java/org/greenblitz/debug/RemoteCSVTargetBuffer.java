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
        int time = 0;
        for (int i = 0; i < buffer.get(0).length; i++) {
            if(target.m_ntNames[i] == "time") time = i;
        }

        for (int i = 0; i < buffer.size() - 2; i++) {
            for(int j = 0; j < buffer.get(i).length; j++){
                if (buffer.get(i)[j] == buffer.get(i+1)[j]) {
                    int l = i+2;
                    while(l < buffer.size() - 1 && buffer.get(l)[j] ==buffer.get(i)[j]) l++;
                    double dV = buffer.get(l)[j] - buffer.get(i)[j];
                    double dT = buffer.get(l)[time] - buffer.get(i)[time];
                    for (int k = i+1; k < l; k++) {
                        double dt = buffer.get(k)[time] - buffer.get(i)[time];
                        buffer.get(k)[j] = dV/dT*dt;
                    }
                }
            }
            target.report(buffer.get(i));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

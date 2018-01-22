package APPC;

import java.util.LinkedList;

import base.ControllerStoppedEvent;
import base.EventListener;
import base.EventManager;

public class MainClass {
    public static void main(String[] args) {
    	EventManager.registerClass(MainClass.class);
    }

    public Path genPath(){
        LinkedList<Point2D> pointList= new LinkedList<Point2D>();
        for(double i = 0;i < 1;i+=0.01)
            pointList.add(new Point2D(0,i,0));
        return new Path(pointList);
    }
    
    @EventListener
    public void onControllerStop(ControllerStoppedEvent e){
    	System.out.println(e.getClass());
    }

}

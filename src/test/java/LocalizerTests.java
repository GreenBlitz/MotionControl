import org.greenblitz.motion.Localizer;
import org.greenblitz.robot.RobotMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LocalizerTests {

    static Method localizerRun;
    static Localizer lizer;

    void runMethod(double left, double right) {
        try {
            localizerRun.invoke(lizer, left, right);
        } catch (InvocationTargetException | IllegalAccessException e){
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @BeforeAll
    static void setup(){
        try {
            localizerRun = Localizer.class.getDeclaredMethod("run", double.class, double.class);
        } catch (NoSuchMethodException e){
            e.printStackTrace();
            assertTrue(false);
        }
        localizerRun.setAccessible(true);
        lizer = Localizer.getInstance();
    }

    @BeforeEach
    void config(){
        lizer.configure(RobotMap.WHEELBASE_WIDTH, null, null);
    }

    @AfterAll
    static void cleanup(){
        localizerRun = null;
        lizer = null;
    }

    @Test
    void noAngleTests(){
        
    }

}

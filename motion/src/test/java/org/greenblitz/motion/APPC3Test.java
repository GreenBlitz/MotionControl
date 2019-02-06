package org.greenblitz.motion;

import org.greenblitz.motion.app.AdaptivePolynomialPursuitController;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class APPC3Test {

    @Test
    void basicLinear(){
        var appc = new AdaptivePolynomialPursuitController(new Path(
                new Position(0, 0),
                new Position(0, 1)
        ), 0.5, 0.69,
                0.1, false, 0.3, 0.5);
        double[] p = appc.iteration(new Position(0, 0, 0));
        assertTrue(Point.isFuzzyEqual(p[0], p[1]));
    }

}

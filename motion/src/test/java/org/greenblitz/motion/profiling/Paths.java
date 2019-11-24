package org.greenblitz.motion.profiling;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.pathing.Path;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Paths {

    private static final double MIDDLE = -4.1;

    private static String mkNativePath(String pathname) {
        return "C:/git/MotionControl/motion/src/test/resources/weaverOutq:q" +
                "/output/" + pathname + ".pf1.csv";
    }

    @Deprecated
    public static Path<Position> getRaw(String pathname) {
        return nativeGetPath(mkNativePath(pathname));
    }

    public static Path<Position> get(String pathname, boolean left) {
        return left ? getLeft(pathname) : getRight(pathname);
    }

    public static Path<Position> getLeft(String pathname) {
        return getRaw(pathname);
    }

    public static Path<Position> getRight(String pathname) {
        Path<Position> pth = getLeft(pathname);
        List<Position> ret = new ArrayList<>();

        for (Position p : pth) {
            ret.add(new Position(2 * MIDDLE - p.getX(), p.getY(), p.getAngle()));
        }

        return new Path<>(ret);
    }

    private static Path<Position> nativeGetPath(String filename) {
        try (CSVParser read = CSVFormat.DEFAULT.parse(new FileReader(new File(filename)))) {
            ArrayList<Position> path = new ArrayList<>();
            List<CSVRecord> records = read.getRecords();
            Position toAdd;
            for (int i = 1; i < records.size(); i++) {
                toAdd = new Position(Double.parseDouble(records.get(i).get(1)),
                        Double.parseDouble(records.get(i).get(2)),
                        Double.parseDouble(records.get(i).get(7))).weaverToLocalizerCoords();

                toAdd.setAngle(toAdd.getAngle() - 2*(toAdd.getAngle() - Math.PI/2));

                if (i == 1){
                    path.add(toAdd);
                    continue;
                }
                if (i == records.size() - 1){
                    path.add(toAdd);
                    continue;
                }
                if (Point.subtract(toAdd, path.get(path.size() - 1)).norm() > 0.1) {
                    path.add(toAdd);
                }
//                System.out.println(path.get(path.size() - 1));
//                Thread.sleep(100);
            }
            return new Path<>(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Path<>();
    }
}
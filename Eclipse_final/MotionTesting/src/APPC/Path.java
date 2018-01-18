package APPC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * Represents a path of points
 */
public class Path implements Iterator<Point2D> {
    protected List<Point2D> m_path;
    protected int m_currentIndex;

    public Path(List<Point2D> path) {
        m_path = path;
        m_currentIndex  = 0;
    }

    public Path() { this(new ArrayList<>()); }

    /**
     * Attains the Location2D at the given tick
     * @param tick The index of the point
     * @return The location at the given tick
     * @throws EndOfPathException if the path doesn't have enough locations in it
     */
    public Point2D getRaw(int tick) throws EndOfPathException{
    	if(tick>=m_path.size())
    		throw new EndOfPathException(tick);
    	return m_path.get(tick);
    	/*
        try {
            
        }
        catch(ArrayIndexOutOfBoundsException e) {
            throw new EndOfPathException(tick);
        }
        */
    }

    public Point2D getLast() {
        return m_path.get(m_path.size()-1);
    }

    /**
     * Attains the next point after the last call to recieve
     * @return The next location in the path
     * @throws EndOfPathException If the path has ended
     */
    public Point2D get() throws EndOfPathException {
        return getRaw(m_currentIndex++);
    }

    /**
     * Adds a location at the end of the path
     * @param l The location to be added
     */
    public void add(Point2D l) {
        m_path.add(l);
    }

    public int getCurrentIndex()
    {
        return m_currentIndex;
    }


    public void setCurrentIndex(int currentIndex){
        m_currentIndex = currentIndex;
    }

    public Point2D closestPointTo(Point2D point2D){
    	System.out.println(point2D);
    	System.out.println(m_path.get(0));
        return m_path.stream().min(Comparator.comparingDouble(value -> value.distance(point2D))).get();
    }

    public int getRemainingLength(){
        return m_path.size() - m_currentIndex;
    }

    public int getTotalLength(){
        return m_path.size();
    }

    public double getCompletion(){
        return getRemainingLength() / getTotalLength();
    }

    public boolean hasNext(){
        try {
            getRaw(m_currentIndex);
            return true;
        } catch (EndOfPathException e){
            return false;
        }
    }
    
    @Override
    public String toString(){
    	return "Start point - " + m_path.get(0).toString() + " | End point - " + getLast().toString();
    }

    public Point2D next() {
        return get();
    }
}

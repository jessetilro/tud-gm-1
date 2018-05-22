package workshop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jvx.project.PjWorkshop;
import jv.vecmath.PiVector;

public class ConnectedComponents extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;

    public ConnectedComponents() {
        super("ConnectedComponents");
        init();
    }
    
    @Override
    public void setGeometry(PgGeometry geom) {
        super.setGeometry(geom);
        m_geom      = (PgElementSet)super.m_geom;
        m_geomSave  = (PgElementSet)super.m_geomSave;
    }
    
    public void init() {        
        super.init();
    }
    
    public int countComponents() {
        int numComponents = 1;
        PiVector[] elements = m_geom.getElements();
        PiVector[] neighbours = m_geom.getNeighbours();
        
        // Initiate collections to keep track of visited and unvisited elements.
        HashSet<Integer> visited = new HashSet<Integer>();
        HashSet<Integer> unvisited = new HashSet<Integer>();
        for (int i = 0; i < elements.length; i++) {
            unvisited.add(i);
        }
        
        // Initiate FIFO queue to traverse the vertices and initialize vertex 0 as root.
        LinkedList<Integer> toTraverse = new LinkedList<Integer>();
        
        toTraverse.add(0);
        
        while(!unvisited.isEmpty()) {
            
            // Main loop. Traverse through the traversal list sequentially.
            int currentIndex = toTraverse.poll();
            unvisited.remove(currentIndex);
            visited.add(currentIndex);
            PiVector currentElement = elements[currentIndex];
//            if (!(currentIndex == currentElement.getEntry(0))) {
//                return -1;
//            }
            
            assert(currentIndex == currentElement.getEntry(0));
            
            // Get the neighbours of the current element and add them to the queue
            PiVector neighbourV = m_geom.getNeighbour(currentIndex);
            
            if (neighbourV != null) {
                for (int i = 0; i < neighbourV.getSize(); i++) {
                    int neighbourIndex = neighbourV.getEntry(i);
                    if ((neighbourIndex != -1) && (!visited.contains(neighbourIndex)) && (!toTraverse.contains(neighbourIndex))) {
                        toTraverse.add(neighbourIndex);
                    }
                }
            }
            
            // Check if the component is traversed and check if there are unvisited elements left
            // Increments number of components and chooses a new root if so.
            if (toTraverse.isEmpty()) {
                if (!unvisited.isEmpty()) {
                    Iterator<Integer> it = unvisited.iterator();
                    int newRoot = it.next();
                    toTraverse.add(newRoot);
                    numComponents++;
                }
            }
        }
        return numComponents;
    }
}
package workshop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jvx.project.PjWorkshop;

public class Genus extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;

    public Genus() {
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

    public int computeGenus() {

        
        // Edge count provided by API
        int E = m_geom.getNumEdges();
        // Face count provided by API
        int F = m_geom.getNumElements();
        // Vertex count provided by API
        int V = m_geom.getNumVertices();

        int X = V - E + F;
        int g = 1 - ( X / 2 );

        // Debugging, left it in since why not
        System.out.println("E (Edges) = " + E);
        System.out.println("V (Vertices) = " + V);
        System.out.println("F (Faces) = " + F);
        System.out.println("X (Euler characteristic) = " + X);
        System.out.println("g (Genus) = " + g);

        return g;
    }
    
    public int getNumComponents() {
        int numComponents = 1;
        PiVector[] elements = m_geom.getElements();
        
        // Initiate collections to keep track of visited and unvisited elements.
        for (int i = 0; i < elements.length; i++) {
            unvisited.add(i);
        }
        HashSet<Integer> unvisited = new HashSet<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        
        // Initiate FIFO queue to traverse the vertices and initialize vertex 0 as root.
        LinkedList<Integer> toTraverse = new LinkedList<Integer>();
        toTraverse.add(0);
        
        while(!unvisited.isEmpty()) {
            // Main loop. Traverse through the traversal list sequentially.
            int currentIndex = toTraverse.poll();
            unvisited.remove(currentIndex);
            visited.add(currentIndex);
            
            currentElement = elements[currentIndex];
            assert(currentIndex == currentElement.getEntry(0));
            
            // Get the neighbours of the current element and add them to the queue
            PiVector[] neighbours = currentElement.getNeighbours();
            for (neighbour : neighbours) {
                neighbourIndex = neighbour.getEntry(0);
                if (!visited.contains(neighbourIndex) && !toTraverse.contains(neighbourIndex)) {
                    toTraverse.add(neighbourIndex);
                }
            }
            
            // Check if the component is traversed and check if there are unvisited elements left
            // Increments number of components and chooses a new root if so.
            if (toTraverse.isEmpty()) {
                if (!unvisited.isEmpty()) {
                    Iterator it = unvisited.getIterator();
                    int newRoot = it.next();
                    toTraverse.add(newRoot);
                    numComponents++;
                }
            }
        }
        return numComponents;
    }

}
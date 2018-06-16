package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jvx.project.PjWorkshop;

public class DifferentialCoordinates extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;

    public DifferentialCoordinates() {
        super("DifferentialCoordinates");
        init();
    }
    
    @Override
    public void setGeometry(PgGeometry geom) {
        super.setGeometry(geom);
        m_geom      = (PgElementSet)super.m_geom;
        m_geomSave  = (PgElementSet)super.m_geomSave;
        PiVector[] elements = elementSet.getElements();
    }
    
    public void init() {        
        super.init();
    }

    public int computeDifferentialCoordinates() {
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
    
    public PdMatrix computeElementaryMatrix(int faceIndex) {
        double length = 1/(2 * m_geom.getAreaOfElement(faceIndex));
        PiVector vertices = m_geom.getElementVertices(faceIndex);
        
        PdVector firstV = vertices[0];
        PdVector secondV = vertices[1];
        PdVector thirdV = vertices[2];

        PdVector e1 = PdVector.subNew(thirdV, secondV);
        PdVector e2 = PdVector.subNew(firstV, thirdV);
        PdVector e3 = PdVector.subNew(secondV, firstV);
        
        PdVector normal = m_geom.getElementNormal(faceIndex);
        
        PdVector[] cols = new PdVector[3];
        PdVector cols[0] = length * PdVector.crossNew(normal, e1);
        PdVector cols[1] = length * PdVector.crossNew(normal, e2);
        PdVector cols[2] = length * PdVector.crossNew(normal, e3);
        
        PdMatrix elementaryMatrix = new PdMatrix(3);
        elementaryMatrix.setColumn(cols);
        return elementaryMatrix;
    }
}
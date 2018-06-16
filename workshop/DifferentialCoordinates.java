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

    public PnSparseMatrix computeGradientMatrix() {
        // Edge count provided by API
        int E = m_geom.getNumEdges();
        // Face count provided by API
        int F = m_geom.getNumElements();
        // Vertex count provided by API
        int V = m_geom.getNumVertices();
        
        // Create sparse gradient matrix
        PnSparseMatrix G = PnSparseMatrix(3*F, V, 3);
        
        // Fill in sparse gradient matrix
        for (int i = 0; i < F; i++) {
            PiVector vertices = m_geom.getElementVertices(i);
            PdMatrix elementary = computeElementaryMatrix(i, vertices);
            elementaryFillG(G, elementary, vertices);
        }
        return G;
    }
    
    public PdMatrix computeElementaryMatrix(int faceIndex, PiVector vertices) {
        double length = 1/(2 * m_geom.getAreaOfElement(faceIndex));
        
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
    
    public void elementaryFillG(PnSparseMatrix G, PdMatrix elementary, int faceIndex, PiVector vertices) {
        // Fill in sparse matrix with elementary matrix according to face index + offset and vertex index
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3 j++) {
                G.addEntry(j, vertices[i]);
            }
        }
    }
}
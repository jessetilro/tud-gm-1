package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;

import jvx.project.PjWorkshop;
import jvx.numeric.PnSparseMatrix;

public class DifferentialCoordinates extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;
    PiVector[] elements;
    PiVector[] neighboursList;
    // Edge count provided by API
    int E;
    // Face count provided by API
    int F;
    // Vertex count provided by API
    int V;

    public DifferentialCoordinates() {
        super("DifferentialCoordinates");
        init();
    }
    
    @Override
    public void setGeometry(PgGeometry geom) {
        super.setGeometry(geom);
        m_geom      = (PgElementSet)super.m_geom;
        m_geomSave  = (PgElementSet)super.m_geomSave;
        elements = m_geom.getElements();
        E = m_geom.getNumEdges();
        F = m_geom.getNumElements();
        V = m_geom.getNumVertices();
        neighboursList = m_geom.getNeighbours();
    }
    
    public void init() {        
        super.init();
    }
    
    public PnSparseMatrix computeGradientMatrix() {
        // Create sparse gradient matrix
        PnSparseMatrix G = new PnSparseMatrix(3*F, V, 3);
        
        // Fill in sparse gradient matrix
        for (int i = 0; i < F; i++) {
            PdVector[] vertices = m_geom.getElementVertices(i);
            PdMatrix elementary = computeElementaryMatrix(i, vertices);
            elementaryFillG(G, elementary, i, vertices);
        }
        return G;
    }
    
    public PnSparseMatrix computeMv() {
        PnSparseMatrix Mv = new PnSparseMatrix(3*F, 3*F, 1);
        
        for (int i = 0; i < F; i++) {
            for (int j = 0; j < 3; j++) {
                double area = m_geom.getAreaOfElement(i);
                Mv.addEntry(i*3+j, i*3+j, area);
            }
        }
        return Mv;
    }
    
    public PnSparseMatrix computeCotangentMatrix(PnSparseMatrix G, PnSparseMatrix Mv) {
        PnSparseMatrix cotangentMatrix = PnSparseMatrix.multMatrices(PnSparseMatrix.multMatrices(G.transposeNew(), Mv, null), G, null);
        return cotangentMatrix;
    }
    
    public PnSparseMatrix computeCombinatorialLaplace() {
        PnSparseMatrix cLaplace = new PnSparseMatrix(V, V);
        for (int i = 0; i < V; i++) {
            PiVector neighbours = neighboursList[i];
            int degree = neighbours.m_data.length;
            double iets = -1.0 / degree;
            for (int j = 0; j < degree; j++) {
                cLaplace.addEntry(i, j, iets); 
            }
            cLaplace.addEntry(i, 1, -1); 
        }
        return cLaplace;
    }
    
//    public PnSparseMatrix computeMassMatrix() {
//        PnSparseMatrix massMatrix = PnSparseMatrix(V, V, 1);
//        
//    }
    
    public PdMatrix computeElementaryMatrix(int faceIndex, PdVector[] vertices) {
        double length = 1/(2 * m_geom.getAreaOfElement(faceIndex));
        
        PdVector firstV = vertices[0];
        PdVector secondV = vertices[1];
        PdVector thirdV = vertices[2];

        PdVector e1 = PdVector.subNew(thirdV, secondV);
        PdVector e2 = PdVector.subNew(firstV, thirdV);
        PdVector e3 = PdVector.subNew(secondV, firstV);
        
        PdVector normal = m_geom.getElementNormal(faceIndex);
        
        PdVector[] cols = new PdVector[3];
        
        PdVector col1 = PdVector.crossNew(normal, e1);
        PdVector col2 = PdVector.crossNew(normal, e2);
        PdVector col3 = PdVector.crossNew(normal, e3);
        
        col1.m_data[0] *= length;
        col1.m_data[1] *= length;
        col1.m_data[2] *= length;
        
        col2.m_data[0] *= length;
        col2.m_data[1] *= length;
        col2.m_data[2] *= length;
        
        col3.m_data[0] *= length;
        col3.m_data[1] *= length;
        col3.m_data[2] *= length;
                
        
        cols[0] = col1;
        cols[1] = col2;
        cols[2] = col3;
        
        PdMatrix elementaryMatrix = new PdMatrix(3);
        elementaryMatrix.setColumns(cols);
        return elementaryMatrix;
    }
    
    public void elementaryFillG(PnSparseMatrix G, PdMatrix elementary, int faceIndex, PdVector[] vertices) {
        // Fill in sparse matrix with elementary matrix according to face index + offset and vertex index
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                G.addEntry(faceIndex * 3, elements[faceIndex].getEntries()[i], elementary.getEntry(j, i));
            }
        }
    }
}
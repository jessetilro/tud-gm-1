package workshop;

import java.util.Hashtable;

import jv.geom.PgElementSet;
import jv.geom.PgPointSet;
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
        PgPointSet ps = new PgPointSet(3);
        
        PgElementSet es = new PgElementSet(1);
        es.setNumVertices(3);
        es.setVertex(1, 1, 1);
        es.setVertex(2, 1, 1);
        es.setVertex(1, 2, 1);
        es.setNumElements(1);
        es.setElement(0, 0, 1, 2);
        // m_geom = es;
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
        System.out.println("Gradient matrix: " + G);
        System.out.println("Gradient vector: " + calculateVector(G));
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
    
    public PnSparseMatrix computeMassMatrix() {
        PnSparseMatrix massMatrix = PnSparseMatrix(V, V, 1);
        
        // Initialize hashtable keeping track of vertex mass
        Hashtable<Double, Double> mass = new Hashtable<Double, Double>();
        for(int i = 0; i < V; i++) {
            mass.put(i, 0.0);
        }
        
        // Iterate over all faces and add its surface area to eacht of the participating vertices
        for(int i = 0; i < F; i++) {
            PdVector element = elements[i];
            double area = m_geom.getAreaOfElement(i);
            for(double index : element.getEntries()) {
                mass.put(index, mass.get(index) + area)
            }
        }
        
        // Fill in mass matrix with mass table values divided by 3.
        for(int i = 0; i < V; i++) {
            massMatrix.addEntry(i, i, mass.get(i) / 3.0);
        }
        
        return massMatrix();
    }
    
    public PdMatrix computeElementaryMatrix(int faceIndex, PdVector[] vertices) {
        double length = 1/(2 * m_geom.getAreaOfElement(faceIndex));
        
        PdVector firstV = vertices[0];
        PdVector secondV = vertices[1];
        PdVector thirdV = vertices[2];
        System.out.println("vertex: " + secondV);
        PdVector e1 = PdVector.subNew(thirdV, secondV);
        PdVector e2 = PdVector.subNew(firstV, thirdV);
        PdVector e3 = PdVector.subNew(secondV, firstV);
        System.out.println("Side e1: " + e1);
        System.out.println("Side e2: " + e2);
        System.out.println("Side e3: " + e3);
        System.out.println("length e1: " + e1.length());
        System.out.println("length e2: " + e2.length());
        System.out.println("length e3: " + e3.length());
        PdVector normal = PdVector.crossNew(e1, e2);
        normal.normalize();
        System.out.println("Normal: " + normal);
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
        System.out.println("Elementary: " + elementaryMatrix);
        return elementaryMatrix;
    }
    
    public void elementaryFillG(PnSparseMatrix G, PdMatrix elementary, int faceIndex, PdVector[] vertices) {
        // Fill in sparse matrix with elementary matrix according to face index + offset and vertex index
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println("vertex indices?: " + elements[faceIndex].getEntries()[i]);
                G.addEntry(faceIndex * 3 + j, elements[faceIndex].getEntries()[i], elementary.getEntry(j, i));
            }
        }
    }
    
    public PdVector calculateVector(PnSparseMatrix G) {
        double[] function = new double[3];
        function[0] = 0.0008;
        function[1] = 0.0;
        function[2] = 0.5564;
        PdVector functionVals = new PdVector(function);
        return PnSparseMatrix.rightMultVector(G, functionVals, null);
    }
}
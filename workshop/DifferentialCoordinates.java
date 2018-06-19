package workshop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import jv.geom.PgElementSet;
import jv.geom.PgPointSet;
import jv.object.PsObject;
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

//    public PdVector[] computeTransformedGradientVectorStacksAlt(PdMatrix transformationMatrix) {
//        PdVector g_x = new PdVector(F);
//        PdVector g_y = new PdVector(F);
//        PdVector g_z = new PdVector(F);
//
//        // compute (transformed) gradient vectors per face and stack the coordinates
//        for (int i = 0; i < F; i++) {
//            PiVector element = m_geom.getElement(i);
//            boolean selected = element.hasTag(PsObject.IS_SELECTED);
//
//            PdVector[] vertices = m_geom.getElementVertices(i);
//            PdVector gradientVector = computeGradientVector(i, vertices);
//
//            if (selected) {
//                gradientVector = gradientVector.leftMultMatrix(transformationMatrix);
//            }
//
//            System.out.println("Gradient vector for face " + i);
//            System.out.println(gradientVector);
//
//            g_x.setEntry(i, gradientVector.getEntry(0));
//            g_y.setEntry(i, gradientVector.getEntry(1));
//            g_z.setEntry(i, gradientVector.getEntry(2));
//        }
//
//        PdVector[] gradientVectorStacks = {g_x, g_y, g_z};
//        return gradientVectorStacks;
//    }

    public PdVector[] computeGradientVectorStacks(PnSparseMatrix G, PdVector[] vs) {
        PdVector v_x = vs[0];
        PdVector v_y = vs[1];
        PdVector v_z = vs[2];

        PdVector g_x = new PdVector(3 * F);
        PdVector g_y = new PdVector(3 * F);
        PdVector g_z = new PdVector(3 * F);

        g_x = PnSparseMatrix.rightMultVector(G, v_x, g_x);
        g_y = PnSparseMatrix.rightMultVector(G, v_y, g_y);
        g_z = PnSparseMatrix.rightMultVector(G, v_z, g_z);

        PdVector[] gradientVectorStacks = {g_x, g_y, g_z};
        return gradientVectorStacks;
    }

    public PdVector[] computeTransformedGradientVectorStacks(PdVector[] gs, PdMatrix transformationMatrix) {
        PdVector gt_x = (PdVector) gs[0].clone();
        PdVector gt_y = (PdVector) gs[1].clone();
        PdVector gt_z = (PdVector) gs[2].clone();
        PdVector[] gts = {gt_x, gt_y, gt_z};

        for (int i = 0; i < F; i++) {
            PiVector element = m_geom.getElement(i);
            boolean selected = element.hasTag(PsObject.IS_SELECTED);

            if (selected) {
                for (int dimension = 0; dimension < gts.length; dimension++) {
                    PdVector gt = gts[dimension];
                    double[] faceGradientVectorInDimensionEntries = {
                            gt.getEntry(3 * i + 0),
                            gt.getEntry(3 * i + 1),
                            gt.getEntry(3 * i + 2)
                    };
                    PdVector faceGradientVectorInDimension = new PdVector(faceGradientVectorInDimensionEntries);

                    PdVector transformedGradient = faceGradientVectorInDimension.leftMultMatrix(transformationMatrix);

                    gt.setEntry(3 * i + 0, transformedGradient.getEntry(0));
                    gt.setEntry(3 * i + 1, transformedGradient.getEntry(1));
                    gt.setEntry(3 * i + 2, transformedGradient.getEntry(2));
                }
            }
        }

        return gts;
    }

    public PdVector[] computeVertexPositionVectorStacks() {
        PdVector v_x = new PdVector(F);
        PdVector v_y = new PdVector(F);
        PdVector v_z = new PdVector(F);

        // compute position vectors per vertex and stack the coordinates
        for (int i = 0; i < V; i++) {
            PdVector vertex = m_geom.getVertex(i);

            v_x.setEntry(i, vertex.getEntry(0));
            v_y.setEntry(i, vertex.getEntry(1));
            v_z.setEntry(i, vertex.getEntry(2));
        }

        PdVector[] vectorStacks = {v_x, v_y, v_z};
        return vectorStacks;
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
        
        // Initialize hashtable with hashsets to keep track of neighbours.
        Hashtable<Integer, HashSet<Integer>> vertexNeighbours = new Hashtable<Integer, HashSet<Integer>>();
        for (int i = 0; i < V; i++) {
            vertexNeighbours.put(i, new HashSet<Integer>());
        }
        
        // Calculate degree of vertices
        for (int i = 0; i < F; i++) {
            ArrayList<Integer> vertices = new ArrayList<Integer>();
            for (int v1 : elements[i].getEntries()) {
                for (int v2 : elements[i].getEntries()) {
                    if (v1 != v2) {
                        vertexNeighbours.get(v1).add(v2);
                    }
                }
            }
        }

        // Fill in combinatorial Laplace matrix
        for (int i = 0; i < V; i++) {
            HashSet<Integer> neighbours = vertexNeighbours.get(i);
            int degree = neighbours.size();
            double iets = -1.0 / degree;
            for (int neighbour : neighbours) {
                cLaplace.addEntry(i, neighbour, iets); 
            }
            cLaplace.addEntry(i, i, 1); 
        }
        return cLaplace;
    }
    
    public PnSparseMatrix computeMassMatrix() {
        PnSparseMatrix massMatrix = new PnSparseMatrix(V, V, 1);
        
        // Initialize hashtable keeping track of vertex mass
        Hashtable<Integer, Double> mass = new Hashtable<Integer, Double>();
        for(int i = 0; i < V; i++) {
            mass.put(i, 0.0);
        }
        // Iterate over all faces and add its surface area to eacht of the participating vertices
        for(int i = 0; i < F; i++) {
            PiVector element = elements[i];
            double area = m_geom.getAreaOfElement(i);
            for(int index : element.getEntries()) {
                mass.put(index, mass.get(index) + area);
            }
        }
        
        // Fill in mass matrix with mass table values divided by 3.
        for(int i = 0; i < V; i++) {
            massMatrix.addEntry(i, i, mass.get(i) / 3.0);
        }
        
        return massMatrix;
    }

//    public PdVector computeGradientVector(int faceIndex, PdVector[] vertices) {
//        PdMatrix elementaryMatrix = computeElementaryMatrix(faceIndex, vertices);
//        PdVector u = computeUVector(vertices);
//
//        return u.leftMultMatrix(elementaryMatrix);
//    }
//
//    public PdVector computeUVector(PdVector[] vertices) {
//        PdVector u = new PdVector(3);
//        u.setEntry(0, vertices[0].getEntry(1));
//        u.setEntry(1, vertices[1].getEntry(1));
//        u.setEntry(2, vertices[2].getEntry(1));
//
//        System.out.println("U vector: " + u);
//        return u;
//    }

    public PdVector[] computeElementaryMatrixCols(int faceIndex, PdVector[] vertices) {
        double length = 1/(2 * m_geom.getAreaOfElement(faceIndex));

        PdVector firstV = vertices[0];
        PdVector secondV = vertices[1];
        PdVector thirdV = vertices[2];
        PdVector e1 = PdVector.subNew(thirdV, secondV);
        PdVector e2 = PdVector.subNew(firstV, thirdV);
        PdVector e3 = PdVector.subNew(secondV, firstV);
        PdVector normal = PdVector.crossNew(e1, e2);
        normal.normalize();
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

        return cols;
    }

    public PdMatrix computeElementaryMatrix(int faceIndex, PdVector[] vertices) {
        PdVector[] cols = computeElementaryMatrixCols(faceIndex, vertices);
        
        PdMatrix elementaryMatrix = new PdMatrix(3);
        elementaryMatrix.setColumns(cols);
        return elementaryMatrix;
    }
    
    public void elementaryFillG(PnSparseMatrix G, PdMatrix elementary, int faceIndex, PdVector[] vertices) {
        // Fill in sparse matrix with elementary matrix according to face index + offset and vertex index
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
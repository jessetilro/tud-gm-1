package workshop;

import java.awt.Color;
import java.util.*;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import jv.geom.PgBndPolygon;
import jv.geom.PgElementSet;
import jv.geom.PgPolygonSet;
import jv.geom.PgVectorField;
import jv.geom.PuCleanMesh;
import jv.number.PdColor;
import jv.object.PsConfig;
import jv.object.PsDebug;
import jv.object.PsObject;
import jv.project.PgGeometry;
import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jv.vecmath.PiVector;
import jv.vecmath.PuMath;
import jv.viewer.PvDisplay;
import jv.project.PvGeometryIf;

import jvx.project.PjWorkshop;

/**
 *  Workshop for surface registration
 */

public class Registration extends PjWorkshop {
	
	/** First surface to be registered. */	
	PgElementSet	m_surfP;	
	/** Second surface to be registered. */
	PgElementSet	m_surfQ;

	/** Optimal rotation matrix */
	Matrix			R;
	/** Optimal translation vector */
	Matrix			t;

	/** Number of vertices to sample from surface P */
	int 			n = 100;
	/** Number of iterations of the registration algorithm to perform */
	int				iterations = 10;
	
	
	/** Constructor */
	public Registration() {
		super("Surface Registration");
		if (getClass() == Registration.class) {
			init();
		}
	}
	
	/** Initialization */
	public void init() {
		super.init();
	}
	
	
	/** Set two Geometries. */
	public void setGeometries(PgElementSet surfP, PgElementSet surfQ) {
		m_surfP = surfP;
		m_surfQ = surfQ;

		// Cap sample size to total number of vertices in surface P
		int numP = m_surfP.getNumVertices();
		if (n > numP) {
			n = numP;
		}
	}

	/** Register surface Q with respect to P */
	public void register() {
		if (m_surfP == null || m_surfQ == null) return;

		for (int iteration = 0; iteration < iterations; iteration++) {
			// Compute optimal rotation matrix and translation vector
			computeOptimalRotationAndTranslation();

			// Apply optimal rotation matrix and translation vector to surface Q
			for (int i = 0; i < m_surfP.getNumVertices(); i++) {
				PdVector q = m_surfP.getVertex(i);
				Matrix vertex = convertPdVectorToJama(q);
				Matrix newP = R.times(vertex).plus(t);

				m_surfP.setVertex(i, newP.get(0, 0), newP.get(1, 0), newP.get(2, 0));
				// System.out.println("Updating position of vertex " + i + " to \t" + newP.get(0, 0) + "\t" + newP.get(1, 0) + "\t" + newP.get(2, 0));
			}
		}
	}

	public void computeOptimalRotationAndTranslation() {
		if (m_surfP == null || m_surfQ == null) return;

		PdVector[] allP = m_surfP.getVertices();
		PdVector[] allQ = m_surfQ.getVertices();
		List<PdVector> subsetP = getSubset(allP, n);

		Map<PdVector, PdVector> closestPairs = findClosestPairs(subsetP, allQ);

		PdVector centroidP = m_surfP.getCenterOfGravity();
		System.out.println("Centroid P = " + centroidP.toString());
		PdVector centroidQ = m_surfQ.getCenterOfGravity();
		System.out.println("Centroid Q = " + centroidQ.toString());

		PdMatrix covarianceMatrix = computeCovarianceMatrix(closestPairs, centroidP, centroidQ);
		System.out.println("Covariance matrix = " + covarianceMatrix.toString());

		Matrix matrix = convertPdMatrixToJama(covarianceMatrix);
		SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
		Matrix U = svd.getU();
		Matrix V = svd.getV();
		Matrix D = svd.getS();

		double detVUT = V.times(U.transpose()).det();
		double[][] diagonalRows = {{1, 0, 0}, {0, 1, 0}, {0, 0, detVUT}};
		Matrix diagonal = new Matrix(diagonalRows);

		R = V.times(diagonal).times(U.transpose());

		System.out.println("Rotation matrix = ");
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				System.out.print(R.get(i, j) + "\t");
			}
			System.out.println();
		}
		System.out.println();

		Matrix qc = convertPdVectorToJama(centroidQ);
		Matrix pc = convertPdVectorToJama(centroidP);

		t = qc.minus(R.times(pc));

		System.out.println("Translation vector = ");
		for (int i = 0; i < 3; i++) {
			System.out.println(t.get(i, 0));
		}
		System.out.println();
	}

	private Map<PdVector, PdVector> findClosestPairs(Collection<PdVector> subsetP, PdVector[] allQ) {
		Map<PdVector, PdVector> closestPairs = new HashMap();
		for (PdVector p : subsetP) {
			PdVector closest = null;
			double closestDistance = 0;

			for (PdVector q : allQ) {
				double distance = PdVector.subNew(p, q).length();
				if (closest == null || distance < closestDistance) {
					closest = q;
					closestDistance = distance;
				}
			}

			closestPairs.put(p, closest);
		}
		return closestPairs;
	}

	private List<PdVector> getSubset(PdVector[] vertices, int n) {
		Random rand = new Random(System.currentTimeMillis());

		int totalNumberOfVertices = vertices.length;

		List<PdVector> subset = new ArrayList();
		Set<Integer> selectedIndices = new HashSet();

		while (subset.size() < n) {
			int index;
			do {
				index = rand.nextInt(totalNumberOfVertices);
			} while (selectedIndices.contains(index));
			selectedIndices.add(index);
			subset.add(vertices[index]);
		}

		return subset;
	}

	private PdMatrix computeCovarianceMatrix(Map<PdVector, PdVector> closestPairs, PdVector centroidP, PdVector centroidQ) {
		PdMatrix covarianceMatrix = new PdMatrix(3, 3);
		for (Map.Entry<PdVector, PdVector> pair : closestPairs.entrySet()) {
			PdVector pDiff = PdVector.subNew(pair.getKey(), centroidP);
			PdVector qDiff = PdVector.subNew(pair.getValue(), centroidQ);

			// Super nasty manual multiplication between vector and transposed vector
			// because the javaview API sucks really bad!
			PdVector[] cols = new PdVector[3];
			for (int i = 0; i < 3; i++) {
				PdVector col = (PdVector) pDiff.clone();
				col.multScalar(qDiff.getEntry(i));
				cols[i] = col;
			}

			PdMatrix matrix = new PdMatrix(3, 3);
			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < 3; i++) {
					matrix.setEntry(i, j, cols[j].getEntry(i));
				}
			}

			covarianceMatrix.add(matrix);
		}
		covarianceMatrix.multScalar(1D / (double) closestPairs.size());

		return covarianceMatrix;
	}

	private Matrix convertPdMatrixToJama(PdMatrix matrix) {
		double[][] entries = matrix.getEntries();
		Matrix newMatrix = new Matrix(entries);
		return newMatrix;
	}

	private Matrix convertPdVectorToJama(PdVector vector) {
		double[] entries = vector.getEntries();
		Matrix newMatrix = new Matrix(entries, 3);
		return newMatrix;
	}
}

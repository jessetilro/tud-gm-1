package workshop;

import java.awt.Color;
import java.util.*;

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
	}

	/** Register surface Q with respect to P */
	public void register() {
		if (m_surfP == null || m_surfQ == null) return;

		PdVector[] allP = m_surfP.getVertices();
		PdVector[] allQ = m_surfQ.getVertices();
		List<PdVector> subsetP = getSubset(allP,5);

		Map<PdVector, PdVector> closestPairs = findClosestPairs(subsetP, allQ);

		PdVector centroidP = m_surfP.getCenterOfGravity();
		PdVector centroidQ = m_surfQ.getCenterOfGravity();
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

			System.out.println(p.toString() + " matched with " + closest.toString());
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
	
	
}

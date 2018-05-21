package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Genus extends PjWorkshop {

	PgElementSet m_geom;
	PgElementSet m_geomSave;

	public Genus() {
		super("Genus");
		init();
	}
	
	@Override
	public void setGeometry(PgGeometry geom) {
		super.setGeometry(geom);
		m_geom 		= (PgElementSet)super.m_geom;
		m_geomSave 	= (PgElementSet)super.m_geomSave;
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

}
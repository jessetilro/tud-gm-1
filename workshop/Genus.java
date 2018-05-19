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

		// Could not find anything in API for counting vertices,
		// so implemented a strategy using a HashSet to count
		// the number of unique vertices
		int maxNumVertices = E * 2 + m_geom.getNumUnusedVertices();
		Set uniqueVertices = new HashSet<PdVector>(maxNumVertices);
		for(int i = 0; i < F; i++) {
			PdVector[] vertices = m_geom.getElementVertices(i);
			for (PdVector vertex : vertices) {
				uniqueVertices.add(vertex);
			}
		}
		int V = uniqueVertices.size();

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
	
	public void makeRandomElementColors() {
		//assure that the color array is allocated
		m_geom.assureElementColors();
		
		Random rand = new Random();
		Color randomColor;
		
		int noe = m_geom.getNumElements();
		for(int i=0; i<noe; i++){
			randomColor = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);//new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
			m_geom.setElementColor(i, randomColor);
		}
		m_geom.showElementColorFromVertices(false);
		m_geom.showElementColors(true);	
		m_geom.showSmoothElementColors(false);
	}
	
	public void makeRandomVertexColors() {
		//assure that the color array is allocated
		m_geom.assureVertexColors();
		
		Random rand = new Random();
		Color randomColor;
		
		int nov = m_geom.getNumVertices();
		for(int i=0; i<nov; i++){
			randomColor = Color.getHSBColor(rand.nextFloat(), 1.0f, 1.0f);
			m_geom.setVertexColor(i, randomColor);
		}
		
		m_geom.showElementColors(true);	
		m_geom.showVertexColors(true);
		m_geom.showElementColorFromVertices(true);	
		m_geom.showSmoothElementColors(true);
	}
	
	
	public void setXOff(double xOff) {
		int nov = m_geom.getNumVertices();
		PdVector v = new PdVector(3);
		// the double array is v.m_data 
		for (int i=0; i<nov; i++) {
			v.copyArray(m_geomSave.getVertex(i));
			v.setEntry(0, v.getEntry(0)+xOff);
			m_geom.setVertex(i, v);
		}
	}
}
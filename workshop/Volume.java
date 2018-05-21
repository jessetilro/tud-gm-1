package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop;

public class Volume extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;

    public Volume() {
        super("Volume");
        init();
    }

    // Super triangulates the geometry as is needed for pre-processing.
    @Override
    public void setGeometry(PgGeometry geom) {
        super.setGeometry(geom);
        m_geom 		= (PgElementSet)super.m_geom;
        m_geomSave 	= (PgElementSet)super.m_geomSave;
    }

    public void init() {
        super.init();
    }

    /**
     * Computes the volume of a mesh by computing the volume of the tetrahedrons (with respect to the origin).
     * This method was implemented according to the paper by C. Zhang and T. Chen.
     * There is also a method available for the element set called getVolume() that computes this value.
     *
     * @return The volume (double) of the current mesh.
     */
    public double computeVolume() {

        double volume_total = 0.0;

        for(int i = 0; i < m_geom.getNumElements(); i++){
            PdVector[] vertices = m_geom.getElementVertices(i);

            if (vertices.length > 3){
                System.err.println("The number of vertices of triangle " + i + " is greater than 3");
            }

            PdVector firstV = vertices[0];
            PdVector secondV = vertices[1];
            PdVector thirdV = vertices[2];

            double x1 = firstV.m_data[0];
            double x2 = firstV.m_data[1];
            double x3 = firstV.m_data[2];

            double y1 = secondV.m_data[0];
            double y2 = secondV.m_data[1];
            double y3 = secondV.m_data[2];

            double z1 = thirdV.m_data[0];
            double z2 = thirdV.m_data[1];
            double z3 = thirdV.m_data[2];

            volume_total += (1D/6D) * (-x3*y2*z1 + x2*y3*z1 + x3*y1*z2
                    -x1*y3*z2 - x2*y1*z3 + x1*y2*z3);
        }

        return Math.abs(volume_total);
    }
}

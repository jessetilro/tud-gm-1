package workshop;

import jv.geom.PgElementSet;
import jv.project.PgGeometry;
import jvx.project.PjWorkshop;

public class Volume extends PjWorkshop {

    PgElementSet m_geom;
    PgElementSet m_geomSave;

    public Volume() {
        super("Volume");
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

    public int computeVolume() {

        return 0;
    }
}

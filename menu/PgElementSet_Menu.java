package menu;

import jv.geom.PgPointSet_Menu;
import jv.geom.PgElementSet;
import jv.geom.PgPointSet;
import jv.object.PsDebug;
import jv.object.PsDialog;
import jv.objectGui.PsMethodMenu;
import jv.object.PsObject;
import jv.project.PgGeometryIf;
import jv.project.PvDisplayIf;
import jv.project.PvViewerIf;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop_Dialog;
import workshop.*;

public class PgElementSet_Menu extends PgPointSet_Menu {

	private enum MenuEntry{
		MyWorkshop			("Color All The Things Machine"),
		Registration		("Surface Registrator Deluxe"),
		Genus				("Genus Computator 2000"),
		Volume				("Volume Megatron 10X"),
		ConnectedComponents ("Connected Component Enumerator 7000"),
		DifferentialCoordinates ("Differential Coordinates")
		// Additional entries...
		;
		protected final String name;
		MenuEntry(String name) { this.name  = name; }

		public static MenuEntry fromName(String name){
			for (MenuEntry entry : MenuEntry.values()) {
				if(entry.name.equals(name)) return entry;
			}
			return null;
		}
	}

	protected PgElementSet m_elementSet;

	protected PvViewerIf m_viewer;

	public void init(PsObject anObject) {
		super.init(anObject);
		m_elementSet = (PgElementSet)anObject;

		String menuDev = "Floris Tim Jesse Awesome Tools";
		addMenu(menuDev);
		for (MenuEntry entry : MenuEntry.values()) {
			addMenuItem(menuDev, entry.name);
		}
	}

	public boolean applyMethod(String aMethod) {
		if (super.applyMethod(aMethod))
			return true;

		if (PsDebug.NOTIFY) PsDebug.notify("trying method = "+aMethod);

		PvDisplayIf currDisp = null;
		if (getViewer() == null) {
			if (PsDebug.WARNING) PsDebug.warning("missing viewer");
		} else {
			currDisp = getViewer().getDisplay();
			if (currDisp == null) PsDebug.warning("missing display.");
		}

		PsDialog dialog;
		MenuEntry entry = MenuEntry.fromName(aMethod);
		if(entry == null) return false;
		switch (entry) {
		case MyWorkshop:
			MyWorkshop ws = new MyWorkshop();
			ws.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				ws.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(ws);
			dialog.update(ws);
			dialog.setVisible(true);
			break;
		case Registration:
			Registration reg = new Registration();
			reg.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				reg.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(reg);
			dialog.update(reg);
			dialog.setVisible(true);
			break;
		case Genus:
			Genus genus = new Genus();
			genus.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				genus.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(genus);
			dialog.update(genus);
			dialog.setVisible(true);
			break;
		case Volume:
			Volume volume = new Volume();
			volume.setGeometry(m_elementSet);
			if (currDisp == null) {
				if (PsDebug.WARNING) PsDebug.warning("missing display.");
			} else
				volume.setDisplay(currDisp);
			dialog = new PjWorkshop_Dialog(false);
			dialog.setParent(volume);
			dialog.update(volume);
			dialog.setVisible(true);
			break;
		case ConnectedComponents:
		    ConnectedComponents cc = new ConnectedComponents();
		    cc.setGeometry(m_elementSet);
		    if (currDisp == null) {
                if (PsDebug.WARNING) PsDebug.warning("missing display.");
            } else
                cc.setDisplay(currDisp);
            dialog = new PjWorkshop_Dialog(false);
            dialog.setParent(cc);
            dialog.update(cc);
            dialog.setVisible(true);
            break;
	    case DifferentialCoordinates:
	        DifferentialCoordinates dc = new DifferentialCoordinates();
            dc.setGeometry(m_elementSet);
            if (currDisp == null) {
                if (PsDebug.WARNING) PsDebug.warning("missing display.");
            } else
                dc.setDisplay(currDisp);

            dialog = new PjWorkshop_Dialog(false);
            dialog.setParent(dc);
            dialog.update(dc);
            dialog.setSize(600, 1000);
            dialog.setVisible(true);
            break;
        }

		return true;
	}

	public PvViewerIf getViewer() { return m_viewer; }

	public void setViewer(PvViewerIf viewer) { m_viewer = viewer; }

}

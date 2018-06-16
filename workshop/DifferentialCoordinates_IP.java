package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;

import jvx.project.PjWorkshop_IP;
import jvx.numeric.PnSparseMatrix;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifferentialCoordinates_IP extends PjWorkshop_IP implements ActionListener {

    protected Button m_bDifferentialCoordinates;
    protected TextField m_tDifferentialCoordinates;
    protected PuDouble m_xOff;

    DifferentialCoordinates m_ws;

    public DifferentialCoordinates_IP() {
        super();
        if(getClass() == DifferentialCoordinates_IP.class)
            init();
    }
    
    public void init() {
        super.init();
        setTitle("aaaaaaaaaaaa");
    }
    
    public String getNotice() {
        return "Press the \"Count\" button!";
    }
    
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_ws = (DifferentialCoordinates)parent;
    
        addSubTitle("Tool to count the number of connected components.");
        
        m_bDifferentialCoordinates = new Button("Count");
        m_bDifferentialCoordinates.addActionListener(this);

        m_tDifferentialCoordinates = new TextField("-- resulting number of components --");

        Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel1.add(m_bDifferentialCoordinates);
        panel1.add(m_tDifferentialCoordinates);

        add(panel1);
        
        validate();
    }
    
    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bDifferentialCoordinates) {
            PnSparseMatrix gradientMatrix = m_ws.computeGradientMatrix();
            return;
        }
    }
    /**
     * Get information which bottom buttons a dialog should create
     * when showing this info panel.
     */
    protected int getDialogButtons()        {
        return PsDialog.BUTTON_OK;
    }
}

package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectedComponents_IP extends PjWorkshop_IP implements ActionListener {

    protected Button m_bCountComponents;
    protected TextField m_tComponents;
    protected PuDouble m_xOff;

    ConnectedComponents m_ws;

    public ConnectedComponents_IP() {
        super();
        if(getClass() == ConnectedComponents_IP.class)
            init();
    }
    
    public void init() {
        super.init();
        setTitle("Connected Component Enumerator 7000");
    }
    
    public String getNotice() {
        return "Press the \"Count\" button!";
    }
    
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_ws = (ConnectedComponents)parent;
    
        addSubTitle("Tool to count the number of connected components.");
        
        m_bCountComponents = new Button("Count");
        m_bCountComponents.addActionListener(this);

        m_tComponents = new TextField("-- resulting number of components --");

        Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel1.add(m_bCountComponents);
        panel1.add(m_tComponents);

        add(panel1);
        
        validate();
    }
    
    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bCountComponents) {
            int numComponents = m_ws.countComponents();
            m_tComponents.setText(Integer.toString(numComponents));
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

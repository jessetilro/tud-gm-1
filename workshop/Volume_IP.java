package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Volume_IP extends PjWorkshop_IP implements ActionListener {
    protected Button m_bComputeVolume;
    protected TextField m_tVolume;
    protected PuDouble m_xOff;

    Volume m_ws;

    public Volume_IP() {
        super();
        if(getClass() == Volume_IP.class)
            init();
    }

    public void init() {
        super.init();
        setTitle("Volume Megatron 10X");
    }

    public String getNotice() {
        return "Press \"Compute Volume\" to find out the volume of the object.";
    }

    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_ws = (Volume)parent;

        addSubTitle("Surface analysis tool that computes the volume.");

        m_bComputeVolume = new Button("Compute volume");
        m_bComputeVolume.addActionListener(this);

        m_tVolume = new TextField("volume will appear here...");

        Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
        panel1.add(m_bComputeVolume);
        panel1.add(m_tVolume);

        add(panel1);

        validate();
    }

    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == m_bComputeVolume) {
            double volume = m_ws.computeVolume();
            m_tVolume.setText(Double.toString(volume));
            return;
        }
    }
    /**
     * Get information which bottom buttons a dialog should create
     * when showing this info panel.
     */
    protected int getDialogButtons()		{
        return PsDialog.BUTTON_OK;
    }

}

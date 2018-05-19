package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Genus_IP extends PjWorkshop_IP implements ActionListener {

	protected Button m_bComputeGenus;
	protected TextField m_tGenus;
	protected PuDouble m_xOff;

	Genus m_ws;

	public Genus_IP() {
		super();
		if(getClass() == Genus_IP.class)
			init();
	}
	
	public void init() {
		super.init();
		setTitle("Genus Computator 2000");
	}
	
	public String getNotice() {
		return "Press \"Compute Genus\" to find out the genus of the surface.";
	}
	
	public void setParent(PsUpdateIf parent) {
		super.setParent(parent);
		m_ws = (Genus)parent;
	
		addSubTitle("Surface analysis tool that computes the genus.");
		
		m_bComputeGenus = new Button("Compute Genus");
		m_bComputeGenus.addActionListener(this);

		m_tGenus = new TextField("genus will appear here...");

		Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER));
		panel1.add(m_bComputeGenus);
		panel1.add(m_tGenus);

		add(panel1);
		
		validate();
	}
	
	/**
	 * Handle action events fired by buttons etc.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == m_bComputeGenus) {
			int genus = m_ws.computeGenus();
			m_tGenus.setText(Integer.toString(genus));
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

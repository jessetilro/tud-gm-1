package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;

import jvx.project.PjWorkshop_IP;
import jvx.numeric.PnSparseMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifferentialCoordinates_IP extends PjWorkshop_IP implements ActionListener {

    protected Button buttonComputeG;
    protected Button buttonComputeS;
    protected Button buttonComputeMass;
    protected Button buttonComputeL;
    protected TextArea textAreaG;
    protected TextArea textAreaS;
    protected TextArea textAreaMass;
    protected TextArea textAreaL;
    protected PuDouble m_xOff;

    protected PnSparseMatrix gradientMatrix;
    DifferentialCoordinates m_ws;

    public DifferentialCoordinates_IP() {
        super();
        if(getClass() == DifferentialCoordinates_IP.class)
            init();
    }
    
    public void init() {
        super.init();
        setTitle("Differntial Coordinates");
        gradientMatrix = null;
    }
    
    public String getNotice() {
        return "Press the \"Compute\" buttons!";
    }
    
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_ws = (DifferentialCoordinates)parent;
    
        addSubTitle("Tool to compute sparse matrix G, cotangent matrix S, laplace matrix L, and more.");

        buttonComputeG = new Button("Compute sparse matrix G");
        buttonComputeG.addActionListener(this);
        buttonComputeS = new Button("Compute cotangent matrix S");
        buttonComputeS.addActionListener(this);
        buttonComputeMass = new Button("Compute mass matrix M");
        buttonComputeMass.addActionListener(this);
        buttonComputeL = new Button("Compute laplace matrix L");
        buttonComputeL.addActionListener(this);

        textAreaG = new TextArea("-- sparse matrix G --");
        textAreaS = new TextArea("-- cotangent S --");
        textAreaMass = new TextArea("-- mass matrix M --");
        textAreaL = new TextArea("-- laplace matrix L --");

        Panel panel1 = new Panel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.add(buttonComputeG);
        panel1.add(textAreaG);

        panel1.add(buttonComputeS);
        panel1.add(textAreaS);

        panel1.add(buttonComputeMass);
        panel1.add(textAreaMass);

        panel1.add(buttonComputeL);
        panel1.add(textAreaL);

        add(panel1);
        
        validate();
    }
    
    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == buttonComputeG) {
            gradientMatrix = m_ws.computeGradientMatrix();
            textAreaG.setText(gradientMatrix.toString());

            return;
        }
        if (source == buttonComputeS) {
            if(gradientMatrix == null){
                gradientMatrix = m_ws.computeGradientMatrix();
            }
            PnSparseMatrix mv = m_ws.computeMv();
            PnSparseMatrix cotangentMatrix = m_ws.computeCotangentMatrix(gradientMatrix, mv);
            textAreaS.setText(cotangentMatrix.toString());
            return;
        }
        if (source == buttonComputeMass) {
            PnSparseMatrix massMatrix = m_ws.computeMassMatrix();
            textAreaMass.setText(massMatrix.toString());
            return;
        }
        if (source == buttonComputeL) {
            PnSparseMatrix laplaceMatrix = m_ws.computeCombinatorialLaplace();
            textAreaL.setText(laplaceMatrix.toShortString());
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

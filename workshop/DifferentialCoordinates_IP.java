package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;

import jv.vecmath.PdMatrix;
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

    protected TextField textFieldG1;
    protected TextField textFieldG2;
    protected TextField textFieldG3;
    protected TextField textFieldG4;
    protected TextField textFieldG5;
    protected TextField textFieldG6;
    protected TextField textFieldG7;
    protected TextField textFieldG8;
    protected TextField textFieldG9;

    protected Button buttonComputeInput;
    protected TextArea testTextArea;

    protected PnSparseMatrix gradientMatrix;
    DifferentialCoordinates m_ws;

    public DifferentialCoordinates_IP() {
        super();
        if(getClass() == DifferentialCoordinates_IP.class)
            init();
    }
    
    public void init() {
        super.init();
        setTitle("Differential Coordinates");
        gradientMatrix = null;
    }
    
    public String getNotice() {
        return "Press the \"Compute\" buttons!";
    }
    
    public void setParent(PsUpdateIf parent) {
        super.setParent(parent);
        m_ws = (DifferentialCoordinates)parent;
    
        addSubTitle("Tool to compute sparse matrix G, cotangent matrix S, laplace matrix L, and more.");

        computeMatricesUI();
        inputMatrixUI();
        validate();
    }

    public void computeMatricesUI(){
        Panel panel1 = new Panel();
        buttonComputeG = new Button("Compute sparse matrix G");
        buttonComputeG.addActionListener(this);
        buttonComputeS = new Button("Compute cotangent matrix S");
        buttonComputeS.addActionListener(this);
        buttonComputeMass = new Button("Compute mass matrix M");
        buttonComputeMass.addActionListener(this);
        buttonComputeL = new Button("Compute laplace matrix L");
        buttonComputeL.addActionListener(this);

        textAreaG = new TextArea("-- sparse matrix G --", 4, 20);
        textAreaS = new TextArea("-- cotangent S --", 4, 20);
        textAreaMass = new TextArea("-- mass matrix M --", 4, 20);
        textAreaL = new TextArea("-- laplace matrix L --", 4, 20);

        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.add(buttonComputeG);
        panel1.add(textAreaG);

        panel1.add(buttonComputeS);
        panel1.add(textAreaS);

        panel1.add(buttonComputeMass);
        panel1.add(textAreaMass);

        panel1.add(buttonComputeL);
        panel1.add(textAreaL);
        panel1.add(new JLabel("Insert matrix G to transform"));
        add(panel1);
    }

    public void inputMatrixUI() {
        Panel panel2 = new Panel();

        panel2.setLayout(new GridLayout(3,3));
        textFieldG1 = new TextField();
        panel2.add(textFieldG1);
        textFieldG2 = new TextField();
        panel2.add(textFieldG2);
        textFieldG3 = new TextField();
        panel2.add(textFieldG3);
        textFieldG4 = new TextField();
        panel2.add(textFieldG4);
        textFieldG5 = new TextField();
        panel2.add(textFieldG5);
        textFieldG6 = new TextField();
        panel2.add(textFieldG6);
        textFieldG7 = new TextField();
        panel2.add(textFieldG7);
        textFieldG8 = new TextField();
        panel2.add(textFieldG8);
        textFieldG9 = new TextField();
        panel2.add(textFieldG9);
        add(panel2);

        Panel panel3 = new Panel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        buttonComputeInput = new Button("Use input matrix to transform");
        buttonComputeInput.addActionListener(this);
        testTextArea = new TextArea("Text Area for testing the compute button",4, 20);
        panel3.add(buttonComputeInput);
        panel3.add(testTextArea);
        add(panel3);

    }
    
    /**
     * Handle action events fired by buttons etc.
     */
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == buttonComputeG) {
            gradientMatrix = m_ws.computeGradientMatrix();
            textAreaG.setText(gradientMatrix.toShortString());

            return;
        }
        if (source == buttonComputeS) {
            if(gradientMatrix == null){
                gradientMatrix = m_ws.computeGradientMatrix();
            }
            PnSparseMatrix mv = m_ws.computeMv();
            PnSparseMatrix cotangentMatrix = m_ws.computeCotangentMatrix(gradientMatrix, mv);
            textAreaS.setText(cotangentMatrix.toShortString());
            return;
        }
        if (source == buttonComputeMass) {
            PnSparseMatrix massMatrix = m_ws.computeMassMatrix();
            textAreaMass.setText(massMatrix.toShortString());
            return;
        }
        if (source == buttonComputeL) {
            PnSparseMatrix laplaceMatrix = m_ws.computeCombinatorialLaplace();
            textAreaL.setText(laplaceMatrix.toShortString());
            return;
        }
        if (source == buttonComputeInput) {
            PdMatrix inputMatrix = new PdMatrix(3);
            inputMatrix.addEntry(0, 0, evaluateTextField(textFieldG1));
            inputMatrix.addEntry(0, 1, evaluateTextField(textFieldG2));
            inputMatrix.addEntry(0, 2, evaluateTextField(textFieldG3));

            inputMatrix.addEntry(1, 0, evaluateTextField(textFieldG4));
            inputMatrix.addEntry(1, 1, evaluateTextField(textFieldG5));
            inputMatrix.addEntry(1, 2, evaluateTextField(textFieldG6));

            inputMatrix.addEntry(2, 0, evaluateTextField(textFieldG7));
            inputMatrix.addEntry(2, 1, evaluateTextField(textFieldG8));
            inputMatrix.addEntry(2, 2, evaluateTextField(textFieldG9));
            testTextArea.setText(inputMatrix.toShortString());
            return;
        }
    }

    /**
     * Makes sure the textField has value 0 instead of nothing.
     * @param text
     * @return A double to place in the matrix
     */
    protected double evaluateTextField(TextField text){
        if(text.getText().isEmpty()){
            text.setText("0");
        }

        return Double.parseDouble(text.getText());
    }

    /**
     * Get information which bottom buttons a dialog should create
     * when showing this info panel.
     */
    protected int getDialogButtons()        {
        return PsDialog.BUTTON_OK;
    }
}

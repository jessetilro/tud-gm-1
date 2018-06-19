package workshop;

import jv.number.PuDouble;
import jv.object.PsDialog;
import jv.object.PsUpdateIf;

import jv.vecmath.PdMatrix;
import jv.vecmath.PdVector;
import jvx.project.PjWorkshop_IP;
import jvx.numeric.PnSparseMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dev6.numeric.PnMumpsSolver;

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

        textAreaG = new TextArea("-- sparse matrix G --", 16, 20);
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
        textFieldG1 = new TextField("1");
        panel2.add(textFieldG1);
        textFieldG2 = new TextField("0");
        panel2.add(textFieldG2);
        textFieldG3 = new TextField("0");
        panel2.add(textFieldG3);
        textFieldG4 = new TextField("0");
        panel2.add(textFieldG4);
        textFieldG5 = new TextField("1");
        panel2.add(textFieldG5);
        textFieldG6 = new TextField("0");
        panel2.add(textFieldG6);
        textFieldG7 = new TextField("0");
        panel2.add(textFieldG7);
        textFieldG8 = new TextField("0");
        panel2.add(textFieldG8);
        textFieldG9 = new TextField("1");
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
        String[] dims = {"x", "y", "z"};
        Object source = event.getSource();
        if (source == buttonComputeG) {
            PnSparseMatrix G = m_ws.computeGradientMatrix();
            gradientMatrix = G;

            PdMatrix transformationMatrix = getInputMatrix();

            PdVector[] vs = m_ws.computeVertexPositionVectorStacks();
            PdVector[] gs = m_ws.computeGradientVectorStacks(gradientMatrix, vs);
            PdVector[] gts = m_ws.computeTransformedGradientVectorStacks(gs, transformationMatrix);

            String output = "=== Gradient Matrix ===\n" + gradientMatrix.toString() + "\n";

            output += "=== Vertex Position Vector Stacks ===\n";
            for (int i = 0; i < 3; i++) {
                output += "v_" + dims[i] + vs[i].toShortString() + "\n";
            }

            output += "=== Gradient Vector Stacks ===\n";
            for (int i = 0; i < 3; i++) {
                output += "g_" + dims[i] + gs[i].toShortString() + "\n";
            }

            output += "=== Transformed Gradient Vector Stacks ===\n";
            for (int i = 0; i < 3; i++) {
                output += "gt_" + dims[i] + gts[i].toShortString() + "\n";
            }

            PnSparseMatrix M_v = m_ws.computeMv();

            // Euler-Lagrange equation: Ax = b in 3 dimensions
            PnSparseMatrix A = PnSparseMatrix.multMatrices(G.transposeNew(), PnSparseMatrix.multMatrices(M_v, G, new PnSparseMatrix()), new PnSparseMatrix());
            PdVector b_x = PnSparseMatrix.rightMultVector(PnSparseMatrix.multMatrices(G.transposeNew(), M_v, new PnSparseMatrix()), gts[0], new PdVector());
            PdVector b_y = PnSparseMatrix.rightMultVector(PnSparseMatrix.multMatrices(G.transposeNew(), M_v, new PnSparseMatrix()), gts[1], new PdVector());
            PdVector b_z = PnSparseMatrix.rightMultVector(PnSparseMatrix.multMatrices(G.transposeNew(), M_v, new PnSparseMatrix()), gts[2], new PdVector());

            PdVector x_x = new PdVector(3 * m_ws.F);
            PdVector x_y = new PdVector(3 * m_ws.F);
            PdVector x_z = new PdVector(3 * m_ws.F);

            try {
                PnMumpsSolver.solve(A, x_x, b_x, PnMumpsSolver.Type.GENERAL_SYMMETRIC);
                PnMumpsSolver.solve(A, x_y, b_y, PnMumpsSolver.Type.GENERAL_SYMMETRIC);
                PnMumpsSolver.solve(A, x_z, b_z, PnMumpsSolver.Type.GENERAL_SYMMETRIC);

                output += "=== Solution ===\n";
                output += "vt_x = " + x_x.toShortString() + "\n";
                output += "vt_y = " + x_y.toShortString() + "\n";
                output += "vt_z = " + x_z.toShortString() + "\n";

                PdVector[] vts = {x_x, x_y, x_z};

                // translate solution to original centroid
                PdVector centroidOriginal = DifferentialCoordinates.computeCentroid(vs);
                PdVector centroidAfterSolving = DifferentialCoordinates.computeCentroid(vts);

                output += "Centroid Original: " + centroidOriginal.toShortString() + "\n";
                output += "Centroid After Solving: " + centroidAfterSolving.toShortString() + "\n";
                
                PdVector translation = PdVector.subNew(centroidBefore, centroidAfter);

                String xCoordAfterSolving = x_x.toShortString();

                // x_x.add(translation.getEntry(0));
                // x_y.add(translation.getEntry(1));
                // x_z.add(translation.getEntry(2));

                PdVector centroidAfterTranslation = DifferentialCoordinates.computeCentroid(vts);

                output += "Centroid After Translation: " + centroidAfterTranslation.toShortString() + "\n";

                output += "Translation: " + translation.toShortString() + "\n";

                output += "X coords Original............:" + vs[0].toShortString() + "\n";
                output += "X Coords After Solving.......: " + x_x.toShortString() + "\n";
                output += "X Coords After Translation...: " + x_x.toShortString() + "\n";


                m_ws.updateGeometry(vts);
                m_ws.m_geom.update(m_ws.m_geom);
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("System could not be solved, please refer to stacktrace above.");
            }

            textAreaG.setText(output);
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
            testTextArea.setText(getInputMatrix().toShortString());
            return;
        }
    }

    /**
     * Returns the matrix represented by the form input
     * @return
     */
    protected PdMatrix getInputMatrix() {
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

        return inputMatrix;
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

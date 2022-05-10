package gui.panels;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import gui.MainForm;
import renderer.Shader;
import scene.Resources;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class CodePanel extends JPanel {
    public MainForm mf;

    JPanel mainPanel;
    GroupLayout mainLayout;
    JCheckBox liveMode;
    public JComboBox<String> shaderComboBox;

    RSyntaxTextArea fragmentShaderEditor, vertexShaderEditor;

    public CodePanel(MainForm mf) {
        this.mf = mf;

        mainLayout = new GroupLayout(this);
        setLayout(mainLayout);

        fragmentShaderEditor = new RSyntaxTextArea();
        fragmentShaderEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        fragmentShaderEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (liveMode.isSelected()) {
                    mf.renderer.activeShader.updateFragmentShaderSourceCode(fragmentShaderEditor.getText());
                }
            }
        });

        vertexShaderEditor = new RSyntaxTextArea();
        vertexShaderEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        vertexShaderEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (liveMode.isSelected()) {
                    mf.renderer.activeShader.updateVertexShaderSourceCode(vertexShaderEditor.getText());
                }
            }
        });
        
        RTextScrollPane fragmentShaderScrollPane = new RTextScrollPane(fragmentShaderEditor);
        RTextScrollPane vertexShaderScrollPane = new RTextScrollPane(vertexShaderEditor);
        
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Fragment Shader", fragmentShaderScrollPane);
        tabPane.addTab("Vertex Shader", vertexShaderScrollPane);



        liveMode = new JCheckBox("Live Mode");
        
        shaderComboBox = new JComboBox<String>();
        shaderComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (evt.getActionCommand().equals("comboBoxChanged")) {
                    changeShader(evt);
                }
            }
        });

        mainLayout.setHorizontalGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
            .addComponent(liveMode)
            .addComponent(shaderComboBox));

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
            .addComponent(tabPane)
            .addComponent(liveMode)
            .addComponent(shaderComboBox));
    }

    private void changeShader(java.awt.event.ActionEvent evt) {
        String name = shaderComboBox.getSelectedItem().toString();
        
        for (Shader shader : Resources.shaders) {
            if (shader.name.equals(name)) {
                mf.renderer.activeShader = shader;
                mf.glPanel.repaint();

                fragmentShaderEditor.setText(shader.fragmentSourceCode);
                vertexShaderEditor.setText(shader.vertexSourceCode);
                return;
            }
        }

        throw new RuntimeException("Shader not found");
    }

    public void updateShaders() {
        shaderComboBox.removeAllItems();

        for (Shader shader : Resources.shaders) {
            shaderComboBox.addItem(shader.name);
        }
    }
}

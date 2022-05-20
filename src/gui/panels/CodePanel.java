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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class CodePanel extends JPanel {
    public MainForm mainForm;

    JPanel mainPanel;
    GroupLayout mainLayout;
    public JComboBox<String> shaderComboBox;

    public RSyntaxTextArea fragmentShaderEditor, vertexShaderEditor;
    public JCheckBox liveMode;

    public CodePanel(MainForm mainForm) {
        this.mainForm = mainForm;

        mainLayout = new GroupLayout(this);
        setLayout(mainLayout);
        
        liveMode = new JCheckBox("Live Mode");
        JButton compileButton = new JButton("Compile shaders");
        compileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainForm.renderer.activeShader.updateFragmentShaderSourceCode(fragmentShaderEditor.getText());
                mainForm.renderer.activeShader.updateVertexShaderSourceCode(vertexShaderEditor.getText());
                mainForm.renderer.reloadShaders();
                mainForm.glPanel.repaint();
            }
        });

        fragmentShaderEditor = new RSyntaxTextArea();
        fragmentShaderEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        fragmentShaderEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (liveMode.isSelected()) {
                    mainForm.renderer.activeShader.updateFragmentShaderSourceCode(fragmentShaderEditor.getText());
                    mainForm.renderer.reloadShaders();
                    mainForm.glPanel.repaint();
                }
            }
        });

        vertexShaderEditor = new RSyntaxTextArea();
        vertexShaderEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        vertexShaderEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (liveMode.isSelected()) {
                    mainForm.renderer.activeShader.updateVertexShaderSourceCode(vertexShaderEditor.getText());
                    mainForm.renderer.reloadShaders();
                    mainForm.glPanel.repaint();
                }
            }
        });
        
        RTextScrollPane fragmentShaderScrollPane = new RTextScrollPane(fragmentShaderEditor);
        RTextScrollPane vertexShaderScrollPane = new RTextScrollPane(vertexShaderEditor);
        
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Fragment Shader", fragmentShaderScrollPane);
        tabPane.addTab("Vertex Shader", vertexShaderScrollPane);
        
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
            .addGroup(mainLayout.createSequentialGroup()
                .addComponent(liveMode)
                .addGap(10)
                .addComponent(compileButton)
                .addGap(10)
                .addComponent(shaderComboBox)));

        mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
            .addComponent(tabPane)
            .addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(liveMode)
                .addComponent(compileButton)
                .addComponent(shaderComboBox)));
    }

    private void changeShader(java.awt.event.ActionEvent evt) {
        String name = shaderComboBox.getSelectedItem().toString();
        
        for (Shader shader : Resources.shaders) {
            if (shader.name.equals(name)) {
                mainForm.renderer.activeShader = shader;
                mainForm.glPanel.repaint();
                
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

package gui;

import gui.panels.CodePanel;
import gui.panels.CreateEntitiesPanel;
import gui.panels.MovePanel;
import math.M;
import math.Quaternion;
import math.Transform;
import math.Vector3f;
import renderer.Renderer;
import scene.Entity;
import scene.MeshEntity;
import scene.Resources;
import scene.Scene;
import utils.CustomColor;
import utils.Parse;
import utils.StringFormats;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

public final class MainForm extends javax.swing.JFrame {
    public final ArrayList<Entity> selectedEntities = new ArrayList<>();

    boolean cameraFixed, glInitialized;

    public final Renderer renderer;
    public final Scene scene;

    public GLJPanel glPanel;

    public final CreateEntitiesPanel createEntitiesPanel;
    public final MovePanel movePanel;
    public final CodePanel codePanel;

    private int lastButton, lastX, lastY;

    private JPanel containerPanel;

    private JLabel labelXCoord, labelYCoord, labelZCoord;
    private JTextField inputXCoord, inputYCoord, inputZCoord;

    private JLabel labelXRotation, labelYRotation, labelZRotation;
    private JTextField inputXRotation, inputYRotation, inputZRotation;

    private JLabel labelXSize, labelYSize, labelZSize;
    private JTextField inputXSize, inputYSize, inputZSize;

    private JPanel rightPanel;
    private JPanel wPanel;
    private JTabbedPane mainTabPanel;

    public enum FieldsToChange {
        xField, yField, zField, rxField, ryField, rzField, sxField, syField, szField
    };

    public MainForm() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LiveGL");

        cameraFixed = true;
        glInitialized = false;

        scene = new Scene();
        renderer = new Renderer(scene, this);
        initOpenGL();

        createEntitiesPanel = new CreateEntitiesPanel(this);
        movePanel = new MovePanel(this);
        codePanel = new CodePanel(this);

        initComponents();
        initSimulation();
        mainTabPanel.addTab("Create entities", createEntitiesPanel);
        mainTabPanel.addTab("Move entities", movePanel);
    }

    private void initOpenGL() {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        System.out.println("Initialized opengl");

        glPanel = new GLJPanel(glcapabilities);
        if (glInitialized) {
            System.out.println("COUNT TTTTTTTTTTTTTTTTTTT A" + glPanel.getGLEventListenerCount());
            return;
        }

        glInitialized = true;
        glPanel.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable glad) {
                System.out.println("OpenGL evemt init\n\n\n");
                renderer.init(glad.getGL().getGL2(), glad.getSurfaceWidth(), glad.getSurfaceHeight());
                renderer.resources.updateAvailableShaders(glad.getGL().getGL2());
                renderer.activeShader = Resources.shaders.get(0);
                codePanel.updateShaders();
            }

            @Override
            public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
                renderer.reshape(glautodrawable.getGL().getGL2(), width, height);
            }

            @Override
            public void dispose(GLAutoDrawable glautodrawable) {
                renderer.dispose(glautodrawable.getGL().getGL2());
            }

            @Override
            public void display(GLAutoDrawable glautodrawable) {
                renderer.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(),
                        glautodrawable.getSurfaceHeight());
            }
        });
    }

    private void initComponents() {
        GroupLayout panelLayout = new GroupLayout(glPanel);
        glPanel.setLayout(panelLayout);

        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE));

        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE));

        mainTabPanel = new JTabbedPane();

        rightPanel = new JPanel();
        wPanel = new JPanel();

        labelXCoord = new JLabel("X");
        labelYCoord = new JLabel("Y");
        labelZCoord = new JLabel("Z");

        inputXCoord = new JTextField("0");
        inputYCoord = new JTextField("0");
        inputZCoord = new JTextField("0");

        labelXRotation = new JLabel("RX");
        labelYRotation = new JLabel("RY");
        labelZRotation = new JLabel("RZ");

        inputXRotation = new JTextField("0");
        inputYRotation = new JTextField("0");
        inputZRotation = new JTextField("0");

        labelXSize = new JLabel("SX:");
        labelYSize = new JLabel("SY:");
        labelZSize = new JLabel("SZ:");

        inputXSize = new JTextField("0");
        inputYSize = new JTextField("0");
        inputZSize = new JTextField("0");

        containerPanel = new JPanel();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenu menuCamera = new JMenu("Camera");
        menuBar.add(menuCamera);

        JMenuItem newShadersMenu = new JMenuItem("New shaders");
        newShadersMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                createShaders();
            }
        });
        menuFile.add(newShadersMenu);

        JMenuItem camViewMenu = new JMenuItem("Edit view");
        camViewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camViewMenuActionPerformed(evt);
            }
        });
        menuCamera.add(camViewMenu);

        JMenuItem resetCamMenu = new JMenuItem("Reset");
        resetCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCamMenuActionPerformed(evt);
            }
        });
        menuCamera.add(resetCamMenu);

        JMenuItem unlockCameraMenu = new JMenuItem("Lock / unlock cam");
        unlockCameraMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlockCameraMenuActionPerformed(evt);
            }
        });
        menuCamera.add(unlockCameraMenu);

        JMenuItem cameraMovementMenu = new JMenuItem("Automatic movement");
        cameraMovementMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraMovMenuActionPerformed(evt);
            }
        });
        menuCamera.add(cameraMovementMenu);
        

        inputXRotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rxTextActionPerformed(evt);
            }
        });

        inputYRotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ryTextActionPerformed(evt);
            }
        });

        inputZRotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rzTextActionPerformed(evt);
            }
        });

        inputXSize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sxTextFocusGained(evt);
            }
        });
        inputXSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sxTextActionPerformed(evt);
            }
        });

        inputYSize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                syTextFocusGained(evt);
            }
        });
        inputYSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syTextActionPerformed(evt);
            }
        });

        inputZSize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                szTextFocusGained(evt);
            }
        });
        inputZSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szTextActionPerformed(evt);
            }
        });

        inputXCoord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xTextActionPerformed(evt);
            }
        });

        inputYCoord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yTextActionPerformed(evt);
            }
        });

        inputZCoord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zTextActionPerformed(evt);
            }
        });

        GroupLayout wLayout = new GroupLayout(wPanel);
        wPanel.setLayout(wLayout);

        wLayout.setHorizontalGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(wLayout.createSequentialGroup().addContainerGap()
                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelZSize)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputZSize, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))

                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelXSize)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputXSize))

                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelZCoord)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputZCoord))

                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelYCoord)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputYCoord))

                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelXCoord)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputXCoord)))

                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)

                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(wLayout.createSequentialGroup()
                    .addComponent(labelYSize)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputYSize, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                    .addGroup(wLayout.createSequentialGroup()
                        .addComponent(labelZRotation)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(inputZRotation))
                    .addGroup(wLayout.createSequentialGroup()
                        .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(labelXRotation)
                            .addComponent(labelYRotation))

                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(inputXRotation)
                            .addComponent(inputYRotation))))
                    .addContainerGap()));

        wLayout.setVerticalGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(wLayout.createSequentialGroup().addContainerGap()
                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(wLayout
                    .createSequentialGroup()
                    .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(labelXCoord)
                            .addComponent(inputXCoord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(inputXRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    
                    .addPreferredGap(ComponentPlacement.RELATED)

                    .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(labelYCoord)
                        .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(inputYCoord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelYRotation)
                            .addComponent(inputYRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                    .addComponent(labelXRotation))

                .addPreferredGap(ComponentPlacement.RELATED)

                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelZCoord)
                    .addComponent(inputZCoord, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelZRotation)
                    .addComponent(inputZRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))

                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)

                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelXSize)
                    .addComponent(inputXSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelYSize)
                    .addComponent(inputYSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                
                .addPreferredGap(ComponentPlacement.RELATED)

                .addGroup(wLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelZSize)
                    .addComponent(inputZSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap()));

        GroupLayout rightLayout = new GroupLayout(rightPanel);
        rightPanel.setLayout(rightLayout);

        rightLayout.setHorizontalGroup(rightLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(codePanel)
            .addComponent(mainTabPanel, GroupLayout.Alignment.TRAILING)
            .addComponent(wPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        rightLayout.setVerticalGroup(rightLayout.createSequentialGroup()
            .addComponent(codePanel)
            .addComponent(wPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(mainTabPanel, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE));

        containerPanel.setLayout(new java.awt.BorderLayout());

        glPanel.setBackground(new java.awt.Color(0, 0, 0));
        glPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelMouseDragged(evt);
            }
        });

        glPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelMousePressed(evt);
            }
        });

        containerPanel.add(glPanel, java.awt.BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, containerPanel, rightPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(900);
        // Set the split pane as the central widget
        getContentPane().add(splitPane);

        pack();
    }

    protected void createShaders() {
        // Get the name of the shader file using a message box
        String shaderFileName = JOptionPane.showInputDialog(this, "Enter the name of the shader file", "Shader File Name", JOptionPane.QUESTION_MESSAGE);
        if (shaderFileName == null) {
            return;
        }

        // Create the vertex shader file
        try {
            File vertexShaderFile = new File("src/shaders/" + shaderFileName + ".vsh");
            if (vertexShaderFile.exists()) {
                int result = JOptionPane.showConfirmDialog(this, "The file " + shaderFileName + ".vsh already exists. Do you want to overwrite it?", "Overwrite File?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            vertexShaderFile.createNewFile();

            // Write a basic vertex shader source code in the file
            FileWriter fw = new FileWriter(vertexShaderFile);
            fw.write("attribute vec4 vertexPosition;\n\nuniform mat4 modelViewProjectionMatrix;\n\nvoid main()\n{\n    gl_Position = modelViewProjectionMatrix * vertexPosition;\n}");
            fw.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating shader file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create the fragment shader file
        try {
            File fragmentShaderFile = new File("src/shaders/" + shaderFileName + ".fsh");
            if (fragmentShaderFile.exists()) {
                int result = JOptionPane.showConfirmDialog(this, "The file " + shaderFileName + ".fsh already exists. Do you want to overwrite it?", "Overwrite File?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            fragmentShaderFile.createNewFile();

            // Write a basic fragment shader code in the file
            FileWriter fw = new FileWriter(fragmentShaderFile);
            fw.write("uniform vec4 colorMod;\n\nvoid main()\n{\n    gl_FragColor = colorMod;\n}");
            fw.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating shader file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Trigger init method of glPanel
        initOpenGL();
        codePanel.shaderComboBox.addItem(shaderFileName);
    }

    public void initSimulation() {
        // remove old transducers
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_TRANSDUCER);
        // remove old Control Points
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_CONTROL_POINT);
        // remove old Masks
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_OBJ);
        // remove old slices
        Scene.removeWithTag(scene.getEntities(), Entity.TAG_SLICE);

        // just add a basic cube and sphere
        MeshEntity me = new MeshEntity(Resources.MESH_BOX);
        me.getTransform().getScale().set(0.1f);
        me.setTag(Entity.TAG_OBJ);
        me.setColor(CustomColor.RED);
        scene.getEntities().add(me);

        MeshEntity me2 = new MeshEntity(Resources.MESH_SPHERE);
        me2.getTransform().getScale().set(0.12f);
        me2.getTransform().getTranslation().set(0.2f, 0, 0);
        me2.setTag(Entity.TAG_OBJ);
        me2.setColor(CustomColor.BLUE);
        scene.getEntities().add(me2);

        adjustGUIGainAndCameras();

        needUpdate();
    }

    public void adjustGUIGainAndCameras() {
        scene.updateSimulationBoundaries();

        // Todo: why this has to be done twice ???
        scene.adjustCameraToSimulation(scene, getGLAspect());
        scene.adjustCameraToSimulation(scene, getGLAspect());
    }

    private void panelMousePressed(java.awt.event.MouseEvent evt) {
        lastButton = evt.getButton();
        lastX = evt.getX();
        lastY = evt.getY();

        if (lastButton == 3) {
            // Right button clicked
            if (cameraFixed) {
                scene.getCamera().activateObservation(true, scene.getCamera().getObservationPoint());
            }

        } else if (lastButton == 1) {
            // Left button clicked
            updateSelection(evt);
        }
    }

    private void panelMouseDragged(java.awt.event.MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        final float rotGain = 0.01f;
        float diffX = (x - lastX);
        float diffY = (y - lastY);

        if (lastButton == 1) {
            updateSelection(evt);

        } else if (lastButton == 3) {
            if (cameraFixed) {
                scene.getCamera().moveAzimuthAndInclination(-diffX * rotGain, -diffY * rotGain);
                scene.getCamera().updateObservation();
            } else {
                scene.getCamera().getTransform().rotateLocal(-diffY * rotGain, -diffX * rotGain, 0);
            }
        }

        needUpdate();
        lastX = x;
        lastY = y;
    }

    public float getGLAspect() {
        return glPanel.getWidth() / (float)glPanel.getHeight();
    }

    public void clearSelection() {
        for (Entity e : selectedEntities) {
            e.selected = false;
        }
        selectedEntities.clear();
    }

    private void resetCamMenuActionPerformed(java.awt.event.ActionEvent evt) {
        scene.adjustCameraToSimulation(scene, getGLAspect());
        needUpdate();
    }

    public void updateTransForField(FieldsToChange field, String text) {
        if (text.length() < 1) {
            return;
        }
        boolean absolute;
        float value;
        if (text.charAt(0) == 'a') {
            absolute = false;
            value = Parse.toFloat(text.substring(1));
        } else {
            absolute = true;
            value = Parse.toFloat(text);
        }
        changeSelectionField(field, value, absolute, false);
        needUpdate();
    }

    private void xTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.xField, inputXCoord.getText());
    }

    private void rxTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.rxField, inputXRotation.getText());
    }

    private void yTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.yField, inputYCoord.getText());
    }

    private void ryTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.ryField, inputYRotation.getText());
    }

    private void zTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.zField, inputZCoord.getText());
    }

    private void rzTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.rzField, inputZRotation.getText());
    }

    private void sxTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.sxField, inputXSize.getText());
    }

    private void syTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.syField, inputYSize.getText());
    }

    private void szTextActionPerformed(java.awt.event.ActionEvent evt) {
        updateTransForField(FieldsToChange.szField, inputZCoord.getText());
    }

    private void sxTextFocusGained(java.awt.event.FocusEvent evt) {
        // changeSlider(FieldsToChange.sxField, "SX", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    private void syTextFocusGained(java.awt.event.FocusEvent evt) {
        // changeSlider(FieldsToChange.syField, "SY", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    private void szTextFocusGained(java.awt.event.FocusEvent evt) {
        // changeSlider(FieldsToChange.szField, "SZ", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    public void addMeshEntityToSceneCenterAndResizeIt(MeshEntity me) {
        me.getTransform().getTranslation().set(scene.getSimulationCenter());
        me.getTransform().getScale().set(scene.maxDistanceBoundary());
        scene.getEntities().add(me);
    }

    private void camViewMenuActionPerformed(java.awt.event.ActionEvent evt) {
        showNewFrame(new TransformForm(scene.getCamera().getTransform(), this));
    }

    private void unlockCameraMenuActionPerformed(java.awt.event.ActionEvent evt) {
        cameraFixed = !cameraFixed;
    }

    private void cameraMovMenuActionPerformed(java.awt.event.ActionEvent evt) {
        showNewFrame(new CameraMoveFrame(this));
    }

    private void showNewFrame(final JFrame frame) {
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    public void needUpdate() {
        glPanel.repaint();
    }

    public ArrayList<Entity> selectWithDrag(final int sx, final int sy, final int ex, final int ey, final int tags) {
        final float panelWidth = glPanel.getWidth();
        final float panelHeight = glPanel.getHeight();

        return scene.pickObjectsWithDrag(sx / panelWidth, 1.0f - sy / panelHeight, ex / panelWidth,
                1.0f - ey / panelHeight, tags);
    }

    public MeshEntity clickRaySelectEntity(final int x, final int y, final int tags) {
        return scene.pickObject(x / (float)glPanel.getWidth(), 1.0f - y / (float)glPanel.getHeight(), tags);
    }

    public Vector3f clickRayIntersectObject(final MeshEntity e, final int x, final int y) {
        return scene.clickToObject(x / (float)glPanel.getWidth(), 1.0f - y / (float)glPanel.getHeight(), e);
    }

    private int addTagsForSelectionFilter(int tags) {
        final Component comp = mainTabPanel.getSelectedComponent();

        if (comp == createEntitiesPanel) {
            tags |= Entity.TAG_OBJ;
        } else if (comp == movePanel) {
            tags |= Entity.TAG_CONTROL_POINT;
        }

        return tags;
    }

    private void updateSelection(MouseEvent evt) {
        int tags = Entity.TAG_NONE;

        tags = addTagsForSelectionFilter(tags);

        Entity e = scene.pickObject(lastX / (float)glPanel.getWidth(), 1.0f - lastY / (float)glPanel.getHeight(), tags);

        clearSelection();

        if (e != null) {
            e.selected = true;
            selectedEntities.add(e);
            entityToGUI(e);
        }

        needUpdate();
    }

    private void vectorToTextFields(final Vector3f v, JTextField x, JTextField y, JTextField z) {
        x.setText(StringFormats.get().dc4(v.x));
        y.setText(StringFormats.get().dc4(v.y));
        z.setText(StringFormats.get().dc4(v.z));
    }

    public void transformToGUI(final Transform t) {
        vectorToTextFields(t.getTranslation(), inputXCoord, inputYCoord, inputZCoord);
        final Vector3f angles = t.getRotation().toAngles(null).multLocal(M.RAD_TO_DEG);
        vectorToTextFields(angles, inputXRotation, inputYRotation, inputZRotation);
        vectorToTextFields(t.getScale(), inputXSize, inputYSize, inputZCoord);
    }

    private void entityToGUI(Entity e) {
        transformToGUI(e.getTransform());

    }

    private void changeSelectionField(FieldsToChange field, float value, boolean absolute, boolean updateTextField) {
        final Vector3f angles = new Vector3f();

        for (Entity e : selectedEntities) {
            Transform tra = e.getTransform();

            if (field == FieldsToChange.xField) {
                tra.getTranslation().x = absolute ? value : tra.getTranslation().x + value;
                if (updateTextField) {
                    inputXCoord.setText(StringFormats.get().dc4(tra.getTranslation().x));
                }
            } else if (field == FieldsToChange.yField) {
                tra.getTranslation().y = absolute ? value : tra.getTranslation().y + value;
                if (updateTextField) {
                    inputYCoord.setText(StringFormats.get().dc4(tra.getTranslation().y));
                }
            } else if (field == FieldsToChange.zField) {
                tra.getTranslation().z = absolute ? value : tra.getTranslation().z + value;
                if (updateTextField) {
                    inputZCoord.setText(StringFormats.get().dc4(tra.getTranslation().z));
                }
            } else if (field == FieldsToChange.sxField) {
                tra.getScale().x = absolute ? value : tra.getScale().x + value;
                if (updateTextField) {
                    inputXSize.setText(StringFormats.get().dc4(tra.getScale().x));
                }
            } else if (field == FieldsToChange.syField) {
                tra.getScale().y = absolute ? value : tra.getScale().y + value;
                if (updateTextField) {
                    inputYSize.setText(StringFormats.get().dc4(tra.getScale().y));
                }
            } else if (field == FieldsToChange.szField) {
                tra.getScale().z = absolute ? value : tra.getScale().z + value;
                if (updateTextField) {
                    inputZSize.setText(StringFormats.get().dc4(tra.getScale().z));
                }
            } else if (field == FieldsToChange.rxField || field == FieldsToChange.ryField
                    || field == FieldsToChange.rzField) {

                float rads = value * M.DEG_TO_RAD;
                Quaternion q = tra.getRotation();
                q.toAngles(angles);

                if (field == FieldsToChange.rxField) {
                    angles.x = absolute ? rads : angles.x + rads;
                    if (updateTextField) {
                        inputXRotation.setText(StringFormats.get().dc4(angles.x * M.RAD_TO_DEG));
                    }
                } else if (field == FieldsToChange.ryField) {
                    angles.y = absolute ? rads : angles.y + rads;
                    if (updateTextField) {
                        inputYRotation.setText(StringFormats.get().dc4(angles.y * M.RAD_TO_DEG));
                    }
                } else if (field == FieldsToChange.rzField) {
                    angles.z = absolute ? rads : angles.z + rads;
                    if (updateTextField) {
                        inputZRotation.setText(StringFormats.get().dc4(angles.z * M.RAD_TO_DEG));
                    }
                }
                q.fromAngles(angles);
            }

            updateTextField = false;
        }
    }
}

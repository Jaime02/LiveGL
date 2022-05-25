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
import utils.TCPClient;
import utils.TCPServer;

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
import javax.swing.GroupLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.FocusAdapter;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;


public final class MainForm extends JFrame {
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

    private JTextField inputXCoord, inputYCoord, inputZCoord;
    private JTextField inputXRotation, inputYRotation, inputZRotation;
    private JTextField inputXSize, inputYSize, inputZSize;

    private JPanel rightPanel;
    private JPanel widgetsPanel;
    private JTabbedPane mainTabPanel;

    public NetworkConfigWindow networkConfigWindow;
    public TCPServer server;
    public TCPClient client;

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

        networkConfigWindow = new NetworkConfigWindow(this);
    }

    private void initOpenGL() {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);

        glPanel = new GLJPanel(glcapabilities);
        if (glInitialized) {
            return;
        }

        glInitialized = true;
        glPanel.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable glad) {
                renderer.init(glad.getGL().getGL2(), glad.getSurfaceWidth(), glad.getSurfaceHeight());
                renderer.resources.updateAvailableShaders(glad.getGL().getGL2());
                renderer.activeShader = Resources.shaders.get(0);
                codePanel.updateAvailableShaders();
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
        mainTabPanel = new JTabbedPane();
        widgetsPanel = new JPanel();

        JLabel labelXCoord = new JLabel("X");
        JLabel labelYCoord = new JLabel("Y");
        JLabel labelZCoord = new JLabel("Z");

        inputXCoord = new JTextField("0");
        inputYCoord = new JTextField("0");
        inputZCoord = new JTextField("0");

        JLabel labelXRotation = new JLabel("RX");
        JLabel labelYRotation = new JLabel("RY");
        JLabel labelZRotation = new JLabel("RZ");

        inputXRotation = new JTextField("0");
        inputYRotation = new JTextField("0");
        inputZRotation = new JTextField("0");

        JLabel labelXSize = new JLabel("SX:");
        JLabel labelYSize = new JLabel("SY:");
        JLabel labelZSize = new JLabel("SZ:");

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
        newShadersMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createShaders();
            }
        });
        menuFile.add(newShadersMenu);

        JMenuItem camViewMenu = new JMenuItem("Edit view");
        camViewMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                camViewMenuActionPerformed(evt);
            }
        });
        menuCamera.add(camViewMenu);

        JMenuItem resetCamMenu = new JMenuItem("Reset");
        resetCamMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetCamMenuActionPerformed(evt);
            }
        });
        menuCamera.add(resetCamMenu);

        JMenuItem unlockCameraMenu = new JMenuItem("Lock / unlock cam");
        unlockCameraMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                unlockCameraMenuActionPerformed(evt);
            }
        });
        menuCamera.add(unlockCameraMenu);

        JMenuItem cameraMovementMenu = new JMenuItem("Automatic movement");
        cameraMovementMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cameraMovMenuActionPerformed(evt);
            }
        });
        menuCamera.add(cameraMovementMenu);
        

        inputXRotation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rxTextActionPerformed(evt);
            }
        });

        inputYRotation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ryTextActionPerformed(evt);
            }
        });

        inputZRotation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rzTextActionPerformed(evt);
            }
        });

        inputXSize.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                sxTextFocusGained(evt);
            }
        });
        inputXSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sxTextActionPerformed(evt);
            }
        });

        inputYSize.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                syTextFocusGained(evt);
            }
        });
        inputYSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                syTextActionPerformed(evt);
            }
        });

        inputZSize.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                szTextFocusGained(evt);
            }
        });
        inputZSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                szTextActionPerformed(evt);
            }
        });

        inputXCoord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                xTextActionPerformed(evt);
            }
        });

        inputYCoord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                yTextActionPerformed(evt);
            }
        });

        inputZCoord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                zTextActionPerformed(evt);
            }
        });

        GroupLayout widgetsLayout = new GroupLayout(widgetsPanel);
        // Add margins to widgetsLayout
        widgetsLayout.setAutoCreateContainerGaps(true);
        widgetsLayout.setAutoCreateGaps(true);

        widgetsPanel.setLayout(widgetsLayout);

        widgetsLayout.setHorizontalGroup(widgetsLayout.createSequentialGroup()
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(labelXCoord)
                .addComponent(labelYCoord)
                .addComponent(labelZCoord))
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(inputXCoord)
                .addComponent(inputYCoord)
                .addComponent(inputZCoord))
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(labelXRotation)
                .addComponent(labelYRotation)
                .addComponent(labelZRotation))
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(inputXRotation)
                .addComponent(inputYRotation)
                .addComponent(inputZRotation))
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(labelXSize)
                .addComponent(labelYSize)
                .addComponent(labelZSize))
            .addGroup(widgetsLayout.createParallelGroup()
                .addComponent(inputXSize)
                .addComponent(inputYSize)
                .addComponent(inputZSize))
        );

        widgetsLayout.setVerticalGroup(widgetsLayout.createSequentialGroup()
            .addGroup(widgetsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelXCoord)
                .addComponent(inputXCoord)
                .addComponent(labelXRotation)
                .addComponent(inputXRotation)
                .addComponent(labelXSize)
                .addComponent(inputXSize))
            .addGroup(widgetsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelYCoord)
                .addComponent(inputYCoord)
                .addComponent(labelYRotation)
                .addComponent(inputYRotation)
                .addComponent(labelYSize)
                .addComponent(inputYSize))
            .addGroup(widgetsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(labelZCoord)
                .addComponent(inputZCoord)
                .addComponent(labelZRotation)
                .addComponent(inputZRotation)
                .addComponent(labelZSize)
                .addComponent(inputZSize))
        );
        
        rightPanel = new JPanel();

        JSplitPane codeSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, rightPanel);
        codeSplitter.setOneTouchExpandable(true);
        codeSplitter.setDividerLocation(400);

        GroupLayout rightLayout = new GroupLayout(rightPanel);
        rightPanel.setLayout(rightLayout);

        rightLayout.setHorizontalGroup(rightLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(widgetsPanel)
            .addComponent(mainTabPanel));

        rightLayout.setVerticalGroup(rightLayout.createSequentialGroup()
            .addComponent(widgetsPanel)
            .addComponent(mainTabPanel));

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
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, containerPanel, codeSplitter);
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

    private void resetCamMenuActionPerformed(ActionEvent evt) {
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

    private void xTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.xField, inputXCoord.getText());
    }

    private void rxTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.rxField, inputXRotation.getText());
    }

    private void yTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.yField, inputYCoord.getText());
    }

    private void ryTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.ryField, inputYRotation.getText());
    }

    private void zTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.zField, inputZCoord.getText());
    }

    private void rzTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.rzField, inputZRotation.getText());
    }

    private void sxTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.sxField, inputXSize.getText());
    }

    private void syTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.syField, inputYSize.getText());
    }

    private void szTextActionPerformed(ActionEvent evt) {
        updateTransForField(FieldsToChange.szField, inputZCoord.getText());
    }

    private void sxTextFocusGained(FocusEvent evt) {
        // changeSlider(FieldsToChange.sxField, "SX", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    private void syTextFocusGained(FocusEvent evt) {
        // changeSlider(FieldsToChange.syField, "SY", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    private void szTextFocusGained(FocusEvent evt) {
        // changeSlider(FieldsToChange.szField, "SZ", scene.maxDistanceBoundary() /
        // 8.0f);
    }

    public void addMeshEntityToSceneCenterAndResizeIt(MeshEntity me) {
        me.getTransform().getTranslation().set(scene.getSimulationCenter());
        me.getTransform().getScale().set(scene.maxDistanceBoundary());
        scene.getEntities().add(me);
    }

    private void camViewMenuActionPerformed(ActionEvent evt) {
        JFrame frame = new TransformForm(scene.getCamera().getTransform(), this);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void unlockCameraMenuActionPerformed(ActionEvent evt) {
        cameraFixed = !cameraFixed;
    }

    private void cameraMovMenuActionPerformed(ActionEvent evt) {
        JFrame frame = new CameraMoveFrame(this);
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

    public void setUpServer(int port) {
        server = new TCPServer(this, port);
        server.start();
    }

    public void setUpClient(int port, String host) {
        client = new TCPClient(this, port, host);
    }
}

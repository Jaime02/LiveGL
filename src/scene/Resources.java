package scene;

import renderer.Shader;
import shapes.Box;
import shapes.Cylinder;
import shapes.Mesh;
import shapes.Quad;
import shapes.Sphere;
import shapes.Torus;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import com.jogamp.opengl.GL2;


public class Resources {
    public static final int SHADER_SLICE_PRE = 5;
    public static final int SHADER_MASK = 9;

    public static final String MESH_CUSTOM = "custom";
    public static final String MESH_QUAD = "quad";
    public static final String MESH_QUADDS = "quadDSide";
    public static final String MESH_BOX = "box";
    public static final String MESH_SPHERE = "sphere";
    public static final String MESH_DONUT = "donut";
    public static final String MESH_CYLINDER = "cylinder";
    public static final String MESH_TRANSDUCER = "transducer";
    public static final String MESH_GRID = "grid";
    public static final int MESH_GRID_DIVS = 128;

    public static LinkedList<Shader> shaders;
    private static HashMap<String, Mesh> meshes;

    private GL2 gl;

    public Resources(GL2 gl) {
        this.gl = gl;

        shaders = new LinkedList<>();
        meshes = new HashMap<>();

        // Load shaders
        updateAvailableShaders();

        // Load meshes
        meshes.put(MESH_BOX, new Box(0.5f, 0.5f, 0.5f));
        meshes.put(MESH_SPHERE, new Sphere(16, 32, 0.5f));
        meshes.put(MESH_DONUT, new Torus(10, 10, 0.2f, 0.5f));
        meshes.put(MESH_CYLINDER, new Cylinder(4, 16, 0.5f, 1, true, false));
        meshes.put(MESH_TRANSDUCER, new Cylinder(4, 16, 0.3f, 0.5f, 1, true, false, -0.5f));
        meshes.put(MESH_QUAD, new Quad(1, 1, 1, false));
        meshes.put(MESH_GRID, new Quad(1, 1, MESH_GRID_DIVS, false));
        meshes.put(MESH_QUADDS, new Quad(1, 1, 1, true));
    }

    public Shader getShader(int s) {
        return shaders.get(s);
    }

    public static Mesh getMesh(String m) {
        return meshes.get(m);
    }

    public void updateAvailableShaders() {
        shaders.clear();

        // Each shader must have a fragment and vertex shader file
        for (String file : new File("src/shaders").list()) {
            if (file.endsWith(".fsh")) {
                String fragmentShader = file;

                if (Files.exists(Paths.get("src/shaders/" + file.substring(0, file.length() - 4) + ".vsh"))) {
                    
                    String vertexShader = file.substring(0, file.length() - 4) + ".vsh";

                    Shader shader = new Shader(gl, this, vertexShader, fragmentShader, Shader.ORDER_OPAQUE);
                    shader.fragmentSourceCode = readFile("src/shaders/" + fragmentShader);
                    shader.vertexSourceCode = readFile("src/shaders/" + vertexShader);
                    
                    shader.init();

                    shaders.add(shader);
                }
            }
        };
    }

    private String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            return "Exception reading the shader source code";
        }
    }

    public void releaseResources(GL2 gl) {
        // Delete shaders
        for (Shader shader : shaders) {
            shader.unloadShader();
        }

        // Delete textures (No textures used)
    }
}

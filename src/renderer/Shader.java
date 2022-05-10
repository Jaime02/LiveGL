package renderer;

import math.Matrix4f;
import math.Vector3f;
import scene.MeshEntity;
import scene.Resources;
import scene.Scene;
import shapes.Mesh;
import utils.BufferUtils;
import utils.Color;
import utils.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;

public class Shader {
    private final String vertexShaderFileName, fragmentShaderFileName;
    public final String name;
    private GL2 gl;

    public String fragmentSourceCode, vertexSourceCode;
    
    int shaderProgramID;
    int fragmentShader, vertexShader;

    int vertexHandle;
    int normalHandle;
    int textureCoordHandle;

    int lightPosHandle;
    int eyePosHandle;

    int mvpMatrixHandle;
    int mvMatrixHandle;
    int mMatrixHandle;

    int colorHandle;
    int texDiffuse;
    int ambient, diffuse, specular, shininess;

    private int renderingOrder;
    public static int ORDER_BACKGROUND = 0;
    public static int ORDER_MASK = 1;
    public static int ORDER_OPAQUE = 3;
    public static int ORDER_TRANSLUCENT = 4;
    public static int ORDER_GUI = 100;

    public Shader(GL2 gl, Resources resources, String vProgram, String fProgram, int renderingOrder) {
        this.gl = gl;
        this.vertexShaderFileName = vProgram;
        this.fragmentShaderFileName = fProgram;

        this.name = vProgram.substring(0, vProgram.indexOf('.'));
        this.renderingOrder = renderingOrder;
    }

    public Shader init() {
        shaderProgramID = createProgramFromBuffer(vertexShaderFileName, fragmentShaderFileName);
        getUniforms(gl);

        return this;
    }

    public int getRenderingOrder(MeshEntity me) {
        if (Color.alpha(me.getColor()) < 255) {
            return ORDER_TRANSLUCENT;
        }
        return renderingOrder;
    }

    public void reload() {
        unloadShader();
        init();
    }

    void bindUniforms(Scene scene, Renderer renderer, Scene s, MeshEntity me,
            Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model,
            FloatBuffer fb) {

        fb.rewind();
        gl.glUniformMatrix4fv(mvpMatrixHandle, 1, false, projectionViewModel.fillFloatBuffer(fb, true));
        gl.glUniformMatrix4fv(mvMatrixHandle, 1, false, viewModel.fillFloatBuffer(fb, true));
        gl.glUniformMatrix4fv(mMatrixHandle, 1, false, model.fillFloatBuffer(fb, true));

        Vector3f lightPos = scene.getLight().getTransform().getTranslation();
        Vector3f eyePos = scene.getCamera().getTransform().getTranslation();

        gl.glUniform4f(lightPosHandle, lightPos.x, lightPos.y, lightPos.z, 1);
        gl.glUniform4f(eyePosHandle, eyePos.x, eyePos.y, eyePos.z, 1);

        float r = Color.red(me.getColor()) / 255.0f;
        float g = Color.green(me.getColor()) / 255.0f;
        float b = Color.blue(me.getColor()) / 255.0f;
        float a = Color.alpha(me.getColor()) / 255.0f;
        gl.glUniform4f(colorHandle, r, g, b, a);

        gl.glUniform1f(ambient, me.getMaterial().getAmbient());
        gl.glUniform1f(diffuse, me.getMaterial().getDiffuse());
        gl.glUniform1f(specular, me.getMaterial().getSpecular());
        gl.glUniform1f(shininess, me.getMaterial().getShininess());
    }

    void bindAttribs(Scene s, MeshEntity me) {
        Mesh mesh;

        if (me.getMesh().equals(Resources.MESH_CUSTOM)) {
            mesh = me.customMesh;
        } else {
            mesh = Resources.getMesh(me.getMesh());
        }

        if (mesh == null) {
            return;
        }

        gl.glVertexAttribPointer(vertexHandle, 3, GL.GL_FLOAT, false, 12, mesh.getPosition());
        gl.glVertexAttribPointer(textureCoordHandle, 2, GL2.GL_FLOAT, false, 8, mesh.getTexture());
        gl.glVertexAttribPointer(normalHandle, 3, GL2.GL_FLOAT, false, 12, mesh.getNormal());

        gl.glEnableVertexAttribArray(vertexHandle);
        gl.glEnableVertexAttribArray(normalHandle);
        gl.glEnableVertexAttribArray(textureCoordHandle);
    }

    void render(Scene s, MeshEntity me) {
        Mesh mesh;
        if (me.getMesh().equals(Resources.MESH_CUSTOM)) {
            mesh = me.customMesh;
        } else {
            mesh = Resources.getMesh(me.getMesh());
        }
        if (mesh != null) {
            gl.glDrawElements(GL2.GL_TRIANGLES, mesh.getTrianCount() * 3, GL2.GL_UNSIGNED_SHORT, mesh.getIndices());
        }
    }

    void unbindAttribs(Scene s, MeshEntity me) {
        gl.glDisableVertexAttribArray(vertexHandle);
        gl.glDisableVertexAttribArray(normalHandle);
        gl.glDisableVertexAttribArray(textureCoordHandle);
    }

    void changeGLStatus(Renderer r, Scene s, MeshEntity e) {
        r.enableBlend(gl, Color.alpha(e.getColor()) < 255);

        r.enableCullFace(gl, !e.isDoubledSided());

        r.enableDepthTest(gl, true);
        r.enableTexture2D(gl, false);
        r.enableTexture3D(gl, false);
        r.enableWriteColor(gl, renderingOrder != ORDER_MASK);
    }

    void getUniforms(GL2 gl) {
        vertexHandle = gl.glGetAttribLocation(shaderProgramID, "vertexPosition");
        normalHandle = gl.glGetAttribLocation(shaderProgramID, "vertexNormal");
        textureCoordHandle = gl.glGetAttribLocation(shaderProgramID, "vertexTexCoord");

        lightPosHandle = gl.glGetUniformLocation(shaderProgramID, "lightPos");
        eyePosHandle = gl.glGetUniformLocation(shaderProgramID, "eyePos");

        mvpMatrixHandle = gl.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        mvMatrixHandle = gl.glGetUniformLocation(shaderProgramID, "modelViewMatrix");
        mMatrixHandle = gl.glGetUniformLocation(shaderProgramID, "modelMatrix");

        colorHandle = gl.glGetUniformLocation(shaderProgramID, "colorMod");
        ambient = gl.glGetUniformLocation(shaderProgramID, "ambient");
        diffuse = gl.glGetUniformLocation(shaderProgramID, "diffuse");
        specular = gl.glGetUniformLocation(shaderProgramID, "specular");
        shininess = gl.glGetUniformLocation(shaderProgramID, "shininess");
        texDiffuse = gl.glGetUniformLocation(shaderProgramID, "texSampler2D");
    }

    public void unloadShader() {
        // deatach shader just in case that the current one is attached
        gl.glUseProgram(0);

        gl.glDeleteShader(vertexShader);
        vertexShader = 0;
        gl.glDeleteShader(fragmentShader);
        fragmentShader = 0;
        gl.glDeleteProgram(shaderProgramID);
        shaderProgramID = 0;
    }

    // Create a shader program
    int createProgramFromBuffer(String vProgram, String fProgram) {
        int program = 0;
        vertexShader = initShader(GL2.GL_VERTEX_SHADER, getSourceCode(vProgram));
        fragmentShader = initShader(GL2.GL_FRAGMENT_SHADER, getSourceCode(fProgram));

        if (vertexShader == 0 || fragmentShader == 0) {
            throw new RuntimeException("Error creating shader");
        }
        program = gl.glCreateProgram();

        if (program == 0) {
            throw new RuntimeException("Could not create program");
        }

        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);
        IntBuffer linkStatus = BufferUtils.createIntBuffer(1);
        gl.glGetProgramiv(program, GL2.GL_LINK_STATUS, linkStatus);

        if (linkStatus.get(0) != GL2.GL_TRUE) {
            IntBuffer infoLen = BufferUtils.createIntBuffer(1);
            gl.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, infoLen);
            int length = infoLen.get();
            if (length != 0) {
                ByteBuffer buf = BufferUtils.createByteBuffer(length);
                infoLen.flip();
                gl.glGetProgramInfoLog(program, length, infoLen, buf);
                byte[] b = new byte[length];
                buf.get(b);
                System.err.println("Could not link program: " + new String(b));
            }
        }

        return program;
    }

    int initShader(int nShaderType, String source) {        
        int shader = gl.glCreateShader(nShaderType);

        int error = gl.glGetError();
        if (error != 0) {
            System.out.println("Error: " + gl.glGetError());
        }

        if (shader == 0) {
            throw new RuntimeException("Error initializating shader.");
        }

        String[] sources = new String[] { source };
        gl.glShaderSource(shader, 1, sources, null);
        gl.glCompileShader(shader);
        IntBuffer compiled = BufferUtils.createIntBuffer(1);
        gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compiled);

        if (compiled.get() == 0) {
            IntBuffer infoLen = BufferUtils.createIntBuffer(1);
            gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, infoLen);
            int length = infoLen.get();
            if (length > 0) {
                ByteBuffer buf = BufferUtils.createByteBuffer(length);
                infoLen.flip();
                gl.glGetShaderInfoLog(shader, length, infoLen, buf);
                byte[] b = new byte[infoLen.get()];
                buf.get(b);
                System.out.println("Program : " + fragmentShaderFileName);
                System.out.println(source);
                System.err.println("Error compiling shader " + vertexShaderFileName + " " + fragmentShaderFileName + " -> " + new String(b));
            }
        }

        return shader;
    }

    public static String getSourceCode(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("src/shaders/" + fileName)));
        } catch (IOException ex) {
            Logger.getLogger(Shader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void updateFragmentShaderSourceCode(String sourceCode) {
        // Open the file with the shader source code
        File file = new File("src/shaders/" + fragmentShaderFileName);
        // Overwrite the file with the new source code
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(sourceCode);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Shader.class.getName()).log(Level.SEVERE, null, ex);
        }

        fragmentSourceCode = sourceCode;
        reload();
    }

    public void updateVertexShaderSourceCode(String sourceCode) {
        // Open the file with the shader source code
        File file = new File("src/shaders/" + vertexShaderFileName);
        
        // Overwrite the file with the new source code
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(sourceCode);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Shader.class.getName()).log(Level.SEVERE, null, ex);
        }
        vertexSourceCode = sourceCode;

        reload();
    }
}

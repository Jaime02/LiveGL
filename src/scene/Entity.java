package scene;

import math.Frustrum;
import math.Transform;
import math.Vector3f;
import renderer.Material;
import utils.CustomColor;


public class Entity {
    public static final int TAG_NONE = 1 << 0;
    public static final int TAG_TRANSDUCER = 1 << 1;
    public static final int TAG_CONTROL_POINT = 1 << 2;
    public static final int TAG_SLICE = 1 << 3;
    public static final int TAG_SIMULATION_BOUNDINGS = 1 << 4;
    public static final int TAG_CUBE_HELPER = 1 << 5;
    public static final int TAG_GROUND_LINE = 1 << 6;
    public static final int TAG_MASK = 1 << 9;
    public static final int TAG_OBJ = 1 << 11;

    Material material;
    int color;
    Transform transform;
    int tag;
    int number;
    public boolean selected;

    public Entity() {
        tag = TAG_NONE;
        color = CustomColor.WHITE;
        material = new Material();
        transform = new Transform();
        selected = false;
    }

    public int getColor() {
        if (!selected) {
            return color;
        } else {
            return CustomColor.GREEN;
        }
    }


    public void setAlpha(float alpha) {
        color = CustomColor.changeAlpha(color, (int)(alpha * 255));
    }

    public int getRealColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void update(Scene s) {

    }

    public void lookAt(Entity other) {
        Vector3f dir = other.getTransform().getTranslation().subtract(getTransform().getTranslation());
        dir.negateLocal();
        getTransform().getRotation().lookAt(dir, Vector3f.UNIT_Y);
    }

    public void rotateAround(final Vector3f pivot, float rx, float ry, float rz) {
        getTransform().rotateAround(pivot, rx, ry, rz);
    }

    public boolean boxInside(final Frustrum frustrum) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of
                                                                       // generated methods, choose
                                                                       // Tools | Templates.
    }

    public float distanceTo(final Entity e) {
        return getTransform().getTranslation().distance(e.getTransform().getTranslation());
    }

    public float distanceTo(final Vector3f v) {
        return getTransform().getTranslation().distance(v);
    }
}

package scene;

import math.Frustrum;
import math.Ray;
import math.Vector3f;
import shapes.Mesh;


public class MeshEntity extends Entity {
    public float distanceToCamera;
    public int renderingOrder;
    
    public String mesh;
    boolean visible = true;
    boolean doubledSided = false;
    
    public Mesh customMesh;
    public int id;

    public MeshEntity(int id, String mesh) {
        this.id = id;
        this.mesh = mesh;
    }
    
    
    public float rayToSphere(final Ray r){
        //transform the ray with inverse transform
        Ray rSpace = new Ray(r.origin, r.direction, false);
        transform.transformInversePoint(rSpace.origin, rSpace.origin);
        transform.transformInverseVector(rSpace.direction, rSpace.direction);
        
        //intersection points with the sphere
        Mesh m = Resources.getMesh(mesh);
        if(m == null) { return -1.0f; }
        Vector3f p = m.getbSphere().intersectPoint(rSpace);
        if (p == null){return -1.0f;}
        
        //apply transform to points
        transform.transformPoint(p, p);
        
        //return distance if there was a collision
        return p.distance( r.origin );
    }
    
    public float rayToBox(final Ray r){
        //transform the ray with inverse transform
        Ray rSpace = new Ray(r.origin, r.direction, false);
        transform.transformInversePoint(rSpace.origin, rSpace.origin);
        transform.transformInverseVector(rSpace.direction, rSpace.direction);
        
        //intersection points with the box
        Mesh m = Resources.getMesh(mesh);
        if(m == null) { return -1.0f; }
        Vector3f p = m.getbBox().intersectPoint(rSpace);
        if (p == null){return -1.0f;}
        
        //apply transform to points
        transform.transformPoint(p, p);
        
        //return distance if there was a collision
        return p.distance( r.origin );
    }
    
    public boolean boxInside(final Frustrum frustrum){
       /* Mesh m = Resources.get().getMesh(mesh);
        if(m == null) { return false; }
        
        Ray rSpace = new Ray(r.origin, r.direction, false);
        transform.transformInversePoint(rSpace.origin, rSpace.origin);
        transform.transformInverseVector(rSpace.direction, rSpace.direction);
        
        //intersection points with the box
        
        Vector3f p = m.getbBox().intersectPoint(rSpace);
        if (p == null){return false;}
        
        //apply transform to points
        transform.transformPoint(p, p);
        
        return true;*/
        return false;
    }

    public boolean isDoubledSided() {
        return doubledSided;
    }

    public void setDoubledSided(boolean isDoubledSided) {
        this.doubledSided = isDoubledSided;
    }

    
    public String getMesh() {
        return mesh;
    }

    public void setMesh(String mesh) {
        this.mesh = mesh;
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

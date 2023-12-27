import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameObject {
    public AffineTransform transform; //the location/scale/rotation of our object
    public Shape shape; //the collider/rendered shape of this object
    public Material material; //data about the fill color, border color, and border thickness
    public ArrayList<ScriptableBehavior> scripts = new ArrayList<>(); //all scripts attached to the object
    public boolean active = true; //whether this gets Updated() and Draw()n

    // Create the default GameObject use a default AffineTransform, default Material, and a 10x10 pix rectangle Shape at 0,0
    public GameObject()
    {
        // Default gameObject:
        this.transform = new AffineTransform(); // Default AffineTransform
        this.shape = new Rectangle2D.Float(0,0,10,10); // Use rectangle2D to set the 0,0 origin and 10x10 size
        this.material = new Material(); // Use default material values provided in material subclass
        //this.active = true;
    }

    // Create the default GameObject, but with its AffineTransform translated to the coordinate x,y
    public GameObject(int x, int y)
    {
        // Default gameObject:
        this.transform = new AffineTransform(); // Default AffineTransform
        transform.translate(x,y);
        this.shape = new Rectangle2D.Float(0,0,10,10); // Use rectangle2D to set the 0,0 origin and 10x10 size
        this.material = new Material(); // Use default material values provided in material subclass
        //this.active = true;
    }

    //TODO: 1) save the pen's old transform, 2) transform it based on this object's transform, 3) draw either the styled shape, or the image scaled to the bounds of the shape.
    public void Draw(Graphics2D pen)
    {
        // Only draw if active
        if(this.active)
        {
            // Save the previous transform
            AffineTransform prevTransform = pen.getTransform();
            pen.setTransform(this.transform); // Transform the pen to the object's transform

            // Draw the shape using the style in Material or the Image
            // The material is a shape:
            if(this.material.isShape)
            {
                // Draw the shape using the style contained in the gameObject
                pen.setColor(this.material.getFill()); // Set fill color
                pen.fill(this.shape); // Fill in
                pen.setStroke(new BasicStroke(this.material.getBorderWidth())); // Border width
                pen.setColor(this.material.getBorder()); // Border color
                pen.draw(this.shape); // Draw the completed shape
            }
            // The material is an image:
            else {
                // Draw the image at the size of the shape: Ignore how horribly convoluted this code is, I just combined all steps into one line
                pen.drawImage(this.material.getImg().getScaledInstance((int)this.shape.getBounds2D().getWidth(), (int)this.shape.getBounds2D().getHeight(), 0), this.shape.getBounds().x, this.shape.getBounds().y, null);
            }
            pen.setTransform(prevTransform); // Reset the pen
        }
    }

    // Start all scripts on the object
    public void Start()
    {
        if(this.active)
        {
            for (int i = 0; i < scripts.size(); i++) {
                // Calls the Start() in the scripts class onto the current script, which is called on an object.
                this.scripts.get(i).Start();
            }
        }
    }

    // Update all scripts on the object
    public void Update()
    {
        if(this.active)
        {
            for (int i = 0; i < scripts.size(); i++) {
                // Calls the Update() in the scripts class onto the current script, which is called on an object.
                this.scripts.get(i).Update();
            }
        }
    }

    // Move the GameObject's transform
    public void Translate(float dX, float dY)
    {
        this.transform.translate(dX,dY);
    }

    // Scale the GameObject's transform around the CENTER of its shape
    public void Scale(float sX, float sY)
    {
        // Get the coordinates
        float middleX = (float) (this.shape.getBounds().getCenterX());
        float middleY = (float) (this.shape.getBounds().getCenterY());
        this.transform.translate(middleX,middleY); // Initial Translate
        this.transform.scale(sX,sY); // Scale
        this.transform.translate(-middleX,-middleY); // Translate back
    }

    // Should return true if the two objects are touching (i.e., the intersection of their areas is not empty)
    public boolean CollidesWith(GameObject other)
    {
        Area thisArea = new Area(this.shape);
        Area otherArea = new Area(other.shape);

        thisArea.transform(this.transform);
        otherArea.transform(other.transform);

        thisArea.intersect(otherArea);

        return !thisArea.isEmpty(); // Return false if there is no collision
    }

    // Should return true of the shape on screen contains the point
    public boolean Contains(Point2D point)
    {
        Area shapeArea = new Area(this.shape);
        return shapeArea.contains(point);
    }

}

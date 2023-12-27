import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class Material {
    Color fill, border;
    int borderWidth;
    boolean isShape = true;
    BufferedImage img;

    // Create default, black fill and border with zero borderWidth
    Material()
    {
        this.fill = new Color(0, 0, 0); // Black fill
        this.border = new Color(0,0,0); // Black border
        this.borderWidth = 0; // Zero border
    }

    // Set the fields
    public Material(Color fill, Color border, int borderWidth)
    {
        // Same as default but with values
        this.setFill(fill);
        this.setBorder(border);
        this.setBorderWidth(borderWidth);
    }

    // Load the image at the path and set isShape flag to false
    public Material(String path)
    {
        // Load image:
        setImg(path);
        // Update the flag:
        this.isShape = false;
    }

    //Getters and Setters, done for you!
    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public Color getBorder() {
        return border;
    }

    public void setBorder(Color border) {
        this.border = border;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int stroke_width) {
        this.borderWidth = stroke_width;
    }

    public BufferedImage getImg(){return img;}

    // Write this part, load the image and set it
    public void setImg(String path)
    {
        // Load image:
        try {
            this.img = ImageIO.read(new File(path));
        } catch (IOException e) {
            // Catch if image is not found:
            System.out.println("Image Not Found");
        }
    }

    public void setImg(BufferedImage img){this.img=img;}
}

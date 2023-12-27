import java.util.ArrayList;

public class Input {
    static public ArrayList<Character> pressed = new ArrayList<>(); //contains characters that were just pressed this frame
    static public ArrayList<Character> held = new ArrayList<>(); //contains characters that were either just pressed this frame, or have been down for any number of previous frames
    static public ArrayList<Character> released = new ArrayList<>(); //contains characters that were just released this frame

    public static int MouseX; //the current mouse X position on the DISPLAY BufferedImage
    public static int MouseY; //the current mouse Y position on the DISPLAY BufferedImage
    public static boolean MousePressed; //true if the mouse is currently down
    public static boolean MouseClicked; //true if the mouse was clicked this frame

    // This function should happen every frame, and should clear any values in the Input class that need to be removed
    //i.e., if a key was pressed on one frame, on the next it should be removed from the pressed list
    static void UpdateInputs()
    {
        // Analyze each character list to see what should be removed:
        // Remove ever character from pressed and released
        for(int i = pressed.size() - 1; i >= 0; i--)
        {
            if(GetKeyPressed(pressed.get(i)))
                pressed.remove(i);
        }
        // Only remove held if it was released:
        for(int i = held.size() - 1; i >= 0; i--)
        {
            if(GetKeyUp(held.get(i)))
            {
                held.remove(i);
            }
        }
        // Clear the released array:
        for(int i = released.size() - 1; i >= 0; i--)
        {
            if(GetKeyUp(released.get(i)))
                released.remove(i);
        }
    }

    // Return true if c is in the released list
    static boolean GetKeyPressed(char c)
    {
        for(int i = 0; i < released.size(); i++)
        {
            if(released.get(i) == c)
                return true;
        }
        return false;
    }

    // Return true if c is in the held list
    static boolean GetKeyDown(char c)
    {
        for(int i = 0; i < held.size(); i++)
        {
            if(held.get(i) == c)
                return true;
        }
        return false;
    }

    // Return true if c is in the released list
    static boolean GetKeyUp(char c)
    {
        for(int i = 0; i < released.size(); i++)
        {
            if (released.get(i) == c)
                return true;
        }
        return false;
    }
}

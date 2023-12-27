// GatorEngine Project: Lawson Fall 2023 (References via class provided code | UF CAP3027)
// Art by me! :)

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GatorEngine {
    //UI Components (things that are "more" related to the UI)
    static JFrame WINDOW;
    static JPanel DISPLAY_CONTAINER;
    static JLabel DISPLAY_LABEL;
    static BufferedImage DISPLAY;
    static int WIDTH=500, HEIGHT=500;

    //Engine Components (things that are "more" related to the engine structures)
    static Graphics2D RENDERER; // The "pen" for the window
    static ArrayList<GameObject> OBJECTLIST = new ArrayList<>(); //list of GameObjects in the scene
    static ArrayList<GameObject> CREATELIST = new ArrayList<>(); //list of GameObjects to add to OBJECTLIST at the end of the frame
    static ArrayList<GameObject> DELETELIST = new ArrayList<>(); //list of GameObjects to remove from OBJECTLIST at the end fo the frame
    static float FRAMERATE = 60; //target frames per second;
    static float FRAMEDELAY = 1000/FRAMERATE; //target delay between frames
    static Timer FRAMETIMER; //Timer controlling the update loop
    static Thread FRAMETHREAD; //the Thread implementing the update loop
    static Thread ACTIVE_FRAMETHREAD; //a copy of FRAMETHREAD that actually runs.


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                CreateEngineWindow();
            }
        });
    }

    static void CreateEngineWindow(){
        //Sets up the GUI
        WINDOW = new JFrame("Gator Engine");
        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WINDOW.setVisible(true);

        DISPLAY = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
        RENDERER = (Graphics2D) DISPLAY.getGraphics();
        DISPLAY_CONTAINER = new JPanel();
        DISPLAY_CONTAINER.setFocusable(true);
        DISPLAY_LABEL = new JLabel(new ImageIcon(DISPLAY));
        DISPLAY_CONTAINER.add(DISPLAY_LABEL);
        WINDOW.add(DISPLAY_CONTAINER);
        WINDOW.pack();

        // Make this 1)execute Update(), 2) clear any inputs that need to be removed between frames, and 3) repaint the GUI back on the EDT.
        FRAMETHREAD = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // 1) Update
                Update();
                // 2) Clear inputs between frames using the input class: UpdateInputs();
                Input.UpdateInputs();
                // 3) Update Objects in ObjectList
                UpdateObjectList();
                // 4) Repaint WINDOW
                WINDOW.repaint();
            }
        });

        // This copies the template thread made above
        ACTIVE_FRAMETHREAD = new Thread(FRAMETHREAD);

        // Create a timer that will create/run ACTIVE_FRAMETHREAD, but only if it hasn't started/has ended
        FRAMETIMER = new Timer((int)FRAMEDELAY, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // isAlive() returns true if it's still running! Only create if it returns false (hasn't started/ended)
                if(!ACTIVE_FRAMETHREAD.isAlive())
                {
                    ACTIVE_FRAMETHREAD = new Thread(FRAMETHREAD);
                    ACTIVE_FRAMETHREAD.start();
                }
            }
        });
        FRAMETIMER.start();

        Start();

        // ===================INPUT=========================
        // Set up some action listeners for input on the PANEL
        // These should update the Input classes ArrayLists and other members
        // Use the correct listener functions to modify INPUT
        DISPLAY_CONTAINER.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                Input.pressed.add(e.getKeyChar());
                Input.held.add(e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                Input.released.add(e.getKeyChar()); // Key is released!
            }
        });
        DISPLAY_CONTAINER.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(!Input.MousePressed)
                    Input.MouseClicked = true;;
            }
            @Override
            public void mousePressed(MouseEvent e)
            {
                Input.MousePressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                Input.MouseClicked = false;
                Input.MousePressed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
            }

            @Override
            public void mouseExited(MouseEvent e)
            {

            }
        });
        DISPLAY_CONTAINER.addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {

            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                Input.MouseX = e.getX();
                Input.MouseY = e.getY();
            }
        });
    }

    // Add the GameObject provided to the CREATELIST
    static void Create(GameObject g)
    {
        CREATELIST.add(g);
    }

    // Add the GameObject provided to the DELETELIST
    static void Delete(GameObject g)
    {
        DELETELIST.add(g);
    }

    // 1) remove objects in DELETELIST from OBJECTLIST, 2) add objects in CREATELIST to OBJECTLIST, 3) remove all items from DELETELIST and CREATELIST
    static void UpdateObjectList()
    {
        // Add CREATELIST to Object:
        for(int i = CREATELIST.size() - 1; i >= 0; i--)
        {
            OBJECTLIST.add(CREATELIST.get(i));
            // Clear CREATELIST
            CREATELIST.remove(CREATELIST.get(i));
        }
        // Remove DELETELIST from Object:
        for(int i = DELETELIST.size() - 1; i >= 0; i--)
        {
            OBJECTLIST.remove(DELETELIST.get(i));
            // Clear DELETELIST
            DELETELIST.remove(DELETELIST.get(i));
        }
    }

    //This begins the "user-side" of the software; above should set up the engine loop, data, etc.
    //Here you can create GameObjects, assign scripts, set parameters, etc.
    static void Start()
    {
        Catpocalypse.Start(); // Run GatorInvaders
        // Start() all objects in OBJECTLIST
        for(int i = 0; i < OBJECTLIST.size(); i++)
        {
            // Start() in the Object class calls all scripts on the object:
            OBJECTLIST.get(i).Start();
        }
    }

    // Redraw the Background(), then Draw() and Update() all GameObjects in OBJECTLIST
    static void Update()
    {
        // Redraw background
        Background();

        // Draw and update
        for(int i = 0; i < OBJECTLIST.size(); i++)
        {
            OBJECTLIST.get(i).Draw(RENDERER);
            OBJECTLIST.get(i).Update();
        }
    }

    // Draws a background on the Renderer. Set to the Game Background right now!
    static void Background()
    {
        BufferedImage background;
        // Get/Set the background image:
        try
        {
            background = ImageIO.read(new File("resources/Background.png"));
            RENDERER.drawImage(background, null, 0,0);
        } catch (IOException e)
        {
            // Catch if image is not found:
            System.out.println("Image Not Found");
        }
    }
}

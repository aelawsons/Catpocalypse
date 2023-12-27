import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

// Project 5: GatorInvaders Game:
// Movement based off paddle movement scripts provided previously!
public class Catpocalypse
{
    static GameObject background; // Will change depending on start/death
    static GameObject startButton, restartButton; // Start button and return to start button
    static GameObject ship; // User ship
    static GameObject health1, health2, health3; // Player health

    static GameObject inbetween;

    static ArrayList<Enemy> enemyList = new ArrayList<>(); // List of all enemies
    static Enemy enemy1, enemy2, enemy3, enemy4, enemy5; // Back row of enemy ships:
    static Enemy enemy6, enemy7, enemy8, enemy9, enemy10; // Middle row
    static Enemy enemy11, enemy12, enemy13, enemy14, enemy15; // Front row

    static int level = 1; // Goes up to 3

    static int lifeCount; // Starts at 3, once hits 0, then mark as dead!
    static boolean dead = false; // Start at false, and update to true when collide with bullet
    static boolean shipMade = false;
    static boolean invincible = false;

    static void Start()
    {
        // If startButton is clicked: Run the game:
        Runnable startGame = new Runnable()
        {
            @Override
            public void run()
            {
                // Remove title background and start button! AND anything else:
                GatorEngine.OBJECTLIST.clear();
                // Call the actual game function:
                PlayGame();
            }
        };

        if(!dead && !shipMade) // ONLY recall if ship hasn't been made (level progression) AND not dead!
        {
            // Run title menu until button pressed: and change button color when mouse pass over.
            background = new GameObject(0,0);
            background.shape = new Rectangle2D.Float(0,0, 500,500);
            background.material = new Material("resources/Title.png");
            GatorEngine.OBJECTLIST.add(background); // Spawn in the title screen! (Is finicky if "Create-d" so add manually)

            // Make a button (clear and overlay onto start button on image)
            startButton = new GameObject();
            startButton.shape = new Rectangle2D.Float(200,314,100,50);
            startButton.material = new Material(new Color(0,0,0, 0),Color.BLACK,5);

            startButton.scripts.add(new Button(startButton, Color.GREEN, Color.BLACK, startGame));
            GatorEngine.Create(startButton);
        }
    }

    static public void Dead()
    {
        // buttonPressed: startGame (this is used when the exit screen is called: restarts the game to start screen!)
        Runnable buttonPressed = new Runnable()
        {
            @Override
            public void run()
            {
                Start();
            }
        };

        // Clear everything: (I use this just in case)
        GatorEngine.OBJECTLIST.clear();

        // Clear enemyList!!
        if(!enemyList.isEmpty())
            for(int i = 0; i < enemyList.size(); i++)
                enemyList.remove(i);

        shipMade = false; // Reset to false
        dead = false;
        level = 1; // Reset level!

        background.material = new Material("resources/DeathScreen.png");

        restartButton = new GameObject();
        restartButton.shape = new Rectangle2D.Float(145, 384, 224, 50);
        restartButton.material = new Material(new Color(0, 0, 0, 0), Color.BLACK, 5);
        restartButton.scripts.add(new Button(restartButton, Color.GREEN, Color.BLACK, buttonPressed));

        GatorEngine.OBJECTLIST.add(background); // Re-add the updated background
        GatorEngine.Create(restartButton); // Recreate the startButton
    }

    static public void PlayGame()
    {
        // Create the user ship ONLY if it hasnt been made before:
        if(!shipMade)
        {
            ship = new GameObject();
            ship.shape = new Rectangle2D.Float(230,400, 30, 30);
            ship.material = new Material("resources/Ship.png");
            ship.scripts.add(new MoveShip(ship, 5)); // Set base speed to 5
            ship.scripts.add(new ShipHit(ship, false));
            GatorEngine.Create(ship);
            shipMade = true;
        }

        HealthStart();

        // Level 1: Spawn in 3 rows of Level 1 Enemies
        if(level == 1)
        {
            lifeCount = 3; // Initialize at start
            LevelSpawnOne();
        }
        // Level 2: Spawn in 1 Row Level 1, 2 Rows Level 2
        if(level == 2)
        {
            lifeCount = 3; // Initialize at start
            LevelSpawnTwo();
        }
        // Level 3: Spawn in 1 row Level 1, 1 row Level 2, 1 row Level 3
        if(level == 3)
        {
            lifeCount = 3; // Initialize at start
            LevelSpawnThree();
        }
        if(level == 4) // WINNER! WOOHOO!
        {
            // Delete all objects and go back to start screen:
            GatorEngine.OBJECTLIST.clear();
            // Clear health (gets rid of health glitch on restart screen)
            GatorEngine.Delete(health1);
            GatorEngine.Delete(health2);
            GatorEngine.Delete(health3);
            // Restart shipMade, level and dead:
            shipMade = false;
            dead = false;
            level = 1;
            Start();
        }
    }

    // Enemy: Separate GameObject that also holds a level and hit count! (Also decides look of enemy)
    static public class Enemy extends GameObject
    {
        int level;
        int hits; // Once hits = level, enemy delete!

        Enemy(int level)
        {
            this.level = level;
            this.hits = 0;
            UpdateImage(level);
        }

        public void UpdateImage(int level)
        {
            // Depending on level, the enemy looks different
            if(level == 1)
                this.material = new Material("resources/Enemy1.png");
            if(level == 2)
                this.material = new Material("resources/Enemy2.png");
            if(level == 3)
                this.material = new Material("resources/Enemy3.png");
        }
    }

    // Handles when the row of enemies reaches player y coordinate! (400)
    static public class EnemyBounds extends ScriptableBehavior
    {
        int difference;
        EnemyBounds(GameObject g, int difference)
        {
            super(g);
            this.difference = difference;
        }
        @Override
        public void Start() {
        }
        @Override
        public void Update()
        {
            // If game object reaches bottom OR if hits player (moved 300): Dead! and call Start()
            if(gameObject.transform.getTranslateY() >= difference)
                Dead();
        }
    }

    // FIX: Enemy Movement class handles the enemies movements: calls enemy bullet!
    static public class EnemyMovement extends ScriptableBehavior
    {
        int delay;
        int velocity = 50;
        int movement = 0; // 0 = right, 1 = down, and 2 = left, 3 = down
        int level;

        EnemyMovement(GameObject g, int level)
        {
            super(g);
            this.level = level;
        }
        @Override
        public void Start() {
            delay = 0;
        }
        @Override
        public void Update()
        {
            if(delay > 20)
            {
                // Right
                if(movement == 0)
                {
                    gameObject.Translate(velocity, 0);
                    movement++;
                    delay = 0;
                    EnemyShoot(gameObject, level); // Chance to shoot a bullet!
                }
            }
            if(delay > 40)
            {
                // Down
                if(movement == 1)
                {
                    gameObject.Translate(0, velocity);
                    movement++;
                    delay = 0;
                    EnemyShoot(gameObject, level); // Chance to shoot a bullet!
                }
            }
            if(delay > 60)
            {
                // Left
                if(movement == 2)
                {
                    gameObject.Translate(-velocity, 0);
                    movement++;
                    delay = 0;
                    EnemyShoot(gameObject, level); // Chance to shoot a bullet!
                }
            }
            if(delay > 70)
            {
                // Down
                if(movement == 3)
                {
                    gameObject.Translate(0, velocity);
                    movement = 0;
                    delay = 0;
                    EnemyShoot(gameObject, level); // Chance to shoot a bullet!
                }
            }
            if(movement == 4)
            {
                movement = 0;
                delay = 0;
            }
            delay++;
        }
    }

    // Enemy shoots here: Decided by level: decided by random integer from 0 -> 10
    static public void EnemyShoot(GameObject gameObject, int level)
    {
        Random rand = new Random();
        int random;
        boolean user = false; // false = these are enemy bullets
        GameObject bullet = new GameObject();
        // Start it at the enemies spot (middle):
        bullet.shape = new Ellipse2D.Float((int)gameObject.shape.getBounds().getX() + (int)gameObject.transform.getTranslateX() + 15, (int)gameObject.shape.getBounds().getY() + (int)gameObject.transform.getTranslateY(), 6,6);
        bullet.material = new Material(Color.RED, Color.RED, 0);
        bullet.scripts.add(new Bullet(bullet, 10, user)); // Assign a bullet script that moves it

        // Different levels have different shooting power!
        random = rand.nextInt(60); // random number from 0 -> 59 (60)
        if(level == 1)
            // 8% chance of shooting
            if(random >= 0 && random <= 5)
                GatorEngine.Create(bullet);
        if(level == 2)
            // 16%  chance of shooting
            if(random >= 0 && random <= 10)
                GatorEngine.Create(bullet);
        if(level == 3)
            // 33% chance of shootng
            if(random >= 0 && random <= 20)
                GatorEngine.Create(bullet);
    }

    // Set up the images (this allows for life loss to be a separate function)
    static public void HealthStart()
    {
        // Set up initial health (3 lives)
        health1 = new GameObject();
        health1.shape = new Rectangle2D.Float(25, 450, 25, 25);
        health1.material = new Material("resources/Ship.png");

        // Copy for health 2 and health 3, but fix position
        health2 = new GameObject();;
        health2.shape = new Rectangle2D.Float(55, 450, 25, 25);
        health2.material = new Material("resources/Ship.png");

        health3 = new GameObject();;
        health3.shape = new Rectangle2D.Float(85, 450, 25, 25);
        health3.material = new Material("resources/Ship.png");

        GatorEngine.Create(health1);
        GatorEngine.Create(health2);
        GatorEngine.Create(health3);
    }

    // Sets up the ship life count in the corner!
    static public void Health()
    {
        if(lifeCount == 2)
        {
            GatorEngine.Delete(health3);
        }
        if(lifeCount == 1)
        {
            GatorEngine.Delete(health2);
        }
        if(lifeCount == 0)
        {
            GatorEngine.Delete(health1);
            Dead();
        }
    }

    // REF: Button class taken/edited via provided Tests.java: Calls hover + runnable!
    static public class Button extends ScriptableBehavior
    {
        Color c1, c2;
        Runnable r;

        Button(GameObject g, Color nohover, Color hover, Runnable r)
        {
            super(g);
            c1 = nohover;
            c2 = hover;
            this.r = r;
        }
        @Override
        public void Start() {

        }
        @Override
        public void Update()
        {
            if(gameObject.Contains(new Point2D.Float(Input.MouseX,Input.MouseY)))
            {
                gameObject.material.setBorder(c1);
                if(Input.MousePressed && r!=null)
                {
                    r.run();
                }
            }else
            {
                gameObject.material.setBorder(c2);
            }
        }
    }

    // REF: Based on Pong.java provided code!
    static public class MoveShip extends ScriptableBehavior
    {
        int speed = 1;
        int delay; // Allows for delay between shots
        boolean user = true;

        // Or change speed:
        MoveShip(GameObject g, int speed)
        {
            super(g);
            this.speed = speed;
        }
        @Override
        public void Start() {
            gameObject.Translate(340,400);
            delay = 0;
        }
        @Override
        public void Update() {
            // Moving the ship left and right:
            if(Input.GetKeyDown('d'))
                gameObject.Translate(speed,0);
            if(Input.GetKeyDown('a'))
                gameObject.Translate(-speed,0);
            // Shooting enemies
            if(Input.GetKeyDown(' '))
            {
                // Create a new game object (bullet)
                GameObject bullet = new GameObject();
                // Start bullet out at 240 + transform (moves) and 400
                bullet.shape = new Ellipse2D.Float(240 + (int)ship.transform.getTranslateX(), 400, 6,6);
                bullet.material = new Material(Color.YELLOW, Color.GREEN, 0);
                // Assign a bullet script that moves it
                bullet.scripts.add(new Bullet(bullet, 10, user));
                // GatorEngine.Create(bullet): ONLY creat if delay > 3
                if(delay > 9 && !dead) // !dead removes issue of doubling delay in levels!
                {
                    GatorEngine.Create(bullet);
                    delay = 0;
                }
                delay++;
            }
        }
    }

    // REF: Delay based off post provided in Slack by TA :]!
    static public class Bullet extends ScriptableBehavior
    {
        int velocity;
        int delay;
        boolean user; // True if bullet shoots from user, false if from enemy!

        // g = bullet, and enemyHit = the enemy colliding with bullet
        Bullet(GameObject g, int v, boolean user)
        {
            super(g);
            this.velocity = v;
            this.user = user; // Is the bullet from enemy or user
        }
        @Override
        public void Start() {
            delay = 0;
        }
        @Override
        public void Update()
        {
            if(user)
            {
                gameObject.Translate(0, -velocity);
                // Check if bullet hits enemy:
                for(int i = 0; i < enemyList.size(); i++)
                {
                    // BULLETS DELETE AFTER IMPACT!!!
                    if(gameObject.CollidesWith(enemyList.get(i)))
                    {
                        // Up enemy hits
                        enemyList.get(i).level--;
                        // Update enemyImage:
                        enemyList.get(i).UpdateImage(enemyList.get(i).level);

                        // Once hits = level, enemy delete!
                        if(enemyList.get(i).level <= 0)
                        {
                            GatorEngine.Delete(enemyList.get(i));
                            enemyList.remove(i);
                            while (enemyList.isEmpty())
                            {
                                GatorEngine.OBJECTLIST.clear();
                                level++;
                                if(level == 4)
                                {
                                    PlayGame(); // Call the spawn for the specific level! DELAY HERE!//
                                    break;
                                }
                                shipMade = false;
                                PlayGame(); // Call the spawn for the specific level! DELAY HERE!//
                            }
                        }
                        // Get rid of the bullet:
                        GatorEngine.Delete(gameObject);
                    }
                }
            }
            else // Otherwise: These are bullets from an enemy!
            {
                gameObject.Translate(0, velocity);
                // Check if bullet hits enemy:
                if(gameObject.CollidesWith(ship))
                {
                    // Flicker ship and invincible: ONLY ADD HIT SCRIPT IF YOU CAN REMOVE THE PREVIOUS!
                    if(ship.scripts.size() > 1)
                    {
                        ship.scripts.remove(1);
                        ship.scripts.add(new ShipHit(ship, true));
                    }
                    // Up hits on user ship ONLY if not invincible:
                    if(!invincible)
                        lifeCount--;
                    Health();
                    GatorEngine.Delete(gameObject); // Delete the bullet once it hits user ship!
                }

                // After a delay, remove that script:
                if(delay > 45)
                {
                    // Empty the recent script and update to false hit (makes flickering stop!)
                    if(ship.scripts.size() > 1)
                    {
                        ship.scripts.remove(1);
                        ship.scripts.add(new ShipHit(ship, false));
                    }
                    invincible = false;
                    delay = 0;
                }
                delay++;
            }
        }
    }

    static public class ShipHit extends ScriptableBehavior
    {
        // Flicker the ship image and don't allow hits for another few seconds (set lives to 100 and then back): USE DELAY
        int delay;
        int timer; // Keeps track of how long to flicker/invincible
        boolean hit;

        ShipHit(GameObject g, boolean hit)
        {
            super(g);
            delay = 0;
            timer = 0;
            this.hit = hit;
        }

        @Override
        public void Start() {}
        @Override
        public void Update() {
            ship.material = new Material("resources/Ship.png");
            if(hit)
            {
                invincible = true;
                if (delay > 5) // FLICKER
                {
                    ship.material = new Material("resources/ShipHit.png");
                    delay = 0;
                }
                delay++;
            }
            else
            {
                ship.material = new Material("resources/Ship.png");
            }
        }
    }

    // The following code is going to be unnecessarily tedious I know :( but this helps level generation!
    // Level 1: All rows are level 1 enemies
    static public void LevelSpawnOne()
    {
        // Back row:
        enemy1 = new Enemy(1);
        enemy1.shape = new Rectangle2D.Float(170,100,30,30);
        enemy1.scripts.add(new EnemyMovement(enemy1, 1));
        enemy1.scripts.add(new EnemyBounds(enemy1, 300));
        enemyList.add(enemy1);
        GatorEngine.Create(enemy1);

        enemy2 = new Enemy(1);
        enemy2.shape = new Rectangle2D.Float(200,100,30,30);
        enemy2.scripts.add(new EnemyMovement(enemy2, 1));
        enemy2.scripts.add(new EnemyBounds(enemy2, 300));
        enemyList.add(enemy2);
        GatorEngine.Create(enemy2);

        enemy3 = new Enemy(1);
        enemy3.shape = new Rectangle2D.Float(230,100,30,30);
        enemy3.scripts.add(new EnemyMovement(enemy3, 1));
        enemy3.scripts.add(new EnemyBounds(enemy3, 300));
        enemyList.add(enemy3);
        GatorEngine.Create(enemy3);

        enemy4 = new Enemy(1);
        enemy4.shape = new Rectangle2D.Float(260,100,30,30);
        enemy4.scripts.add(new EnemyMovement(enemy4, 1));
        enemy4.scripts.add(new EnemyBounds(enemy4, 300));
        enemyList.add(enemy4);
        GatorEngine.Create(enemy4);

        enemy5 = new Enemy(1);
        enemy5.shape = new Rectangle2D.Float(290,100,30,30);
        enemy5.scripts.add(new EnemyMovement(enemy5, 1));
        enemy5.scripts.add(new EnemyBounds(enemy5, 300));
        enemyList.add(enemy5);
        GatorEngine.Create(enemy5);

        // Middle row:
        enemy6 = new Enemy(1);
        enemy6.shape = new Rectangle2D.Float(170,130,30,30);
        enemy6.scripts.add(new EnemyMovement(enemy6, 1));
        enemy6.scripts.add(new EnemyBounds(enemy6, 270));
        enemyList.add(enemy6);
        GatorEngine.Create(enemy6);

        enemy7 = new Enemy(1);
        enemy7.shape = new Rectangle2D.Float(200,130,30,30);
        enemy7.scripts.add(new EnemyMovement(enemy7, 1));
        enemy7.scripts.add(new EnemyBounds(enemy7, 270));
        enemyList.add(enemy7);
        GatorEngine.Create(enemy7);

        enemy8 = new Enemy(1);
        enemy8.shape = new Rectangle2D.Float(230,130,30,30);
        enemy8.scripts.add(new EnemyMovement(enemy8, 1));
        enemy8.scripts.add(new EnemyBounds(enemy8, 270));
        enemyList.add(enemy8);
        GatorEngine.Create(enemy8);

        enemy9 = new Enemy(1);
        enemy9.shape = new Rectangle2D.Float(260,130,30,30);
        enemy9.scripts.add(new EnemyMovement(enemy9, 1));
        enemy9.scripts.add(new EnemyBounds(enemy9, 270));
        enemyList.add(enemy9);
        GatorEngine.Create(enemy9);

        enemy10 = new Enemy(1);
        enemy10.shape = new Rectangle2D.Float(290,130,30,30);
        enemy10.scripts.add(new EnemyMovement(enemy10, 1));
        enemy10.scripts.add(new EnemyBounds(enemy10, 270));
        enemyList.add(enemy10);
        GatorEngine.Create(enemy10);

        // Front:
        enemy11 = new Enemy(1);
        enemy11.shape = new Rectangle2D.Float(170,160,30,30);
        enemy11.scripts.add(new EnemyMovement(enemy11, 1));
        enemy11.scripts.add(new EnemyBounds(enemy11, 240));
        enemyList.add(enemy11);
        GatorEngine.Create(enemy11);

        enemy12 = new Enemy(1);
        enemy12.shape = new Rectangle2D.Float(200,160,30,30);
        enemy12.scripts.add(new EnemyMovement(enemy12, 1));
        enemy12.scripts.add(new EnemyBounds(enemy12, 240));
        enemyList.add(enemy12);
        GatorEngine.Create(enemy12);

        enemy13 = new Enemy(1);
        enemy13.shape = new Rectangle2D.Float(230,160,30,30);
        enemy13.scripts.add(new EnemyMovement(enemy13, 1));
        enemy13.scripts.add(new EnemyBounds(enemy13, 240));
        enemyList.add(enemy13);
        GatorEngine.Create(enemy13);

        enemy14 = new Enemy(1);
        enemy14.shape = new Rectangle2D.Float(260,160,30,30);
        enemy14.scripts.add(new EnemyMovement(enemy14, 1));
        enemy14.scripts.add(new EnemyBounds(enemy14, 240));
        enemyList.add(enemy14);
        GatorEngine.Create(enemy14);

        enemy15 = new Enemy(1);
        enemy15.shape = new Rectangle2D.Float(290,160,30,30);
        enemy15.scripts.add(new EnemyMovement(enemy15, 1));
        enemy15.scripts.add(new EnemyBounds(enemy15, 240));
        enemyList.add(enemy15);
        GatorEngine.Create(enemy15);
    }

    // Same but level 2: level 1 enemies and level 2 enemies
    static public void LevelSpawnTwo()
    {
        // Back row:
        enemy1 = new Enemy(2);
        enemy1.shape = new Rectangle2D.Float(170,100,30,30);
        enemy1.scripts.add(new EnemyMovement(enemy1, 2));
        enemy1.scripts.add(new EnemyBounds(enemy1, 300));
        enemyList.add(enemy1);
        GatorEngine.Create(enemy1);

        enemy2 = new Enemy(2);
        enemy2.shape = new Rectangle2D.Float(200,100,30,30);
        enemy2.scripts.add(new EnemyMovement(enemy2, 2));
        enemy2.scripts.add(new EnemyBounds(enemy2, 300));
        enemyList.add(enemy2);
        GatorEngine.Create(enemy2);

        enemy3 = new Enemy(2);
        enemy3.shape = new Rectangle2D.Float(230,100,30,30);
        enemy3.scripts.add(new EnemyMovement(enemy3, 2));
        enemy3.scripts.add(new EnemyBounds(enemy3, 300));
        enemyList.add(enemy3);
        GatorEngine.Create(enemy3);

        enemy4 = new Enemy(2);
        enemy4.shape = new Rectangle2D.Float(260,100,30,30);
        enemy4.scripts.add(new EnemyMovement(enemy4, 2));
        enemy4.scripts.add(new EnemyBounds(enemy4, 300));
        enemyList.add(enemy4);
        GatorEngine.Create(enemy4);

        enemy5 = new Enemy(2);
        enemy5.shape = new Rectangle2D.Float(290,100,30,30);
        enemy5.scripts.add(new EnemyMovement(enemy5, 2));
        enemy5.scripts.add(new EnemyBounds(enemy5, 300));
        enemyList.add(enemy5);
        GatorEngine.Create(enemy5);

        // Middle row:
        enemy6 = new Enemy(1);
        enemy6.shape = new Rectangle2D.Float(170,130,30,30);
        enemy6.scripts.add(new EnemyMovement(enemy6, 1));
        enemy6.scripts.add(new EnemyBounds(enemy6, 270));
        enemyList.add(enemy6);
        GatorEngine.Create(enemy6);

        enemy7 = new Enemy(2);
        enemy7.shape = new Rectangle2D.Float(200,130,30,30);
        enemy7.scripts.add(new EnemyMovement(enemy7, 2));
        enemy7.scripts.add(new EnemyBounds(enemy7, 270));
        enemyList.add(enemy7);
        GatorEngine.Create(enemy7);

        enemy8 = new Enemy(2);
        enemy8.shape = new Rectangle2D.Float(230,130,30,30);
        enemy8.scripts.add(new EnemyMovement(enemy8, 2));
        enemy8.scripts.add(new EnemyBounds(enemy8, 270));
        enemyList.add(enemy8);
        GatorEngine.Create(enemy8);

        enemy9 = new Enemy(2);
        enemy9.shape = new Rectangle2D.Float(260,130,30,30);
        enemy9.scripts.add(new EnemyMovement(enemy9, 2));
        enemy9.scripts.add(new EnemyBounds(enemy9, 270));
        enemyList.add(enemy9);
        GatorEngine.Create(enemy9);

        enemy10 = new Enemy(1);
        enemy10.shape = new Rectangle2D.Float(290,130,30,30);
        enemy10.scripts.add(new EnemyMovement(enemy10, 1));
        enemy10.scripts.add(new EnemyBounds(enemy10, 270));
        enemyList.add(enemy10);
        GatorEngine.Create(enemy10);

        // Front:
        enemy11 = new Enemy(1);
        enemy11.shape = new Rectangle2D.Float(170,160,30,30);
        enemy11.scripts.add(new EnemyMovement(enemy11, 1));
        enemy11.scripts.add(new EnemyBounds(enemy11, 240));
        enemyList.add(enemy11);
        GatorEngine.Create(enemy11);

        enemy12 = new Enemy(1);
        enemy12.shape = new Rectangle2D.Float(200,160,30,30);
        enemy12.scripts.add(new EnemyMovement(enemy12, 1));
        enemy12.scripts.add(new EnemyBounds(enemy12, 240));
        enemyList.add(enemy12);
        GatorEngine.Create(enemy12);

        enemy13 = new Enemy(1);
        enemy13.shape = new Rectangle2D.Float(230,160,30,30);
        enemy13.scripts.add(new EnemyMovement(enemy13, 1));
        enemy13.scripts.add(new EnemyBounds(enemy13, 240));
        enemyList.add(enemy13);
        GatorEngine.Create(enemy13);

        enemy14 = new Enemy(1);
        enemy14.shape = new Rectangle2D.Float(260,160,30,30);
        enemy14.scripts.add(new EnemyMovement(enemy14, 1));
        enemy14.scripts.add(new EnemyBounds(enemy14, 240));
        enemyList.add(enemy14);
        GatorEngine.Create(enemy14);

        enemy15 = new Enemy(1);
        enemy15.shape = new Rectangle2D.Float(290,160,30,30);
        enemy15.scripts.add(new EnemyMovement(enemy15, 1));
        enemy15.scripts.add(new EnemyBounds(enemy15, 240));
        enemyList.add(enemy15);
        GatorEngine.Create(enemy15);
    }

    // Same but level 3: level 1 enemies + level 2 enemies + level 3 enemies
    static public void LevelSpawnThree()
    {
        // Back row:
        enemy1 = new Enemy(3);
        enemy1.shape = new Rectangle2D.Float(170,100,30,30);
        enemy1.scripts.add(new EnemyMovement(enemy1, 2));
        enemy1.scripts.add(new EnemyBounds(enemy1, 300));
        enemyList.add(enemy1);
        GatorEngine.Create(enemy1);

        enemy2 = new Enemy(3);
        enemy2.shape = new Rectangle2D.Float(200,100,30,30);
        enemy2.scripts.add(new EnemyMovement(enemy2, 2));
        enemy2.scripts.add(new EnemyBounds(enemy2, 300));
        enemyList.add(enemy2);
        GatorEngine.Create(enemy2);

        enemy3 = new Enemy(3);
        enemy3.shape = new Rectangle2D.Float(230,100,30,30);
        enemy3.scripts.add(new EnemyMovement(enemy3, 2));
        enemy3.scripts.add(new EnemyBounds(enemy3, 300));
        enemyList.add(enemy3);
        GatorEngine.Create(enemy3);

        enemy4 = new Enemy(3);
        enemy4.shape = new Rectangle2D.Float(260,100,30,30);
        enemy4.scripts.add(new EnemyMovement(enemy4, 2));
        enemy4.scripts.add(new EnemyBounds(enemy4, 300));
        enemyList.add(enemy4);
        GatorEngine.Create(enemy4);

        enemy5 = new Enemy(3);
        enemy5.shape = new Rectangle2D.Float(290,100,30,30);
        enemy5.scripts.add(new EnemyMovement(enemy5, 2));
        enemy5.scripts.add(new EnemyBounds(enemy5, 300));
        enemyList.add(enemy5);
        GatorEngine.Create(enemy5);

        // Middle row:
        enemy6 = new Enemy(2);
        enemy6.shape = new Rectangle2D.Float(170,130,30,30);
        enemy6.scripts.add(new EnemyMovement(enemy6, 1));
        enemy6.scripts.add(new EnemyBounds(enemy6, 270));
        enemyList.add(enemy6);
        GatorEngine.Create(enemy6);

        enemy7 = new Enemy(2);
        enemy7.shape = new Rectangle2D.Float(200,130,30,30);
        enemy7.scripts.add(new EnemyMovement(enemy7, 2));
        enemy7.scripts.add(new EnemyBounds(enemy7, 270));
        enemyList.add(enemy7);
        GatorEngine.Create(enemy7);

        enemy8 = new Enemy(2);
        enemy8.shape = new Rectangle2D.Float(230,130,30,30);
        enemy8.scripts.add(new EnemyMovement(enemy8, 2));
        enemy8.scripts.add(new EnemyBounds(enemy8, 270));
        enemyList.add(enemy8);
        GatorEngine.Create(enemy8);

        enemy9 = new Enemy(2);
        enemy9.shape = new Rectangle2D.Float(260,130,30,30);
        enemy9.scripts.add(new EnemyMovement(enemy9, 2));
        enemy9.scripts.add(new EnemyBounds(enemy9, 270));
        enemyList.add(enemy9);
        GatorEngine.Create(enemy9);

        enemy10 = new Enemy(2);
        enemy10.shape = new Rectangle2D.Float(290,130,30,30);
        enemy10.scripts.add(new EnemyMovement(enemy10, 1));
        enemy10.scripts.add(new EnemyBounds(enemy10, 270));
        enemyList.add(enemy10);
        GatorEngine.Create(enemy10);

        // Front:
        enemy11 = new Enemy(1);
        enemy11.shape = new Rectangle2D.Float(170,160,30,30);
        enemy11.scripts.add(new EnemyMovement(enemy11, 1));
        enemy11.scripts.add(new EnemyBounds(enemy11, 240));
        enemyList.add(enemy11);
        GatorEngine.Create(enemy11);

        enemy12 = new Enemy(1);
        enemy12.shape = new Rectangle2D.Float(200,160,30,30);
        enemy12.scripts.add(new EnemyMovement(enemy12, 1));
        enemy12.scripts.add(new EnemyBounds(enemy12, 240));
        enemyList.add(enemy12);
        GatorEngine.Create(enemy12);

        enemy13 = new Enemy(1);
        enemy13.shape = new Rectangle2D.Float(230,160,30,30);
        enemy13.scripts.add(new EnemyMovement(enemy13, 1));
        enemy13.scripts.add(new EnemyBounds(enemy13, 240));
        enemyList.add(enemy13);
        GatorEngine.Create(enemy13);

        enemy14 = new Enemy(1);
        enemy14.shape = new Rectangle2D.Float(260,160,30,30);
        enemy14.scripts.add(new EnemyMovement(enemy14, 1));
        enemy14.scripts.add(new EnemyBounds(enemy14, 240));
        enemyList.add(enemy14);
        GatorEngine.Create(enemy14);

        enemy15 = new Enemy(1);
        enemy15.shape = new Rectangle2D.Float(290,160,30,30);
        enemy15.scripts.add(new EnemyMovement(enemy15, 1));
        enemy15.scripts.add(new EnemyBounds(enemy15, 240));
        enemyList.add(enemy15);
        GatorEngine.Create(enemy15);
    }
}

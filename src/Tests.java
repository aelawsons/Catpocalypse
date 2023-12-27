// To use, put
// TestControllerUpdate() in your Update() function if you have input done.
// or
// Call one of the test functions in Start()

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class Tests {

    static void TestControllerUpdate(){
        if(Input.GetKeyDown('1')){
            ResetEngine();
            TestOne();
        }

        if(Input.GetKeyDown('2')){
            ResetEngine();
            TestTwo();
        }

        if(Input.GetKeyDown('3')){
            ResetEngine();
            TestThree();
        }

        if(Input.GetKeyDown('4')){
            ResetEngine();
            TestFour();
        }

        if(Input.GetKeyDown('5')){
            ResetEngine();
            TestFive();
        }

        if(Input.GetKeyDown('6')){
            ResetEngine();
            TestSix();
        }

        if(Input.GetKeyDown('7')){
            ResetEngine();
            TestSeven();
        }

        if(Input.GetKeyDown('8')){
            ResetEngine();
            TestEight();
        }

        if(Input.GetKeyDown('9')){
            ResetEngine();
            TestNine();
        }

        if(Input.GetKeyDown('0')){
            ResetEngine();
            TestTen();
        }

        if(Input.GetKeyDown('r')){
            ResetEngine();
            TestEleven();
        }
        if(Input.GetKeyDown('t')){
            ResetEngine();
            TestTwelve();
        }
    }

    static void ResetEngine(){
        GatorEngine.OBJECTLIST.clear();
    }


    //Test: Default GameObject Constructor. Position GameObject Constructor.
    static void TestOne(){
        //Example: creating a default GameObject
        GameObject g = new GameObject();
        GatorEngine.OBJECTLIST.add(g);

        //Example: creating a default GameObject
        GameObject g2 = new GameObject(50,50);
        GatorEngine.OBJECTLIST.add(g2);
    }

    //Test: Materials
    static void TestTwo(){
        //Example: creating a GameObject at a custom location, then overriding the default shape/material
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.ORANGE,Color.BLUE,3);
        GatorEngine.OBJECTLIST.add(g);

        //Example: creating a GameObject at a location, setting a shape and image material
        GameObject g2 = new GameObject(150,150);
        g2.shape = new Rectangle2D.Float(0, 0, 100, 100);
        g2.material = new Material("resources/gator.jpg");
        GatorEngine.OBJECTLIST.add(g2);

        //Example: creating a GameObject at a location, setting a shape and image material. Same image as g2, but different size
        GameObject g3 = new GameObject(250,250);
        g3.shape = new Rectangle2D.Float(0, 0, 200, 200);
        g3.material = new Material("resources/gator.jpg");
        GatorEngine.OBJECTLIST.add(g3);
    }

    //Test: movement script
    static void TestThree(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.ORANGE,Color.BLUE,3);
        g.scripts.add(new Mover(g));
        GatorEngine.OBJECTLIST.add(g);
    }

    //Test: scale script
    static void TestFour(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.ORANGE,Color.BLUE,3);
        g.scripts.add(new Scaler(g));
        GatorEngine.OBJECTLIST.add(g);
    }

    //Tests: key input
    static void TestFive(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.ORANGE,Color.BLUE,3);
        g.scripts.add(new Mover2(g));
        GatorEngine.OBJECTLIST.add(g);
    }

    //Tests: mouse inputs
    static void TestSix(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.GREEN,Color.BLACK,3);
        g.scripts.add(new MouseFollow(g));
        GatorEngine.OBJECTLIST.add(g);
    }

    //Tests: mouse inputs + scripts with references to other objects
    static void TestSeven(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.GREEN,Color.BLACK,3);
        g.scripts.add(new MouseFollow(g));
        GatorEngine.OBJECTLIST.add(g);

        GameObject g2 = new GameObject(50,50);
        g2.scripts.add(new ObjectFollow(g2,g));
        GatorEngine.OBJECTLIST.add(g2);
    }

    //Tests: collision
    static void TestEight(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.GREEN,Color.BLACK,3);
        g.scripts.add(new MouseFollow(g));
        GatorEngine.OBJECTLIST.add(g);

        GameObject g2 = new GameObject(250,250);
        g2.shape = new Ellipse2D.Float(0,0,150,150);
        g2.scripts.add(new CollisionCheck(g2,g));
        GatorEngine.OBJECTLIST.add(g2);

    }
    static void TestNine(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,50,50);
        g.material = new Material(Color.GREEN,Color.BLACK,3);
        g.scripts.add(new MouseFollow(g));
        g.scripts.add(new Spawner(g));
        GatorEngine.OBJECTLIST.add(g);
    }

    //Contains()
    static void TestTen(){
        GameObject g = new GameObject(50,50);
        g.shape = new Ellipse2D.Float(0,0,200,200);

        Runnable button_func = new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int x = r.nextInt(0,500);
                int y = r.nextInt(0,500);
                GameObject g = new GameObject(x,y);
                GatorEngine.Create(g);
            }
        };

        g.scripts.add(new Button(g,Color.BLUE, Color.ORANGE,button_func));
        GatorEngine.OBJECTLIST.add(g);
    }

    //RandomWalker when you press a button (that's what we call a callback)
    static void TestEleven(){
        GameObject button = new GameObject(0,0);
        button.shape = new Rectangle2D.Float(0,0,150,100);

        GameObject walker = new GameObject(250,250);
        Runnable button_func = new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int x = r.nextInt(-5,6);
                int y = r.nextInt(-5,6);
                walker.Translate(x,y);
            }
        };

        button.scripts.add(new Button(button,Color.BLUE, Color.ORANGE,button_func));
        GatorEngine.Create(button);
        GatorEngine.Create(walker);
    }

    //Pong: A/D move paddles, balls should bounce on walls and paddles, move to middle if they move past a paddle
    static void TestTwelve(){
        Pong.Start();
    }


    static public class Spawner  extends ScriptableBehavior {
        ArrayList<GameObject> spawned = new ArrayList<>();

        Spawner(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update(){
            GameObject g = new GameObject((int)gameObject.transform.getTranslateX(), (int)gameObject.transform.getTranslateY());
            g.material=new Material("resources/gator.jpg");
            GatorEngine.Create(g);
            spawned.add(g);

            if(spawned.size()>100){
                GameObject old = spawned.get(0);
                GatorEngine.Delete(old);
                spawned.remove(0);
            }
        }
    }


    static public class Mover  extends ScriptableBehavior {
        Mover(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update(){
            gameObject.Translate(1,0);
        }
    }

    static public class Mover2  extends ScriptableBehavior {
        int frameCount = 0;
        int x_move = 1;
        int y_move = 0;
        Mover2(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update(){
            frameCount++;
            //do something based on framecount
            if(frameCount > 50)
                //change direction
                //reset framecount



                if(Input.GetKeyDown('a'))
                    gameObject.Translate(-1,0);
            if(Input.GetKeyDown('d'))
                gameObject.Translate(1,0);
            if(Input.GetKeyDown('w'))
                gameObject.Translate(0, -1);
            if(Input.GetKeyDown('s'))
                gameObject.Translate(0,1);
        }


    }

    static public class Scaler extends ScriptableBehavior {
        Scaler(GameObject g) {
            super(g);
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update(){
            gameObject.Scale(1.01f,1.01f);
        }
    }

    static public class MouseFollow extends ScriptableBehavior{
        Color not_clicked;
        Color clicked;

        MouseFollow(GameObject g) {
            super(g);
            not_clicked = Color.RED;
            clicked = Color.GREEN;
        }

        @Override
        public void Start() {
        }

        @Override
        public void Update() {
            if(!Input.MousePressed) {
                float scaleX = (float)gameObject.transform.getScaleX();
                float scaleY = (float)gameObject.transform.getScaleY();
                gameObject.transform.setToTranslation(Input.MouseX-gameObject.shape.getBounds().getWidth()/2, Input.MouseY-gameObject.shape.getBounds().getHeight()/2);
                gameObject.transform.scale(scaleX, scaleY);
                gameObject.material.setFill(not_clicked);
            }else
                gameObject.material.setFill(clicked);
        }
    }

    static public class ObjectFollow extends ScriptableBehavior{
        GameObject follow_this;

        ObjectFollow(GameObject g, GameObject other) {
            super(g);
            this.follow_this = other;
        }

        @Override
        public void Start() {
        }

        @Override
        public void Update() {
            double x = follow_this.transform.getTranslateX()-gameObject.transform.getTranslateX();
            double y = follow_this.transform.getTranslateY()-gameObject.transform.getTranslateY();

            x/=50;
            y/=50;
            gameObject.Translate((float)x,(float)y);
        }
    }

    static public class CollisionCheck extends ScriptableBehavior{
        Color not_colliding;
        Color colliding;
        GameObject other;

        CollisionCheck(GameObject g, GameObject other) {
            super(g);
            this.other = other;
            not_colliding = Color.BLUE;
            colliding = Color.ORANGE;
        }

        @Override
        public void Start() {
        }

        @Override
        public void Update() {
            if(gameObject.CollidesWith(other)) {
                gameObject.material.setFill(not_colliding);
            }else
                gameObject.material.setFill(colliding);
        }
    }

    public static class Button extends ScriptableBehavior{

        Color c1, c2;
        Runnable r;

        Button(GameObject g, Color nohover, Color hover, Runnable r) {
            super(g);
            c1=nohover;
            c2=hover;
            this.r = r;
        }

        @Override
        public void Start() {

        }

        @Override
        public void Update() {
            if(gameObject.Contains(new Point2D.Float(Input.MouseX,Input.MouseY))){
                gameObject.material.setFill(c1);
                if(Input.MousePressed && r!=null) {
                    r.run();
                }
            }else{
                gameObject.material.setFill(c2);
            }
        }
    }

    public class Pong {
        static GameObject paddle, paddle2;
        static GameObject ball, ball2;

        static void Start(){
            paddle = new GameObject(250,450);
            paddle.shape = new Rectangle2D.Float(0,0,150,30);
            paddle.material = new Material(new Color(30,30,30),Color.BLACK, 5);
            paddle.scripts.add(new PaddleMovement(paddle,15));
            GatorEngine.Create(paddle);

            paddle2 = new GameObject(250,50);
            paddle2.shape = new Rectangle2D.Float(0,0,150,30);
            paddle2.material = new Material(new Color(30,30,30),Color.BLACK, 5);
            paddle2.scripts.add(new PaddleMovement(paddle2,15));
            GatorEngine.Create(paddle2);

            ball = new GameObject(250,250);
            ball.shape = new Ellipse2D.Float(0,0,20,20);
            ball.scripts.add(new Ball(ball,paddle, paddle2));
            GatorEngine.Create(ball);

            ball2 = new GameObject(250,250);
            ball2.shape = new Ellipse2D.Float(0,0,20,20);
            ball2.scripts.add(new Ball(ball2,paddle, paddle2));
            GatorEngine.Create(ball2);
        }

        static class PaddleMovement extends ScriptableBehavior{
            int speed=1;
            PaddleMovement(GameObject g, int speed) {
                super(g);
                this.speed = speed;
            }

            @Override
            public void Start() {
                gameObject.Translate(-150,0);
            }

            @Override
            public void Update() {
                //moving the ship
                if(Input.GetKeyDown('d'))
                    gameObject.Translate(speed,0);
                if(Input.GetKeyDown('a'))
                    gameObject.Translate(-speed,0);

                //shoot stuff
                if(Input.GetKeyDown(' ')){
                    System.out.println(Ball.list.size());
                    //create a new game object (g)
                    //set it up, with material, shape, img...
                    //assign a bullet script that moves it
                    //GatorEngine.Create(g)
                }
            }
        }


        static class Ball extends ScriptableBehavior{
            static ArrayList<Ball> list = new ArrayList<>();

            int vX;
            int vY;
            int velocity = 10;

            GameObject paddle1, paddle2;

            Ball(GameObject g, GameObject paddle1, GameObject paddle2) {
                super(g);
                Random r = new Random();
                vX = r.nextInt(-velocity,velocity+1);
                vY = r.nextInt(-velocity,velocity+1);

                this.paddle1=paddle1;
                this.paddle2=paddle2;
                list.add(this);
            }

            @Override
            public void Start() {

            }

            @Override
            public void Update() {
                gameObject.Translate(vX,vY);

                if(gameObject.CollidesWith(paddle1))
                    vY = -vY;

                if(gameObject.CollidesWith(paddle2))
                    vY = -vY;

                if(gameObject.transform.getTranslateX()<=0)
                    vX = -vX;

                if(gameObject.transform.getTranslateX()+gameObject.shape.getBounds2D().getWidth() >=GatorEngine.WIDTH)
                    vX = -vX;

                if(gameObject.transform.getTranslateY()<0){
                    gameObject.Translate(0,GatorEngine.HEIGHT/2);
                }

                if(gameObject.transform.getTranslateY()>GatorEngine.HEIGHT){
                    gameObject.Translate(0,-GatorEngine.HEIGHT/2);

                }
            }
        }
    }
}

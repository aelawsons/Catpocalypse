import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
public class Pong {
    static GameObject paddle, paddle2;
    static GameObject ball, ball2;
    static void Start(){
        paddle = new GameObject(250,450);
        paddle.shape = new Rectangle2D.Float(0,0,150,30);
        paddle.material = new Material(new Color(30,30,30),Color.BLACK, 5);
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
            //moving the pong paddle
            if(Input.GetKeyDown('d'))
                gameObject.Translate(speed,0);
            if(Input.GetKeyDown('a'))
                gameObject.Translate(-speed,0);
            if(Input.GetKeyDown(' ')){
                System.out.println(Ball.list.size());
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
            if(gameObject.transform.getTranslateX()
                    +gameObject.shape.getBounds2D().getWidth() >=GatorEngine.WIDTH)
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

package sut.game01.core.Character.InScreen2;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import playn.core.Keyboard;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.util.Callback;
import playn.core.util.Clock;
import sut.game01.core.Screen.GameScreen;
import sut.game01.core.Screen.GameScreen2;
import sut.game01.core.sprite.Sprite;
import sut.game01.core.sprite.SpriteLoader;
import tripleplay.game.Screen;
import playn.core.DebugDrawBox2D;

import java.util.ArrayList;
import java.util.List;


public class SwatScreen2 extends Screen{
    public  static void setNumbullet2(int numbullet){
        SwatScreen2.numbullet2=numbullet;
    }
    public static int getNumbullet2(){
        return numbullet2;
    }
    private static int numbullet2;
    private Sprite sprite;
    private int si = 0;
    private boolean hasLoaded = false;
    public static float M_PER_PIXEL = 1/26.666667f;
    private float x;
    private float y;

    private boolean contacted;
    private int contactCheck;
    private Body other;

    private static int width = 24;
    private  static  int height = 18;

    private World world;
    private DebugDrawBox2D debugDraw;
    private boolean showDebugDraw=true;
    private float position;

    private int numbullet=20;



    private  GameScreen g;
    private List<BulletScreen2> bulletScreen2List;
    public Body getBody() {
        return body;
    }


    public enum State {
        RIDLE,LWALK,RWALK,RSHOOT,LIDLE,LSHOOT
    }
    private State state = State.RIDLE;
    private boolean left=false;
    public static Body body;

    private int e = 0;
    //  private int offset = 0;

    public SwatScreen2(final World world, final float x, final float y) {
        this.x=x;
        this.y=y;

        bulletScreen2List = new ArrayList<BulletScreen2>();
        sprite = SpriteLoader.getSprite("images/swat.json");
        sprite.addCallback(new Callback<Sprite>() {

            @Override
            public void onSuccess(Sprite result) {
                sprite.setSprite(si);
                sprite.layer().setOrigin(
                        sprite.width() / 2f,
                        sprite.height() / 2f);
                sprite.layer().setTranslation(x, y + 13f);

                body = initPhysicsBody(world,
                        GameScreen2.M_PER_PIXEL * x,
                        GameScreen2.M_PER_PIXEL * y);
                hasLoaded = true;
            }

            @Override
            public void onFailure(Throwable cause) {
                PlayN.log().error("Error loading image!", cause);
            }
        });

        PlayN.keyboard().setListener(new Keyboard.Adapter() {
            @Override
            public void onKeyUp(Keyboard.Event event) {
                switch(event.key()){
                    case LEFT:
                        left=true;
                        state = State.LIDLE;
                        break;
                    case RIGHT:
                        left=false;
                        state = State.RIDLE;
                        break;
                    case UP:
                        if( left == true ){
                            state=State.LIDLE;
                            body.applyForce(new Vec2(-1f, -700f), body.getPosition());
                        }
                        else {state = State.RIDLE;
                            body.applyForce(new Vec2(1f, -700f), body.getPosition());
                        }
                        break;
                    case SPACE:
                        if(numbullet2>0) {
                            numbullet2=numbullet2-1;
                            GameScreen2.setNumbullet3(numbullet2);
                            if (left == true) {
                                state = State.LSHOOT;
                            } else {
                                state = State.RSHOOT;
                            }
                            break;
                        }

                }
            }


            @Override
            public void onKeyDown(Keyboard.Event event) {
                switch (event.key()){
                    case LEFT:
                        left=true;
                        state = State.LWALK; break;
                    case RIGHT:
                        left=false;
                        state = State.RWALK; break;
                    case UP:
                        if(left == true) { state = State.LIDLE; }
                        else { state = State.RIDLE; }
                        break;
                    case DOWN:
                        if(left == true) { state = State.LIDLE; }
                        else { state = State.RIDLE; }
                        break;
                    case SPACE:
                        BulletScreen2 bu;
                        if(numbullet2>0) {

                            if (left == true) {
                                state = State.LSHOOT;
                                bu = new BulletScreen2(world,
                                        (body.getPosition().x) / GameScreen2.M_PER_PIXEL - 250,
                                        body.getPosition().y / GameScreen2.M_PER_PIXEL - 20, 'L');
                                body.applyForce(new Vec2(-10f, 200f), body.getPosition());
                                GameScreen2.addBulletScreen2(bu);
                            } else {
                                state = State.RSHOOT;
                                bu = new BulletScreen2(world,
                                        body.getPosition().x / GameScreen2.M_PER_PIXEL + 55,
                                        body.getPosition().y / GameScreen2.M_PER_PIXEL - 20, 'R');
                                body.applyForce(new Vec2(10f, 0f), body.getPosition());
                                GameScreen2.addBulletScreen2(bu);
                            }
                            break;
                        }

                }
            }
        });
    }

    private Body initPhysicsBody(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0, 0);
        Body body = world.createBody(bodyDef);
/*
        bodies.put(body, "test_" + GameScreen.k);
        GameScreen.k++ ;

        bodies2.put(body, "test_" + GameScreen2.j);
        GameScreen2.j++ ;
*/
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(54 * GameScreen2.M_PER_PIXEL / 2,
                sprite.layer().height()*GameScreen2.M_PER_PIXEL / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.4f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution=0.0f;
        body.createFixture(fixtureDef);

        body.setLinearDamping(0.2f);
        body.setTransform(new Vec2(x, y), 0f);

        return body;
    }

    public Layer layer() {
        return sprite.layer();
    }

    public void update(int delta) {
        if(hasLoaded == false) return;


        e += delta;
        if(e > 250) {
            switch(state) {
                case RIDLE:
                    if(!(si>=0&&si<=0)){
                        si = 0;
                    }
                    break;
                case RWALK:
                    if(!(si>=3&&si<=4)){
                        si = 3;
                    }
                    break;
                case RSHOOT:
                    if(!(si>=5&&si<=7)){
                        si = 5;
                    }
                    break;
                case LIDLE:
                    if(!(si>=8&&si<=8)){
                        si = 8;
                    }
                    break;
                case LWALK:
                    if(!(si>=11&&si<=13)){
                        si = 11;
                    }
                    break;
                case LSHOOT:
                    if(!(si>=14&&si<=15)){
                        si = 14;
                    }
                    break;
            }
            si++;
            sprite.setSprite(si);
            e = 0;
        }
    }

    @Override
    public void paint(Clock clock) {
        if (!hasLoaded) return;

        sprite.layer().setTranslation(
                (body.getPosition().x / GameScreen2.M_PER_PIXEL) - 10,
                body.getPosition().y / GameScreen2.M_PER_PIXEL);

        sprite.layer().setRotation(body.getAngle());

        switch (state){
            case LWALK:
                left=true;
                body.applyForce(new Vec2(-10f, 0f), body.getPosition());
                break;

            case RWALK:
                left=false;
                body.applyForce(new Vec2(10f, 0f), body.getPosition());
                break;

        }

    }



    public void contact(Contact contact){
        contacted = true;
        contactCheck = 0;

        if(state == State.RWALK||state==State.RIDLE||state==State.RSHOOT){
            state = State.RIDLE;
        }
        if(state == State.LWALK||state==State.LIDLE||state==State.LSHOOT){
            state=State.LIDLE;
        }
        if(contact.getFixtureA().getBody()==body){
            other = contact.getFixtureB().getBody();
        }else{
            other = contact.getFixtureA().getBody();
        }
    }

}


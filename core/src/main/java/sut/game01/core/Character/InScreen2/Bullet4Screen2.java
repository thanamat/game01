package sut.game01.core.Character.InScreen2;


import static playn.core.PlayN.*;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.util.Clock;
import sut.game01.core.Screen.GameScreen2;
import sut.game01.core.sprite.Sprite;
import tripleplay.game.Screen;

public class Bullet4Screen2 extends Screen {
    private Sprite sprite;
    private int spriteIndex = 0;
    private boolean hasLoaded = false;
    private float x;
    private float y;
    public Body body;
    private boolean contacted;
    private int contactCheck;
    private Body other;
    private World world;
    private boolean checkContact = false;
    private ImageLayer bullet4Screen2Layer;

    public enum State{
        IDLE
    };

    private State state = State.IDLE;
    private int offset = 0;
    private int e = 0;
    public Bullet4Screen2(final World world, final float x_px, final float y_px) {
        this.x = x_px;
        this.y = y_px;
        this.world = world;

        Image bullet4Screen2Image = assets().getImage("images/bullet2.png");
        bullet4Screen2Layer  = graphics().createImageLayer(bullet4Screen2Image);
        body = initPhysicsBody(world, GameScreen2.M_PER_PIXEL * x_px,GameScreen2.M_PER_PIXEL * y_px);

    }

    private Body initPhysicsBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 * GameScreen2.M_PER_PIXEL/2,
                10*GameScreen2.M_PER_PIXEL / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1000.0f;
        fixtureDef.restitution = 0.35f;
        body.createFixture(fixtureDef);

        body.setLinearDamping(1.0f);
        body.setTransform(new Vec2(x, y), 0f);
        body.applyLinearImpulse(new Vec2(-5f,0f), body.getPosition());
        return body;
    }

    public Layer layer(){
        return bullet4Screen2Layer;
    }

    public void update(int delta) {
        if(checkContact == true){
            body.setActive(false);
            checkContact = false;
        }
    }

    public void paint(Clock clock){


        bullet4Screen2Layer.setTranslation(
                (body.getPosition().x / GameScreen2.M_PER_PIXEL),
                body.getPosition().y / GameScreen2.M_PER_PIXEL);

    }

    public Body getBody(){
        return this.body;
    }
    public void contact(Contact contact){
        // body.setActive(false);
        checkContact = true;
        bullet4Screen2Layer.setVisible(false);
    }


}


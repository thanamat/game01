package sut.game01.core.Character.InScreen3;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.util.Callback;
import playn.core.util.Clock;
import sut.game01.core.Character.InScreen1.Militia;
import sut.game01.core.Screen.GameScreen3;
import sut.game01.core.sprite.Sprite;
import sut.game01.core.sprite.SpriteLoader;
import tripleplay.game.Screen;

public class Boss extends Screen{

    private Militia militia;
    private Sprite sprite;

    private boolean hasLoaded = false;
    private boolean checkContact = false;
    private boolean contacted;
    private int contactCheck;
    private Body other;
    private float x;
    private float y;
    private World world;
    private Bullet2Screen3 bullet2Screen3;
    public Body getBody() {
        return this.body;
    }


    public enum State {
        IDLE,SHOOT
    }

    private State state = State.SHOOT;
    public   Body body;
    private int e = 0;
    private int si = 0;

    public Boss(final World world, final float x_px, final float y_px) {
        this.x = x_px;
        this.y = y_px;
        sprite = SpriteLoader.getSprite("images/boss.json");
        sprite.addCallback(new Callback<Sprite>() {

            @Override
            public void onSuccess(Sprite result) {
                sprite.setSprite(si);
                sprite.layer().setOrigin(
                        sprite.width() / 2f,
                        sprite.height() / 2f);
                sprite.layer().setTranslation(x, y + 13f);

                body = initPhysicsBody(world,
                        GameScreen3.M_PER_PIXEL * x,
                        GameScreen3.M_PER_PIXEL * y);

                hasLoaded = true;
            }

            @Override
            public void onFailure(Throwable cause) {
                PlayN.log().error("Error loading image!", cause);
            }
        });


    }

    private Body initPhysicsBody(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0, 0);
        Body body = world.createBody(bodyDef);



        PolygonShape shape = new PolygonShape();
        shape.setAsBox(54 * GameScreen3.M_PER_PIXEL / 2,
                sprite.layer().height() * GameScreen3.M_PER_PIXEL / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.4f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.35f;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
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
                case IDLE: if(!(si>=0&&si<=1)){
                    si = 0;
                }
                    break;
                case SHOOT:
                        if(!(si>=2&&si<=3)){
                            si = 2;

                        }
                        break;
                    //  Bullet2 bu2 = new Bullet2(world,body.getPosition().x /GameScreen.M_PER_PIXEL +1,body.getPosition().y / GameScreen.M_PER_PIXEL-30);
                    //GameScreen.addBullet2(bu2);break;
            }
            si++;
            sprite.setSprite(si);
            e = 0;
        }
        if (checkContact == true)
            body.setActive(false);
    }

    @Override
    public void paint(Clock clock) {
        if (!hasLoaded) return;

        sprite.layer().setTranslation(
                (body.getPosition().x / GameScreen3.M_PER_PIXEL) - 10,
                body.getPosition().y / GameScreen3.M_PER_PIXEL);

        sprite.layer().setRotation(body.getAngle());

        switch (state){
            case SHOOT:
                body.applyForce(new Vec2(-10f, 0f), body.getPosition());
                break;

        }
    }
 /*   public void shooting(){
        if (checkContact == false){
            bullet2Screen3 = new Bullet2Screen3(world,body.getPosition().x / GameScreen3.M_PER_PIXEL ,body.getPosition().y / GameScreen3.M_PER_PIXEL-20);
            GameScreen3.shootBossScreen3(bullet2Screen3);
        }else{

        }
    }*/
    public void contact(Contact contact){
        //body.setActive(false);
        checkContact = true;
        sprite.layer().setVisible(false);

    }
    public void contact2(Contact contact){
        contacted = true;
        contactCheck = 0;
        if(state == State.SHOOT || state==State.IDLE){
            state = State.IDLE;
        }
        if(contact.getFixtureA().getBody()==body){
            other = contact.getFixtureB().getBody();
        }else{
            other = contact.getFixtureA().getBody();
        }
    }

}


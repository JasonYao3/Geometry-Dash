package com.Component;

import com.file.Parser;
import com.jade.Component;
import com.jade.GameObject;
import com.util.Vector2;

public class BoxBounds extends Bounds {
    public float width, height;
    public float halfWidth, halfHeight;
    public Vector2 center = new Vector2();

    public BoxBounds(float width, float height) {
        this.width = width;
        this.height = height;
        this.halfWidth = this.width / 2.0f;
        this.halfHeight = this.height / 2.0f;
        this.type = BoundsType.Box;
    }

    @Override
    public void start() {
        this.calculateCenter();
    }

    public void calculateCenter() {
        this.center.x = this.gameObject.transform.position.x + this.halfWidth;
        this.center.y = this.gameObject.transform.position.y + this.halfHeight;
    }
    @Override
    public void update(double dt) {

    }

    public static boolean checkCollision(BoxBounds b1, BoxBounds b2) {
        b1.calculateCenter();
        b2.calculateCenter();

        float dx = b2.center.x - b1.center.x;
        float dy = b2.center.y - b1.center.y;

        float combineHalfWidths = b1.halfWidth + b2.halfWidth;
        float combineHalfHeights = b1.halfHeight + b2.halfHeight;

        if (Math.abs(dx) <= combineHalfWidths) {
            return Math.abs(dy) <= combineHalfHeights;
        }
        return false;
    }

    public void resolveCollision(GameObject player) {
        BoxBounds playerBounds = player.getComponent(BoxBounds.class);
        playerBounds.calculateCenter();
        this.calculateCenter();

        float dx = this.center.x - playerBounds.center.x;
        float dy = this.center.y - playerBounds.center.y;

        float combineHalfWidths = playerBounds.halfWidth + this.halfWidth;
        float combineHalfHeights = playerBounds.halfHeight + this.halfHeight;

        float overlapX = combineHalfWidths - Math.abs(dx);
        float overlapY = combineHalfHeights - Math.abs(dy);

        if (overlapX >= overlapY) {
            if (dy > 0) {
                // Collision on the top of the player
                player.transform.position.y = gameObject.transform.position.y - playerBounds.getHeight();
                player.getComponent(Rigidbody.class).velocity.y = 0;
                player.getComponent(Player.class).onGround = true;
            } else {
                // Collision on the bottom of the player
                player.getComponent(Player.class).die();
            }
        } else {
            // Collision on the left or right of the player
            if (dx < 0 && dy <= 0.3) {
                player.transform.position.y = gameObject.transform.position.y - playerBounds.getHeight();
                player.getComponent(Rigidbody.class).velocity.y = 0;
                player.getComponent(Player.class).onGround = true;
            } else {
                player.getComponent(Player.class).die();
            }
        }
    }

    @Override
    public Component copy() {
        return new BoxBounds(width, height);
    }

    @Override
    public String serialize(int tabSize) {
        StringBuilder builder = new StringBuilder();

        builder.append(beginObjectProperty("BoxBounds", tabSize));
        builder.append(addFloatProperty("Width", this.width, tabSize + 1, true, true));
        builder.append(addFloatProperty("Height", this.height, tabSize + 1, true, false));
        builder.append(closeObjectProperty(tabSize));
        return builder.toString();
    }

    public static BoxBounds deserialize() {
        float width = Parser.consumeFloatProperty("Width");
        Parser.consume(',');
        float height = Parser.consumeFloatProperty("Height");
        Parser.consumeEndObjectProperty();

        return new BoxBounds(width, height);
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }
}

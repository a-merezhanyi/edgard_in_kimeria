package com.jlogicsoftware.kimeria.entity.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jlogicsoftware.kimeria.Assets;
import com.jlogicsoftware.kimeria.TrapWarnings;
import com.jlogicsoftware.kimeria.effects.Torch;
import com.jlogicsoftware.kimeria.entity.Actor;
import com.jlogicsoftware.kimeria.entity.Collidable;
import com.jlogicsoftware.kimeria.entity.WorldObject;

import java.util.function.Consumer;

/** A one-shot platform that lights up, then collapses after a shared warning delay once the player lands on it. */
public class FallingPlatform extends Actor<FallingPlatform.State> implements Collidable {
    public enum State {IDLE, FALLING}

    /** Native sprite height (source art is 32x10 per frame) -- see Escalator's VISUAL_HEIGHT for why. */
    private static final float VISUAL_HEIGHT = 10f;

    private final TrapWarnings warnings;
    private boolean falling = false;
    private float fallDelay = -1f;
    private Torch torch;
    private final Consumer<WorldObject> spawner;
    private float fallVelocity = 0f;
    private static final float FALL_DISTANCE = 200f;
    private static final float FALL_DURATION = 1.5f;
    private static final int WARNING_TORCH_INTENSITY = 5;

    public FallingPlatform(Assets assets, float x, float y, float w, float h, Consumer<WorldObject> spawner,
                           TrapWarnings warnings) {
        super(x, y, w, h, State.class);
        this.spawner = spawner;
        this.warnings = warnings;
        String image = "images/objects/FallingOn.png";
        // The source art is 128x10 (four 32x10 frames), not the 32x16 the
        // original Dart code assumed -- use the real sheet size to avoid
        // sampling outside the image.
        putAnimation(State.IDLE, assets.animation(image, 4, 0.1f, 32, 10, 0, 0, 4), true);
        putAnimation(State.FALLING, assets.animation(image, 4, 0.3f, 32, 10, 0, 0, 4), true);
        setState(State.IDLE);
    }

    public boolean isFalling() {
        return falling;
    }

    public record Snapshot(float x, float y, boolean falling, float warningSeconds,
                           float fallSeconds, boolean facingRight, float animationTime,
                           boolean removed) { }

    public Snapshot snapshot() {
        return new Snapshot(position.x, position.y, falling, fallDelay, fallVelocity,
            facingRight, animationTime(), removed);
    }

    /** Restores platform progress and reconstructs only a live warning light. */
    public void restore(Snapshot snapshot) {
        removeWarningTorch();
        position.set(snapshot.x(), snapshot.y());
        falling = snapshot.falling();
        fallDelay = snapshot.warningSeconds();
        fallVelocity = snapshot.fallSeconds();
        facingRight = snapshot.facingRight();
        setState(falling ? State.FALLING : State.IDLE);
        restoreAnimationTime(snapshot.animationTime());
        removed = snapshot.removed();
        if (!removed && falling && fallDelay > 0f) {
            torch = new Torch(centerX(), centerY(), size.x, size.y, WARNING_TORCH_INTENSITY, "");
            spawner.accept(torch);
        }
    }

    @Override
    public void render(Batch batch) {
        Animation<TextureRegion> anim = animations.get(current);
        if (anim == null) return;
        TextureRegion frame = anim.getKeyFrame(stateTime);
        float drawY = position.y + (size.y - VISUAL_HEIGHT) / 2f;
        float w = facingRight ? size.x : -size.x;
        float x = facingRight ? position.x : position.x + size.x;
        batch.draw(frame, x, drawY, w, VISUAL_HEIGHT);
    }

    public void collideWithActor() {
        if (!falling) {
            triggerFall();
        }
    }

    private void triggerFall() {
        falling = true;
        setState(State.FALLING);
        torch = new Torch(centerX(), centerY(), size.x, size.y, WARNING_TORCH_INTENSITY, "");
        spawner.accept(torch);
        fallDelay = warnings.duration(TrapWarnings.FALLING_PLATFORM_SECONDS);
    }

    @Override
    public void update(float dt) {
        tickAnimation(dt);
        if (fallDelay > 0) {
            fallDelay -= dt;
            if (fallDelay <= 0) {
                if (torch != null) torch.toggleFire(false);
                fallVelocity = 0.0001f; // marks "now actually falling"
            }
        } else if (fallVelocity > 0) {
            fallVelocity += dt;
            position.y += (FALL_DISTANCE / FALL_DURATION) * dt;
            if (fallVelocity >= FALL_DURATION) {
                removeFromParent();
                removeWarningTorch();
            }
        }
        if (torch != null) {
            torch.update(dt);
        }
    }

    private void removeWarningTorch() {
        if (torch == null) return;
        torch.removeFromParent();
        torch = null;
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public float getWidth() {
        return size.x;
    }

    @Override
    public float getHeight() {
        return size.y;
    }

    @Override
    public boolean isQuickSand() {
        return false;
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public boolean isWall() {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isEscalator() {
        return false;
    }
}

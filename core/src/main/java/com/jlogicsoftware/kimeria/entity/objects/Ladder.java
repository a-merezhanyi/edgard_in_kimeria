package com.jlogicsoftware.kimeria.entity.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jlogicsoftware.kimeria.Assets;
import com.jlogicsoftware.kimeria.SpriteSheets;
import com.jlogicsoftware.kimeria.entity.Entity;
import com.jlogicsoftware.kimeria.entity.items.Actionable;
import com.jlogicsoftware.kimeria.entity.environment.CollisionBlock;

/**
 * A vertical climbing zone. The authored rectangle is the whole climbable span: its top edge is
 * the surface the hero steps onto and its bottom edge is where the hero steps off. The art is
 * stacked from the top, one registered frame per segment, so an authored height is a whole number of segments.
 */
public final class Ladder extends Entity implements Actionable {
    private static final int TOP_FRAME = 0, MIDDLE_FRAME = 1;
    private static final float MOVE_SPEED = 160f;
    private final TextureRegion top, middle;
    private final Rectangle bounds;
    private final CollisionBlock topSupport;
    private final String targetId;
    private final float raisedY, loweredY;
    private boolean lowered;

    public Ladder(Assets assets, float x, float y, float w, float h) {
        this(assets, x, y, w, h, "", 0f);
    }

    public Ladder(Assets assets, float x, float y, float w, float h, String targetId, float dropDistance) {
        super(x, y, w, h);
        if (w != SpriteSheets.LADDER.width() || h <= 0 || h % SpriteSheets.LADDER.height() != 0)
            throw new IllegalArgumentException("Ladder must be one frame wide and whole frames tall: " + w + "x" + h);
        if (dropDistance < 0) throw new IllegalArgumentException("Ladder drop distance cannot be negative");
        top = SpriteSheets.LADDER.frame(assets, TOP_FRAME, 0);
        middle = SpriteSheets.LADDER.frame(assets, MIDDLE_FRAME, 0);
        bounds = new Rectangle(x, y, w, h);
        topSupport = CollisionBlock.platform(x, y, w, SpriteSheets.LADDER.height());
        this.targetId = targetId == null ? "" : targetId;
        raisedY = y;
        loweredY = y + dropDistance;
    }

    /** World rectangle of the climbable span. */
    public Rectangle bounds() { return bounds; }
    /** One-way support occupying the top rung's visual footprint. */
    public CollisionBlock topSupport() { return topSupport; }
    public float topY() { return getY(); }
    public float bottomY() { return getY() + getHeight(); }

    public boolean isLowered() { return lowered; }

    public record Snapshot(float y, boolean lowered) { }

    public Snapshot snapshot() { return new Snapshot(getY(), lowered); }

    public void restore(Snapshot snapshot) {
        position.y = snapshot.y();
        lowered = snapshot.lowered();
        bounds.y = snapshot.y();
        topSupport.position.y = snapshot.y();
    }

    @Override public String getTargetId() { return targetId; }

    @Override public void performAction() {
        if (targetId.isEmpty() || raisedY == loweredY) return;
        lowered = !lowered;
    }

    @Override public void update(float dt) {
        float targetY = lowered ? loweredY : raisedY;
        float distance = targetY - position.y;
        float step = MOVE_SPEED * dt;
        if (Math.abs(distance) <= step) position.y = targetY;
        else position.y += Math.signum(distance) * step;
        bounds.y = position.y;
        topSupport.position.y = position.y;
    }

    @Override public void render(Batch batch) {
        float segment = SpriteSheets.LADDER.height();
        for (float offset = 0; offset < getHeight(); offset += segment) {
            batch.draw(offset == 0 ? top : middle, getX(), getY() + offset, getWidth(), segment);
        }
    }
}

package com.jlogicsoftware.kimeria.entity.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.jlogicsoftware.kimeria.Assets;
import com.jlogicsoftware.kimeria.SpriteSheets;
import com.jlogicsoftware.kimeria.entity.Entity;

/**
 * A vertical climbing zone. The authored rectangle is the whole climbable span: its top edge is
 * the surface the hero steps onto and its bottom edge is where the hero steps off. The art is
 * stacked from the top, one registered frame per segment, so an authored height is a whole number of segments.
 */
public final class Ladder extends Entity {
    private static final int TOP_FRAME = 0, MIDDLE_FRAME = 1;
    private final TextureRegion top, middle;
    private final Rectangle bounds;

    public Ladder(Assets assets, float x, float y, float w, float h) {
        super(x, y, w, h);
        if (w != SpriteSheets.LADDER.width() || h <= 0 || h % SpriteSheets.LADDER.height() != 0)
            throw new IllegalArgumentException("Ladder must be one frame wide and whole frames tall: " + w + "x" + h);
        top = SpriteSheets.LADDER.frame(assets, TOP_FRAME, 0);
        middle = SpriteSheets.LADDER.frame(assets, MIDDLE_FRAME, 0);
        bounds = new Rectangle(x, y, w, h);
    }

    /** World rectangle of the climbable span. */
    public Rectangle bounds() { return bounds; }
    public float topY() { return getY(); }
    public float bottomY() { return getY() + getHeight(); }

    @Override public void update(float dt) { }

    @Override public void render(Batch batch) {
        float segment = SpriteSheets.LADDER.height();
        for (float offset = 0; offset < getHeight(); offset += segment) {
            batch.draw(offset == 0 ? top : middle, getX(), getY() + offset, getWidth(), segment);
        }
    }
}

package com.jlogicsoftware.kimeria.entity.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jlogicsoftware.kimeria.Assets;
import com.jlogicsoftware.kimeria.SpriteSheets;
import com.jlogicsoftware.kimeria.effects.Torch;
import com.jlogicsoftware.kimeria.entity.Entity;

import java.util.function.BooleanSupplier;

/** Visible control at its authored interaction zone; the linked mechanism owns its state. */
public final class Lever extends Entity {
    public final Trigger trigger;
    private final BooleanSupplier active;
    private final TextureRegion up, down;

    public Lever(Assets assets, Trigger trigger, Torch torch) {
        this(assets, trigger, torch::isBurning);
    }

    public Lever(Assets assets, Trigger trigger, BooleanSupplier active) {
        super(trigger.centerX() - SpriteSheets.LEVER.width() / 2f,
            trigger.getY() + trigger.getHeight() - SpriteSheets.LEVER.height(),
            SpriteSheets.LEVER.width(), SpriteSheets.LEVER.height());
        this.trigger = trigger;
        this.active = active;
        // Registered states share one sheet: left = up/on, right = down/off.
        up = SpriteSheets.LEVER.frame(assets, 0, 0);
        down = SpriteSheets.LEVER.frame(assets, 1, 0);
    }

    public boolean isUp() { return active.getAsBoolean(); }
    @Override public void update(float dt) { }
    @Override public void render(Batch batch) { batch.draw(isUp() ? up : down, getX(), getY(), getWidth(), getHeight()); }
}

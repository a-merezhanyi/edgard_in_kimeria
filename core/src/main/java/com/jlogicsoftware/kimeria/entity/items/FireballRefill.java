package com.jlogicsoftware.kimeria.entity.items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jlogicsoftware.kimeria.GameContext;
import com.jlogicsoftware.kimeria.SpriteSheets;
import com.jlogicsoftware.kimeria.entity.Entity;
import com.jlogicsoftware.kimeria.world.Level;

/** An authored, room-owned single charge; a full inventory leaves it available. */
public final class FireballRefill extends Entity {
    private final Level level;
    private final GameContext game;
    private final int id;
    private final TextureRegion sprite;

    public FireballRefill(Level level, GameContext game, int id, float x, float y) {
        super(x, y, SpriteSheets.FIREBALL.width(), SpriteSheets.FIREBALL.height());
        this.level = level;
        this.game = game;
        this.id = id;
        sprite = level.objectSprite(SpriteSheets.FIREBALL);
    }

    public boolean inventoryFull() { return level.rangedStock.isFull(); }

    public void collideWithPlayer() {
        if (isRemoved() || !level.rangedStock.refill()) return;
        level.recordPickup(id);
        if (game.playSounds()) game.playSound("collect");
        removeFromParent();
    }

    @Override public void update(float dt) { }

    @Override public void render(Batch batch) {
        batch.draw(sprite, getX(), getY(), size.x, size.y);
    }
}

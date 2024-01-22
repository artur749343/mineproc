package net.kruassan.mineproc.block.entity.client;

import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.block.entity.UpdaterEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class UpdaterModel extends GeoModel<UpdaterEntity> {
    @Override
    public Identifier getModelResource(UpdaterEntity animatable) {
        return new Identifier(Mineproc.MOD_ID, "geo/updater.geo.json");
    }

    @Override
    public Identifier getTextureResource(UpdaterEntity animatable) {
        return new Identifier(Mineproc.MOD_ID, "textures/block/updater_texture.png");
    }

    @Override
    public Identifier getAnimationResource(UpdaterEntity animatable) {
        return new Identifier(Mineproc.MOD_ID, "animations/updater.animation.json");
    }
}

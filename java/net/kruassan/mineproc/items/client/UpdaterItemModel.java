package net.kruassan.mineproc.items.client;

import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.items.custom.UpdaterItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class UpdaterItemModel extends GeoModel<UpdaterItem> {
    @Override
    public Identifier getModelResource(UpdaterItem animatable) {
        return new Identifier(Mineproc.MOD_ID, "geo/updater.geo.json");
    }

    @Override
    public Identifier getTextureResource(UpdaterItem animatable) {
        return new Identifier(Mineproc.MOD_ID, "textures/block/updater_texture.png");
    }

    @Override
    public Identifier getAnimationResource(UpdaterItem animatable) {
        return new Identifier(Mineproc.MOD_ID, "animations/updater.animation.json");
    }
}

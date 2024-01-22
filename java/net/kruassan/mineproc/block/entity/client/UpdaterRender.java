package net.kruassan.mineproc.block.entity.client;

import net.kruassan.mineproc.block.entity.UpdaterEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class UpdaterRender extends GeoBlockRenderer<UpdaterEntity>{
    public UpdaterRender(BlockEntityRendererFactory.Context context){
        super(new UpdaterModel());
    }
}

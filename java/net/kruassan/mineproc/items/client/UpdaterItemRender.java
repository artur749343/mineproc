package net.kruassan.mineproc.items.client;


import net.kruassan.mineproc.items.custom.UpdaterItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class UpdaterItemRender extends GeoItemRenderer<UpdaterItem> {
   public UpdaterItemRender(){
       super(new UpdaterItemModel());
   }
}

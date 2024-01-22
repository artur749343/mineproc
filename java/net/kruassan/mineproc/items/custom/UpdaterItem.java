package net.kruassan.mineproc.items.custom;

import net.kruassan.mineproc.items.client.UpdaterItemRender;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdaterItem extends BlockItem implements GeoItem {
    private AnimatableInstanceCache cache=new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider=GeoItem.makeRenderer(this);
    private static final RawAnimation Nothing = RawAnimation.begin().thenLoop("nothing");
    public UpdaterItem(Block block, Settings settings) {
        super(block, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final UpdaterItemRender render=new UpdaterItemRender();
            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return this.render;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, state -> {
            return state.setAndContinue(Nothing);
    }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }
}

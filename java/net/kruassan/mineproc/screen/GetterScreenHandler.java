package net.kruassan.mineproc.screen;

import net.kruassan.mineproc.block.entity.GetterEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

public class GetterScreenHandler extends ScreenHandler {
    //    private final PropertyDelegate propertyDelegate;
    public final GetterEntity blockEntity;

    public GetterScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(2));
    }

    public GetterScreenHandler(int syncId, PlayerInventory PlayerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate){
        super(ModScreenHandlers.GETTER_SCREEN_HANDLER, syncId);
        this.blockEntity=((GetterEntity) blockEntity);
    }

//    public int ReadProperty(int index){
//        return propertyDelegate.get(index);
//    }
//    public void SetProperty(int index, int value){
//        propertyDelegate.set(index, value);
//    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return null;
    }
}
package net.kruassan.mineproc.screen;

import net.kruassan.mineproc.block.entity.MonitorEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

public class MonitorScreenHandler extends ScreenHandler {
//    private final Inventory inventory;
//    private final PropertyDelegate propertyDelegate;
    public final MonitorEntity blockEntity;

    public MonitorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(2));
    }

    public MonitorScreenHandler(int syncId, PlayerInventory PlayerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate){
        super(ModScreenHandlers.MONITOR_SCREEN_HANDLER, syncId);
        this.blockEntity=((MonitorEntity) blockEntity);

//        addProperties(arrayPropertyDelegate);
//        checkSize(((Inventory)blockEntity), 1);
//        this.inventory=((Inventory)blockEntity);
//        inventory.onOpen(PlayerInventory.player);
//        this.propertyDelegate=arrayPropertyDelegate;
//        this.addSlot(new Slot(((ComputerEntity)PlayerInventory.player.getWorld().getBlockEntity(this.blockEntity.comp_pos)), 0, 0, 0));
//        addPlayerInventory(PlayerInventory);
//        addPlayerHotbar(PlayerInventory);
//        addProperties(arrayPropertyDelegate);
    }

//    public int ReadProperty(int index){
//        return propertyDelegate.get(index);
//    }
//    public void SetProperty(int index, int value){
//        propertyDelegate.set(index, value);
//    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true; //this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
//        ItemStack newStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(invSlot);
//        if (slot != null && slot.hasStack()) {
//            ItemStack originalStack = slot.getStack();
//            newStack = originalStack.copy();
//            if (invSlot < this.inventory.size()) {
//                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (originalStack.isEmpty()) {
//                slot.setStack(ItemStack.EMPTY);
//            } else {
//                slot.markDirty();
//            }
//        }
//
//        return newStack;
        return null;
    }

//    private void addPlayerInventory(PlayerInventory playerInventory) {
//        for (int i = 0; i < 3; ++i) {
//            for (int l = 0; l < 9; ++l) {
//                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
//            }
//        }
//    }
//
//    private void addPlayerHotbar(PlayerInventory playerInventory) {
//        for (int i = 0; i < 9; ++i) {
//            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
//        }
//    }
}
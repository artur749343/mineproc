package net.kruassan.mineproc.screen;

import net.kruassan.mineproc.block.entity.UpdaterEntity;
import net.kruassan.mineproc.items.ModItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class UpdaterScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final UpdaterEntity blockEntity;

    public UpdaterScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(3));
    }

    public UpdaterScreenHandler(int syncId, PlayerInventory PlayerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate){
        super(ModScreenHandlers.UPDATER_SCREEN_HANDLER, syncId);
        checkSize(((Inventory)blockEntity), 5);
        this.inventory=((Inventory)blockEntity);
        inventory.onOpen(PlayerInventory.player);
        this.propertyDelegate=arrayPropertyDelegate;
        this.blockEntity=((UpdaterEntity) blockEntity);
        this.addSlot(new Slot(inventory, 0, 42, 7));
        this.addSlot(new Slot(inventory, 1, 116, 7));
        this.addSlot(new Slot(inventory, 2, 42, 58));
        this.addSlot(new Slot(inventory, 3, 116, 58));
        this.addSlot(new Slot(inventory, 4, 78, 33) {public boolean canInsert(ItemStack stack) {return false;}});
        addPlayerInventory(PlayerInventory);
        addPlayerHotbar(PlayerInventory);
        addProperties(arrayPropertyDelegate);
    }
    public int ReadProperty(int index){
        return propertyDelegate.get(index);
    }
    public void SetProperty(int index, int value){
        propertyDelegate.set(index, value);
    }
    public boolean isCrafting(){
        return propertyDelegate.get(0)>0;
    }
    public int getScaledProgress(){
        int progress=this.propertyDelegate.get(0);
        int maxProgress=this.propertyDelegate.get(1);
        int progressArrowSize=24;
        return maxProgress!=0&&progress!=0?progress*progressArrowSize/maxProgress:0;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
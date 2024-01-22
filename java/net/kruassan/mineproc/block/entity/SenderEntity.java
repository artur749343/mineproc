package net.kruassan.mineproc.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.kruassan.mineproc.screen.SenderScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class SenderEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory=DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
//    public ComputerEntity comp_ent;
    public String Id;
    public SenderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SENDER_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
        this.propertyDelegate=new PropertyDelegate() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Sender");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SenderScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }
    public void tick(World world, BlockPos pos, BlockState state){
        //do
        markDirty(world, pos, state);
    }

//    private ComputerEntity Get_comp_ent(){
//        for (int i2 = -1; i2 < 2; i2++){
//            for (int i1 = -1; i1 < 2; i1++) {
//                for (int i = -1; i < 2; i++) {
//                    BlockEntity block=this.world.getBlockEntity(BlockPos.ofFloored(this.pos.getX() + i, this.pos.getY() + i1, this.pos.getZ() + i2));
//                    if (block!=null&&ModBlockEntities.COMPUTER_ENTITY_BLOCK_ENTITY_TYPE.equals(block.getType())) {
//                        return ((ComputerEntity)block);
//                    }
//                }
//            }
//        }
//        return null;
//    }
}
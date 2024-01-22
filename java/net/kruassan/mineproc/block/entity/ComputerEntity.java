package net.kruassan.mineproc.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.kruassan.mineproc.block.custom.Computer;
import net.kruassan.mineproc.screen.ComputerScreenHandler;
import net.minecraft.block.Block;
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

public class ComputerEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private DefaultedList<ItemStack> inventory=DefaultedList.ofSize(7, ItemStack.EMPTY);
    private final BlockPos[] list_pos=new BlockPos[]{BlockPos.ofFloored(pos.getX(), pos.getY(), pos.getZ()-1), BlockPos.ofFloored(pos.getX(), pos.getY(), pos.getZ()+1), BlockPos.ofFloored(pos.getX()-1, pos.getY(), pos.getZ()), BlockPos.ofFloored(pos.getX()+1, pos.getY(), pos.getZ())};

    protected final PropertyDelegate propertyDelegate;
    public boolean turn_on=false;


    public ComputerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
        this.propertyDelegate=new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> ComputerEntity.this.turn_on?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> ComputerEntity.this.turn_on=1==value;
                };
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
        return Text.literal("Computer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        return new ComputerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }



    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (null!=nbt) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (null!=nbt) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }
    public void tick(World world, BlockPos pos, BlockState state){
        world.setBlockState(pos, state.with(Computer.HAS, this.turn_on?8:(this.getStack(4).isEmpty()?0:4)+(this.getStack(5).isEmpty()?0:2)+(this.getStack(6).isEmpty()?0:1)), Block.NOTIFY_ALL);
        this.getStack(6).getOrCreateNbt().putInt("energy", get_energy(world)+this.getStack(6).getOrCreateNbt().getInt("energy"));
        if (!(this.getStack(6).isEmpty()||this.getStack(5).isEmpty()||this.getStack(4).isEmpty())) {
            this.turn_on=0<this.getStack(6).getOrCreateNbt().getInt("energy");
            if (this.turn_on) this.getStack(6).getOrCreateNbt().putInt("energy", this.getStack(6).getOrCreateNbt().getInt("energy")-1);
        } else this.turn_on=false;
    }


    private int get_energy(World world){
        int res=0;
        if (this.getStack(6).isEmpty()) return 0;
        for (BlockPos pos: list_pos){
            if (world!=null) {
                BlockEntity block = world.getBlockEntity(pos);
                if (block!=null&&ModBlockEntities.SUN_PANEL_BLOCK_ENTITY_TYPE.equals(block.getType())){
                    if (this.getStack(6).getOrCreateNbt().getInt("energy")<this.getStack(6).getOrCreateNbt().getInt("max_energy")) {
                        int n=Math.min(5, this.getStack(6).getOrCreateNbt().getInt("max_energy")-this.getStack(6).getOrCreateNbt().getInt("energy"));
                        if (n<((SunPanelEntity)block).propertyDelegate.get(0)) {
                            res+=n;
                            ((SunPanelEntity) block).propertyDelegate.set(0,((SunPanelEntity)block).propertyDelegate.get(0)-n);
                        }
                    }
                }
            }
        }
        return res;
    }
}

package net.kruassan.mineproc.block.entity;


import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.items.ModItem;
import net.kruassan.mineproc.screen.SunPanelScreenHandler;
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

public class SunPanelEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private DefaultedList<ItemStack> inventory=DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int power=0;
    private int max_power=1000;

    public SunPanelEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SUN_PANEL_BLOCK_ENTITY_TYPE, pos, state);
        this.propertyDelegate=new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> SunPanelEntity.this.power;
                    case 1 -> SunPanelEntity.this.max_power;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SunPanelEntity.this.power = value;
                    case 1 -> SunPanelEntity.this.max_power = value;
                }
            }
            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("SunPanel");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        return new SunPanelScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
        if (world.isClient()){
            return;
        }
        int energy=this.getStack(0).getOrCreateNbt().getInt("energy");
        int max_energy=this.getStack(0).getOrCreateNbt().getInt("max_energy");

        if (Mineproc.Server_player!=null&&Mineproc.Server_player.getWorld()!=null) {
            long time = Mineproc.Server_player.getWorld().getTimeOfDay() % 24000;
            if (1000L < time && time < 11000L&&power<=max_power) power++;
        }
        if (this.getStack(0).isOf(ModItem.Energy_block)&&energy<max_energy) {
            AddPower(energy, max_energy,this.getStack(0));
        }
        markDirty(world, pos, state);
    }

    private void AddPower(int energy, int max_energy, ItemStack stack){
        if (power>0) {
            int i=energy+4<max_energy?energy+5:max_energy-energy;
            stack.getOrCreateNbt().putInt("energy", i);
            power-=i;
        } else {
            stack.getOrCreateNbt().putInt("energy", energy+1);
            power--;
        }
    }
}

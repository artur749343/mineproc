package net.kruassan.mineproc.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.kruassan.mineproc.items.ModItem;
import net.kruassan.mineproc.screen.UpdaterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.util.RenderUtils;

public class UpdaterEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {
    private final DefaultedList<ItemStack> inventory=DefaultedList.ofSize(5, ItemStack.EMPTY);

    public final PropertyDelegate propertyDelegate;
    private int progress=0;
    private int maxProgress=72;
    private int Energy=0;

    private static final RawAnimation Crafting = RawAnimation.begin().thenLoop("crafting");
    private static final RawAnimation Nothing = RawAnimation.begin().thenPlay("crafting_end").thenLoop("nothing");

    private AnimatableInstanceCache cache=new SingletonAnimatableInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, state -> {
            return state.setAndContinue(state.getAnimatable().hasRecipe()?Crafting:Nothing);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    public UpdaterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UPDATER_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
        this.propertyDelegate=new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> UpdaterEntity.this.progress;
                    case 1 -> UpdaterEntity.this.maxProgress;
                    case 2 -> UpdaterEntity.this.Energy;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> UpdaterEntity.this.progress=value;
                    case 1 -> UpdaterEntity.this.maxProgress=value;
                    case 2 -> UpdaterEntity.this.Energy=value;
                };
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Updater");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        return new UpdaterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("updater.progress", progress);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress=nbt.getInt("updater.progress");
    }
    public void tick(World world, BlockPos pos, BlockState state){
        if(isOutputSlotEmpty()){
            if (this.hasRecipe()) {
                this.increaseCraftProgress();
                markDirty(world, pos, state);
                if(hasCraftingFinished()){
                    this.craftItem();
                    this.resetProgress();
                }
            } else this.resetProgress();
        } else{
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void resetProgress() {
        this.progress=0;
    }

    private void craftItem() {
        ItemStack result=new ItemStack(getStack(0).getItem(), getStack(4).getCount()+1);
        NbtCompound nbt=getStack(0).hasNbt()?new NbtCompound().copyFrom(getStack(0).getNbt()):new NbtCompound();
        nbt.putInt("mineproc.memory_speed", getStack(0).hasNbt()?nbt.getInt("mineproc.memory_speed")+1:0);
        result.setNbt(nbt);
        this.setStack(4, result);
        this.removeStack(0, 1);
        this.removeStack(1, 1);
        this.removeStack(2, 1);
        this.removeStack(3, 1);
    }

    private boolean hasCraftingFinished() {
        return progress>=maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack result=new ItemStack(getStack(0).getItem());
        for (int i=1;i<4;i++) {
            ItemStack j=getStack(i);
            if (!ModItem.IsMemory(j.getItem())||getStack(0).getItem()!=j.getItem()) return false;
            if (getStack(0).hasNbt()&&getStack(i).hasNbt()&&getStack(0).getNbt().getInt("mineproc.memory_speed")!=j.getNbt().getInt("mineproc.memory_speed")) return false;
            if (getStack(0).hasNbt()&&!getStack(i).hasNbt()) return false;
        }
        return canInsertAmountIntoOutputSlot(result)&&canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(4).getItem()==item||this.getStack(4).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(4).getCount()+result.getCount()<=getStack(4).getMaxCount();
    }

    private boolean isOutputSlotEmpty() {
        return this.getStack(4).isEmpty()||this.getStack(4).getCount()<this.getStack(4).getMaxCount();
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}

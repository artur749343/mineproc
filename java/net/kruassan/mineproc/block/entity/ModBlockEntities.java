package net.kruassan.mineproc.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.block.ModBlock;

public class ModBlockEntities {
    public static final BlockEntityType<ComputerEntity> COMPUTER_ENTITY_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "computer_entity"),
            FabricBlockEntityTypeBuilder.create(ComputerEntity::new, ModBlock.Computer).build()
            );

    public static final BlockEntityType<SunPanelEntity> SUN_PANEL_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "sun_panel_entity"),
            FabricBlockEntityTypeBuilder.create(SunPanelEntity::new, ModBlock.SunPanel).build()
    );
    public static final BlockEntityType<MonitorEntity> MONITOR_ENTITY_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "monitor_entity"),
            FabricBlockEntityTypeBuilder.create(MonitorEntity::new, ModBlock.Monitor).build()
    );

    public static final BlockEntityType<UpdaterEntity> UPDATER_ENTITY_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "updater_entity"),
            FabricBlockEntityTypeBuilder.create(UpdaterEntity::new, ModBlock.Updater).build()
    );

    public static final BlockEntityType<SenderEntity> SENDER_ENTITY_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "sender_entity"),
            FabricBlockEntityTypeBuilder.create(SenderEntity::new, ModBlock.Sender).build()
    );
    public static final BlockEntityType<GetterEntity> GETTER_ENTITY_BLOCK_ENTITY_TYPE= Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(Mineproc.MOD_ID, "getter_entity"),
            FabricBlockEntityTypeBuilder.create(GetterEntity::new, ModBlock.Getter).build()
    );
    public static void registerBlockEntities(){
    }
}

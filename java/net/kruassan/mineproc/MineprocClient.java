package net.kruassan.mineproc;

import net.fabricmc.api.ClientModInitializer;
import net.kruassan.mineproc.block.entity.ModBlockEntities;
import net.kruassan.mineproc.block.entity.client.UpdaterRender;
import net.kruassan.mineproc.screen.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MineprocClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, ComputerScreen::new);
        HandledScreens.register(ModScreenHandlers.MONITOR_SCREEN_HANDLER, MonitorScreen::new);
        HandledScreens.register(ModScreenHandlers.UPDATER_SCREEN_HANDLER, UpdaterScreen::new);
        HandledScreens.register(ModScreenHandlers.SUN_PANEL_SCREEN_HANDLER, SunPanelScreen::new);
        HandledScreens.register(ModScreenHandlers.SENDER_SCREEN_HANDLER, SenderScreen::new);
        HandledScreens.register(ModScreenHandlers.GETTER_SCREEN_HANDLER, GetterScreen::new);
        BlockEntityRendererFactories.register(ModBlockEntities.UPDATER_ENTITY_BLOCK_ENTITY_TYPE, UpdaterRender::new);
    }
}

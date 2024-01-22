package net.kruassan.mineproc.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.kruassan.mineproc.Mineproc;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers{
    public static ScreenHandlerType<ComputerScreenHandler> COMPUTER_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "computer")
    , new ExtendedScreenHandlerType<>(ComputerScreenHandler::new));

    public static ScreenHandlerType<MonitorScreenHandler> MONITOR_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "monitor")
            , new ExtendedScreenHandlerType<>(MonitorScreenHandler::new));

    public static ScreenHandlerType<UpdaterScreenHandler> UPDATER_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "updater")
            , new ExtendedScreenHandlerType<>(UpdaterScreenHandler::new));
    public static ScreenHandlerType<SunPanelScreenHandler> SUN_PANEL_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "sun_panel")
            , new ExtendedScreenHandlerType<>(SunPanelScreenHandler::new));
    public static ScreenHandlerType<SenderScreenHandler> SENDER_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "sender")
            , new ExtendedScreenHandlerType<>(SenderScreenHandler::new));
    public static ScreenHandlerType<GetterScreenHandler> GETTER_SCREEN_HANDLER= Registry.register(Registries.SCREEN_HANDLER, new Identifier(Mineproc.MOD_ID, "getter")
            , new ExtendedScreenHandlerType<>(GetterScreenHandler::new));
    public static void registerAllScreenHandlers(){

    }
}
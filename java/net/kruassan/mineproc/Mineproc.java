package net.kruassan.mineproc;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.kruassan.mineproc.block.ModBlock;
import net.kruassan.mineproc.block.entity.ModBlockEntities;
import net.kruassan.mineproc.items.ModItem;
import net.kruassan.mineproc.items.ModItemGroup;
import net.kruassan.mineproc.screen.ModScreenHandlers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.cache.GeckoLibCache;


public class Mineproc implements ModInitializer {
	public static final String MOD_ID="mineproc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerPlayerEntity Server_player;
	@Override
	public void onInitialize() {
		ModItem.registerModItems();
		ModBlock.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerAllScreenHandlers();
		GeckoLib.initialize();

		Registry.register(Registries.ITEM_GROUP, ModItemGroup.Mineproc_group, FabricItemGroup.builder().displayName(Text.translatable("mineproc.mod_group")).icon(() -> new ItemStack(ModItem.Memorycell10)).entries((context, entries) -> {
		}).build());

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ClientPlayerEntity client_player=MinecraftClient.getInstance().player;
			for (ServerWorld world : server.getWorlds()) {
				for (ServerPlayerEntity player : world.getPlayers()) {
					if (player!=null && client_player!=null && player.getUuid()== client_player.getUuid()){
						Server_player=player;
					}
				}
			}
		});

	}
}
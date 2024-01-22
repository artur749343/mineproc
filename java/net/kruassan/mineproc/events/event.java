package net.kruassan.mineproc.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kruassan.mineproc.block.entity.ComputerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

//public class event implements ServerTickEvents.StartTick {
//    @Override
//    public void onStartTick(MinecraftServer server) {
//        if(set!=null) {
//            ((ComputerEntity)server.getPlayerManager().getPlayerList().get(0).getWorld().getBlockEntity(set.pos)).setStack(set.index, set.item);
//        }
//    }
//}
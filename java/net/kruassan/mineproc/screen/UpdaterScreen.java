package net.kruassan.mineproc.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kruassan.mineproc.Mineproc;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UpdaterScreen extends HandledScreen<UpdaterScreenHandler> {
    private static final Identifier TEXTURE=new Identifier(Mineproc.MOD_ID, "textures/gui/updater_gui.png");
    public UpdaterScreen(UpdaterScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
        titleY=1000;
        playerInventoryTitleY=1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x=(width-backgroundWidth)/2;
        int y=(height-backgroundHeight)/2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()){
            context.drawTexture(TEXTURE, x+48, y+25, 177, 0, handler.getScaledProgress(),31);
            context.drawTexture(TEXTURE, x+125-handler.getScaledProgress(), y+25, 229-handler.getScaledProgress(), 0, handler.getScaledProgress(),31);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}

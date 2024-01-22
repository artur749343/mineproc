package net.kruassan.mineproc.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kruassan.mineproc.Mineproc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SenderScreen extends HandledScreen<SenderScreenHandler> {
    private static final Identifier TEXTURE=new Identifier(Mineproc.MOD_ID, "textures/gui/monitor_gui.png");
//    protected Console searchBox;
    public SenderScreen(SenderScreenHandler handler, PlayerInventory inventory, Text title){
        super(handler, inventory, title);
    }

    @Override
    protected void init(){
        super.init();
        titleY=1000;
        playerInventoryTitleY=1000;
//        this.searchBox=new Console(this.textRenderer, this.width/2-100, this.height/2-100, 200, 200, null);
//        this.addSelectableChild(this.searchBox);
//        this.setInitialFocus(this.searchBox);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x=(width-backgroundWidth)/2;
        int y=(height-backgroundHeight)/2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getWindow().calculateScaleFactor(0, minecraftClient.forcesUnicodeFont());
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
//        if (this.searchBox!=null) this.searchBox.render(context, mouseX, mouseY, delta);
    }


//    @Override
//    public boolean charTyped(char chr, int modifiers) {
//        return this.searchBox.charTyped(chr, modifiers);
//    }
//
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (keyCode!=256) return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
//        if (super.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        }
//        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
//    }
}
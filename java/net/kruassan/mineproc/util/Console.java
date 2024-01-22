package net.kruassan.mineproc.util;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.block.entity.ComputerEntity;
import net.kruassan.mineproc.block.entity.MonitorEntity;
import net.kruassan.mineproc.items.ModItem;
import net.kruassan.mineproc.screen.MonitorScreen;
import net.kruassan.mineproc.screen.MonitorScreenHandler;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Console extends ClickableWidget implements Drawable {
    private static final ButtonTextures TEXTURES = new ButtonTextures(new Identifier("widget/text_field"), new Identifier("widget/text_field_highlighted"));
    private static final String HORIZONTAL_CURSOR = "_";
    protected final TextRenderer textRenderer;
    public int min_size=2;
    public String[] All_Disk={"A", "B", "C", "D"};
    public MonitorEntity monitor=((MonitorScreenHandler)Mineproc.Server_player.currentScreenHandler).blockEntity;
    private int maxLength = 1024;
    private boolean drawsBackground = true;
    private boolean focusUnlocked = true;
    private boolean editable = true;
    private int firstCharacterIndex;
    public int start=0;
    private int selectionStart;
    private int selectionEnd;
    private int editableColor = 0xE0E0E0;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> changedListener;
    private Predicate<String> textPredicate = Objects::nonNull;
    private BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
    public String text=Joiner.on("\\").join(monitor.path)+">";
    public String text1;
    private int saves_index=monitor.saves.size();
//    private int file_pos=monitor.file_text.split("\n").length;
    @Nullable
    private Text placeholder;
    public final MonitorScreen screen;
    public boolean is_editor;

    private long lastSwitchFocusTime = Util.getMeasuringTimeMs();
    public Console(TextRenderer textRenderer, int x, int y, int width, int height, Text text, MonitorScreen screen) {
        this(textRenderer, x, y, width, height, null, text, screen);
    }

    public Console(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable Console copyFrom, Text text, MonitorScreen screen) {
        super(x, y, width, height, text);
        this.textRenderer = textRenderer;
        this.setCursor(this.min_size, Screen.hasShiftDown());
        if (copyFrom != null) {
            this.setText(copyFrom.getText());
        }
        this.screen=screen;
    }

    public void setChangedListener(Consumer<String> changedListener) {
        this.changedListener = changedListener;
    }

    public void setRenderTextProvider(BiFunction<String, Integer, OrderedText> renderTextProvider) {
        this.renderTextProvider = renderTextProvider;
    }

    @Override
    protected MutableText getNarrationMessage() {
        Text text = this.getMessage();
        return Text.translatable("gui.narrate.editBox", text, this.text);
    }

    public void setText(String text) {
        if (!this.textPredicate.test(text)) {
            return;
        }
        this.text = text.length() > this.maxLength ? text.substring(0, this.maxLength) : text;
        this.setCursorToEnd(false);
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(text);
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void setTextPredicate(Predicate<String> textPredicate) {
        this.textPredicate = textPredicate;
    }

    public void write(String text) {
        String string2;
        String string;
        int l;
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        int k = this.maxLength - this.text.length() - (i - j);
        if (k < (l = (string = SharedConstants.stripInvalidChars(text)).length())) {
            string = string.substring(0, k);
            l = k;
        }
        if (!this.textPredicate.test(string2 = new StringBuilder(this.text).replace(i, j, string).toString())) {
            return;
        }
        this.text = string2;
        this.setSelectionStart(i + l);
        this.setSelectionEnd(this.selectionStart);
        this.onChanged(this.text);
    }

    private void onChanged(String newText) {
        if (this.changedListener != null) {
            this.changedListener.accept(newText);
        }
    }

    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }
    }

    public void eraseWords(int wordOffset) {
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
    }

    public void eraseCharacters(int characterOffset) {
        int k;
        if (this.text.isEmpty()) {
            return;
        }
        if (this.selectionEnd != this.selectionStart) {
            this.write("");
            return;
        }
        int i = this.getCursorPosWithOffset(characterOffset);
        int j = Math.min(i, this.selectionStart);
        if (j == (k = Math.max(i, this.selectionStart))) {
            return;
        }
        String string = new StringBuilder(this.text).delete(j, k).toString();
        if (!this.textPredicate.test(string)) {
            return;
        }
        this.text = string;
        this.setCursor(j, false);
    }

    public int getWordSkipPosition(int wordOffset) {
        return this.getWordSkipPosition(wordOffset, this.getCursor());
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition) {
        return this.getWordSkipPosition(wordOffset, cursorPosition, true);
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
        int i = cursorPosition;
        boolean bl = wordOffset < 0;
        int j = Math.abs(wordOffset);
        for (int k = 0; k < j; ++k) {
            if (bl) {
                while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ' && this.min_size<i) {
                    --i;
                }
                while (i > this.min_size && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
                continue;
            }
            int l = this.text.length();
            if ((i = this.text.indexOf(32, i)) == -1) {
                i = l;
                continue;
            }
            while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
                ++i;
            }
        }
        return i;
    }

    public void moveCursor(int offset, boolean shiftKeyPressed) {
        this.setCursor(this.getCursorPosWithOffset(offset), shiftKeyPressed);
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(this.text, this.selectionStart, offset);
    }

    public void setCursor(int cursor, boolean shiftKeyPressed) {
        this.setSelectionStart(cursor);
        if (!shiftKeyPressed) {
            this.setSelectionEnd(this.selectionStart);
        }
        this.onChanged(this.text);
    }

    public void setSelectionStart(int cursor) {
        this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
        this.updateFirstCharacterIndex(this.selectionStart);
    }

    public void setCursorToStart(boolean shiftKeyPressed) {
        this.setCursor(this.min_size, shiftKeyPressed);
    }

    public void setCursorToEnd(boolean shiftKeyPressed) {
        this.setCursor(this.text.length(), shiftKeyPressed);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isActive()) {
            return false;
        }
        if (Screen.isSelectAll(keyCode)) {
            this.setCursorToEnd(false);
            this.setSelectionEnd(0);
            return true;
        }
        if (keyCode==79&&Screen.hasControlDown()&&is_editor){
            is_editor=false;
            byte[] bytes;
            try {
                Commands command = new Commands(monitor.comp_ent, this);
                bytes = command.assembling(monitor.file_text);
            } catch (Exception e) {
                output("syntax error");
                monitor.file_text="";
//            this.file_pos=0;
                this.text = Joiner.on("\\").join(monitor.path) + ">";
                this.min_size = this.text.length();
                this.setCursor(this.min_size, Screen.hasShiftDown());
                this.start = monitor.old_text.size() >= this.getHeight() / 10 ? monitor.old_text.size() - this.getHeight() / 10 + 1 : 0;
                return true;
            }
            byte[] data=this.monitor.comp_ent.getStack(get_memory()).getOrCreateNbt().getByteArray("mineproc.data");
            for (int i=0;i<data.length&&i<bytes.length;i++){
                data[i]=bytes[i];
            }
            this.monitor.comp_ent.getStack(get_memory()).getOrCreateNbt().putByteArray("mineproc.data", data);
            monitor.file_text="";
//            this.file_pos=0;
            this.text = Joiner.on("\\").join(monitor.path) + ">";
            this.min_size = this.text.length();
            this.setCursor(this.min_size, Screen.hasShiftDown());
            this.start = monitor.old_text.size() >= this.getHeight() / 10 ? monitor.old_text.size() - this.getHeight() / 10 + 1 : 0;
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            if (this.editable) {
                this.write(MinecraftClient.getInstance().keyboard.getClipboard());
            }
            return true;
        }
        if (Screen.isCut(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            if (this.editable) {
                this.write("");
            }
            return true;
        }
        switch (keyCode) {
            case 45: {
                if (Screen.hasControlDown()) {
                    if (monitor.size>0.2f) {
                        monitor.size-=monitor.size<1.5f?0.1f:0.5f;
                    }
                }
                return true;
            }
            case 61: {
                if (Screen.hasControlDown()) {
                    if (monitor.size<5f) {
                        monitor.size+=monitor.size<1.5f?0.1f:0.5f;
                    }
                }
                return true;
            }
            case 265: {
                if (this.is_editor){
//                    if (0 < this.file_pos) {
//                        this.file_pos--;
//                        this.text=monitor.file_text.split("\n")[this.file_pos];
//                        this.setCursor(this.text.length(), Screen.hasShiftDown());
//                    }
                } else {
                    if (this.saves_index + 1 == monitor.saves.size()) {
                        this.text1 = text;
                    }
                    if (0 < this.saves_index) {
                        this.saves_index--;
                        this.text = monitor.saves.get(this.saves_index);
                        this.setCursor(this.text.length(), Screen.hasShiftDown());
                    }
                }
                return true;
            }
            case 264: {
                if (this.is_editor){
//                    if (this.file_pos+1<monitor.file_text.split("\n").length){
//                        this.file_pos++;
//                        this.text=monitor.file_text.split("\n")[this.file_pos];
//                        this.setCursor(this.text.length(), Screen.hasShiftDown());
//                    }
                } else {
                    if (this.saves_index + 1 < monitor.saves.size()) {
                        this.saves_index++;
                        this.text = monitor.saves.get(this.saves_index);
                        this.setCursor(this.text.length(), Screen.hasShiftDown());
                    } else if (this.saves_index + 1 == monitor.saves.size()) {
                        this.saves_index++;
                        this.text = this.text1 != null ? this.text1 : Joiner.on("\\").join(monitor.path) + ">";
                        this.setCursor(this.text.length(), Screen.hasShiftDown());
                    }
                }
                return true;
            }
            case 263: {
                if (this.min_size<this.getCursor()){
                    if (Screen.hasControlDown()) {
                        this.setCursor(this.getWordSkipPosition(-1), Screen.hasShiftDown());
                    } else {
                        this.moveCursor(-1, Screen.hasShiftDown());
                    }
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.setCursor(this.getWordSkipPosition(1), Screen.hasShiftDown());
                } else {
                    this.moveCursor(1, Screen.hasShiftDown());
                }
                return true;
            }
            case 257: {
                if (editable) {
                    if (this.is_editor){
                        monitor.old_text.add(this.text);
                        monitor.file_text=monitor.file_text+this.text+"\n";
//                        this.file_pos=monitor.file_text.split("\n").length;
                        this.text="";
                        this.min_size=0;
                        this.setCursor(this.min_size, Screen.hasShiftDown());
                        this.start = monitor.old_text.size() >= this.getHeight() / 10 ? monitor.old_text.size() - this.getHeight() / 10 + 1 : 0;
                    } else {
                        monitor.old_text.add(this.text);
                        monitor.saves.add(this.text);
                        this.saves_index = monitor.saves.size();
                        Commands command = new Commands(monitor.comp_ent, this);
                        Object[] results = command.Command_executor(this.text.substring(this.min_size));
                        if (results != null) {
                            int wait_arg = (int) results[0];
                            Function function = (Function) results[1];
                            Object[] args = (Object[]) results[2];
                            new Thread(() -> {
                                for (int i = 0; i < 100; i++) {
                                    try {
                                        Thread.sleep(wait_arg);
                                        this.text = i + "%";
                                        this.setCursor(this.text.length(), Screen.hasShiftDown());
                                        this.editable = false;
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                this.text = Joiner.on("\\").join(monitor.path) + ">";
                                this.min_size = this.text.length();
                                this.setCursor(this.min_size, Screen.hasShiftDown());
                                this.start = monitor.old_text.size() >= this.getHeight() / 10 ? monitor.old_text.size() - this.getHeight() / 10 + 1 : 0;
                                this.editable = true;
                                function.apply(args);
                            }).start();
                        }
                        this.text = Joiner.on("\\").join(monitor.path) + ">";
                        this.min_size = this.text.length();
                        this.setCursor(this.min_size, Screen.hasShiftDown());
                        this.start = monitor.old_text.size() >= this.getHeight() / 10 ? monitor.old_text.size() - this.getHeight() / 10 + 1 : 0;
                        return true;
                    }
                }
            }
            case 259: {
                if (this.editable&&this.min_size<this.getCursor()) {
                    this.erase(-1);
                }
                return true;
            }
            case 261: {
                if (this.editable) {
                    this.erase(1);
                }
                return true;
            }
            case 268: {
                this.setCursorToStart(Screen.hasShiftDown());
                return true;
            }
            case 269: {
                this.setCursorToEnd(Screen.hasShiftDown());
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isActive()) {
            return false;
        }
        if (SharedConstants.isValidChar(chr)) {
            if (this.editable) {
                this.write(Character.toString(chr));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int i = MathHelper.floor(mouseX) - this.getX();
        if (this.drawsBackground) {
            i -= 4;
        }
        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        this.setCursor(Math.max(this.textRenderer.trimToWidth(string, i).length() + this.firstCharacterIndex, this.min_size), Screen.hasShiftDown());
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    public int drawText(DrawContext context, TextRenderer textRenderer, OrderedText text, float x, float y, int color) {
        MatrixStack matrix=new MatrixStack();
        matrix.scale(monitor.size,monitor.size,monitor.size);
        int i = textRenderer.draw(text, x/monitor.size, y/monitor.size, color, true, matrix.peek().getPositionMatrix(), (VertexConsumerProvider)context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        context.draw();
        return i;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) {
            return;
        } else if (!monitor.comp_ent.getStack(5).isOf(ModItem.Video_card)||monitor.comp_ent==null||!monitor.comp_ent.turn_on){
            context.drawCenteredTextWithShadow(textRenderer, "NO SIGNAL", screen.width/2, screen.height/2, editableColor);
            return;
        }
        if (this.drawsBackground()) {
            Identifier identifier = TEXTURES.get(this.isNarratable(), this.isFocused());
            context.drawGuiTexture(identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        int i = this.editableColor;
        int j = this.selectionStart - this.firstCharacterIndex;
        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        boolean bl = j >= 0 && j <= string.length();
        boolean bl2 = this.isFocused() && (Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L && bl;
        int k = this.drawsBackground ? this.getX() + 4 : this.getX();
        int l = this.getY();
        int m = k;
        int n = MathHelper.clamp(this.selectionEnd - this.firstCharacterIndex, 0, string.length());
        for (int itr=0; itr < monitor.old_text.size()-this.start; itr++){
            if (this.getHeight()<=itr*10*monitor.size) break;
            drawText(context, this.textRenderer, this.renderTextProvider.apply(monitor.old_text.get(itr+this.start), this.firstCharacterIndex), m, l+itr*10*monitor.size+3, i);
        }
        if (this.getHeight()-10*monitor.size<(monitor.old_text.size()-this.start)*10*monitor.size) return;
        l+=(this.monitor.old_text.size()-this.start)*10*monitor.size+3;
        if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, j) : string;
            m = drawText(context, this.textRenderer, this.renderTextProvider.apply(string2, this.firstCharacterIndex), m, l, i);
        }
        boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
        int o = m;
        if (!bl) {
            o = j > 0 ? k + this.width : k;
        } else if (bl3) {
            --o;
            --m;
        }
        if (!string.isEmpty() && bl && j < string.length()) {
            drawText(context, this.textRenderer, this.renderTextProvider.apply(string.substring(j), this.selectionStart), m*monitor.size, l, i);
        }
        if (this.placeholder != null && string.isEmpty() && !this.isFocused()) {
            drawText(context, this.textRenderer, this.placeholder.asOrderedText(), m*monitor.size, l, i);
        }
        if (!bl3 && this.suggestion != null) {
            drawText(context, this.textRenderer, this.renderTextProvider.apply(this.suggestion, 0), (o-1)*monitor.size, l, -8355712);
        }
        if (bl2) {
            if (bl3) {
                context.fill(RenderLayer.getGuiOverlay(), (int)(o*monitor.size), l - 1, (int)((o + 1)*monitor.size), l + 1 + this.textRenderer.fontHeight, -3092272);
            } else {
                drawText(context, this.textRenderer, this.renderTextProvider.apply(HORIZONTAL_CURSOR, 0), o*monitor.size, l, i);
            }
        }
        if (n != j) {
            int p = k + this.textRenderer.getWidth(string.substring(0, n));
            this.drawSelectionHighlight(context, o, l - 1, p - 1, l + 1 + this.textRenderer.fontHeight);
        }
    }

    private void drawSelectionHighlight(DrawContext context, int x1, int y1, int x2, int y2) {
        int i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        if (x2 > this.getX() + this.width) {
            x2 = this.getX() + this.width;
        }
        if (x1 > this.getX() + this.width) {
            x1 = this.getX() + this.width;
        }
        context.fill(RenderLayer.getGuiTextHighlight(), x1, y1, x2, y2, -16776961);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
            this.onChanged(this.text);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    public boolean drawsBackground() {
        return this.drawsBackground;
    }

    public void setDrawsBackground(boolean drawsBackground) {
        this.drawsBackground = drawsBackground;
    }

    public void setEditableColor(int editableColor) {
        this.editableColor = editableColor;
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (!this.visible || !this.editable) {
            return null;
        }
        return super.getNavigationPath(navigation);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= (double)this.getX() && mouseX < (double)(this.getX() + this.width) && mouseY >= (double)this.getY() && mouseY < (double)(this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean focused) {
        if (!this.focusUnlocked && !focused) {
            return;
        }
        super.setFocused(focused);
        if (focused) {
            this.lastSwitchFocusTime = Util.getMeasuringTimeMs();
        }
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getInnerWidth() {
        return this.drawsBackground() ? this.width - 8 : this.width;
    }

    public void setSelectionEnd(int index) {
        this.selectionEnd = MathHelper.clamp(index, 0, this.text.length());
        this.updateFirstCharacterIndex(this.selectionEnd);
    }

    private void updateFirstCharacterIndex(int cursor) {
        if (this.textRenderer == null) {
            return;
        }
        this.firstCharacterIndex = Math.min(this.firstCharacterIndex, this.text.length());
        int i = this.getInnerWidth();
        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), i);
        int j = string.length() + this.firstCharacterIndex;
        if (cursor == this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.textRenderer.trimToWidth(this.text, i, true).length();
        }
        if (cursor > j) {
            this.firstCharacterIndex += cursor - j;
        } else if (cursor <= this.firstCharacterIndex) {
            this.firstCharacterIndex -= this.firstCharacterIndex - cursor;
        }
        this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, this.text.length());
    }

    public void setFocusUnlocked(boolean focusUnlocked) {
        this.focusUnlocked = focusUnlocked;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public int getCharacterX(int index) {
        if (index > this.text.length()) {
            return this.getX();
        }
        return this.getX() + this.textRenderer.getWidth(this.text.substring(0, index));
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
    }

    public void setPlaceholder(Text placeholder) {
        this.placeholder = placeholder;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (0<=this.start-verticalAmount&&this.start-verticalAmount<=monitor.old_text.size()) this.start-=(int)verticalAmount;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    public void output(Object x){
        if (x!=null){
            monitor.old_text.add(String.valueOf(x));
        }
    }

    public void output(List<Object> x){
        if (x!=null && 0<x.size()) {
            monitor.old_text.addAll(x.stream().map(String::valueOf).toList());
        }
    }

    public void write_data(int start, int end, byte[] value, ComputerEntity computer){
        ItemStack item=computer.getStack(get_memory());
        NbtCompound nbt=item.getOrCreateNbt();
        byte[] result=nbt.getByteArray("mineproc.data");
        for (int i=start;i<=end;i++) {
            result[i] = value[i-start];
        }
        nbt.putByteArray("mineproc.data", result);
        item.setNbt(nbt);
        computer.setStack(get_memory(), item);
    }

    public void read_data(int start, int end, ComputerEntity computer){
        StringBuilder res= new StringBuilder();
        for (int i=start;i<=end;i++) {
            res.append(String.valueOf(computer.getStack(get_memory()).getOrCreateNbt().getByteArray("mineproc.data")[i])+", ");
        }
        output(res.toString());
    }

    public int get_memory() {
        for (int i = 0; i < 4; i++) {
            if (Objects.equals(monitor.path[0], this.All_Disk[i])) {
                return i;
            }
        }
        return 0;
    }
}


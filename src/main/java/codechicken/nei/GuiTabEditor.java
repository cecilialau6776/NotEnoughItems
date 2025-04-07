package codechicken.nei;

import codechicken.lib.render.CCRenderState;
import codechicken.nei.AutoFocusWidget.INEIAutoFocusSearchEnable;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import codechicken.nei.drawable.DrawableBuilder;
import codechicken.nei.drawable.DrawableResource;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IGuiClientSide;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import static codechicken.nei.NEIClientUtils.translate;

import java.util.List;

public class GuiTabEditor extends GuiContainer
        implements IGuiClientSide, INEIAutoFocusSearchEnable, INEIGuiHandler {

    public GuiScreen prevGui;

    // Background image calculations
    private static final int BG_TOP_HEIGHT = 6;
    private static final int BG_MIDDLE_HEIGHT = 154;
    private static final int BG_BOTTOM_HEIGHT = 6;

    private static final int BG_TOP_Y = 0;
    private static final int BG_MIDDLE_Y = BG_TOP_Y + BG_TOP_HEIGHT;
    private static final int BG_BOTTOM_Y = BG_MIDDLE_Y + BG_MIDDLE_HEIGHT;

    // Button sizes
    private static final int borderPadding = 6;
    private static final int buttonHeight = 20;
    private static final int slotPadding = 1;

    // Images
    final DrawableResource bgTop = new DrawableBuilder("nei:textures/gui/recipebg.png", 0, BG_TOP_Y, 176,
            BG_TOP_HEIGHT)
            .build();
    final DrawableResource bgMiddle = new DrawableBuilder(
            "nei:textures/gui/recipebg.png",
            0,
            BG_MIDDLE_Y,
            176,
            BG_MIDDLE_HEIGHT).build();
    final DrawableResource bgBottom = new DrawableBuilder(
            "nei:textures/gui/recipebg.png",
            0,
            BG_BOTTOM_Y,
            176,
            BG_BOTTOM_HEIGHT).build();
    final DrawableResource tabSlot = new DrawableBuilder(
            "nei:textures/slot.png",
            0,
            0,
            18,
            18).setTextureSize(18, 18).build();

    public ContainerTabEditor slotContainer;

    public TextField titleField;

    public GuiTabEditor(GuiScreen prevgui) {
        super(new ContainerTabEditor());

        slotContainer = (ContainerTabEditor) inventorySlots;
        final int padding = borderPadding + slotPadding;
        slotContainer.addSlot(null, guiLeft + padding, guiTop + padding);
        this.prevGui = prevgui;
    }

    public void initGui() {
        super.initGui();

        // Buttons
        final int buttonWidth = (xSize - borderPadding * 3) / 2;
        final int buttonY = ySize + guiTop - borderPadding - buttonHeight;
        final int cancelButtonX = guiLeft + borderPadding;
        final int confirmButtonX = guiLeft + xSize - borderPadding - buttonWidth;

        buttonList.add(
                new GuiNEIButton(0, cancelButtonX, buttonY, buttonWidth, buttonHeight,
                        translate("bookmark.tab.cancel")) {
                    @Override
                    public void mouseReleased(int mouseX, int mouseY) {
                        mc.displayGuiScreen(prevGui);
                        NEICPH.sendRequestContainer();
                    }
                });
        buttonList.add(
                new GuiNEIButton(0, confirmButtonX, buttonY, buttonWidth, buttonHeight,
                        translate("bookmark.tab.confirm")) {
                    @Override
                    public void mouseReleased(int mouseX, int mouseY) {
                        // todo save data
                        mc.displayGuiScreen(prevGui);
                        NEICPH.sendRequestContainer();
                    }
                });

        // Text fields
        titleField = new TextField("title") {
            {
                this.h = 20;
                this.w = xSize - borderPadding * 2 - 18 - 3;
                this.x = borderPadding * 2 + 18 - 3;
                this.y = borderPadding - 1;
            }

            @Override
            public void onTextChange(String oldText) {
            }
        };
    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        return currentVisibility;
    }

    @Override
    public void keyTyped(char c, int i) {
        if (i == Keyboard.KEY_ESCAPE) { // esc
            mc.displayGuiScreen(this.prevGui);
            NEICPH.sendRequestContainer();
            return;
        }

        if (i == Keyboard.KEY_TAB) {
            titleField.setFocus(true);
        }

        // TODO add tab functionality
    }

    // @Override
    // protected void mouseClicked(int mousex, int mousey, int button) {
    // final GuiButton cancelButton = buttonList.get(0);
    // final GuiButton confirmButton = buttonList.get(1);
    // if (titleField.contains(mousex - guiLeft, mousey - guiTop)) {
    // titleField.handleClick(mousex - guiLeft, mousey - guiTop, button);
    // }
    // }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);

        CCRenderState.changeTexture("nei:textures/gui/recipebg.png");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 176, 166);

        // Draw the slot
        tabSlot.draw(guiLeft + borderPadding, guiTop + borderPadding);

    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiContainerManager.enable2DRender();
        titleField.draw(mouseX - guiLeft, mouseY - guiTop);
    }

    public Dimension getWidgetSize() {
        return new Dimension(xSize, ySize);
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return null;
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        // TODO drag/drop item/icon?
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        // TODO Auto-generated method stub
        return false;
    }
}

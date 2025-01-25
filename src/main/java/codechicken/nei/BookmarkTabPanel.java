package codechicken.nei;

import static codechicken.lib.gui.GuiDraw.drawRect;
import static codechicken.lib.gui.GuiDraw.drawStringC;
import static codechicken.nei.NEIClientUtils.getGuiContainer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.BookmarkPanel.BookmarkGrid;
import codechicken.nei.ItemPanel.ItemPanelSlot;
import codechicken.nei.recipe.StackInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BookmarkTabPanel extends PanelWidget {

    protected static class TabPanelGrid extends ItemsGrid {
        @Override
        public int getNumPages() {
            if (perPage > 0) {
                return (int) Math.ceil((float) (realItems.size() + 1) / (float) perPage);
            }

            return 0;
        }
    }

    protected Label newTabLabel;

    // public BookmarkTabPanel() {
    // }

    protected String getNamespaceLabelText(boolean shortFormat) {
        String activePage = String.valueOf(getPage());

        return shortFormat ? activePage : getLabelText();
    }

    public void addTab() {
        ItemStack is = new ItemStack(Item.getItemById(1));
        addTab(is);
    }

    public void addTab(ItemStack stack) {
        List<BookmarkGrid> namespaces = ItemPanels.bookmarkPanel.namespaces;
        namespaces.add(new BookmarkGrid());
        ItemPanels.bookmarkPanel.setNamespace(namespaces.size() - 1);
        grid.realItems.add(stack);
        grid.onItemsChanged();
    }

    @Override
    public void init() {
        super.init();
        grid = new TabPanelGrid();
        newTabLabel = new Label("+", false) {
            @Override
            public void draw(int mousex, int mousey) {
                drawStringC(text, x, y, w, h, colour, false);
            }

            @Override
            public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
                if (!contains(mx, my))
                    return tooltip;

                tooltip.add("New Tab");
                return tooltip;
            }

            @Override
            public boolean handleClick(int mx, int my, int button) {
                addTab();
                return true;
            }
        };

        // ItemStack is = new ItemStack(Item.getItemById(1));
        // addTab(is);
    }

    @Override
    public void mouseUp(int mousex, int mousey, int button) {
        setTabIcon(mousex, mousey);

        // switch tab
        ItemPanelSlot hoverSlot = getSlotMouseOver(mousex, mousey);
        if (hoverSlot != null && hoverSlot.slotIndex == mouseDownSlot) {
            int tabIndex = hoverSlot.slotIndex;
            // System.out.println("ti: " + tabIndex);
            ItemPanels.bookmarkPanel.setNamespace(tabIndex);
            // return true;
        }
    }

    protected boolean setTabIcon(int mousex, int mousey) {
        ItemPanelSlot hoverSlot = getSlotMouseOver(mousex, mousey);
        ItemStack draggedItem;
        if (ItemPanels.itemPanel.draggedStack != null) {
            draggedItem = ItemPanels.itemPanel.draggedStack;
        } else {
            draggedItem = ItemPanels.bookmarkPanel.draggedStack;
        }

        if (draggedItem != null) {
            final ItemStack draggedStack = draggedItem.copy();
            final NBTTagCompound nbTag = StackInfo.itemStackToNBT(draggedStack);
            final ItemStack normalized = StackInfo.loadFromNBT(nbTag, 0);
            if (hoverSlot != null) {
                grid.getItems().set(hoverSlot.slotIndex, normalized);
                ItemPanels.itemPanel.draggedStack = null;
                ItemPanels.bookmarkPanel.draggedStack = null;
                return true;
            } else if (newTabLabel.contains(mousex, mousey)) {
                addTab(normalized);
                ItemPanels.itemPanel.draggedStack = null;
                ItemPanels.bookmarkPanel.draggedStack = null;
                return true;
            }
        }

        return false;
    }

    @Override
    public String getLabelText() {
        return String.format("%d/%d", getPage(), Math.max(1, getNumPages()));
    }

    @Override
    protected String getPositioningSettingName() {
        return "world.panels.bookmark.tabs";
    }

    @Override
    public int getMarginLeft(GuiContainer gui) {
        return PADDING;
    }

    @Override
    public int getMarginTop(GuiContainer gui) {
        return PADDING;
    }

    @Override
    public int getWidth(GuiContainer gui) {
        return gui.width - (gui.xSize + gui.width) / 2 - PADDING * 2;
    }

    @Override
    public int getHeight(GuiContainer gui) {
        return gui.height - getMarginTop(gui) - PADDING;
    }

    @Override
    public void resize(GuiContainer gui) {
        final Rectangle4i margin = new Rectangle4i(
                getMarginLeft(gui),
                getMarginTop(gui),
                getWidth(gui),
                getHeight(gui));

        final int minWidth = 5 * ItemsGrid.SLOT_SIZE;
        final int minHeight = 9 * ItemsGrid.SLOT_SIZE;

        final String settingName = getPositioningSettingName();
        int paddingLeft = (int) Math
                .ceil(margin.w * NEIClientConfig.getSetting(settingName + ".left").getIntValue() / 100000.0);
        int paddingTop = (int) Math
                .ceil(margin.h * NEIClientConfig.getSetting(settingName + ".top").getIntValue() / 100000.0);
        int paddingRight = (int) Math
                .ceil(margin.w * NEIClientConfig.getSetting(settingName + ".right").getIntValue() / 100000.0);
        int paddingBottom = (int) Math
                .ceil(margin.h * NEIClientConfig.getSetting(settingName + ".bottom").getIntValue() / 100000.0);

        int deltaHeight = Math.min(0, margin.h - paddingTop - paddingBottom - minHeight) / 2;

        paddingLeft = Math.min(paddingLeft, Math.max(0, margin.w - paddingRight - minWidth));
        paddingRight = Math.min(paddingRight, Math.max(0, margin.w - paddingLeft - minWidth));
        paddingTop = Math.min(margin.h - minHeight, Math.max(0, paddingTop + deltaHeight));
        paddingBottom = Math.min(margin.h - paddingTop - minHeight, Math.max(0, paddingBottom - deltaHeight));

        final int header = resizeHeader(gui);
        final int footer = resizeFooter(gui);

        grid.setGridSize(x, y + header, w, h - header - footer);
        grid.refresh(gui);
    }

    @Override
    protected int resizeHeader(GuiContainer gui) {
        return 0;
    }

    @Override
    protected int resizeFooter(GuiContainer gui) {
        final int BUTTON_SIZE = 16;

        BookmarkPanel bp = LayoutManager.bookmarkPanel;

        final ButtonCycled button = LayoutManager.bookmarksButton;
        final int leftBorder = bp.y + bp.h > button.y ? button.x + button.w + 2 : bp.x;
        final int rightBorder = bp.x + bp.w;
        final int center = leftBorder + Math.max(0, (rightBorder - leftBorder) / 2);
        int labelWidth;

        pagePrev.h = pageNext.h = BUTTON_SIZE;
        pagePrev.w = pageNext.w = BUTTON_SIZE;
        pagePrev.y = pageNext.y = bp.y + bp.h - BUTTON_SIZE;

        if (rightBorder - leftBorder >= 70) {
            labelWidth = 36;
            pageLabel.text = getNamespaceLabelText(false);
        } else {
            labelWidth = 18;
            pageLabel.text = getNamespaceLabelText(true);
        }

        pageLabel.y = pagePrev.y + 5;
        pageLabel.x = center;

        pagePrev.x = center - labelWidth / 2 - PADDING - pagePrev.w;
        pageNext.x = center + labelWidth / 2 + PADDING;

        bp.pullBookmarkedItems.h = BUTTON_SIZE;
        bp.pullBookmarkedItems.w = BUTTON_SIZE;
        bp.pullBookmarkedItems.y = bp.y + bp.h - BUTTON_SIZE;
        bp.pullBookmarkedItems.x = center + 2 * labelWidth / 2 + 2;

        return 0;
        // return BUTTON_SIZE + PADDING;
    }

    @Override
    public void setVisible() {
        LayoutManager.addWidget(pagePrev);
        LayoutManager.addWidget(pageNext);
        LayoutManager.addWidget(pageLabel);
        if (isNewTabLabelVisible())
            LayoutManager.addWidget(newTabLabel);
        grid.setVisible();
    }

    @Override
    protected ItemStack getDraggedStackWithQuantity(int mouseDownSlot) {
        final ItemStack stack = grid.getItem(mouseDownSlot);

        if (stack == null) {
            return null;
        }

        return NEIServerUtils.copyStack(stack, 1);
    }

    private void drawSplittingArea(int x, int y, int width, int height, int color) {
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glLineWidth(2F);
        GL11.glLineStipple(2, (short) 0x00FF);

        GL11.glBegin(GL11.GL_LINE_LOOP);

        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x + width, y);
        GL11.glVertex2i(x + width, y + height);
        GL11.glVertex2i(x, y + height);

        GL11.glEnd();

        GL11.glLineStipple(1, (short) 0xFFFF);
        GL11.glLineWidth(1F);
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glPopMatrix();
    }

    private boolean isNewTabLabelVisible() {
        return !(grid.getNumPages() > 0 && grid.getPage() != grid.getNumPages());
    }

    private void drawNewTabLabel(int mousex, int mousey) {
        if (!isNewTabLabelVisible()) {
            return;
        }
        final Rectangle4i labelRect = grid.getSlotRect(grid.size() - grid.page * grid.getPerPage());
        if (labelRect.contains(mousex, mousey)) {
            // draw hover highlight
            drawRect(labelRect.x, labelRect.y, labelRect.w, labelRect.h, 0xee555555);
        }

        newTabLabel.x = labelRect.x;
        newTabLabel.y = labelRect.y;
        newTabLabel.w = labelRect.w;
        newTabLabel.h = labelRect.h;
    }

    private void drawActiveTabBackground() {
        // check if the tab is visible
        BookmarkPanel bp = LayoutManager.bookmarkPanel;
        final int perTabPage = this.grid.getPerPage();
        // System.out.printf("%d, %d, %d, %d\n", (bp.getPage() - 1),
        // bp.activeNamespaceIndex / perTabPage,
        // bp.activeNamespaceIndex,
        // bp.namespaces.size());
        if ((bp.getPage() - 1) != bp.activeNamespaceIndex / perTabPage)
            return;
        // get index of the active tab on the page
        final int index = bp.activeNamespaceIndex - ((bp.activeNamespaceIndex / perTabPage) * perTabPage);

        final Rectangle4i labelRect = grid.getSlotRect(index);
        drawRect(labelRect.x, labelRect.y, labelRect.w, labelRect.h, 0xee888888);
    }

    @Override
    public void draw(int mousex, int mousey) {
        // draw background for active tab
        drawActiveTabBackground();

        super.draw(mousex, mousey);
        // draw border/background
        if (NEIClientConfig.getIntSetting("inventory.history.splittingMode") == 0) {
            drawRect(x, y, w, h, NEIClientConfig.getSetting("inventory.history.historyColor").getHexValue());
        } else {
            drawSplittingArea(x, y, w, h, NEIClientConfig.getSetting("inventory.history.historyColor").getHexValue());
        }

        drawNewTabLabel(mousex, mousey);
    }
}

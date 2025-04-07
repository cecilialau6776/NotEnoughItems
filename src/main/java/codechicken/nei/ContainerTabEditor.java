package codechicken.nei;

import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTabEditor extends Container {

    private class TabInventory implements IInventory {

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slotIn) {
            if (slotIn != 0) {
                return null;
            }
            return (ItemStack) inventoryItemStacks.get(0);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int index) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            if (index != 0) {
                return;
            }
            inventoryItemStacks.set(0, stack);
        }

        @Override
        public String getInventoryName() {
            return null;
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory() {
        }

        @Override
        public void closeInventory() {
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return true;
        }
    }

    private final TabInventory tabInventory = new TabInventory();

    public ContainerTabEditor() {
        super();
    }

    public void addSlot(ItemStack stack, int x, int y) {
        int slot = inventorySlots.size();
        addSlotToContainer(new Slot(tabInventory, slot, x, y));
        // tabInventory.setInventorySlotContents(slot, stack);
    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void putStackInSlot(int par1, ItemStack par2ItemStack) {
        // Server side updates do nothing!
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        return null; // no shift clicking (scrolling...)
    }
}

package com.github.ipecter.rtustudio.varmor.protocol.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import kr.rtuserver.framework.bukkit.api.platform.MinecraftVersion;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayClientWindowClick extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.WINDOW_CLICK;

    public WrapperPlayClientWindowClick() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientWindowClick(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Window ID.
     * <p>
     * Notes: the id of the window which was clicked. 0 for player inventory.
     *
     * @return The current Window ID
     */
    public int getWindowId() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Window ID.
     *
     * @param value - new value.
     */
    public void setWindowId(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve Slot.
     * <p>
     * Notes: the clicked slot. See below.
     *
     * @return The current Slot
     */
    public int getSlot() {
        if (MinecraftVersion.isSupport("1.21.5"))
            return handle.getShorts().read(0); // I think Its bug from ProtocolLib dev build
        return handle.getIntegers().read(2);
    }

    /**
     * Set Slot.
     *
     * @param value - new value.
     */
    public void setSlot(int value) {
        if (MinecraftVersion.isSupport("1.21.5")) {
            handle.getShorts().write(0, (short) value);
        } else handle.getIntegers().write(2, value);
    }

    /**
     * Retrieve Button.
     * <p>
     * Notes: the button used in the click. See below.
     *
     * @return The current Button
     */
    public int getButton() {
        return handle.getIntegers().read(3);
    }

    /**
     * Set Button.
     *
     * @param value - new value.
     */
    public void setButton(int value) {
        handle.getIntegers().write(3, value);
    }

    /**
     * Retrieve Action number.
     * <p>
     * Notes: a unique number for the action, used for transaction handling (See
     * the Transaction packet).
     *
     * @return The current Action number
     */
    public short getActionNumber() {
        return handle.getShorts().read(1);
    }

    /**
     * Set Action number.
     *
     * @param value - new value.
     */
    public void setActionNumber(short value) {
        handle.getShorts().write(1, value);
    }

    /**
     * Retrieve Clicked item.
     *
     * @return The current Clicked item
     */
    public ItemStack getClickedItem() {
        return handle.getItemModifier().read(0);
    }

    /**
     * Set Clicked item.
     *
     * @param value - new value.
     */
    public void setClickedItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }

    public InventoryClickType getShift() {
        return handle.getEnumModifier(InventoryClickType.class, 4).read(0);
    }

    public void setShift(InventoryClickType value) {
        handle.getEnumModifier(InventoryClickType.class, 4).write(0, value);
    }

    public enum InventoryClickType {
        PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL
    }
}
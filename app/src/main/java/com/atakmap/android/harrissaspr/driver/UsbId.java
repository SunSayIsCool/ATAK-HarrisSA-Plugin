package com.atakmap.android.harrissaspr.driver;

/**
 * Registry of USB vendor/product ID constants.
 *
 * Culled from various sources; see
 * <a href="http://www.linux-usb.org/usb.ids">usb.ids</a> for one listing.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public final class UsbId {

    public static final int VENDOR_L3Harris = 0x19a5;
    public static final int RF7800S = 0x0012;
    public static final int RNDIS = 0x0004;
    public static final int RF7800V = 0x0402;
    private UsbId() {
        throw new IllegalAccessError("Non-instantiable class");
    }

}

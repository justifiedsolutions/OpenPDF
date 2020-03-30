package com.justifiedsolutions.openpdf.pdf;

/**
 * Represents common page sizes.
 */
public enum PageSize {
    /**
     * US Letter
     */
    LETTER(new Rectangle(612, 792)),
    /**
     * US Legal
     */
    LEGAL(new Rectangle(612, 1008)),
    /**
     * A0
     */
    A0(new Rectangle(2384, 3370)),
    /**
     * A1
     */
    A1(new Rectangle(1684, 2384)),
    /**
     * A2
     */
    A2(new Rectangle(1191, 1684)),
    /**
     * A3
     */
    A3(new Rectangle(842, 1191)),
    /**
     * A4
     */
    A4(new Rectangle(595, 842)),
    /**
     * A5
     */
    A5(new Rectangle(420, 595)),
    /**
     * A6
     */
    A6(new Rectangle(297, 420)),
    /**
     * A7
     */
    A7(new Rectangle(210, 297)),
    /**
     * A8
     */
    A8(new Rectangle(148, 210));

    private final Rectangle size;

    PageSize(Rectangle size) {
        this.size = size;
    }

    /**
     * Gets the size of the page
     *
     * @return the page size
     */
    Rectangle size() {
        return this.size;
    }
}

/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text.pdf;

/** Interface for customizing the split character.
 *
 * @author Paulo Soares (psoares@consiste.pt)
 */

public interface SplitCharacter {
    
    /**
     * Returns <CODE>true</CODE> if the character can split a line. The splitting implementation
     * is free to look ahead or look behind characters to make a decision.
     * <p>
     * The default implementation is:
     * <p>
     * <pre>{@code
     * public boolean isSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
     *    char c;
     *    if (ck == null)
     *        c = cc[current];
     *    else
     *        c = (char) ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
     *    if (c <= ' ' || c == '-') {
     *        return true;
     *    }
     *    if (c < 0x2e80)
     *        return false;
     *    return ((c >= 0x2e80 && c < 0xd7a0)
     *    || (c >= 0xf900 && c < 0xfb00)
     *    || (c >= 0xfe30 && c < 0xfe50)
     *    || (c >= 0xff61 && c < 0xffa0));
     * }
     * }</pre>
     * @param current the pointer to the character in <CODE>cc</CODE>
     * @param cc an array of characters at least <CODE>end</CODE> sized
     * @param ck an array of <CODE>PdfChunk</CODE>. It may be <CODE>null</CODE>
     * or shorter than <CODE>end</CODE>. If <CODE>null</CODE> no conversion takes place.
     * If shorter than <CODE>end</CODE> the last element is used
     * @return <CODE>true</CODE> if the character(s) can split a line
     */
    boolean isSplitCharacter(int current, char[] cc, PdfChunk[] ck);
}

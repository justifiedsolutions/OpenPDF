/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.VerticalAlignment;

public class AlignmentConverter {

    public static int convertHorizontalAlignment(HorizontalAlignment alignment) {
        int result = Element.ALIGN_UNDEFINED;
        if (alignment != null) {
            switch (alignment) {
                case LEFT:
                    result = Element.ALIGN_LEFT;
                    break;
                case CENTER:
                    result = Element.ALIGN_CENTER;
                    break;
                case RIGHT:
                    result = Element.ALIGN_RIGHT;
                    break;
                case JUSTIFIED:
                    result = Element.ALIGN_JUSTIFIED;
                    break;
            }
        }
        return result;
    }

    public static int convertVerticalAlignment(VerticalAlignment alignment) {
        int result = Element.ALIGN_UNDEFINED;
        if (alignment != null) {
            switch (alignment) {
                case TOP:
                    result = Element.ALIGN_TOP;
                    break;
                case MIDDLE:
                    result = Element.ALIGN_MIDDLE;
                    break;
                case BOTTOM:
                    result = Element.ALIGN_BOTTOM;
                    break;
                case BASELINE:
                    result = Element.ALIGN_BASELINE;
                    break;
            }
        }
        return result;
    }
}

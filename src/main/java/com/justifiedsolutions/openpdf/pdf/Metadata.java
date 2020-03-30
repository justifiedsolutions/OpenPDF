package com.justifiedsolutions.openpdf.pdf;

/**
 * Enumerates the supported metadata fields in a PDF document.
 */
public enum Metadata {
    /**
     * Title of the document
     */
    TITLE,
    /**
     * Subject of the document
     */
    SUBJECT,
    /**
     * Keywords associated with the document
     */
    KEYWORDS,
    /**
     * Author of the document
     */
    AUTHOR,
    /**
     * The creation date of the document Date values used in a PDF shall conform to a standard date
     * format, which closely follows that of the international standard ASN.1 (Abstract Syntax
     * Notation One), defined in ISO/IEC 8824. A date shall be a text string of the form ( D :
     * YYYYMMDDHHmmSSOHH ' mm ) where:
     * <pre>
     * YYYY shall be the year
     * MM shall be the month (01–12) DD shall be the day (01–31) HH shall be the hour (00–23)
     * mm shall be the minute (00–59) SS shall be the second (00–59)
     * O shall be the relationship of local time to Universal Time (UT), and shall be denoted by one of the characters PLUS SIGN (U+002B) (+), HYPHEN-MINUS (U+002D) (-), or LATIN CAPITAL LETTER Z (U+005A) (Z) (see below)
     * HH followed by APOSTROPHE (U+0027) (') shall be the absolute value of the offset from UT in hours (00–23)
     * mm shall be the absolute value of the offset from UT in minutes (00–59)
     * </pre>
     * The prefix D: shall be present, the year field (YYYY) shall be present and all other fields
     * may be present but only if all of their preceding fields are also present. The APOSTROPHE
     * following the hour offset field (HH) shall only be present if the HH field is present. The
     * minute offset field (mm) shall only be present if the APOSTROPHE following the hour offset
     * field (HH) is present. The default values for MM and DD shall be both 01; all other numerical
     * fields shall default to zero values. A PLUS SIGN as the value of the O field signifies that
     * local time is later than UT, a HYPHEN-MINUS signifies that local time is earlier than UT, and
     * the LATIN CAPITAL LETTER Z signifies that local time is equal to UT. If no UT information is
     * specified, the relationship of the specified time to UT shall be considered to be GMT.
     * Regardless of whether the time zone is specified, the rest of the date shall be specified in
     * local time. EXAMPLE For example, December 23, 1998, at 7:52 PM, U.S. Pacific Standard Time,
     * is represented by the string <code>D:199812231952-08'00</code>
     */
    CREATE_DATE,
    /**
     * The product that created the document
     */
    CREATOR
}

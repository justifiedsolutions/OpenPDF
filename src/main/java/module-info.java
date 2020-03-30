/**
 * This module allows you to model a PDF document and then write it out. Start by looking at
 * creating a {@link com.justifiedsolutions.openpdf.pdf.Document} by utilizing the {@link
 * com.justifiedsolutions.openpdf.pdf.DocumentFactory}. Then proceed by adding {@link
 * com.justifiedsolutions.openpdf.pdf.Metadata} and {@link com.justifiedsolutions.openpdf.pdf.content.Content}
 * to the Document. Finally, the Document can be written to an {@link java.io.OutputStream}.
 */
module com.justifiedsolutions.openpdf {
    requires java.desktop;

    requires static org.bouncycastle.provider;
    requires static org.bouncycastle.pkix;

    exports com.justifiedsolutions.openpdf.pdf;
    exports com.justifiedsolutions.openpdf.pdf.font;
    exports com.justifiedsolutions.openpdf.pdf.content;
}
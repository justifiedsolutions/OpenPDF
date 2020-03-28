package com.justifiedsolutions.openpdf.text.pdf.fonts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.Font;
import com.justifiedsolutions.openpdf.text.FontFactory;
import com.justifiedsolutions.openpdf.text.RectangleReadOnly;
import com.justifiedsolutions.openpdf.text.pdf.DefaultFontMapper;
import com.justifiedsolutions.openpdf.text.pdf.PdfContentByte;
import com.justifiedsolutions.openpdf.text.pdf.PdfLiteral;
import com.justifiedsolutions.openpdf.text.pdf.PdfName;
import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

/**
 * {@link Font Font}-related test cases.
 *
 * @author noavarice
 * @since 1.2.7
 */
class FontTest {

    private static final Map<Integer, Predicate<Font>> STYLES_TO_TEST_METHOD = new HashMap<>() {{
        put(Font.NORMAL,
                f -> !f.isBold() && !f.isItalic() && !f.isStrikethru() && !f.isUnderlined());
        put(Font.BOLD, Font::isBold);
        put(Font.ITALIC, Font::isItalic);
        put(Font.UNDERLINE, Font::isUnderlined);
        put(Font.STRIKETHRU, Font::isStrikethru);
        put(Font.BOLDITALIC, f -> f.isBold() && f.isItalic());
    }};

    private static final String FONT_NAME_WITHOUT_STYLES = "non-existing-font";

    private static final String FONT_NAME_WITH_STYLES = "Courier";

    private static final float DEFAULT_FONT_SIZE = 16.0f;

    @Test
    void testStyleSettingByValue() {
        FontFactory.registerDirectories();
        for (final int style : STYLES_TO_TEST_METHOD
                .keySet()) { // TODO: complement tests after adding enum with font styles
            final Font font = FontFactory
                    .getFont(FONT_NAME_WITHOUT_STYLES, DEFAULT_FONT_SIZE, style);
            assertEquals(font.getStyle(), style);
        }
    }

    @Test
    void testStyleSettingByPredicate() {
        for (final int style : STYLES_TO_TEST_METHOD.keySet()) {
            final Font font = FontFactory
                    .getFont(FONT_NAME_WITHOUT_STYLES, DEFAULT_FONT_SIZE, style);
            final Predicate<Font> p = STYLES_TO_TEST_METHOD.get(style);
            assertTrue(p.test(font));
        }
    }

    @Test
    void testFontStyleOfStyledFont() {
        for (final int style : STYLES_TO_TEST_METHOD.keySet()) {
            final Font font = FontFactory.getFont(FONT_NAME_WITH_STYLES, DEFAULT_FONT_SIZE, style);

            // For the font Courier, there is no Courier-Underline or Courier-Strikethru font available.
            if (style == Font.UNDERLINE || style == Font.STRIKETHRU) {
                assertEquals(font.getStyle(), style);
            } else {
                assertEquals(Font.NORMAL, font.getStyle());
            }
        }
    }

    @Test
    void testBoldSimulationAndStrokeWidth() throws Exception {
        FileOutputStream outputStream = new FileOutputStream("target/resultSimulatedBold.pdf");
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        // set hardcoded documentID to be able to compare the resulting document with the reference
        writer.getInfo().put(PdfName.FILEID, new PdfLiteral("[<1><2>]"));
        document.open();
        document.setPageSize(new RectangleReadOnly(200, 70));
        document.newPage();
        PdfContentByte cb = writer.getDirectContentUnder();

        // Overwrite Helvetica bold with the standard Helvetica, to force simulated bold mode
        DefaultFontMapper fontMapper = new DefaultFontMapper();
        java.awt.Font font = new java.awt.Font("Helvetica", java.awt.Font.BOLD, 8);
        DefaultFontMapper.BaseFontParameters p = new DefaultFontMapper.BaseFontParameters(
                "Helvetica");
        fontMapper.putName("Helvetica", p);
        Graphics2D graphics2D =
                cb.createGraphics(document.getPageSize().getWidth(),
                        document.getPageSize().getHeight(), fontMapper);
        // setting the color is important to pass line 484 of PdfGraphics2D
        graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(2f));
        graphics2D.drawLine(10, 10, 10, 50);
        graphics2D.setFont(font);
        graphics2D.drawString("Simulated Bold String", 20, 30);
        graphics2D.setStroke(new BasicStroke(2f));
        graphics2D.drawLine(120, 10, 120, 50);

        graphics2D.dispose();
        document.close();
        outputStream.close();

        File original = new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("SimulatedBoldAndStrokeWidth.pdf"))
                        .getFile());
        File current = new File("target/resultSimulatedBold.pdf");
        assertTrue(FileUtils.contentEquals(original, current));
    }

}

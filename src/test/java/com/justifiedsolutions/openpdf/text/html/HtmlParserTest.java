package com.justifiedsolutions.openpdf.text.html;

import com.justifiedsolutions.openpdf.text.html.HtmlParser;
import java.io.StringReader;

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.TextElementArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HtmlParserTest {

    /**
     * Bug fix scenario: a table with a space throws a {@link TextElementArray}
     * class cast Exception. A table without spaces is parsed correctly.
     */
    @Test
    void testParse_tableWithNoSpaces() {
        Document doc1 = new Document();
        doc1.open();
        HtmlParser.parse(doc1, new StringReader("<table><tr><td>test</td></tr></table>")); // succeeds
        assertNotNull(doc1);
    }

    /**
     * Bug fix scenario: a table with a space throws a {@link TextElementArray}
     * class cast Exception.
     */
    @Test
    void testParse_tableWithSpaces() {
        Document doc1 = new Document();
        doc1.open();
        HtmlParser.parse(doc1, new StringReader("<table> <tr><td>test</td></tr></table>")); // was throwin exception
        assertNotNull(doc1);
    }

}

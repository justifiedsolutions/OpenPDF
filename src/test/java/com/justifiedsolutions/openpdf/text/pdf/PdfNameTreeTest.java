package com.justifiedsolutions.openpdf.text.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.justifiedsolutions.openpdf.text.pdf.PdfArray;
import com.justifiedsolutions.openpdf.text.pdf.PdfBoolean;
import com.justifiedsolutions.openpdf.text.pdf.PdfDictionary;
import com.justifiedsolutions.openpdf.text.pdf.PdfName;
import com.justifiedsolutions.openpdf.text.pdf.PdfNameTree;
import com.justifiedsolutions.openpdf.text.pdf.PdfObject;
import com.justifiedsolutions.openpdf.text.pdf.PdfString;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class PdfNameTreeTest {

  @Test
  void shouldReadTree() {

    PdfDictionary pdfDictionary = new PdfDictionary(PdfName.PDF);
    final PdfBoolean pdfBoolean = new PdfBoolean(true);
    final String keyName = "test";
    pdfDictionary.put(PdfName.NAMES, new PdfArray(Arrays.asList(new PdfString(keyName), pdfBoolean)));

    final HashMap<String, PdfObject> tree = PdfNameTree.readTree(pdfDictionary);
    assertEquals(1, tree.size());
    assertEquals(pdfBoolean, tree.get(keyName));

  }
}

package com.justifiedsolutions.openpdf.text.pdf.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.Paragraph;
import com.justifiedsolutions.openpdf.text.pdf.PdfReader;
import com.justifiedsolutions.openpdf.text.pdf.PdfStamper;
import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;

public class ProducerTest {

    private static final String PRODUCER = "Producer";

    @Test
    public void changeProducerLineTest() throws IOException {
        String expected = "New Producer.";

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.addProducer(expected);
        document.open();
        document.add(new Paragraph("Hello World!"));
        document.close();

        byte[] pdfBytes = baos.toByteArray();
        baos.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));

        Map<String, String> infoDictionary = reader.getInfo();
        String actual = infoDictionary.get(PRODUCER);

        Assertions.assertEquals(expected, actual);

        reader.close();
    }
}
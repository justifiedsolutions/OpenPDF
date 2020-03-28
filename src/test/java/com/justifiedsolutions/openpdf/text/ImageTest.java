package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.justifiedsolutions.openpdf.text.Image;
import org.junit.jupiter.api.Test;

class ImageTest {

  @Test
  void shouldReturnImageWithUrlForUrl() throws Exception {
    final Image image = Image.getInstance(ClassLoader.getSystemResource("H.gif"));
    assertNotNull(image.getUrl());
  }

  @Test
  void shouldReturnImageWithUrlForPath() throws Exception {
    final Image image = Image.getInstance(ClassLoader.getSystemResource("H.gif").getPath());
    assertNotNull(image.getUrl());
  }
}

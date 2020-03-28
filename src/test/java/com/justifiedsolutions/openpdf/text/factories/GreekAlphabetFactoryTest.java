package com.justifiedsolutions.openpdf.text.factories;

import static com.justifiedsolutions.openpdf.text.factories.GreekAlphabetFactory.getString;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GreekAlphabetFactoryTest {

  @Test
  void shouldGetGreekString() {
    for (int i = 1; i < 1000; i++) {
      assertNotNull(getString(i));
    }
  }

}

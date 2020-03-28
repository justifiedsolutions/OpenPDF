package com.justifiedsolutions.openpdf.text.factories;

import static com.justifiedsolutions.openpdf.text.factories.RomanAlphabetFactory.getString;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RomanAlphabetFactoryTest {

  @Test
  void shouldGetRomanString() {
    for (int i = 1; i < 32000; i++) {
      assertNotNull(getString(i));
    }
  }

}

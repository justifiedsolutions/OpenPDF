module com.justifiedsolutions.openpdf {
    requires java.desktop;

    requires static org.bouncycastle.provider;
    requires static org.bouncycastle.pkix;

    exports com.justifiedsolutions.openpdf.text;
    exports com.justifiedsolutions.openpdf.text.pdf;
}
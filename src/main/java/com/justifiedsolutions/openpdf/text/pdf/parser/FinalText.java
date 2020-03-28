package com.justifiedsolutions.openpdf.text.pdf.parser;

import com.justifiedsolutions.openpdf.text.pdf.PdfReader;

/**
 * @author dgd
 */
public class FinalText implements TextAssemblyBuffer {

    private final String content;

    public FinalText(String content) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     *
     * @see TextAssemblyBuffer#getText()
     */

    @Override
    public String getText() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see TextAssemblyBuffer#accumulate(TextAssembler,
     * String)
     */
    @Override
    public void accumulate(TextAssembler p, String contextName) {
        p.process(this, contextName);
    }

    /**
     * {@inheritDoc}
     *
     * @see TextAssemblyBuffer#assemble(TextAssembler)
     */
    @Override
    public void assemble(TextAssembler p) {
        p.renderText(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see TextAssemblyBuffer#getFinalText(PdfReader, int,
     * TextAssembler, boolean)
     */
    @Override
    public FinalText getFinalText(PdfReader reader, int page, TextAssembler assembler,
            boolean useMarkup) {
        return this;
    }

    @Override
    public String toString() {
        return "[FinalText: [" + getText() + "] d]";
    }
}

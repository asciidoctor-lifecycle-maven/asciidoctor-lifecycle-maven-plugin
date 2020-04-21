package com.coutemeier.maven.plugins.asciidoctor.lifecycle;


import org.junit.Assert;
import org.junit.Test;

public class FormatterTestCase {
    @Test
    public void formatTest() {
        final String message = "Asciidoctor %s: (%s, %s) = %s";

        final Object[] arguments = {
            "Primer argumento",
            true,
            false,
            "Activado"
        };
        final String formatted = String.format( message, arguments );
        Assert.assertEquals( "Asciidoctor Primer argumento: (true, false) = Activado", formatted );
    }
}

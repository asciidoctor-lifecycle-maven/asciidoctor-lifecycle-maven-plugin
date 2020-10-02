package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution( ExecutionMode.CONCURRENT)
public class ZipUtilTestCase {

    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void unzipTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/ZipUtilTest.zip" );
        final File outputDir = new File( "target/ZipUtilTestCase/unzipTest" );
        final File readme = new File( outputDir, "README.txt" );
        final File readme2 = new File( outputDir, "directory/README2.txt" );

        ZipUtil.unzip( zipFile, outputDir );
        assertTrue(  readme.exists() && readme.isFile() && readme2.exists() && readme2.isFile() );
    }

    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void unzipExpectedSlipVulnerabilityTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/ZipUtilTest-Slip.zip" );
        final File outputDir = new File( "target/ZipUtilTestCase/unzipExpectedSlipVulnerabilityTest" );

        assertThrows( IOException.class, () -> {
            ZipUtil.unzip( zipFile, outputDir );
        }, "Expected slip vulnerability detection throws IOException" );
    }


    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void unzipFileNotExistsTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/FileNotExists.zip" );
        final File outputDir = new File( "target/ZipUtilTestCase/unzipFileNotExistsTest" );

        assertThrows( IOException.class, () -> {
            ZipUtil.unzip( zipFile, outputDir );
        }, "File '" + zipFile.getAbsolutePath() + "' should not exists" );

    }
}

package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ZipUtil;

public class ZipUtilTestCase {

    @Test
    public void unzipTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/ZipUtilTest.zip" );
        final File outputDir = new File( "target/ZipUtilTest" );
        final File readme = new File( outputDir, "README.txt" );
        final File readme2 = new File( outputDir, "directory/README2.txt" );

        ZipUtil.unzip( zipFile, outputDir );
        Assert.assertTrue(  readme.exists() && readme.isFile() && readme2.exists() && readme2.isFile() );
    }

    @Test( expected = IOException.class )
    public void unzipSlipTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/ZipUtilTest-Slip.zip" );
        final File outputDir = new File( "target/ZipUtilTest" );

        ZipUtil.unzip( zipFile, outputDir );
        Assert.fail( "Expected slip vulnerability detection throws IOException " );
    }


    @Test( expected = IOException.class )
    public void unzip_fileNotExistsTest()
    throws IOException {
        final File zipFile = new File( "src/test/resources/FileNotExists.zip" );
        final File outputDir = new File( "target/FileNotExists" );

        ZipUtil.unzip( zipFile, outputDir );
        Assert.fail( "File '" + zipFile.getAbsolutePath() + "' should not exists" );

    }

}

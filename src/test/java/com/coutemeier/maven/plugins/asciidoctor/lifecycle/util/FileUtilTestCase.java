package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution( ExecutionMode.CONCURRENT)
public class FileUtilTestCase {
    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void walkRegularFilesTest()
    throws IOException {
        final Path copyDir = new File( FileUtilTestCase.class.getClassLoader().getResource( "copy-dir" ).getFile() ).toPath();
        final Path emptyDir = new File( "empty-dir" ).toPath();
        final Path childIsEmptyDir = new File( "child-is-empty-dir" ).toPath();

        final Stream<Path> paths = FileUtil.walkRegularFiles( copyDir );

        boolean containsEmptyDir =
            paths
                .filter(
                    item->  item.getFileName().equals( emptyDir )
                            || item.getFileName().equals( childIsEmptyDir )
                )
                .count() == 0;
        assertTrue( containsEmptyDir );
    }

    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void deleteDirTest()
    throws Exception {
        Path path = Files.createTempDirectory( "directory-test" + File.pathSeparator );
        Files.createDirectories( path );
        if ( Files.exists( path ) ) {
            FileUtil.deleteDir( path );
            assertFalse( Files.exists( path ) );
        } else {
            fail( "Unable to create temporary path: " + path.toString() );
        }
    }
}

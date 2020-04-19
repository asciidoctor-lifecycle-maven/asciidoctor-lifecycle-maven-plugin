package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public final class FileUtil {
    public static void copyDir( final Path source, final Path target )
    throws IOException {
        walkRegularFiles( source )
            .forEach( file -> {
                try {
                    final Path resolved = target.resolve( source.relativize( file ) );
                    Files.createDirectories( resolved.getParent() );
                    Files.copy( file, resolved, StandardCopyOption.REPLACE_EXISTING );
                } catch ( final IOException e) {
                    e.printStackTrace();
                }
        });
    }

    public static Stream<Path> walkRegularFiles( final Path source )
    throws IOException {
        return Files.walk( source ).filter( Files::isRegularFile );
    }
}

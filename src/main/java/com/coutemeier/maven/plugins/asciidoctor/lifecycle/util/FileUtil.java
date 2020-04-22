package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException ;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() {
    }

    public static void copyDir( final Path source, final Path target )
    throws IOException {
        try {
            walkRegularFiles( source )
                .forEach( file -> {
                    try {
                        final Path resolved = target.resolve( source.relativize( file ) );
                        Files.createDirectories( resolved.getParent() );
                        Files.copy( file, resolved, StandardCopyOption.REPLACE_EXISTING );
                    } catch ( final IOException cause) {
                        throw new UncheckedIOException( cause );
                    }
            });
        } catch ( final UncheckedIOException cause ) {
            throw new IOException( cause );
        }
    }

    public static Stream<Path> walkRegularFiles( final Path source )
    throws IOException {
        return Files.walk( source ).filter( Files::isRegularFile );
    }

    public static void deleteDir( final Path pathToBeDeleted )
    throws IOException {
        Files
            .walk( pathToBeDeleted )
            .sorted( Comparator.reverseOrder() )
            .map( Path::toFile )
            .forEach( File::delete );
    }
}

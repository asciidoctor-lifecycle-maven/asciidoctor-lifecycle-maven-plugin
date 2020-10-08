package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Zip utils
 *
 * @author rrialq
 * @since 1.0
 */
public final class ZipUtil {

    private ZipUtil() {
    }

    /**
     * Decompress a zip file in a specific path
     * <p>
     * Decompresión is made in two steps, to avoid
     *
     * @param zipFile
     *            the file pointing to the zip
     * @param outputDir
     *            the file pointing to the output directory
     * @throws IOException
     *             wwhen Zip Slip vulnerability or other IO problem
     *
     * @since 1.0
     */
    public static void unzip(final File zipFile, final File outputDir) throws IOException {
        try ( final ZipInputStream zis = new ZipInputStream( new FileInputStream(zipFile) ) ) {
            ZipEntry zipEntry = zis.getNextEntry();
            while ( zipEntry != null ) {
                if (!zipEntry.isDirectory()) {
                    final File newFile = resolveZipEntry(outputDir, zipEntry);
                    newFile.getParentFile().mkdirs();
                    Files.copy( zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING );
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
            zis.close();
        }
    }

    /**
     * Resolve the file where the zipEntry it will be write, checking for SLIP vulnerability
     *
     * @param outputDir
     *            the base path for entry
     * @param zipEntry
     *            the entry to resolve path
     * @return the file where the zipEnrty contents will be written
     * @throws IOException
     *             when zip slip vulnerability
     *
     * @since 1.0
     */
    private static File resolveZipEntry(final File outputDir, final ZipEntry zipEntry) throws IOException {
        final File file = new File( outputDir, zipEntry.getName( ));
        final String destDirPath = outputDir.getCanonicalPath() + File.separator;
        final String destFilePath = file.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath)) {
            throw new IOException( "Zip Slip vulnerability: " + zipEntry.getName() );
        }
        return file;
    }
}

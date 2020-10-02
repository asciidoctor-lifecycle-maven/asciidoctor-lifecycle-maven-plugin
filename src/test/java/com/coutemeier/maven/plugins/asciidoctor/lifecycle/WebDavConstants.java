package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

public final class WebDavConstants {
    public static final int PUBLISH_TO_WEBDAV_PORTBASE = 40000;

    public static final int PUBLISH_WITHOUT_CREDENTIALS_PORT = PUBLISH_TO_WEBDAV_PORTBASE + 1;
    public static final int PUBLISH_WITH_CREDENTIALS_PORT = PUBLISH_TO_WEBDAV_PORTBASE + 2;
    public static final int PUBLISH_WITH_WRONG_CREDENTIALS_PORT = PUBLISH_TO_WEBDAV_PORTBASE + 3;
    public static final int PUBLISH_TO_FOLDER_PORT = PUBLISH_TO_WEBDAV_PORTBASE + 4;
}

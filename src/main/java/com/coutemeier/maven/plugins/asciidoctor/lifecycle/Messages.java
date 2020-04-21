package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

/**
 * Messages to show in logs.
 */
public final class Messages {
    protected static String SKIPPING_PLUGIN_EXECUTION = "Skipping plugin execution";

    protected static String SET_PROPERTY = "Setting property %s=%s";

    protected static final String PREPARE_SOURCES_SOURCE_DIRECTORY = "Prepare sources: preparing sources in %s";

    protected static final String PUBLISH_CONNECT_WITH_PROXY = "Publish: connect with proxyInfo";
    protected static final String PUBLISH_CONNECT_WITH_AUTHENTICATION =
        "Publish: connect with authenticationInfo and without proxyInfo";
    protected static final String PUBLISH_CONNECT_NOAUTHENTICATION_AND_NOPROXY =
        "Publish: connect without authenticationInfo and without proxyInfo";
    protected static final String PUBLISH_REPOSITORY_AUTHENTICATION_INFO = "Publish: authenticationInfo with id '%s': %s";
    protected static final String PUBLISH_PUBLISHING_TO = "Publish: Publishing to '%s/%s', using credentials from server id '%s'.";
    protected static final String PUBLISH_PUSHING_DIRECTORY = "Publish: Pushing %s";

    protected static final String THEME_COPY_RESOURCES = "Theme %s: Copying resources...";
    protected static final String THEME_CREATE_PROPERTY = "Theme %s: SetProperty: %s = %s";
    protected static final String THEME_DOWNLOADING = "Theme %s: Downloading...";
    protected static final String THEME_UNPACKING = "Theme %s: Unpacking in %s...";

    protected static final String PREPARE_SOURCES_ERROR_PREPARING = "Error preparing asciidoctor sources";
    protected static final String PUBLISH_ERROR_DISCONNECTING_WAGON = "Publish: Error disconnecting wagon - ignored";
    protected static final String PUBLISH_ERROR_MISSING_GENERATED_FILES =
        "Publish: The Asciidoctor generated files does not exists. Please, run build first.";
    protected static final String PUBLISH_ERROR_PUBLISHING_TO_SERVER = "Publish: Error publishing generated files to server";
    protected static final String PUBLISH_ERROR_UNABLE_TO_CONFIGURE_WAGON = "Publish: Unable to configure Wagon: '%s'";
    protected static final String THEME_ERROR_DOWNLOADING = "Theme: Error downloading theme";
    protected static final String THEME_ERROR_UNPACKING = "Theme: Error unpacking theme";

    private Messages() {
    }
}

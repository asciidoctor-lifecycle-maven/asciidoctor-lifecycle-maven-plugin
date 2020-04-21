package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Wagon utils
 *
 * @author rrialq
 * @since 1.0
 */
public final class WagonUtil {

    private WagonUtil() {
    }

    /**
     * Configure the Wagon with the information from serverConfigurationMap ( which comes from settings.xml )
     *
     * @param wagon
     * @param repositoryId
     * @param settings
     * @param container
     * @param log
     * @throws TransferFailedException
     * @todo Remove when {@link WagonManager#getWagon(Repository) is available}. It's available in Maven 2.0.5.
     */
    public static void configureWagon(
        final Wagon wagon,
        final String repositoryId,
        final Settings settings,
        final PlexusContainer container,
        final Log log )
    throws TransferFailedException {
        log.debug( " configureWagon " );

        for ( final Server server : settings.getServers() ) {
            final String id = server.getId();
            if ( log.isDebugEnabled() ) {
                log.debug( "configureWagon server " + id );
            }

            if ( id != null && id.equals( repositoryId ) && ( server.getConfiguration() != null ) ) {
                final PlexusConfiguration plexusConf = new XmlPlexusConfiguration( (Xpp3Dom) server.getConfiguration() );

                ComponentConfigurator componentConfigurator = null;
                try {
                    componentConfigurator = (ComponentConfigurator) container.lookup( ComponentConfigurator.ROLE, "basic" );
                    componentConfigurator.configureComponent( wagon, plexusConf, container.getContainerRealm() );
                } catch ( final ComponentLookupException e ) {
                    throw new TransferFailedException(
                        "While configuring wagon for \'" + repositoryId + "\': Unable to lookup wagon configurator."
                        + " Wagon configuration cannot be applied.", e );
                } catch ( ComponentConfigurationException e ) {
                    throw new TransferFailedException( "While configuring wagon for \'" + repositoryId
                                                       + "\': Unable to apply wagon configuration.", e );
                } finally {
                    if ( componentConfigurator != null ) {
                        try {
                            container.release( componentConfigurator );
                        } catch ( ComponentLifecycleException e ) {
                            log.error( "Problem releasing configurator - ignoring: " + e.getMessage() );
                        }
                    }
                }
            }
        }
    }


    // Supports only Maven >= 3.0
    public static ProxyInfo getProxyInfo( final MavenSession mavenSession, final Log log, final Repository repository, final SettingsDecrypter settingsDecrypter ) {
        String protocol = repository.getProtocol();
        String url = repository.getUrl();

        if ( log.isDebugEnabled() ) {
            log.debug( "repository protocol " + protocol );
        }

        final String originalProtocol = protocol;

        if ( StringUtils.equalsIgnoreCase( "dav", protocol ) && url.startsWith( "dav:" ) ) {
            url = url.substring( 4 );
            if ( url.startsWith( "http" ) ) {
                try {
                    final URL publishRepository = new URL( url );
                    protocol = publishRepository.getProtocol();
                    if ( log.isDebugEnabled() ) {
                        log.debug( "found dav protocol so transform to real transport protocol " + protocol );
                    }
                } catch ( MalformedURLException e ) {
                    log.warn( "fail to build URL with " + url );
                }
            }
        } else if ( log.isDebugEnabled() ){
            log.debug( "getProxy 'protocol': " + protocol );
        }

        if ( mavenSession != null && protocol != null ) {
            final MavenExecutionRequest request = mavenSession.getRequest();

            if ( request != null ) {
                final List<Proxy> proxies = request.getProxies();

                if ( proxies != null ) {
                    for ( Proxy proxy : proxies ) {
                        if ( proxy.isActive() && ( protocol.equalsIgnoreCase( proxy.getProtocol() )
                            || originalProtocol.equalsIgnoreCase( proxy.getProtocol() ) ) ) {
                            final SettingsDecryptionResult result = settingsDecrypter.decrypt( new DefaultSettingsDecryptionRequest( proxy ) );
                            proxy = result.getProxy();

                            final ProxyInfo proxyInfo = new ProxyInfo();
                            proxyInfo.setHost( proxy.getHost() );
                            // so hackish for wagon the protocol is https for site dav:
                            // dav:https://dav.codehaus.org/mojo/
                            proxyInfo.setType( protocol );
                            proxyInfo.setPort( proxy.getPort() );
                            proxyInfo.setNonProxyHosts( proxy.getNonProxyHosts() );
                            proxyInfo.setUserName( proxy.getUsername() );
                            proxyInfo.setPassword( proxy.getPassword() );

                            log.debug( "found proxyInfo "
                                            + ( "host:port " + proxyInfo.getHost() + ":" + proxyInfo.getPort()
                                            + ", " + proxyInfo.getUserName() ) );

                            return proxyInfo;
                        }
                    }
                }
            }
        }
        if ( log.isDebugEnabled() ) {
            log.debug( "getProxy 'protocol': " + protocol + " no ProxyInfo found" );
        }
        return null;
    }


    /**
     * Get a text with all supported protocols for wagon
     *
     * @param plexusContainer
     *            the container for looking the {@link Wagon} implementation classes availables
     * @param logger
     *            for showing an error in case of exception
     * @return an empty text in case of error or a text with comma separated protocols
     */
    public static String getSupportedProtocols( final PlexusContainer container, Log log ) {
        try {
            final Set<String> protocols = container.lookupMap( Wagon.class ).keySet();

            return StringUtils.join( protocols.iterator(), ", " );
        } catch ( ComponentLookupException cause ) {
            // in the unexpected case there is a problem when instantiating a wagon provider
            log.error( cause );
        }
        return "";
    }
}

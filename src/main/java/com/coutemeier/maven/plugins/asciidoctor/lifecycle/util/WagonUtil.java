package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.Wagon;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

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
	 * Get a text with all supported protocols for wagon
	 *
	 * @param plexusContainer the container for looking the {@link Wagon} implementation classes availables
	 * @param logger for showing an error in case of exception
	 * @return an empty text in case of error or a text with comma separated protocols
	 */
	public static String getSupportedProtocols( final PlexusContainer plexusContainer, final Log logger ) {
		try {
			return String.join( ",", plexusContainer.lookupMap( Wagon.class ).keySet() );

		} catch ( final ComponentLookupException cause ) {
			logger.error( cause );
		}
		return "";
	}
}

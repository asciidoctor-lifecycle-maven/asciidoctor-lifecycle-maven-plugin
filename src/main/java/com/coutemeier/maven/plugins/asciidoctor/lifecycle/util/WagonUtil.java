package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.Wagon;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public final class WagonUtil {

	private WagonUtil() {
		// TODO Auto-generated constructor stub
	}

	public static String getSupportedProtocols( final PlexusContainer plexusContainer, final Log logger ) {
		try {
			return String.join( ",", plexusContainer.lookupMap( Wagon.class ).keySet() );

		} catch ( final ComponentLookupException cause ) {
			logger.error( cause );
		}
		return "";
	}
}

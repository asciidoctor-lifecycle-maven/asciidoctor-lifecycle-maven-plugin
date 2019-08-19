package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
/**
 * Maven Settings utils
 *
 * @author rrialq
 * @since 1.0
 */
public final class SettingsUtil {

	private SettingsUtil() {
	}

	/**
	 * Find the {@link AuthenticationInfo} for the serverId if available
	 *
	 * @param serverId the id of the server to look for credentials
	 * @param settings the settings in settings.xml
	 * @param settingsDecrypter a decrypter implementation of passwords stored in settings.xml
	 * @return an object with the authentication info for that server or null if none founded
	 */
	public static AuthenticationInfo getAuthenticationInfo( final String serverId, final Settings settings, final SettingsDecrypter settingsDecrypter ) {
		AuthenticationInfo authenticationInfo = null;
		// Here some values may be encrypted
		Server server = settings.getServer( serverId );
		if ( server != null ) {
			final SettingsDecryptionResult result = settingsDecrypter.decrypt( new DefaultSettingsDecryptionRequest( server ) );
			// Now the encrypted values are in plain text...
		    server = result.getServer();

		    authenticationInfo = new AuthenticationInfo();
		    authenticationInfo.setUserName( server.getUsername() );
		    authenticationInfo.setPassword( server.getPassword() );
		    authenticationInfo.setPrivateKey( server.getPrivateKey() );
		    authenticationInfo.setPassphrase( server.getPassphrase() );
		    return authenticationInfo;
		}
		return null;
	}
}

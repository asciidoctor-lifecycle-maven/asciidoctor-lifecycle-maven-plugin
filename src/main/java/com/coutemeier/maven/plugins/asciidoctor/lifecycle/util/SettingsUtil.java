package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

public final class SettingsUtil {

	private SettingsUtil() {
		// TODO Auto-generated constructor stub
	}

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

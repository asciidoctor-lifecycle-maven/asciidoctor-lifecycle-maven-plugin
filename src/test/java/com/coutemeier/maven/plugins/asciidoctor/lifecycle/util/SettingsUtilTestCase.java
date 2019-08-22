package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class SettingsUtilTestCase {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Settings settings;

    @Mock
    private SettingsDecrypter settingsDecrypter;

    @Mock
    private SettingsDecryptionResult settingsDecryptionResult;

    @Before
    public void setUp() {
        final Server server = new Server();
        server.setId( "snapshots" );
        server.setUsername(  "username" );
        server.setPassword( "password" );
        Mockito.when(  settings.getServer( anyString() ) ).thenReturn(  server );
        Mockito.when(  settingsDecrypter.decrypt( any() ) ).thenReturn( settingsDecryptionResult );
        Mockito.when(  settingsDecryptionResult.getServer() ).thenReturn( server );
    }

    @Test
    public void getAuthenticationInfoWhenServerIdIsNotNullTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertNotNull( authenticationInfo );
    }

    @Test
    public void getAuthenticationInfoWhenServerIdIsNullTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( null, settings, settingsDecrypter );
        Assert.assertNull( authenticationInfo );
    }

    @Test
    public void getAuthenticationInfoAndCheckPasswordTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertEquals( "password", authenticationInfo.getPassword() );
    }

    @Test
    public void getAuthenticationInfoAndCheckUserNameTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertEquals( "username", authenticationInfo.getUserName() );
    }
}

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

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.SettingsUtil;


public class SettingsUtilTestCase {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Settings settings;

    @Mock
    SettingsDecrypter settingsDecrypter;

    @Mock
    SettingsDecryptionResult settingsDecryptionResult;

    @Before
    public void setup() {
        final Server server = new Server();
        server.setId( "snapshots" );
        server.setUsername(  "username" );
        server.setPassword( "password" );
        Mockito.when(  settings.getServer( anyString() ) ).thenReturn(  server );
        Mockito.when(  settingsDecrypter.decrypt( any() ) ).thenReturn( settingsDecryptionResult );
        Mockito.when(  settingsDecryptionResult.getServer() ).thenReturn( server );
    }

    public SettingsUtilTestCase() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void getAuthenticationInfo_isNotNullTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertNotNull( authenticationInfo );
    }

    @Test
    public void getAuthenticationInfo_isNullTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( null, settings, settingsDecrypter );
        Assert.assertNull( authenticationInfo );
    }

    @Test
    public void getAuthenticationInfo_passwordTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertEquals( "password", authenticationInfo.getPassword() );
    }

    @Test
    public void getAuthenticationInfo_userNameTest() {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo( "snapshots", settings, settingsDecrypter );

        Assert.assertEquals( "username", authenticationInfo.getUserName() );
    }
}

package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.wagon.Wagon;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class WagonUtilTestCase {
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PlexusContainer container;

    @Mock
    private Map<String, Wagon> supportedWagonProtocols;

    @Mock
    private Log log;

    @Test
    public void getSupportedProtocolsTest()
    throws Exception {
        Set<String> supportedProtocols = Stream.of( "http", "https", "file" ).collect( Collectors.toSet() );
        when( supportedWagonProtocols.keySet() ).thenReturn( supportedProtocols );
        when( container.lookupMap( Wagon.class ) ).thenReturn( supportedWagonProtocols );

        final String protocols = WagonUtil.getSupportedProtocols( container, null );
        Assert.assertEquals( "file,http,https", protocols );
    }

    @Test()
    public void getSupportedProtocolsThrowsComponentLookupExceptionTest()
    throws Exception {
        doNothing().when( log ).error( isA( ComponentLookupException.class ) );
        when( container.lookupMap( Wagon.class ) ).thenThrow( ComponentLookupException.class );

        final String protocols = WagonUtil.getSupportedProtocols( container, log );
        Assert.assertEquals( "", protocols );
    }
}

package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Execution( ExecutionMode.CONCURRENT)
public class WagonUtilTestCase {
    @Mock
    private PlexusContainer container;

    @Mock
    private Map<String, Wagon> supportedWagonProtocols;

    @Mock
    private Log log;

    @Test
    public void getSupportedProtocolsTest()
    throws Exception {
        final Set<String> supportedProtocols = Stream.of( "http", "https", "file" ).collect( Collectors.toSet() );
        when( this.supportedWagonProtocols.keySet() ).thenReturn( supportedProtocols );
        when( this.container.lookupMap( Wagon.class ) ).thenReturn( this.supportedWagonProtocols );

        final String protocols = WagonUtil.getSupportedProtocols( this.container, this.log );
        assertEquals( "file, http, https", protocols );
    }

    @Test()
    public void getSupportedProtocolsThrowsComponentLookupExceptionTest()
    throws Exception {
        doNothing().when( this.log ).error( isA( ComponentLookupException.class ) );
        when( this.container.lookupMap( Wagon.class ) ).thenThrow( ComponentLookupException.class );

        final String protocols = WagonUtil.getSupportedProtocols( this.container, this.log );
        assertEquals( "", protocols );
    }
}

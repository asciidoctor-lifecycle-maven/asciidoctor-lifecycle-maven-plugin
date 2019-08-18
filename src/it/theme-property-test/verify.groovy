File buildLog = new File( basedir, 'build.log' )
def propertyName = "asciidoctor.theme.wagon-provider-api.path"
def pattern = ~/asciidoctor\.theme\.wagon-provider-api\.path = "([^"]+)"/
def matcher = ( buildLog.text =~ pattern )
if ( ! matcher.find() ) {
    fail "Property ${propertyName} not founded on log"
} else {
    String propertyValue = matcher[0][1]
    assert propertyValue.endsWith( "/asciidoctor-themes/wagon-provider-api" )
}
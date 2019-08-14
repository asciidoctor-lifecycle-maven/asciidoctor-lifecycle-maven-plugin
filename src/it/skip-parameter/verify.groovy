File pseudoThemeDir = new File( basedir, "target/asciidoctor-themes/wagon-provider-api" )
File dependencyFile = new File( basedir, "target/dependency/junit-4.11.jar" )

assert ! pseudoThemeDir.exists() && ( ! dependencyFile.exists() )
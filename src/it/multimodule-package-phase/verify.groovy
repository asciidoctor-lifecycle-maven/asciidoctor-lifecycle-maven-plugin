File pseudoThemeDir = new File( basedir, "asciidoctor-module/target/asciidoctor-themes/wagon-provider-api" )
File dependencyFile = new File( basedir, "asciidoctor-module/target/dependency/junit-4.11.jar" )

assert dependencyFile.isFile() && ( ! pseudoThemeDir.exists() )
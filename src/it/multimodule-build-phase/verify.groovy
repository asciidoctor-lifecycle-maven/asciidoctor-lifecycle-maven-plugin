File pseudoThemeDir = new File( basedir, "asciidoctor-module/target/mydoctor-themes/wagon-provider-api" )
File dependencyFile = new File( basedir, "asciidoctor-module/target/dependency/junit-4.11.jar" )

assert ! dependencyFile.exists() && pseudoThemeDir.isDirectory()
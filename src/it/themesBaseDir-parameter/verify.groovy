File pseudoThemeDir = new File( basedir, "target/themes/wagon-provider-api" )
File dependencyFile = new File( basedir, "target/dependency/junit-4.11.jar" )

assert pseudoThemeDir.isDirectory() && ( ! dependencyFile.exists() )
# Audlang Specification

The Audience Definition Language aims to define a standard expression language for defining audiences for advertising purposes.

**:bulb: [Here](./doc/Motivation.md) you can find the motivation for this project.**

This is primarily a **specification project** to maintain the [Audience Definition Language Specification](./doc/AudienceDefinitionLanguageSpecification.md). Its main artifact is a zip-archive including the [ANTLR4-grammar](./src/main/antlr4/Audlang.g4), the language documentation and a comprehensive number of samples for testing your parser implementation.

Because it was necessary to test the specification *before* creating a full-fledged parser implementation, I have created supplementary Java-code that runs during the build of this project but is not part of the zip-archive. Instead the build process additionally produces a JAR-artifact. 

**Clarification:** In this project, we don't make any assumptions about the programming language used to implement the Audlang. 

For creating a Java parser-implementation simply create a dependency to this JAR to inherit the generated ANTLR base-classes. The JAR also includes some supplementary code.

```xml
		<dependency>
			<groupId>de.calamanari.adl</groupId>
			<artifactId>audlang-spec</artifactId>
			<version>1.0.0</version>
		</dependency>
```

For further dependencies required to run the java code please refer to this project's POM.

:bulb: The generated samples are not part of the jar, but using the classes from the aforementioned JAR they can be generated on demand. Review the test classes of this project for details.


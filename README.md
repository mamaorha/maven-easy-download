# maven-easy-download
maven plugin for download files over http

example:

```xml
<plugin>
	<groupId>co.il.nmh</groupId>
	<artifactId>maven-easy-download</artifactId>
	<version>0.0.1</version>
	<executions>
		<execution>
			<goals>
				<goal>easy-download</goal>
			</goals>
			<configuration>
				<url>https://url-to-download</url>
				<headers>
					<header>
						<name>header-name</name>
						<value>header-value</value>
					</header>
				</headers>
				<outputDirectory>${project.build.directory}</outputDirectory>
				<outputFileName>downloaded-file-name</outputFileName>
			</configuration>
		</execution>
	</executions>
</plugin>
```

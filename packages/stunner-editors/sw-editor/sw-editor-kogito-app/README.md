# Kogito SW Editor - Webapp

## Building

- Before building this webapp, you should build the whole of the Stunner project before
  - change to the `kogito-tooling/packages/stunner-editors/kie-wb-common-stunner` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
- Build the webapp:
  - Production: `mvn -T 8C clean install -DskipTests=true`
  - Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  - Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`

## Running in Wildfly

- Copy the generated file `target/sw-editor-kogito-app.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080`

## Running in SDM

Start GWT super dev mode by: `mvn gwt:run`

## Running in IntelliJ

Create a new Run/Debug GWT configuration as:

Module: `sw-editor-kogito-app`

GWT Module: `org.kie.workbench.common.stunner.sw.KogitoSWEditor`

User Super Dev Mode: `true`

VM Options:

        -Xmx8G
        -Xms1024m
        -Xss1M
        -Derrai.dynamic_validation.enabled=true
        -Derrai.ioc.jsinterop.support=true

[OPTIONAL] Dev Mode Parameters:

        -style PRETTY
        -generateJsInteropExports
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL]

Start page: `test.html`

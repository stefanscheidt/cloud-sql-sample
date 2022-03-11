# Google Cloud SQL Sample Application

This minimalistic Spring Boot sample application can be used to demonstrate [issue #982](https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/982) of [Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp).

## Prerequisites

* Java 11+ to build and run the sample application
* Access to a CloudSQL PostgreSQL database provided by a service account key
* Port 8080 must be free

## Configure Access

The Spring Cloud GCP starters need credentials configured as described [here](https://googlecloudplatform.github.io/spring-cloud-gcp/3.1.0/reference/html/index.html#credentials).
If your Google Cloud user account has sufficient priviledges to access the Google Cloud SQL database, it should be sufficient to do a [`gcloud auth application-default login`](https://cloud.google.com/sdk/gcloud/reference/auth/application-default/login).

## Build the Sample Application

```shell
./mvn clean package
```

## Run the Sample Application

```shell
./run.sh <INSTANCE_CONNECTION_NAME> <DATABASE_NAME> <DATABASE_USERNAME> <DATABASE_PASSWORD>
```

You should see something like this in the log:

```text
com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
c.g.cloud.sql.core.CoreSocketFactory     : Connecting to Cloud SQL instance [INSTANCE_CONNECTION_NAME] via SSL socket.
c.g.cloud.sql.core.CoreSocketFactory     : First Cloud SQL connection, generating RSA key pair.
c.g.a.oauth2.DefaultCredentialsProvider  : Your application has authenticated using end user credentials from Google Cloud SDK. We recommend that most server applications use service accounts instead. If your application continues to use end user credentials from Cloud SDK, you might receive a "quota exceeded" or "API not enabled" error. For more information about service accounts, see https://cloud.google.com/docs/authentication/.
com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
c.g.cloud.sql.core.CoreSocketFactory     : Connecting to Cloud SQL instance [INSTANCE_CONNECTION_NAME] via SSL socket.
c.g.c.s.core.DefaultCredentialsProvider  : Default credentials provider for user 764086051850-6qr4p6gpi6hn506pt8ejuq83di341hur.apps.googleusercontent.com
c.g.c.s.core.DefaultCredentialsProvider  : Scopes in use by default credentials: [...]
c.g.c.s.a.c.GcpContextAutoConfiguration  : The default project ID is YOUR_GCP_PROJECT_ID
o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/actuator'
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
c.e.c.CloudSqlSampleApplication          : Started CloudSqlSampleApplication in 3.125 seconds (JVM running for 3.4)
```

## Reproduce the Issue

Send a HTTP POST request to `http://localhost:8080/actuator/refresh`. You should see something like this:

```shell
$ curl -X POST http://localhost:8080/actuator/refresh
{"timestamp":"2022-03-11T17:39:41.233+00:00","status":500,"error":"Internal Server Error","path":"/actuator/refresh"}
```

In the service logs, you should see this:

```text
 INFO 96674 --- [nio-8080-exec-6] c.s.a.s.CloudSqlEnvironmentPostProcessor : post-processing Cloud SQL properties for + POSTGRESQL
 INFO 96674 --- [nio-8080-exec-6] c.s.a.s.CloudSqlEnvironmentPostProcessor : Default POSTGRESQL JdbcUrl provider. Connecting to jdbc:postgresql://google/YOUR_DATABASE_NAME?socketFactory=com.google.cloud.sql.postgres.SocketFactory&cloudSqlInstance=YOUR_INSTANCE_CONNECTION_NAME with driver org.postgresql.Driver
ERROR 96674 --- [nio-8080-exec-6] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.IllegalStateException: Unable to set ApplicationName - SQLAdmin client already initialized.] with root cause

java.lang.IllegalStateException: Unable to set ApplicationName - SQLAdmin client already initialized.
        at com.google.cloud.sql.core.CoreSocketFactory.setApplicationName(CoreSocketFactory.java:449) ~[jdbc-socket-factory-core-1.4.2.jar:na]
        at com.google.cloud.spring.autoconfigure.sql.CloudSqlEnvironmentPostProcessor.postProcessEnvironment(CloudSqlEnvironmentPostProcessor.java:92) ~[spring-cloud-gcp-autoconfigure-3.1.0.jar:3.1.0]
        at org.springframework.cloud.context.refresh.ConfigDataContextRefresher.updateEnvironment(ConfigDataContextRefresher.java:92) ~[spring-cloud-context-3.1.1.jar:3.1.1]
...
```

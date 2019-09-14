# RESTful Services mit Spring Boot 

## cURL

Um mit dieser Übung arbeiten zu können, solltest Du cURL installieren. 

- macOS: `brew install curl`
- Windows: https://stackoverflow.com/a/16216825/5155817

Hinweis: Mit `curl -v ...` werden die Details der Kommunikation via HTTP angezeigt.

## Start

Starte die Applikation:

    mvn spring-boot:run
    
Du kannst nun die Ressourcen wie folgt aufrufen: 

    curl -v http://localhost:8080/departments
    curl -v http://localhost:8080/employees


## Aufgaben

### API Dokumentation mit Swagger

Erweitere das Projekt, sodass die API-Dokumentation mit Swagger UI angezeigt wird.

Füge dazu im POM folgende Abhängigkeiten hinzu: 

    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>${springfox.swagger.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>${springfox.swagger.version}</version>
    </dependency>
    
Setze das Property `springfox.swagger.version` auf `2.9.2`.

Füge auf der Klasse `ch.juventus.example.ExampleApplication` die Annotation `@EnableSwagger2` hinzu.

Nach einem Neustart der Applikation kannst Du Swagger UI unter folgender URL aufrufen: http://localhost:8080/swagger-ui.html.
Versuche, über die Weboberfläche einige Requests an die Applikation zu senden.

Die generierte API-Spezifikation lässt sich über eine weitere URL aufrufen:

    curl -v http://localhost:8080/v2/api-docs

### Content negotiation

Rufe die gleiche Ressource in verschiedenen Formaten auf:

- JSON: `curl -v http://localhost:8080/departments`
- XML: `curl -v -H "Accept: application/xml" http://localhost:8080/departments`

Was bedeutet der HTTP Statuscode 406?

Füge Support für XML mit Hilfe folgenden Abschnitts hinzu: 
https://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-write-an-xml-rest-service

### HATEOAS Support

Der `DepartmentController` dekoriert die Ressource `Department` mit Links. 
Erweitere den `EmployeeController`, sodass die Ressource `Employee` mit folgenden Links dekoriert wird:
- `_self`-Link mit Link auf die eigene URL.
- `department`- Link, der die URL des verlinkten Departments enthält.

### POST-Request

Erzeuge einen neuen Employee mit folgendem POST-Request:

    curl -v -H "Content-Type: application/json" -d \
    '{"firstName":"Heidi","lastName":"Keppert","_links":{"department":{"href":"http://localhost:8080/departments/1"}}}' \
    http://localhost:8080/employees

In der ausgegebenen Response, überprüfe folgende Eigenschaften:
- HTTP-Statuscode
- Location-Header

Überprüfe den neu angelegten Employee, indem Du seine URL aufrufst, z.B. 

    curl -v http://localhost:8080/employees/6

Dabei fällt dabei auf, dass die Beziehung von Employee zu Department nicht persistiert wurde. 

Diskutiere folgende Ansätze, das Problem zu lösen:
- Das `links`-Element in der `create`-Methode auswerten.
- Im Employee ein neues Attribut einführen, z.B `departmentId`.
- Die URLs umstrukturieren: `/departments/{id}/employees/{id}` 
- Hast Du weitere Ideen?


### Exception-Handling

Was passiert, wenn ein unbekannter Employee aufgerufen wird?

    curl -v http://localhost:8080/employees/8

Warum ist das keine "angemessene" Antwort vom Server?

Füge mit Hilfe folgenden Abschnitts einen globalen Exceptionhandler hinzu, 
welcher die Exception `javax.persistence.EntityNotFoundException` auf einen passenden HTTP-Statuscode mappt:
 https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-exceptionhandler

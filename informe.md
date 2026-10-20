# Laboratorio 2: Reglas de negocio con Drools

## 1. Objetivo y alcance

El laboratorio consiste en implementar una aplicación Spring Boot que evalúe reglas de negocio de una aerolínea mediante Drools. El ejercicio solicita diez reglas relacionadas con ascensos, equipaje, asientos, compensaciones y beneficios para pasajeros.

## 2. Configuración del proyecto

Se configuró el proyecto Maven `labdrools` con Java 17 y Drools `7.74.1.Final`. La versión de Spring Boot se estableció en `3.4.10`, siguiendo el `pom.xml` del ejemplo del docente. El PDF del laboratorio menciona Spring Boot `3.2.5`; se registra esta diferencia para dejar claro que se priorizó la versión utilizada por el ejemplo entregado.

El archivo [pom.xml](labdrools/pom.xml) incluye los módulos de Drools `drools-core`, `drools-compiler`, `drools-decisiontables`, `drools-mvel` y `kie-api`, además de los starters web, validación y pruebas.

## 3. Modelado de los hechos

Se crearon tres clases JavaBean en `com.udea.labdrools.model` para representar los hechos que las reglas consultan y modifican:

- [Passenger.java](labdrools/src/main/java/com/udea/labdrools/model/Passenger.java): contiene el vuelo asociado, nivel de membresía, edad, equipaje, preferencias, clase y los resultados de la evaluación (check-in prioritario, descuento, ascenso, compensación, puntos, restricción de equipaje, acceso VIP y asignación de asiento).
- [Flight.java](labdrools/src/main/java/com/udea/labdrools/model/Flight.java): contiene identificador, retraso en minutos y duración en horas.
- [Seat.java](labdrools/src/main/java/com/udea/labdrools/model/Seat.java): contiene vuelo, número, indicador de salida de emergencia y disponibilidad.

Esta separación permite que Drools relacione al pasajero con su vuelo y con los asientos candidatos sin introducir persistencia ni el resto de la aplicación anterior.

## 4. Definición de las reglas

Se creó [airline_rules.drl](labdrools/src/main/resources/rules/airline_rules.drl), siguiendo el formato del ejemplo del docente: paquete `rules`, importación de los modelos y reglas declaradas con bloques `when` y `then`. Se añadieron comentarios breves para explicar la condición y el efecto de cada regla.

Las diez reglas implementadas son:

1. `UpgradeToBusinessClassForFrequentFlyersWithDelays`: asciende a clase ejecutiva a miembros Gold o Platinum con más de 60 minutos de retraso, siempre que el equipaje no exceda 23 kg.
2. `PriorityCheckInForSeniors`: activa el check-in prioritario para pasajeros mayores de 65 años.
3. `DiscountForLightLuggage`: aplica un descuento del 10 % si el equipaje pesa menos de 10 kg.
4. `DenyUpgradeForOverweightLuggage`: deniega ascensos si el equipaje supera 23 kg.
5. `AssignEmergencyExitSeatToYoungAdults`: asigna un asiento de salida de emergencia disponible a pasajeros de 18 a 40 años con preferencia `Any`.
6. `CompensationForExtremeDelays`: asigna una compensación de 200 cuando el retraso supera 180 minutos.
7. `ExtraLoyaltyPointsForLongFlights`: suma 500 puntos, una sola vez, a miembros distintos de `Basic` en vuelos de más de cinco horas.
8. `RestrictLuggageOnShortFlights`: marca como restringido el equipaje de más de 15 kg en vuelos de menos de dos horas.
9. `VipLoungeAccessForPlatinumMembers`: habilita el acceso VIP a miembros Platinum.
10. `PreferentialSeatForFamilies`: asigna el tipo de asiento preferencial familiar a pasajeros que viajan con niños y no tienen una preferencia específica.

Para evitar la interferencia entre reglas, la regla de equipaje con exceso tiene mayor saliencia y la regla de ascenso también comprueba el peso máximo. Las reglas modifican los hechos mediante setters y las actualizaciones de Drools.

## 5. Integración de Drools con Spring

Se creó [DroolsConfig.java](labdrools/src/main/java/com/udea/labdrools/config/DroolsConfig.java). La configuración carga `rules/airline_rules.drl` desde el classpath, construye el módulo con `KieBuilder`, comprueba los errores y expone un `KieContainer` como bean de Spring.

También se creó [AirlineEvaluationService.java](labdrools/src/main/java/com/udea/labdrools/service/AirlineEvaluationService.java). El servicio recibe un pasajero, un vuelo y la lista de asientos; crea una sesión nueva, inserta los hechos, ejecuta `fireAllRules()` y libera la sesión con `dispose()` dentro de `finally`. Devuelve el pasajero modificado; los asientos de la lista también reflejan cualquier asignación realizada por las reglas.

## 6. Ajuste de las reglas MVEL

Durante la compilación, Drools detectó una acción incompatible con el dialecto MVEL utilizado: una llamada al getter estaba anidada dentro de un bloque `modify`. Se cambió esa acción a setters directos y `update($passenger)`, siguiendo el estilo compatible con Drools `7.74.1.Final`.


## 7. Verificación realizada

Se ejecutó el test de contexto de Spring Boot con el wrapper Maven y JDK 17:

```powershell
./labdrools/mvnw.cmd -f labdrools/pom.xml -ntp -Dtest=LabdroolsApplicationTests test
```

**Resultado de la verificación inicial:** `Tests run: 1, Failures: 0, Errors: 0` y `BUILD SUCCESS`. Durante el arranque se inicializa el `KieContainer`, por lo que el resultado confirma que Drools pudo cargar y compilar el archivo de reglas. La suite ampliada se documenta en la sección 10.

La prueba de contexto confirma el inicio de Spring y la construcción de Drools. Las pruebas de comportamiento agregadas para el endpoint se describen en la sección siguiente.

## 8. API REST

Se creó el endpoint `POST /airline/api/evaluate`. El cuerpo JSON contiene `passenger`, `flight` y `seats`; los DTO [AirlineEvaluationRequest.java](labdrools/src/main/java/com/udea/labdrools/api/dto/AirlineEvaluationRequest.java) y [AirlineEvaluationResponse.java](labdrools/src/main/java/com/udea/labdrools/api/dto/AirlineEvaluationResponse.java) definen el contrato. El controlador [AirlineEvaluationController.java](labdrools/src/main/java/com/udea/labdrools/controller/AirlineEvaluationController.java) valida la solicitud, delega la evaluación al servicio y responde con el pasajero y la lista de asientos modificados.

Se agregaron pruebas MockMvc para comprobar la respuesta HTTP y dos comportamientos observables: check-in prioritario para una persona mayor y asignación/ocupación de un asiento de salida de emergencia.

**Evidencia inicial:** las pruebas `evaluateEnablesPriorityCheckInForSeniorPassenger` y `evaluateAssignsAndOccupiesAnAvailableEmergencyExitSeat` en [LabdroolsApplicationTests.java](labdrools/src/test/java/com/udea/labdrools/LabdroolsApplicationTests.java) verificaron estos dos comportamientos. En esa etapa, Maven informó `Tests run: 3, Failures: 0, Errors: 0`; después se añadieron los casos de las demás reglas y límites.

## 9. Interfaz web con Thymeleaf

Se agregó `spring-boot-starter-thymeleaf` y se crearon dos vistas: [airline_form.html](labdrools/src/main/resources/templates/airline_form.html) para ingresar membresía, edad, equipaje, preferencia, clase, vuelo y asiento candidato; y [airline_result.html](labdrools/src/main/resources/templates/airline_result.html) para mostrar los beneficios, restricciones y estado de los asientos después de evaluar las reglas.

El controlador [AirlinePageController.java](labdrools/src/main/java/com/udea/labdrools/controller/AirlinePageController.java) presenta el formulario en `GET /airline/form` y procesa los datos en `POST /airline/evaluate`. La respuesta visual muestra la clase resultante, check-in prioritario, descuento, denegación de ascenso, compensación, puntos, restricción de equipaje, acceso VIP, preferencia familiar y estado de los asientos.

**Evidencia inicial:** las pruebas `showAirlineEvaluationForm` y `submitAirlineFormShowsEvaluationResults` verificaron la vista del formulario y el flujo completo hasta la página de resultados. En esa etapa, Maven informó `Tests run: 5, Failures: 0, Errors: 0`; la suite final de 19 pruebas se documenta en la sección 10.

## 10. Verificación final

Se ejecutó el ciclo completo de Maven con JDK 17:

```powershell
./labdrools/mvnw.cmd -f labdrools/pom.xml -ntp verify
```

**Resultado:** `Tests run: 19, Failures: 0, Errors: 0` y `BUILD SUCCESS`. Maven compiló y empaquetó `labdrools/target/labdrools-0.0.1-SNAPSHOT.jar`.

También se probaron los flujos en la aplicación iniciada localmente:

- `GET /airline/form`: HTTP 200 y contenido del formulario.
- `POST /airline/evaluate`: HTTP 200 y página de resultados con el ascenso a clase Business.
- `POST /airline/api/evaluate`: la solicitud de ejemplo produjo clase `Business` y descuento del 10 %.

Durante el empaquetado Maven mostró una advertencia HTTP 503 al consultar metadatos de un repositorio OW2; no afectó la compilación, las pruebas ni la creación del JAR.

**Capturas de evidencia:** [Formulario de evaluación](labdrools/evidence-airline-form.png) y [resultado de evaluación con varias reglas activadas](labdrools/evidence-airline-result.png). El caso ilustrado usa membresía Platinum, edad de 30 años, equipaje de 5 kg, retraso de 181 minutos y vuelo de 6 horas; se observan el ascenso, descuento, compensación, puntos, acceso VIP, asiento familiar y asiento de emergencia ocupado.

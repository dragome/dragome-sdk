###Dragome introduction


###Dragome requirements

* Java 7
* Any IDE
* Maven
* Chrome para debuggear


###Dragome maven

* Nombrar arquetipo
* Repositorios de dependencias
* Forma de hacer el build
* Forma de correr la aplicacion con jetty y tomcat



###Dragome 4 steps

1. Declarar interface del servicio
2. Implementar interface de servicio
3. Crear la vista y sus componentes en java
4. Crear HTML marcando los templates a ser usados

###Dragome features

###Dragome SWOT (FODA)
Fortalezas:

1. Poder desarrollar una aplicacion web client side enteramente en Java
2. No se necesitan conocimientos de programacion tipicos de client side: js, HTML, jquery
3. No se necesita una capacitacion importante para hacer uso del SDK
4. El proceso de desarrollo y las herramientas provistas por el SDK son independientes del tipo de browser, su version, y tambien del IDE y su version
5. La arquitectura permite adaptaciones rapidas a nuevas version del JDK.
6. Virtualizacion de aplicaciones desarrolladas en otros lenguajes: C# J#
7. 

Oportunidades:

1. Las herramientas similares existentes presentan una real complicacion en su instalacion, instalacion de plugins, herramientas de desarrollo especificas, etc que dificultan gravemente la aceptacion del desarrollador. Las cuales presentan problemas graves a la hora de mantenerse vigentes, ya sea actualizando plugins para browser e IDE, debugging. 
2. Se desacopla la dependencia con respecto al proveedor de estos componentes. De aca se desprende que va a funcar en cualquier contexto. Se logra esto mediante el uso de la backward compatibility de los browser y del uso de tecnologias standares de web.
3. Crear capa de compatibilidad para GWT, y incorporar todos los proyectos de GWT sobre el SDK.
4. La arquitectura desacoplada de componentes graficos permite incorporar nuevos entornos visuales aun no contemplados, por ejemplo: Swing, wxWidgets, SWT
5. Incorporar otros lenguages de JVM como jRuby, Jython, Closure, Groovy, Cobol, usando el bytecode generado.
6. Incorporar lenguages de .NET para el desarrollo a nivel de lenguaje intermedio.


Debilidades:

1. Por ser una herramienta nueva no tiene herramientas montadas (componentes adhoc) sobre el SDK
2. Comunidad de usuarios recien en desarrollo
3. Beta version
4. Tarda en init
5. Pesa bastante el archivo generado
6. No esta modularizado en varios js
7. No tiene Classloaders

Amenazas:

1. Que el peso del archivo resultante afecte en algunas plataformas, sobre todo las viejas
2. Que el tiempo de startup sea demasiado largo.
 


###Dragome architecture

Hacer grafico con modulos y agregar descripcion de cada uno aca.

Dragome core
:
Dragome JRE for JS

Bytecode compiler


###Dragome 4 programmers

Dibujar workflow y explicar partes

Reutilizacion de templates.
Reuso de componentes.


###Dragome 4 designers

El maquetado de la UI tiene la ventaja de presentarle al diseñador los distintos estados posibles que puede representar un componente grafico.
Esto se da gracias al uso de HTML sin logica, y estaticos.
En cambio mecanismos como AngularJS impide al diseñador ver los diferentes estados porque necesita ser ejecutado para visualizarlos. Esto impide la manipulacion directa del maquetado por no contar con el HTML estatico completo.

El diseñador tiene que aprender angularjs para poder interpretar el resultado del template de acuerdo a sus diferentes estados/comportamiento.
Y  en caso de HTML estaticos lo unico a tomar en cuenta es el atributo "data-template", preservar, conservarlo.

Gracias a esto se pueden intercambiar diferentes HTML que cumplan con los mismos data-template. 
Y en caso de querer hacerlo en angularjs habria que replicar todas las declaraciones de un template en un nuevo HTML.



###Dragome tunning

Configuraciones:

1. Compilador estricto
2. Alias a paginas
3. Implementador de servicio
4. DragomeConfigurator
5. Callback evictor config
6. Configuracion de serializador por servicio


###Dragome future

1. Multiple threads emulator
 1. crear threadpool
 2. round robin para threads
 3. continuations para pausar
2. Aplicaciones con continuations
3. Entornos visuales adicionales:
 1. Swing
 2. Android
 3. WxWidgets
 4. WinForms .NET
4. Hacer andar jRuby, Jython, Closure, Msil, etc
5. Agregar target PHP, Asm.js, .NET msi
6. Jit para balanceo en cliente/server.
7. Que funcione al estilo viejo de web, solo server.
8. que funque GWT sobre dragome.
9. 


##Ideas

 
* Cuando un data-template se repite, se genera una ambiguedad que detecta el fwk.
* Los casos a resolver son:
* Mediante el builder indicar el parent data-template, o sea que se hace explicito que se trata de un data-template diferente.
* ò Mediante otro builder se indica que corresponde a un estado en particular del modelo. Aqui se deberan especificar todos los estados y casi obligatoriamente un builder default.






```html
<html>
sdgsdgsd
<br>
</html>

``` 

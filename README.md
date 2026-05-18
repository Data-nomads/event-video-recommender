# Event Video Recommender

## Objetivo de la funcionalidad de negocio

El objetivo principal de esta funcionalidad es generar valor cruzando flujos de datos independientes en tiempo real. El sistema detecta próximos conciertos de artistas a través de Ticketmaster y los enriquece cruzándolos con los vídeos más populares de esos mismos artistas en YouTube. De esta manera, se ofrece al usuario final un sistema de recomendaciones unificado donde puede descubrir eventos musicales y, simultáneamente, consumir el contenido audiovisual más relevante del artista, mejorando la experiencia de descubrimiento musical.

## Arquitectura final del sistema

El sistema implementa una **Arquitectura Orientada a Eventos (Event-Driven Architecture)** completamente desacoplada, utilizando **Apache ActiveMQ** como broker de mensajería (siguiendo el patrón Productor/Consumidor).

Se compone de los siguientes cuatro módulos independientes:

1. **Productores (Feeders)**:
   - `ticketmaster-app`: Se conecta periódicamente a la API de Ticketmaster Discovery, extrae próximos eventos y los publica en el topic `TicketmasterEvents`.
   - `youtube-app`: Consulta la API de YouTube Data v3 iterando sobre una lista de artistas configurables, extrae sus vídeos con más visualizaciones (`viewCount`) y los publica en el topic `YouTubeEvents`.
2. **Almacenamiento Histórico (Event Store)**:
   - `event-store-builder`: Actúa como un consumidor silencioso que escucha ambos topics y persiste los mensajes JSON crudos en el disco local (dentro de la carpeta `eventstore/`), organizados por fecha y proveedor, actuando como un Data Lake.
3. **Unidad de Negocio (Business Unit)**:
   - `business-unit`: Es el consumidor interactivo en tiempo real. Mantiene un **Datamart en memoria** que filtra eventos duplicados procesando sus IDs únicos. Expone un `ConsoleDashboard` (CLI) interactivo que cruza la información almacenada y muestra las recomendaciones al usuario de forma estructurada.
<img width="2816" height="1536" alt="Diagramadeclase_datanomads_V8" src="https://github.com/user-attachments/assets/8d582165-74fb-43c4-a1ce-550143f2f5ef" />



## Cómo ejecutar cada componente y probar la interfaz

Para que el sistema funcione de manera fluida y se garantice que no se pierde ningún evento en el arranque, se debe seguir este estricto orden de encendido:

### Paso 1: Inicializar el Broker
1. Abre una terminal externa y arranca el servidor de **Apache ActiveMQ**. Asegúrate de que el proceso indique que está escuchando en el puerto por defecto (`tcp://localhost:61616`).

### Paso 2: Inicializar Consumidores (Listeners)
2. En tu IDE, ejecuta la clase `Main.java` del módulo **`event-store-builder`**.
3. Ejecuta la clase `Main.java` del módulo **`business-unit`**. Verás en la consola el mensaje inicial de *"Esperando a recibir eventos desde ActiveMQ..."*.

### Paso 3: Inicializar Productores (Feeders)
4. Ejecuta la clase `Main.java` del módulo **`ticketmaster-app`**.
5. Ejecuta la clase `Main.java` del módulo **`youtube-app`**.

### Paso 4: Probar la Interfaz (Dashboard)
6. Vuelve a la pestaña de la consola de ejecución del módulo **`business-unit`**.
7. Verás que el menú se ha llenado automáticamente con una lista numerada de los artistas detectados por los emisores.
8. **Escribe el número** correspondiente a uno de los artistas y pulsa **Enter**.
9. La consola imprimirá inmediatamente la información cruzada: el nombre del artista, la lista de sus próximos eventos detallados (fecha y recinto) y el top de sus vídeos recomendados.

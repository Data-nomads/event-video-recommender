# Event Video Recommender

## Business Functionality Objective

The main objective of this project is to generate value by combining independent real-time data streams. The system detects upcoming music events through the Ticketmaster API and enriches them by combining them with the most popular videos of the same artists obtained from YouTube.

This creates a unified recommendation system where users can simultaneously:
- discover upcoming concerts,
- explore artists,
- and consume the most relevant audiovisual content associated with them.

The project improves the musical discovery experience by integrating multiple external data sources into a single recommendation platform.

---

## Final System Architecture

The system implements a fully decoupled **Event-Driven Architecture (EDA)** using **Apache ActiveMQ** as the messaging broker following the **Producer/Consumer** communication pattern.

The application is composed of four independent modules:

### 1. Producers (Feeders)

- **`ticketmaster-app`**

  Periodically connects to the Ticketmaster Discovery API, retrieves upcoming events and publishes them into the `TicketmasterEvents` topic.

- **`youtube-app`**

  Consumes the YouTube Data API v3 and publishes music video information into the `YouTubeEvents` topic.

  Additionally, the system incorporates a dynamic `YoutubeSearchService` capable of performing real-time YouTube searches under demand according to the artist selected by the user.

---

### 2. Historical Storage (Event Store)

- **`event-store-builder`**

  Acts as a silent consumer that listens to both ActiveMQ topics and persists raw JSON events into the local disk inside the `eventstore/` directory.

  Events are organized by:
   - provider,
   - subsystem,
   - and date.

  This module behaves as a lightweight Data Lake for historical event storage.

---

### 3. Business Unit

- **`business-unit`**

  Represents the interactive real-time consumer of the system.

  It maintains an in-memory **Datamart** responsible for:
   - normalizing artist names,
   - removing duplicated events using unique IDs,
   - filtering noisy Ticketmaster entries,
   - and combining information from multiple data sources.

  The module exposes an interactive CLI dashboard (`ConsoleDashboard`) capable of displaying:
   - artists,
   - upcoming concerts,
   - and YouTube recommendations.
<img width="1438" height="649" alt="image" src="https://github.com/user-attachments/assets/1f4ce35a-398d-409f-bccb-b0316a7c43d6" />
PDf del diagrama para mejor visualización: (https://github.com/user-attachments/files/28047810/diagrama_event_video_reccomender_pdf.pdf)




---

# How to Run the System

To guarantee a correct startup sequence and avoid losing events during initialization, the following execution order must be respected.

---
## Things to Take Into Account

- Apache ActiveMQ must be initialized before launching producers and consumers.
- Ticketmaster may return duplicated or noisy event names, therefore normalization filters are applied inside the Datamart.
- YouTube recommendations are obtained dynamically under demand to reduce unnecessary API consumption.
- The dashboard only displays a subset of artists for readability, although the system internally processes a much larger number of events.
- The application requires internet connection in order to access external APIs.

---
## Step 1: Initialize Apache ActiveMQ

Open an external terminal and start the **Apache ActiveMQ** broker.

Make sure the broker is listening on the default port:

```text
tcp://localhost:61616
```

---

## Step 2: Initialize Consumers (Listeners)

Run the following modules from your IDE:

1. `event-store-builder`
2. `business-unit`

The `business-unit` console should display a message similar to:

```text
Waiting for events from ActiveMQ...
```

---

## Step 3: Initialize Producers (Feeders)

Run the following modules:

1. `ticketmaster-app`
2. `youtube-app`

These modules will begin publishing events into ActiveMQ topics.

---

## Step 4: Test the Dashboard

Return to the execution console of the `business-unit` module.

The dashboard will automatically display a dynamically generated list of artists and tours retrieved from Ticketmaster.

Users can:
- select an artist using its corresponding number,
- or manually type any artist name.

The system will immediately display:
- upcoming concerts,
- event dates,
- venues,
- and YouTube recommendations associated with the selected artist.

The application also supports manual artist searches even if the artist currently has no concerts available in Ticketmaster.

---

# Examples of Real Usage

This section presents different real execution scenarios of the application and demonstrates how the system integrates events retrieved from Ticketmaster with video recommendations obtained dynamically from the YouTube Data API.

---

## Example 1: Artist with Available Concerts

### User Input

```text
Choose a number or type any artist: Bad Bunny
```

### Console Output

```text
=================================================
Selected Artist: Bad Bunny
=================================================

Upcoming Events:

1. Bad Bunny - DeBÍ TiRAR MáS FOToS World Tour
   Date: 2026-05-22
   Venue: Estadi Olímpic Lluis Companys

2. Bad Bunny - DeBÍ TiRAR MáS FOToS World Tour
   Date: 2026-05-23
   Venue: Estadi Olímpic Lluis Companys

Recommended YouTube Videos:

1. Becky G, Bad Bunny - Mayores
2. BAD BUNNY x JHAYCO - DÁKITI
3. Cardi B, Bad Bunny & J Balvin - I Like It
4. Bad Bunny - Moscow Mule
5. Bad Bunny - Tití Me Preguntó
```

This example demonstrates the complete integration between:
- Ticketmaster API,
- ActiveMQ,
- Datamart processing,
- YouTube API,
- and real-time recommendation generation.

---

## Example 2: Artist Without Available Concerts

The system also supports artists that currently do not have available concerts in Ticketmaster.

### User Input

```text
Choose a number or type any artist: Eminem
```

### Console Output

```text
=================================================
Selected Artist: Eminem
=================================================

Upcoming Events:

No concerts available for this artist.

Recommended YouTube Videos:

1. Eminem - Lose Yourself
2. Eminem - Without Me
3. Eminem - Mockingbird
4. Eminem - Not Afraid
5. Eminem ft. Rihanna - Love The Way You Lie
```

This behavior increases the flexibility of the recommendation system because it is not limited only to artists stored inside the Datamart.

---

## Example 3: Dynamic Recommendations Under Demand

Initially, the project used a static approach where the `youtube-app` module periodically stored videos from a predefined list of artists.

After the redesign, the system now performs YouTube recommendations dynamically under demand.

This means:
- the user can manually search any artist,
- YouTube requests are only performed when necessary,
- unnecessary API consumption is reduced,
- and scalability is improved.

### Advantages of the New Approach

- Lower API usage
- Reduced unnecessary requests
- Greater scalability
- Support for any artist
- Faster recommendation updates
- More realistic recommendation workflow

---

## Example 4: Artist Normalization and Duplicate Filtering

Ticketmaster frequently returns noisy event names such as:
- VIP packages,
- parking tickets,
- duplicated tours,
- or festival-related entries.

To improve recommendation quality, normalization filters were implemented inside the Datamart.

### Example of Noisy Entries

```text
Plaza de Parking-Bad Bunny - DeBÍ TiRAR MáS FOToS World Tour
VIP Packages
Festival Access
```

After normalization, the dashboard only displays:

```text
Bad Bunny
```

This preprocessing stage improves:
- readability,
- recommendation quality,
- dashboard usability,
- and duplicate reduction.

---

## Example 5: Internal Event-Driven Workflow

The following diagram summarizes the internal execution flow of the system:

```text
Ticketmaster API
        ↓
ticketmaster-app
        ↓
ActiveMQ Topic
        ↓
business-unit consumer
        ↓
Datamart update
        ↓
User selects artist
        ↓
YoutubeSearchService
        ↓
YouTube Data API
        ↓
Real-time recommendations
        ↓
ConsoleDashboard output
```

This architecture guarantees:
- modularity,
- decoupling,
- scalability,
- asynchronous communication,
- and real-time event processing.

# Justification and Utility of Integrated APIs
The system comes to life through the integration of two global APIs wich, when crossed, generate high added value for the end user. The choice of these data sources responds to a clear strategy of information enrichment:
- **Ticketmaster DIscovery API:** Acts as the system's discovery engine. Its utility lies in providing a constant and structured flow of data about real world musical events (artists, dates, venues...). It is the "trigger" that feeds the core of our business logic.
- **Youtube Data API**: Functions as the multimedia enrichment engine. It allows dynamically querying the most popular videos of the detected artists. Its utility is fundamental for user retention and experience: it transforms a simple "concert notice board" into an immersive experience, allowing the user to listen to an artist's greatest hits at the exact moment they discover their next concert.

# Applied Design Principles and Patterns
The project has been developed under the paradigm of an Event-Driven Architecture and rigorously respects SOLID principles, with special emphasis on Dependency Inversion and the SIngle Responsability Principle. The internal design of each module is detailed below:
- **Producer modules**(Ticketmaster app and youtube app): The implement the Publisher pattern to inject messages into ActiveMQ asynchronously. Internally, they make intensive use of the Strategy pattern by relying on interfaces. This allows the controlles to orchetrate the flow without coupling to concrete technologies, facilitating the storage switch without altering the core business logic.
- **Event Store Module**: (Event Store Builder): Implements the Observer/Listener pattern in its messaging variant (Consumer): It strictly applies the Single Responsability Principle: its sole job is to passively listen to the broker's topics and persist the history of raw messages (JSON) to disk. It acts as a Data Lake, isolating the backup logic from the operational logic of the rest of the system.
- **Business unit module**(business-unit): Combines the observer pattern (via durable subscriptions to ActiveMQ) with an architecture inspired by MVC(Model-View-Controller). The ReccommenderDatamart class centralizes the application state, operating as an in-memory store that actively filters out event duplication. Meanwhile, the ConsoleDashboard acts as the view, ensuring a total Separation of Concerns, where the terminal presentation layer is completely unaware of how and from where the data originates at the network level.

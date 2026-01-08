# RUMaps (Java)

## Overview
RUMaps is a Java project that models and visualizes a Rutgers University map using structured graph-like data (streets/intersections/blocks) and supporting utilities. The project is designed around organizing map data, processing it through a network representation, and displaying it through a GUI panel.

---

## Project Structure
src/
  rumaps/
    Block.java
    Coordinate.java
    Driver.java
    Intersection.java
    MapPanel.java
    Network.java
    Queue.java
    RUMaps.java
    StdIn.java
    StdOut.java
    StdRandom.java
assets/
  overlay.png
  satellite.png
data/
  AllCampuses.in
  Busch.in

---

## Core Concepts
- Object-oriented modeling of map components (coordinates, intersections, blocks)
- Network/graph-style representation of connected map structures
- Queue-based traversal utilities (via provided `Queue`)
- GUI rendering via `MapPanel` and a driver launcher

---

## How to Run

### Compile
```bash
javac src/rumaps/*.java
```

---

## What I Learned
Designing systems that model real-world data using Java classes
Working with network-style structures to represent connected locations
Integrating core logic with a provided visualization/scaffolding layer

---

## Notes
RUMaps.java was implemented as part of the course assignment.
Supporting files (Driver, GUI, utilities, and model classes) were provided as scaffolding and were not modified.

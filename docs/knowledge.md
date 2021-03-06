---
layout: page
title: Knowledge
permalink: /knowledge/
---

# Knowledge Representation and Decisions #

Central to object recognition / image understanding is knowledge representation and decisions
making. ShapeLogic Scala need a framework for both. 
Preferably light weight. 
This page describe the requirements and list some solutions.

## Long Term Goals ##

ShapeLogic Scala goal is be a framework for object recognition
using a hybrid approach to A.I. combining machine learning and symbolic A.I.
Using some of the following techniques:
 
* Machine learning
  * Logistic regression, Naive Bayes or random forest
  * Neural network
  * Baysian network / graphical model
* Symbolic artificial intelligence
  * Lisp like tree search / lazy stream
  * Logic programming
  * RDF or knowledge graph
  * Database

## Knowledge Representation ##

The usual suspects are:

* Data centric representation in Json
* Database
* Logic assertions in Prolog or Minikanren
* Facts stored in Lisp like tree search
* RDF, knowledge graph or graph database

## Decision Making ##
  
There need to be a system for decision making.

* Lisp like tree search
* Heuristic search with backtracking
* Lazy stream
* Logic programming Prolog or Minikanren
* Rules working on RDF 
* Fixed scripts with fixed voting procedures, possibly based on ML

## Particle Analyzer Foreground and Background ##

ShapeLogic Java had an implementation of 
[Particle Analyzer](http://www.shapelogic.org/particle.html).
It was geared at extracting particles in medical imaging. 

An important part of finding the particles and their properties was to make 
the decision on what is foreground and is background in an image. 

This is the same problem as you have in OCR / optical character recognition.
It will be the first decision example in ShapeLogic Scala.

The [Java implementation](https://github.com/sami-badawi/shapelogic-java/blob/master/src/main/java/org/shapelogic/imageprocessing/BaseParticleCounter.java) was somewhat involved and porting it is not trivial.

## Example Assertions in Knowledge Representation ##

For an OCR system you will have information of this type:

1. Background color is white
1. Foreground color is black
1. Polygon 123 found in bounding box
1. Polygon 123 has 2 hard corners and 3 soft corners
1. Polygon 123 represent a "D"
1. Polygon 123, polygon 124, multiline 89 together makes the word "Day"

## Representation Candidates ##

ShapeLogic need a representation that is somewhat general but not too 
heavy weight. Here are a few candidates:

### Json / Lisp S Expressions ###

The first 5 assertions in the examples lends themselves well to simple json 
representation, but the last is more tricky.

### RDF, knowledge graph or graph database ###

These solutions are very powerful and frequently used in big serious knowledge
representation systems. They will represent everything needed, 
but they are all pretty heavy dependencies.

### Database ###

Most data in the corporate world is stored in databases. 
Database can also be used to store and process geometric data.

A database could easily represent the OCR assertions above.

It is hard to represent conditional knowledge. 
E.g. under the assumption that background color is blue a white line has 
been found.

This is represented much better in a tree structure.

### Facts Stored in Lisp Decision Three ###

There is a Lisp style decision tree driving the algorithm.
First decision can be what is the background color.

One branch could have background 
color = black, the other background color = white.

From any give node you can find all the facts that are active 
by going up the three and collecting assertions.
This is great at representing conditional facts, 
but is hard to extract information from and make queries for.

### Adhoc Algorithmic Representation ###

It is natural and space efficient, but hard to combine and reason with:

One algorithms looks for a list of lines.
Another combine them into polygons. Inside the polygons there are lists of 
lines.
It is a big redundant mess, but effective for specialized recognition tasks 
say OCR.

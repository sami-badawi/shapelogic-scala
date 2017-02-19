---
layout: page
title: Knowledge
permalink: /knowledge/
---

# Knowledge Representation and Decisions #

Central to image understanding is knowledge representation and decisions 
making. ShapeLogic Scala need a framework to do both in. 

## Long Term Goals of ShapeLogic Scala ##

ShapeLogic Scala goal is serve as a framework that makes it natural to create 
hybrid approach to object recognition using both machine learning and symbolic 
A.I. with some of the following techniques:

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

The standard suspects are:

* Facts stored in Lisp like tree search
* Logic assertions in Prolog or Minikanren
* RDF 
* Json
* Knowledge graph
* Database
  
## Decision Making ##
  
There need to be a system for decision making.

* Lisp like tree search
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


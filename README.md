icatus
======

Icatus represents the core API for exchanging data in my two other projects, categorize.us and animus. This version is the result of an experiment where I spent about two weekends rewriting the core structures in the API, paying attention to RESTful principles. Rate of change should be very fast on this project through the third week of February as other features get merged in. 

The basic structure is a RESTful API for managing documents. This API is based on the principle of dividing data first into two types of objects : data frames and explicit link objects that only describe the relationships between other objects.

The API defines the :HOST:/~/* to represent these relationship objects. 
Also defined is :HOST:/~/_/:URI: which represents metadata about a particular URI. 
This is the general pattern for defining new relationships :HOST:/~/<RELATION>/PARAMS_OR_URI
The URI :HOST:/~/_ gives metadata about the server itself, including the relations defined there and the URI templates for those relations. 

There is also a templatted, plugin based UI framework for rapid development of UI prototypes based on jquery included. The reference implementation only reads from the file system and does not scale well, but improvements will be rapidly forthcoming. 



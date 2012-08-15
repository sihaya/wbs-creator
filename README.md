wbs-creator
===========
wbs-creator is a web application that allows you to create work breakdown 
structures. It features a diagram editor in which you can edit the work breakdown
structure, allowing you to add new tasks and set their properties. You can also
collaborate on a project by sharing it with others.

The application automatically calculates derived values such as the total effort 
of a project. Future versions allow entering more sophisticated estimation techniques
such as best case and worst case estimations.

The architecture of the application follows the server-client paradigm. In which the client
is responsible for converting user input to server commands and for drawing the diagrams. The
server is responsible for persistence of resources and handling authentication and authorization to
these resources.

The implementation consists of a Java server backend using JCR and Apache Jackrabbit as a 
persistence mechanism, communication is handled by AJAX calls using the JAX-WS API. 
The client is a single page web application written in a combination of HTML/CSS, JQuery and Bootstrap.

Currently the application is in an experimental state.

Screenshot
==========
![Screenshot of the wbs-creator diagram editor](wbs-creator/wbs-creator.jpeg)

#Cardboard Gathering
*Totaly not inspired by a certain other game...*

This is a card game I'm writing for a school project.

## Features
Both added and nonadded

* Moving of cards
* Database where you can create your own deck
* Online multiplayer

I have no plans to add actual game rules.

## Running
First run ``./recomp`` (``recomp.bat`` for Windows).  
Then start the client with ``./run`` (``run.bat`` for Windows).

Note that for the program to start you need 2 clients started and connected to
the same [server](https://github.com/hugonikanor/cardboardGatheringServer).

The deck you want to use can be set in ``settings/settings.configuration``.

The port used is ``23732``.

## Dependencies
* Java 8
* JavaFX 8
* [The server software](https://github.com/hugonikanor/cardboardGatheringServer)

Please also note that the program currently doesn't ship with any cards (kinda),
so those need to be supplied yourself. To guarantee that the program works, make
sure that both players have the exact same ``.json`` files (event though it *can*
work with mismatched files).

## Other
The main class is in ``central.Main``

[Link to the api](http://hugonikanor.github.io/cardboardGathering/), not that it's
really anything worth reading...

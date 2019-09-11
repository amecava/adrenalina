# Adrenalina

In the future, war has left the world in complete destruction and split the people into factions. The factions have decided to stop the endless war and settle their dispute in the arena. A new virtual bloodsport was created. The Adrenaline tournament. Every faction has a champion, every champion has a chance to fight and the chance to win. Will you take the chance of becoming the next champion of the Adrenaline tournament?

Play a first-person shooter on your gaming table. Grab some ammo, grab a gun, and start shooting. Build up an arsenal for a killer turn. Combat resolution is quick and diceless. And if you get shot, you get faster!

## Usage


## Run server 
>**Note:** "data.ser" is the serialization data, when the server is disconnected all the available matches are saved to this file. The server loads and saves automatically. If you want a fresh start just delete it from the project's main directory.

```bash
.
└── ing-sw-2019-Bertolini-Cavallo-Capaccio
    ├── data.ser
    └── jar
        └── server-jar-with-dependencies.jar
```


To run the server, open a terminal in the project's main directory and type:

``
./adrenalina -s 
``

>**WARNING:** The server requires an IP address in addition to the loopback (127.0.0.1) to set the "java.rmi.server.hostname" system property for RMI connections. Please either connect to a Wi-Fi, plug the server to an ethernet switch, or connect to any available network. No internet connection is required.

## Run client (CLI version)

To run CLI version of Adrenaline, open a terminal in the project's main directory and type:

``
./adrenalina -c 
``

## Run client (GUI version) 

To run the GUI version of Adrenaline, open a terminal in the project's main directory and type:

``
./adrenalina -g 
``

>**WARNING:** The JavaFX SDK is required to run the GUI version of Adrenaline, follow these steps to correctly download and install all the required packages:
>1. Download the OS dependend version of JavaFX SDK from [this site](https://openjfx.io/).
>2. Unzip the downloaded zip file in the project's main directory.
>3. Rename the extracted folder (ex. "javafx-sdk-12.0.1") to "javafx".

```bash
.
└── ing-sw-2019-Bertolini-Cavallo-Capaccio
    ├── javafx
    |   ├── ...
    |   └── lib
    └── jar
        └── client-jar-with-dependencies.jar
```
## Authors

* **Jacopo Bertolini**
* **Amedeo Cavallo**
* **Federico Capaccio**

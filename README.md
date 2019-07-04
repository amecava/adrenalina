# Prova Finale Ingegneria del Software 2019
## Gruppo AM48

- ###   10565677    Jacopo Bertolini   ([@jacopobertolini](https://github.com/jacopobertolini))<br>jacopo.bertolini@mail.polimi.it
- ###   10562259    Amedeo Cavallo     ([@amecava](https://github.com/amecava))<br>amedeo.cavallo@mail.polimi.it
- ###   10535432    Federico Capaccio  ([@federicoCapaccio](https://github.com/federicoCapaccio))<br>federico.capaccio@mail.polimi.it


| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| RMI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI | [![RED](https://placehold.it/15/ffdd00/ffdd00)](#) |
| CLI | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Multiple games | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Persistence | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Domination or Towers modes | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |
| Terminator | [![RED](https://placehold.it/15/f03c15/f03c15)](#) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

## Usage


## Run server 
>**Note:** "data.ser" is the serialization data, when the server is disconnected all the available matches are saved to this file. The server loads and saves automatically. If you want a fresh start just delete it from the project's directory.'

```bash
.
└── ing-sw-2019-Bertolini-Cavallo-Capaccio
    ├── data.ser
    └── jar
        └── server-jar-with-dependencies.jar
```


To run the server, open a terminal in the project's directory and type:

``
./adrenalina -s 
``

>**WARNING:** The server requires an IP address in addition to the loopback (127.0.0.1) to set the "java.rmi.server.hostname" system property for RMI connections. Please either connect to a Wi-Fi, plug the server to an ethernet switch, or connect to any available network. No internet connection is required.

## Run client (CLI version)

To run the client in CLI version of Adrenalina, open a terminal in the project's directory and type:

``
./adrenalina -c 
``

## Run client (gui version) 

To run the client in GUI version of Adrenalina, open a terminal in the project's directory and type:

``
./adrenalina -g 
``

>**Warning:** The JavaFX SDK is required to run the GUI version of Adrenalina, follow these steps to correctly download and install all the required packages:
>1. Download the OS dependend version of JavaFX SDK from [this site](https://openjfx.io/).
>2. Unzip the downloaded zip file in the project's directory.
>3. Rename the extracted folder (ex. "javafx-sdk-12.0.1/") to "javafx/".

```bash
.
└── ing-sw-2019-Bertolini-Cavallo-Capaccio
    ├── javafx
    |   ├── ...
    |   └── lib
    └── jar
        └── client-jar-with-dependencies.jar
```

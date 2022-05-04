<br />
<p align="center">
  <h1 align="center">Asteroids</h1>
  <p align="center">
      The construction process for our multiplayer asteroid game <br></br> First author: Mohammad Al Shakoush (s4274865) Second author: Dominic Therattil (s4228952)
        </p>

</p>

## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Design Description](#design-description)
  * [Networking Overview](#Networking Overview)
  * [Design Overview](#Design Overview)
    * [Client](#Client)
    * [Control](#Control)
    * [GameObserver](#GameObserver)
    * [Model](#Model)
    * [Server](#Server)
    * [Util](#Util)
    * [View](#View)
* [Evaluation](#evaluation)
* [Teamwork](#teamwork)
* [Extras](#extras)

## About The Project

We were provided with the source code of a basic single player asteroids game. Our task is to persistently store user data in a database. Create a multiplayer version by implementing UDP networking. Enabling a user to host a game and another user to join the hosted game and then continute specating if one of the player dies.

In this report we will give an overview of our impression of the final program and reflect upon the changes we hoped to have made and challenges we faced in the creation of our program.

### Built With

* [Maven](https://maven.apache.org/)

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

The latest versions of the following:

* Java
* Maven 

### Installation

1. Navigate to the Asteroids folder
2. Clean and build the project using:
```sh
mvn install
```
3. Run the `Main` method of Asteroids using:
```sh
mvn exec:java
```
4. Alternatively you can run the `main` method in `Asteroids.java` using an IDE of your choice (e.g. IntelliJ)

Should you want to run this program standalone, you can create a JAR file with the following maven command:

```sh
mvn clean package
```
The JAR file will appear in the `/target` directory.

## Design Description

### Networking Overview

The very first idea we had is sending keystrokes to the server, make the server do the computation, then send the view components to the client and let the client. After some research on this topic we have realised that implementing such a pattern of computing will result in latency. This is because the client has to send the keystrokes, sends that to the server, then receive a computation back of the next GameObject  location / state. 

Therefore, we have decided to take another approach. A 2 sided computation. This however resulted in making the program very hard to implement, since the sending and receiving of the packets had to be perfectly synced and made the choice difficult on choosing which information is essential and what information is redundant and experimenting how each one of these components if put on either side of the computation would affect the players gaming experience. 

Our approach is by making each client compute its own spaceships components, this makes the experience much smoother, since pressing a keystroke instantly resolves in movement / firing rather than waiting for computation on the server. However, some components such as the asteroids, had to computed  on the server. Since we want all the clients to have them united.  So you could imagine our server to be a basket, where each client puts its essentail data in. Then retrives other players data. Such as their location, name, bullets, direction ..etc. This is also the reason we chose to make the server do the collision checking. Since we wanted the latest information about such info in the 'basket'.

We have also perfected the joining and quiting of players and host. We have listeners on both sides of the parties listening on separate sockets for any `Quit` action done by either the client or the host. When the host either exists or quits to the main menu. All other players are notified and have to go back to the main menu too. Each player (inc. host) have 2 choices when dying in the game. Either spectate other players playing or simply quiting to the main menu. Execept when all players have died. The hosts gets notified to either quits to the main menu or hosting a new game for the others to join. 

## Design Overview

### Client

Here we handle the sending of all the clients' data to the host and receiving the client data from the host, thereafter appropriately updating the respective spaceship data, so the correct information can be displayed on screen of the client.

### Control

- actions:
  this is used for the popup Jmenu that will redirect the user to appropriate panel the user has selected

The GameUpdater arguably one of the most important components and can be thought of as the game engine. It is responsible for updating the model data when changes occur in the game. It is a runnable object so when it is started in a thread it is responsible for running the main game loop.

PlayerKeyListener responsible for appropriately make a spaceship perform different actions when the user presses some keys.

### GameObserver

Here we have the ObservableGame and UpdateListener that with another such that the update listener sees an update in the game it notifies the data sender so that it will be able to react.

### Model

The model package holds all the object the user interacts with this includes;

- connections: 
  Here we manage the sending and receiving of packages between the client and host. This is specifically done in the TrafficHandler. The MultiPlayerGamePackage and SinglePlayerGamePackge are the templates that are sent which contain the actual data that is to be displayed on in view for the client and host.

  The ClientHandler receives data packages from the client performs the appropriate updates on the host sides and sends the appreciate information to update on the client side

- gameobjects: 
  these are the entities in the game such as asteroid, bullet, spaceship etc…


### Server

This package contains the vital aspects that enables the server connection for the game by handeling incoming requests.

### Util

Here we store the classes that have that are more general. But still help with the overall functionality of the program. Such as the sound effects and PolarCoordinates.

- database
  Here we handle all the process required for the game to interact with the database; GameData provides the blueprint table they will be used in our Database, DatabaseManger handles the actual interaction with the ODB such as updating and requesting for records and Query contains the sql commands that are used to query the database 

### View

In here we have all the components that are presented to the user. 

- errors

  this is an abstract class that has the error pop-up messages that we use throughout the program

- mainmenu
  here we prepare the different panel displayed game such as the MainMenu, HighScore, HostGame etc... and link it all together in AsteroidFrame

## Evaluation

We find our program to be stable we find that there is next to no lag playing on multiplayer, and we feel have implemented the multiplayer functionality quite well following closely with the design practices we saw in the tutorials and making use of UDP. We each separately tested the program, and we did not find any bugs that would crash our program and any exceptions are caught apportiatly, and we provide output information when necessary.

We also have a persistent data storage implemented that can store new players and update existing player scores via sql queries. Furthermore, In order to ensure the robustness of our database and queries we even created a test batch for it to ensure it functions correctly.

At anypoint of the game the user has the functionality to click on the pop up menu to leave the session, start a new session, join a solo session or exit the program.

One feature that we didn't implement was spectate button in the main menu although, when a player dies in a multiplayer session they always have the option to continue spectating the game or quit to the main menu.

All in all this project and this course was our favourite from this year, the amount of cool skills we were able to learn in such a short time is amazing. We are extremely satisfied with our final program and what we were able to produce over the past weeks.

## Teamwork

The processes that led to the final code and report was challenging to say the least.

Upon the release of the assignment we designated each other tasks and set a deadline to complete them, upon the completion of tasks we would regroup, revaluate and then set each other some more tasks until the project was complete.

Along the way we would communicate regularly to get ideas for superior implementation choices.

Communicating during the debugging process via screen sharing and voice application was crucial to our success in the formation of this program. It really helped expedite some of the issues we were facing, since we could help each other pick out mistakes and quickly bounce ideas to solve bugs. The saying two heads are better than one, was definitely highlighted during these times.

## Extras


Throughout the game we added background music, when the user moves we have a thruster sound effect and similiarly for when they fire bullets, which all improve the user experience.

Each spaceship connecting to the host will have a unique colour along with the different coloured bullets the ship fires, furthermore a player can select their own username in the main menu, this username then hovers under the spacecraft all of this makes it easy for the players to differentiate themselves for another.

In the main menu we a nice highlighting feature on the buttons when the players mouse hovers over the options which is pretty cool.

We have a test batch for the database to ensure it functions correctly.

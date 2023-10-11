# Game-backend

This is a code skeleton for the backend of a simple bomberman game created with Clojure.

## The game

The game consists of a game board 20x20 and two players, which have their initial placements in opposing corners of the square. Positions are defined by their placement on a grid with x and y-axes where the top left corner holds position (0, 0) and x increases to the right and y downwards.

In the `core.clj` game file, everything is purely functional! That's where we place all the "business logic" and all the game functionality that is used by the web server to serve the client. There is also a file named `api.clj` that translates the game state to a list of game entities and their positions.

The client has no logic and only displays the game entities it is sent. The client sends a player id and, when applicable, a direction when an action button is pressed.

The frontend can be found hosted at [clojure-game.stjernberg.com](http://clojure-game.stjernberg.com) and it will connect to your local server and start the game with a simple press of the `enter` button. You can also run the client on localhost if you want to look at it or make changes to it.

Player controls:

| Action       | Player 1   | Player 2 |
| :---         |    :---:   |   :---:  |
| Up           | UpArrow    | W        |
| Left         | LeftArrow  | A        |
| Down         | DownArrow  | S        |
| Right        | RightArrow | D        |
| Place bomb   | J          | C        |
| Explode bomb | K          | V        |

## What is missing?

Your first task is to implement the functions `move`, `place-bomb` and `explode-bomb`. The expected input values are provided and validated in the `move` function, for your convenience. A lot of helper functions are available to get you started, and to abstract the logic of the game away from the implementation of the game state.

- Move: Moves a player (hopefully not outside the world boundaries or into blocks)
- Place-bomb: Places a bomb for the player at the player's position
- Explode-bomb: Explodes the player's bomb and removes blocks and kills players around it. I suggest you initially define the explosion to be the union of a 5x5 cross and a 3x3 square with the bomb at the center.

## How to develop?

The game and all functions live in the REPL when the program is running. The server is run by loading the file `server.clj` into the REPL and running the `(start-server!)` function. When you edit a function in a file, you need to reload that file into the repl and restart the server using `(restart-server!)` to reload the functions in the web server.

I encourage you to use the REPL in developement to experiment and test your progress. For example, by typing `(move (create-game) :p1 :right)` in the REPL you'll see how your move function behaves in a newly created game. Or perhaps just play around with clojure functions to see how they behave.

There are also tests written for the game, they are not very exhaustive but it's always fun to see tests passing. Your editor should have a simple command for running the tests.

## Additional tasks

- Implement bombs as a queue where multiple bombs can be placed and then are triggered in a queue-based fashion.
- Change the layout of the blocks on the map or change the player positions
- Play the game with a friend (or anyone)

## Advanced task

The client accepts a game entity called `powerup` and will display it as a ⭐️. The game might be, when you've implemented the basic functionality, a little boring (and slow, http requests are so good for fast-paced gaming). To spice things up, players can get a power-up to increase their power levels and defeat their opponent.

It is up to you how this is to be done. To implement this a lot of things have to change, even in the `api.clj` file (to send the powerup to the client). Perhaps jumping through walls, wrapping around the grid at the ends, or making the explosions bigger or longer?

### Even more advanced task

Create another game in this skeleton with the provided client (or extend the client). What can you come up with?

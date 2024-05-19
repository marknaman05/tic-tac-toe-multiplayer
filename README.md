# Tic Tac Toe App

## Project Details

- **Name:** Tic Tac Toe
- **Author:** Krishnam Garg, Namam Markhedhkar


## Description

This project is a Tic Tac Toe app that allows users to play the game in either single-player or two-player mode. In single-player mode, users play against the app's logic, while in two-player mode, two users can play against each other. The app includes a sign-in screen, a dashboard displaying the user's score, a list of open games, and a game board for playing Tic Tac Toe. The app also supports features such as sign-out and confirmation dialogs for forfeiting games.

## Screenshots

Include screenshots of the app in action, showcasing the sign-in screen, dashboard, and the Tic Tac Toe game board.

<img src="https://github.com/csf314-2023/a5-tic-tac-toe-a5-naman-krishnam/blob/54454852cc40c21519cd569fbb91769ee93ccf21/Screenshot_20231204_130550_Tic%20Tac%20Toe.jpg" width="200" height="400" />

*Figure 1. Sign-in screen*

<img src="https://github.com/csf314-2023/a5-tic-tac-toe-a5-naman-krishnam/blob/770d0c8d2206e5b979a70b8f55422ef06c9f62bb/Screenshot_20231204_130741_Tic%20Tac%20Toe.jpg" width="200" height="400" />

*Figure 2. Dashboard*

<img src="https://github.com/csf314-2023/a5-tic-tac-toe-a5-naman-krishnam/blob/770d0c8d2206e5b979a70b8f55422ef06c9f62bb/Screenshot_20231204_130749_Tic%20Tac%20Toe.jpg" width="200" height="400" />

*Figure 3. Freshly created game*



## Tasks Completion

1. **Sign-in Screen:**
    - Implemented a sign-in screen where users can register and sign in using email and password.
    - Added a "Sign out" button in the options menu that returns the user to the sign-in screen when clicked.

2. **Dashboard:**
    - Upon successful sign-in, users are shown a dashboard containing their score (wins and losses), a list of open games, and a floating action button.
    - The floating action button opens a dialog prompting the user to choose between creating a single-player or two-player game.

3. **Single-player Mode:**
    - Users make the first move, and the app waits for their input.
    - Marking a cell with an "X" triggers a check for a win. If the user wins, a "Congratulations!" dialog appears, and clicking "OK" updates the win count on the dashboard.
    - If the user doesn't win, the app marks another cell with an "O" and checks for a loss. If the user loses, a "Sorry!" dialog appears, and clicking "OK" updates the loss count on the dashboard.
    - A drawn game triggers a dialog indicating the outcome, and the user is returned to the dashboard.

4. **Two-player Mode:**
    - Implemented a mechanism for two-way communication, optionally using Firebase.
    - The user who created the game makes the first move, and cells are disabled for the other player until then.
    - Dialogs indicating the game outcome are displayed at each turn.
    - Confirmatory dialog for forfeiting the game with score updates for both players.

## Estimated Hours
- We spent almost 50 hrs into this project.
## Difficulty Rating
- 9, as it was difficult to implement two player game 



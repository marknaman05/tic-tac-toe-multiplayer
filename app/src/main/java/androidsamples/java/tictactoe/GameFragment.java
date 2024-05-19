package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;
  private static final String KEY_ARRAY = "GAME_ARRAY";

  private final Button[] mButtons = new Button[GRID_SIZE];
  private NavController mNavController;

  private boolean gameFinish = false;

  private boolean isSinglePlayer = true;

  private String UsrChar = "X";
  private String CompChar = "O";

  private boolean Turn = true;
  private String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};

  private DatabaseReference userRef;
  TextView display;

  private DatabaseReference gameReference;
  private boolean isHost = true;


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    //update the game array from the saved instance
    if (savedInstanceState != null) {
      gameArray = savedInstanceState.getStringArray(KEY_ARRAY);
//        Turn = savedInstanceState.getBoolean("TURN");
//        gameFinish = savedInstanceState.getBoolean("FINISH");
    } else {
      //if no saved instance, start a new game
      gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
      Turn = true;
      gameFinish = false;
    }


    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType());

    isSinglePlayer = (args.getGameType().equals("One-Player"));

    userRef = FirebaseDatabase.getInstance("https://tico-b01c4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());





    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        Log.d(TAG, "Back pressed");
        if (!gameFinish) {
          AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                  .setTitle(R.string.confirm)
                  .setMessage(R.string.forfeit_game_dialog_message)
                  .setPositiveButton(R.string.yes, (d, which) -> {
                    if (!isSinglePlayer) {
                      userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                          int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                          value = value + 1;
                          dataSnapshot.getRef().child("lost").setValue(value);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                      });
                    }
                    mNavController.popBackStack();
                  })
                  .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                  .create();
          dialog.show();
        } else {
          assert getParentFragment() != null;
          NavHostFragment.findNavController(getParentFragment()).navigateUp();
        }
      }
    };
  }



  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mNavController = Navigation.findNavController(view);

    mButtons[0] = view.findViewById(R.id.button0);
    mButtons[1] = view.findViewById(R.id.button1);
    mButtons[2] = view.findViewById(R.id.button2);

    mButtons[3] = view.findViewById(R.id.button3);
    mButtons[4] = view.findViewById(R.id.button4);
    mButtons[5] = view.findViewById(R.id.button5);

    mButtons[6] = view.findViewById(R.id.button6);
    mButtons[7] = view.findViewById(R.id.button7);
    mButtons[8] = view.findViewById(R.id.button8);

    // all the clickables should be false after rotation
    for (int i = 0; i < 9; i++) {
      if (!gameArray[i].equals("")) {
        mButtons[i].setText(gameArray[i]);
        mButtons[i].setClickable(false);
      }
    }

    for (int i = 0; i < mButtons.length; i++) {
      int finalI = i;
      mButtons[i].setOnClickListener(v -> {
        if (Turn){
          Log.d(TAG, "Button " + finalI + " clicked");
          ((Button) v).setText(UsrChar);
          gameArray[finalI] = UsrChar;
          // unusable
          v.setClickable(false);
          display = view.findViewById(R.id.textView4);
          display.setText(R.string.waiting);

          int win = checkWinOrLose();
          if (win == 1 || win == -1) {
            Log.d(TAG, "win: " + win);
            endGame(win);
            return;
          }
          else if (checkDraw()) {

            endGame(0);
            return;
          }
          Turn = !Turn;

          if (isSinglePlayer) {
            generateRandomNumber();
          }
        } else {
          Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }






  private void updateUI() {
    for (int i = 0; i < 9; i++) {
      String v = gameArray[i];
      if (!v.isEmpty()) {
        mButtons[i].setText(v);
        mButtons[i].setClickable(false);
      }
    }
  }


  private void generateRandomNumber() {
    Random random = new Random();
    int randomNumber = random.nextInt(9);
    if (gameArray[randomNumber].equals("")) {
      gameArray[randomNumber] = CompChar;
      mButtons[randomNumber].setText(CompChar);
      mButtons[randomNumber].setClickable(false);
      Turn = !Turn;
      //display.setText("Your Turn");
      int win = checkWinOrLose();
      if(win == 1 || win == -1) {
        endGame(win);
      } else if (checkDraw()) {
        endGame(0);
      }
    } else {
      generateRandomNumber();
    }
  }

  //Check if User won or lost
  private int checkWinOrLose() {
    String winChar = "";
    if(gameArray[0].equals(gameArray[1]) && gameArray[1].equals(gameArray[2]) && !gameArray[0].equals("")) {
      winChar = gameArray[0];
    } else if(gameArray[3].equals(gameArray[4]) && gameArray[4].equals(gameArray[5]) && !gameArray[3].equals("")) {
      winChar = gameArray[3];
    } else if(gameArray[6].equals(gameArray[7]) && gameArray[7].equals(gameArray[8]) && !gameArray[6].equals("")) {
      winChar = gameArray[6];
    } else if(gameArray[0].equals(gameArray[3]) && gameArray[3].equals(gameArray[6]) && !gameArray[0].equals("")) {
      winChar = gameArray[0];
    } else if(gameArray[1].equals(gameArray[4]) && gameArray[4].equals(gameArray[7]) && !gameArray[1].equals("")) {
      winChar = gameArray[1];
    } else if(gameArray[2].equals(gameArray[5]) && gameArray[5].equals(gameArray[8]) && !gameArray[2].equals("")) {
      winChar = gameArray[2];
    } else if(gameArray[0].equals(gameArray[4]) && gameArray[4].equals(gameArray[8]) && !gameArray[0].equals("")) {
      winChar = gameArray[0];
    } else if(gameArray[2].equals(gameArray[4]) && gameArray[4].equals(gameArray[6]) && !gameArray[2].equals("")) {
      winChar = gameArray[2];
    } else return 0;
    return winChar.equals(UsrChar) ? 1 : -1;
  }

  //check draw
  private boolean checkDraw() {
    if(checkWinOrLose() != 0) {
      return false;
    }
    for (String s : gameArray) {
      if (s.equals("")) {
        return false;
      }
    }
    return true;
  }

  private void endGame(int win) {
    switch (win) {
      case 1:
        display.setText(R.string.you_win);
        if (!gameFinish) {
          userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              DataSnapshot wonSnapshot = dataSnapshot.child("won");
              if (wonSnapshot.exists()) {
                Object wonValue = wonSnapshot.getValue();
                if (wonValue != null) {
                  int value = Integer.parseInt(wonValue.toString());
                  value = value + 1;
                  dataSnapshot.getRef().child("won").setValue(value);
                } else {
                  // Handle the case where "won" value is null
                  Log.e(TAG, "Value for 'won' is null");
                }
              } else {
                // Handle the case where "won" key doesn't exist
                Log.e(TAG, "'won' key doesn't exist");
              }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
        break;
      case -1:
        display.setText(R.string.you_lose);
        if (!gameFinish) {
          userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("lost").setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
        break;
      case 0:
        display.setText(R.string.draw);
        if (!gameFinish) {
          userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int value = Integer.parseInt(dataSnapshot.child("draw").getValue().toString());
              value = value + 1;
              dataSnapshot.getRef().child("draw").setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        }
        break;
      default:
        display.setText(R.string.error);
        Log.i("CHECKING DRAW", "Error: " + win);
        break;
    }

    for (int i = 0; i < 9; i++) {
      mButtons[i].setClickable(false);
    }
    gameFinish = true;

    assert getParentFragment() != null;
    //NavHostFragment.findNavController(getParentFragment()).navigateUp();
    //Dialog Box showing Congratulations and on OK go back to Dashboard
    new AlertDialog.Builder(getContext())
            .setTitle("RESULT!!")
            .setMessage(win == 1 ? R.string.you_win : win == -1 ? R.string.you_lose : R.string.draw)
            .setPositiveButton("OK", (d, which) -> {
              // TODO update loss count
              //mNavController.popBackStack();
              NavHostFragment.findNavController(getParentFragment()).navigateUp();
            })
            .create()
            .show();

  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    // Save the array to the savedInstanceState
    outState.putStringArray(KEY_ARRAY, gameArray);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
}

}
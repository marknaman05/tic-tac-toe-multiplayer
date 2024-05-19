package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;

  private FirebaseAuth mAuth = FirebaseAuth.getInstance();

  private FirebaseDatabase db = FirebaseDatabase.getInstance();

  private TextView won, lost, draw;

  private DatabaseReference userRef;




  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    won = view.findViewById(R.id.txt_score);
    lost = view.findViewById(R.id.txt_loss);
    draw = view.findViewById(R.id.txt_draws);

    // TODO if a user is not logged in, go to LoginFragment
    FirebaseUser mUser = mAuth.getCurrentUser();
    if (mUser == null) {
      Navigation.findNavController(view).navigate(R.id.action_need_auth);
      return;
    }

    userRef = FirebaseDatabase.getInstance("https://tico-b01c4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    userRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange( DataSnapshot snapshot) {
        if(snapshot.exists()){
          won.setText(Objects.requireNonNull(snapshot.child("won").getValue()).toString());
          lost.setText(Objects.requireNonNull(snapshot.child("lost").getValue()).toString());
          draw.setText(Objects.requireNonNull(snapshot.child("draw").getValue()).toString());
        }
        else{
          won.setText("0");
          lost.setText("0");
          draw.setText("0");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {

      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";

        if (which == DialogInterface.BUTTON_POSITIVE) {
          gameType = getString(R.string.two_player);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          gameType = getString(R.string.one_player);
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type as a parameter to the action
        // extract it in GameFragment in a type safe way
        NavDirections action = DashboardFragmentDirections.actionGame(gameType);
        mNavController.navigate(action);
      };

      // create the dialog
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
              .setTitle(R.string.new_game)
              .setMessage(R.string.new_game_dialog_message)
              .setPositiveButton(R.string.two_player, listener)
              .setNegativeButton(R.string.one_player, listener)
              .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
              .create();
      dialog.show();
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
}
}
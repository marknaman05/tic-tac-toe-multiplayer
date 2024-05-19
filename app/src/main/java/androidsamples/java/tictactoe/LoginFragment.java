package androidsamples.java.tictactoe;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginFragment extends Fragment {

     FirebaseAuth mAuth = FirebaseAuth.getInstance();
     EditText mEmail;
     EditText mPassword;
    NavController mNavController;
    FirebaseUser mUser = mAuth.getCurrentUser();
    private DatabaseReference userRefrence;
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userRefrence = FirebaseDatabase.getInstance("https://tico-b01c4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users");
        // TODO if a user is logged in, go to Dashboard
        //userRefrence = FirebaseDatabase.getInstance("https://tico-b01c4-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mPassword = view.findViewById(R.id.edit_password);
        mEmail = view.findViewById(R.id.edit_email);


        view.findViewById(R.id.btn_log_in)
                .setOnClickListener(v -> {

                    // TODO implement sign in logic
                    mNavController = Navigation.findNavController(v);
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();
                    if(email.isEmpty() || password.isEmpty()){
                        Toast.makeText(getContext(),"Enter Email and Password",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");

                                            NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                                            mNavController.navigate(action);
                                            userRefrence.child(task.getResult().getUser().getUid()).child("won").setValue(0);
                                            userRefrence.child(task.getResult().getUser().getUid()).child("lost").setValue(0);
                                            userRefrence.child(task.getResult().getUser().getUid()).child("draw").setValue(0);
                                        } else {
//
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                Toast.makeText(getActivity(),
                                                        "Enter a password of length greater than six characters.",
                                                        Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(getActivity(), "Incorrect credentials.", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                            catch (Exception e) {
                                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        mUser = mAuth.getCurrentUser();
                                                        NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                                                        mNavController.navigate(action);

                                                    }
                                                    else {
                                                        Log.w(TAG, "signInWithEmail:failure", task1.getException());
                                                        Toast.makeText(getActivity(), "Login failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                });
                                            }
                                        }
             });});

        return view;
    }



    // No options menu in login fragment.
}
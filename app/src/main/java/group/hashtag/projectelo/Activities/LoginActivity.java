package group.hashtag.projectelo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.hashtag.projectelo.Handlers.UserHandler;
import group.hashtag.projectelo.R;

/**
 * Created by nikhilkamath on 11/02/18.
 */

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    ImageView googleSignIn;
    int RC_SIGN_IN = 110;
    DatabaseReference mDatabase;
    private EditText inputEmail, inputPassword;
    private TextInputLayout emailInput, passwordInput;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;


    /**
     * Adapted From: Validation code taken from:- https://stackoverflow.com/a/6119777/3966666
     * @param email
     * @return
     */

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("users");


        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }


        // set the view now
        setContentView(R.layout.login_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailInput = findViewById(R.id.input_layout_email_login);
        passwordInput = findViewById(R.id.input_layout_password_login);
        inputEmail = findViewById(R.id.login_email);
        inputPassword = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.register_button);
        btnLogin = findViewById(R.id.login_button);
        btnReset = findViewById(R.id.login_btn_reset_password);
        googleSignIn = findViewById(R.id.google_button);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Register_Activity.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    passwordInput.setError(null);
                    emailInput.setError("Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    emailInput.setError(null);
                    passwordInput.setError("Enter correct Password");
                    return;
                }
                if (!isEmailValid(email)) {
                    passwordInput.setError(null);
                    emailInput.setError("Enter a valid email address");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user

                /**
                 * The code to auth users is taken from Firebase docs and from Android Authority
                 *  @link:https://www.androidauthority.com/introduction-to-firebase-765262/
                 **/
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        passwordInput.setError("Enter correct Password");
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to log you in", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    passwordInput.setError(null);
                                    emailInput.setError(null);
                                    hideKeyboard(v);
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            Log.e(LoginActivity.class.getCanonicalName(), "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * Adapted From: Code is adapted from:- https://stackoverflow.com/a/19828165/3966666
     * @param view
     */

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * The code to auth users is taken from Firebase docs
     *
     **/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(user.getUid())) {

                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Log.e(LoginActivity.class.getCanonicalName(), "signInWithCredential:success");
                                        } else {

                                            String userId = user.getUid();
                                            UserHandler userHandler = new UserHandler();
                                            userHandler.setName(user.getDisplayName());
                                            UserHandler userhandler = new UserHandler(user.getDisplayName(), userId, user.getEmail());

                                            mDatabase.child(userId).setValue(userhandler);


                                            Intent intent = new Intent(getApplicationContext(), ProfileSetup.class);
                                            Bundle b = new Bundle();
                                            b.putString("userName", user.getDisplayName());
                                            b.putString("userEmail", user.getEmail());
                                            b.putString("userId", userId);
                                            intent.putExtras(b);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("Here", "" + databaseError);
                                    }
                                });
                            }


                        } else {
                            Log.e(LoginActivity.class.getCanonicalName(), "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }
}
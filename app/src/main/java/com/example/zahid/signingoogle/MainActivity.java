package com.example.zahid.signingoogle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Google sign in api Client
    GoogleSignInClient mGoogleSignInClient;

    //Define Request code for Sign In
    private int RC_SIGN_IN = 6;

    TextView textViewEmail;
    TextView textViewPersonName;
    ImageView imageViewProfilePic;
    LinearLayout llProfileLayout;

    //Logout Button declaration
    Button buttonLogout;

    //Sign in button Declaration
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind views
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPersonName = findViewById(R.id.textViewPersonName);
        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        llProfileLayout = findViewById(R.id.llProfileLayout);
        signInButton = findViewById(R.id.sign_in_button);
        buttonLogout = findViewById(R.id.buttonLogout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //get Sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //get currently signed in user returns null if there is no logged in user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //update ui
        updateUI(account);
    }

    //Method to signIn
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //method to sign out
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateUI(null);
                        }
                    }
                });
    }

    //Handle sign in results
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        //Account is not null then user is logged in
        if (account != null) {
            buttonLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                }
            });
            signInButton.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
            textViewEmail.setText(account.getEmail());
            textViewPersonName.setText(account.getDisplayName());
            Uri imageUri = account.getPhotoUrl();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageViewProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Picasso.with(this).load(account.getPhotoUrl()).fit().into(imageViewProfilePic);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            //user is not logged in
            // Set the dimensions of the sign-in button.
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
            signInButton.setVisibility(View.VISIBLE);
            buttonLogout.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}

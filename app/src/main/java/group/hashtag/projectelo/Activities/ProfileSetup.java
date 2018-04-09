package group.hashtag.projectelo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import group.hashtag.projectelo.Handlers.UserHandler;
import group.hashtag.projectelo.R;

public class ProfileSetup extends AppCompatActivity {

    TextView username;
    Spinner country, month, year, date, gender;
    EditText webLink;
    Button next;
    DatabaseReference userRef;
    FirebaseUser auth;
    String stringUserId, stringUserName, stringUserEmail;
    Integer likes;
    CircleImageView displayImage;
    ProgressDialog savingData;
    StorageReference storageRef;
    String downloadURLString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);
        Typeface ReemKufi_Regular = Typeface.createFromAsset(getAssets(), "fonts/ReemKufi-Regular.ttf");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        downloadURLString = "";
        storageRef = storage.getReference();
        savingData = new ProgressDialog(this);
        username = findViewById(R.id.username_textview);
        country = findViewById(R.id.spinnercountry);
        date = findViewById(R.id.dob_date);
        month = findViewById(R.id.dob_month);
        year = findViewById(R.id.dob_year);
        gender = findViewById(R.id.spinner_gender);
        webLink = findViewById(R.id.webLink);
        next = findViewById(R.id.btnNext);
        /**
         * Adapted From: "hdodenhof/CircleImageView", GitHub, 2018. [Online]. Available:  https://github.com/hdodenhof/CircleImageView. [Accessed: 31- Mar- 2018].
         */
        displayImage = findViewById(R.id.userDisplayPic);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stringUserId = bundle.getString("userId");
            stringUserName = bundle.getString("userName");
            stringUserEmail = bundle.getString("userEmail");
        }
        username.setText(stringUserName);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("Male")) {
                    displayImage.setImageDrawable(getResources().getDrawable(R.drawable.male));
                } else if (adapterView.getItemAtPosition(i).toString().equals("Female")) {
                    displayImage.setImageDrawable(getResources().getDrawable(R.drawable.female));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserDetail();
            }
        });

    }

    public void addUserDetail() {
        final String userCountry = country.getSelectedItem().toString();
        final String userBirthDate = date.getSelectedItem().toString();
        final String userBirthMonth = month.getSelectedItem().toString();
        final String userBirthYear = year.getSelectedItem().toString();
        final String userGender = gender.getSelectedItem().toString();
        final String userWebLink = webLink.getText().toString();
        displayImage.setDrawingCacheEnabled(true);
        Bitmap bitmap = displayImage.getDrawingCache();
        likes = 0;
        uploadDisplayPic(bitmap);
        if (downloadURLString.equals("")) {
            savingData.setMessage("Uploading");
            savingData.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(userCountry) && !TextUtils.isEmpty(userBirthMonth) && !TextUtils.isEmpty(userBirthYear)) {
                        savingData.dismiss();
                        UserHandler item = new UserHandler(stringUserName, stringUserId, userCountry, userBirthMonth, userBirthYear, userWebLink, stringUserEmail, userGender, userBirthDate, likes.toString(), downloadURLString);
                        userRef.child(stringUserId).setValue(item);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    } else {
                    }

                }
            }, 5000);

        } else {
        }
    }

    public void uploadDisplayPic(final Bitmap downloadUri) {
        StorageReference reviewImageRef = storageRef.child(stringUserId + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        downloadUri.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datai = baos.toByteArray();

        UploadTask uploadTask = reviewImageRef.putBytes(datai);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                downloadURLString = downloadUrl.toString();
            }
        });

    }
}

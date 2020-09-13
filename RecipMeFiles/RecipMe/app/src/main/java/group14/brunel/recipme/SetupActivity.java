package group14.brunel.recipme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private FirebaseAuth fAuth;

    private ImageButton setupImageButton;
    private EditText nameField;
    private Button submitButton;
    private static final int GALLERY_REQUEST = 1;

    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        fAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference().child("Profile_images");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);

        setupImageButton = (ImageButton) findViewById(R.id.setup_image_button);
        nameField = (EditText) findViewById(R.id.name_field);
        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupAccount();
            }
        });

        setupImageButton.setOnClickListener(new View.OnClickListener() {
            // Get an image
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, GALLERY_REQUEST);
            }
        });
    }

    private void SetupAccount() {

        final String name = nameField.getText().toString().trim();
        final String user_id = fAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && imageUri != null){

            progressDialog.setMessage("Finishing setup...");
            progressDialog.show();

            StorageReference filepath = storageRef.child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    databaseRef.child(user_id).child("name").setValue(name);
                    databaseRef.child(user_id).child("image").setValue(downloadUri);

                    progressDialog.dismiss();

                    Intent homeIntent = new Intent(SetupActivity.this, Home.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                setupImageButton.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

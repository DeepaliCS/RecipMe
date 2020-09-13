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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileEdit extends AppCompatActivity {

    private DatabaseReference fDatabase;
    private StorageReference storageRef;
    private FirebaseAuth fAuth;

    private ImageButton editProfilePhoto;
    private ImageButton editProfileCoverPhoto;
    private EditText editProfileName;
    private EditText editProfileAbout;
    private Button saveButton;

    private String userID = null;
    private String postKey = null;

    private static final int GALLERY_REQUEST = 1;
    private boolean isCoverPhoto = false;

    private Uri coverPhotoUri = null;
    private Uri profilePhotoUri = null;
    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference().child("Profile_images");
        fAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        // Get user id
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        editProfilePhoto = (ImageButton) findViewById(R.id.user_profile_photo);
        editProfileCoverPhoto = (ImageButton) findViewById(R.id.header_cover_image);
        editProfileName = (EditText) findViewById(R.id.user_profile_name);
        editProfileAbout = (EditText) findViewById(R.id.aboutField);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Run method
                SaveProfile();
            }
        });

        // When coverphoto image button is clicked
        editProfileCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initiate variable for later use
                isCoverPhoto = true;

                // Get image from gallery
                Intent getCoverPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                getCoverPhoto.setType("image/*");
                startActivityForResult(getCoverPhoto, GALLERY_REQUEST);
            }
        });

        // Profile photo
        editProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCoverPhoto = false;

                Intent getProfilePhoto = new Intent(Intent.ACTION_GET_CONTENT);
                getProfilePhoto.setType("image/*");
                startActivityForResult(getProfilePhoto, GALLERY_REQUEST);
            }
        });

        // Get the user's current content
        fDatabase.child(userID).addValueEventListener(new ValueEventListener() {

            // Display the current data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getCoverImage = (String) dataSnapshot.child("cover_image").getValue();
                String getProfileImage = (String) dataSnapshot.child("image").getValue();
                String getUsername = (String) dataSnapshot.child("name").getValue();
                String getAbout = (String) dataSnapshot.child("about").getValue();

                if(getCoverImage == ""){
                    // Do nothing
                }
                else {
                    Picasso.with(ProfileEdit.this).load(getCoverImage).into(editProfileCoverPhoto);
                }

                if(getProfileImage == ""){
                    // Do nothing
                }
                else {
                    Picasso.with(ProfileEdit.this).load(getProfileImage).into(editProfilePhoto);
                }

                editProfileAbout.setText(getAbout);
                editProfileName.setText(getUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SaveProfile(){

        final String name = editProfileName.getText().toString().trim();
        final String about = editProfileAbout.getText().toString();
        final String user_id = fAuth.getCurrentUser().getUid();

        // Error handling
        if(!TextUtils.isEmpty(name) && profilePhotoUri != null){

            progressDialog.setMessage("Updating profile...");
            progressDialog.show();

            // Specifying image location (Storage on firebase)
            StorageReference filepathProfileImage = storageRef.child("cover_photo").child(profilePhotoUri.getLastPathSegment());
            StorageReference filepathCoverImage = storageRef.child(coverPhotoUri.getLastPathSegment());

            // Add data to specific fields
            fDatabase.child(user_id).child("name").setValue(name);
            fDatabase.child(user_id).child("about").setValue(about);

            // Put image to file location
            filepathProfileImage.putFile(profilePhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    fDatabase.child(user_id).child("image").setValue(downloadUri);
                }
            });

            // Put image to file location
            filepathCoverImage.putFile(coverPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    fDatabase.child(user_id).child("cover_image").setValue(downloadUri);
                }
            });

            progressDialog.dismiss();

            // Navigate to profile
            Intent profileIntent = new Intent(ProfileEdit.this, Profile.class);
            profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(profileIntent);
        }
    }

    // When retrieving image from the user's gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            // If cover photo image button is clicked then do not set a fixed aspect ratio
            if (isCoverPhoto == true) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            else{
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                if(isCoverPhoto == true){
                    coverPhotoUri = imageUri;
                    editProfileCoverPhoto.setImageURI(coverPhotoUri);
                }
                else {
                    profilePhotoUri = imageUri;
                    editProfilePhoto.setImageURI(profilePhotoUri);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}

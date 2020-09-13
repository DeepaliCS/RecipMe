package group14.brunel.recipme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Profile extends AppCompatActivity {

    private String userID = null;
    private ImageView profilePhoto;
    private ImageView coverPhoto;
    private TextView user_profile_name;
    private TextView aboutText;

    private DatabaseReference fDatabase;
    private FirebaseAuth fAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        fDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        fAuth = FirebaseAuth.getInstance();

        // Get the user id
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Initiate variables
        profilePhoto = (ImageView) findViewById(R.id.user_profile_photo);
        coverPhoto = (ImageView) findViewById(R.id.header_cover_image);
        user_profile_name = (TextView) findViewById(R.id.user_profile_name);
        aboutText = (TextView) findViewById(R.id.aboutField);

        // Retrieve data from the database
        fDatabase.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the values from each parent
                String postUsername = (String) dataSnapshot.child("name").getValue();
                String postImage = (String) dataSnapshot.child("image").getValue();
                String postCoverImage = (String) dataSnapshot.child("cover_image").getValue();
                String postAbout = (String) dataSnapshot.child("about").getValue();
                user_profile_name.setText(postUsername);
                aboutText.setText(postAbout);

                if(postImage == ""){
                    // Do nothing
                }
                else {
                    // Load image
                    Picasso.with(Profile.this).load(postImage).into(profilePhoto);
                }

                if(postCoverImage == ""){
                    // Do nothing
                }
                else {
                    // Load image
                    Picasso.with(Profile.this).load(postCoverImage).into(coverPhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editprofile:
                //0Toast.makeText(this,"it works", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Profile.this,ProfileEdit.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

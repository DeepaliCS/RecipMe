package group14.brunel.recipme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CreateRecipe extends AppCompatActivity {

    // Views for creating the recipe
    private ImageButton selectImage;
    private EditText recipeTitle;
    private EditText recipeDesc;
    private Spinner recipeDifficulty;
    private EditText recipePrepTime;
    private EditText recipeCookTime;
    private EditText recipeIngredients;
    private EditText recipeMethod;
    private ImageButton addIngredientImgBtn;
    private ImageButton addMethodImgBtn;

    private ArrayList<String> alingredientsList = new ArrayList<String>();
    private List ingredientsList;

    private ArrayList<String> alMethodList = new ArrayList<String>();
    private List methodList;

    // Creates the recipe on Firebase
    private Button selectButton;

    // Image and storage integration on Firebase
    private Uri imageUri = null;
    private static final int GALLERY_REQUEST = 1;

    // Firebase Integration
    private DatabaseReference mDatabase;
    private Firebase mRef;
    private StorageReference storageRef;

    private FirebaseUser fCurrentUser;

    // Allows the user to understand why creating the recipe is taking long
    // Spinner not showing
    // needs fixing (minor bug)
    private ProgressDialog progress;

    private static final String TAG = "Create Recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        Firebase.setAndroidContext(this);

        // Spinner for how difficult it is to create the recipe
        Spinner spin = (Spinner) findViewById(R.id.difficulty_spin);
        ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Difficulty,android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        mRef = new Firebase("https://recipme-6db76.firebaseio.com/created-recipes");


        // storage reference and database reference on Firebase
        storageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Created-Recipe");

        selectImage = (ImageButton) findViewById(R.id.recipe_image);

        recipeTitle = (EditText) findViewById(R.id.recipe_title);
        recipeDesc = (EditText) findViewById(R.id.recipe_desc);
        recipeDifficulty = (Spinner) findViewById(R.id.difficulty_spin);
        recipePrepTime = (EditText) findViewById(R.id.input_prep_mins);
        recipeCookTime = (EditText) findViewById(R.id.input_cook_mins);
        recipeIngredients= (EditText) findViewById(R.id.et_ingredients);
        recipeMethod = (EditText) findViewById(R.id.et_method);

//        addIngredientImgBtn = (ImageButton) findViewById(R.id.add_ingredient_img_btn);
//        addMethodImgBtn = (ImageButton) findViewById(R.id.add_step_img_btn);

        selectButton = (Button) findViewById(R.id.create_recipe_btn);

        progress = new ProgressDialog(this);

        selectImage.setOnClickListener(new View.OnClickListener() {

            // Get an image
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");
                startActivityForResult(imageIntent, GALLERY_REQUEST);
            }
        });


        selectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createRecipe();
            }
        });

//        GetIngredients();
//        GetMethod();
    }

//    private void GetIngredients() {
//        addIngredientImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String ingredientItem = etInsertIng.getText().toString().trim();
//                alingredientsList.add(ingredientItem);
//                String[] ingredientsArr = new String[alingredientsList.size()];
//                ingredientsArr = alingredientsList.toArray(ingredientsArr);
//                ingredientsList = new ArrayList<String>(Arrays.asList(ingredientsArr));
//            }
//        });
//    }
//
//    private void GetMethod() {
//        addMethodImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String methodItem = etInsertMethod.getText().toString().trim();
//                alMethodList.add(methodItem);
//                String[] methodArr = new String[alMethodList.size()];
//                methodArr = alMethodList.toArray(methodArr);
//                methodList = new ArrayList<String>(Arrays.asList(methodArr));
//            }
//        });
//
//    }

    // Creates the recipe after clicking the selectButton
    private void createRecipe() {

        progress.setMessage("Posting...");

        final String titleVal = recipeTitle.getText().toString().trim();
        final String descVal = recipeDesc.getText().toString().trim();
        final String diffVal = recipeDifficulty.getSelectedItem().toString();
        final String prepTimeVal = recipePrepTime.getText().toString().trim();
        final String cookTimeVal = recipeCookTime.getText().toString().trim();
        final String ingredientVal =recipeIngredients.getText().toString().trim();
        final String methodVal = recipeMethod.getText().toString().trim();


        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && imageUri != null){

            progress.show();

            StorageReference filepath = storageRef.child("Recipe_Images").child(imageUri.getLastPathSegment());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newRecipe = mDatabase.push();

                    newRecipe.child("title").setValue(titleVal);
                    newRecipe.child("desc").setValue(descVal);
                    newRecipe.child("image").setValue(downloadUrl.toString());
                    newRecipe.child("difficulty").setValue(diffVal);
                    newRecipe.child("prep_time").setValue(prepTimeVal);
                    newRecipe.child("cook_time").setValue(cookTimeVal);
                    newRecipe.child("user_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newRecipe.child("ingredients").setValue(ingredientVal);
                    newRecipe.child("method").setValue(methodVal);

                    progress.dismiss();

                    Intent homeIntent = new Intent(CreateRecipe.this, Home.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);

                }
            });
        }
    }


    // Retrieve the image and display it in the create_recipe xml
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            selectImage.setImageURI(imageUri);
        }

    }
}

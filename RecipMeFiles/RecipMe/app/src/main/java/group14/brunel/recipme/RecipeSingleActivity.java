package group14.brunel.recipme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeSingleActivity extends AppCompatActivity {

    private String postKey = null;

    private DatabaseReference fDatabase;
    private FirebaseAuth fAuth;

    private ImageView singleRecipeImage;
    private TextView singleRecipeTitle;
    private TextView singleRecipeDesc;
    private TextView singleRecipeDiff;
    private TextView singleRecipePrepTime;
    private TextView singleRecipeCookTime;
    private TextView singleRecipeIngredients;
    private TextView singleRecipeMethod;

    private Button singleRecipeRemoveBtn;

    private List<String> specificRecipeIngredient = new ArrayList<String>();
    private List<String> specificRecipeMethod = new ArrayList<String>();
    private ListView lvIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_single);

        fDatabase = FirebaseDatabase.getInstance().getReference().child("Created-Recipe");
        fAuth = FirebaseAuth.getInstance();
//        fIngredients = FirebaseDatabase.getInstance().getReferenceFromUrl("https://recipme-6db76.firebaseio.com/");

        // Get ingredients and method
//        fDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                collectRecipes((Map<List, Object>) dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        postKey = getIntent().getExtras().getString("recipe_id");

        singleRecipeTitle = (TextView) findViewById(R.id.single_recipe_title);
        singleRecipeDesc = (TextView) findViewById(R.id.single_recipe_desc);
        singleRecipePrepTime = (TextView) findViewById(R.id.single_recipe_prep_time);
        singleRecipeCookTime = (TextView) findViewById(R.id.single_recipe_cook_time);
        singleRecipeDiff = (TextView) findViewById(R.id.single_recipe_diff);
        singleRecipeIngredients = (TextView) findViewById(R.id.single_recipe_ingredient);
        singleRecipeMethod = (TextView) findViewById(R.id.single_recipe_method);
//        singleRecipeIngredients = (TextView) findViewById(R.id.single_recipe_ingredient);
//        singleRecipeMethod = (TextView) findViewById(R.id.single_recipe_method);
        singleRecipeImage = (ImageView) findViewById(R.id.single_recipe_img);
        singleRecipeRemoveBtn = (Button) findViewById(R.id.remove_cuisine_button);

//        lvIngredients = (ListView) findViewById(R.id.lv_ingredients);

//        ArrayAdapter<String> fir = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_1,
//                specificRecipeIngredient
//        );
//
//        lvIngredients.setAdapter(fir);

        fDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String postTitle = (String) dataSnapshot.child("title").getValue();
                String postDesc = (String) dataSnapshot.child("desc").getValue();
                String postImage = (String) dataSnapshot.child("image").getValue();
                String postPrepTime = (String) dataSnapshot.child("prep_time").getValue();
                String postCookTime = (String) dataSnapshot.child("cook_time").getValue();
                String postDiff = (String) dataSnapshot.child("difficulty").getValue();
                String postIngredient = (String) dataSnapshot.child("ingredients").getValue();
                String postMethod = (String) dataSnapshot.child("method").getValue();
                String postUid = (String) dataSnapshot.child("user_id").getValue();

                singleRecipeTitle.setText(postTitle);
                singleRecipeDesc.setText(postDesc);
                singleRecipePrepTime.setText(postPrepTime + " mins");
                singleRecipeCookTime.setText(postCookTime + " mins");
                singleRecipeDiff.setText(postDiff);
                singleRecipeIngredients.setText(postIngredient);
                singleRecipeMethod.setText(postMethod);
//                singleRecipeIngredients.setText(postIngredients);
//                singleRecipeMethod.setText(postMethod);

                Picasso.with(RecipeSingleActivity.this).load(postImage).into(singleRecipeImage);

                if(fAuth.getCurrentUser().getUid().equals(postUid)){
                    singleRecipeRemoveBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        singleRecipeRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fDatabase.child(postKey).removeValue();

                Intent homeIntent = new Intent(RecipeSingleActivity.this, Home.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }

//    private void collectRecipes(Map<List, Object> value) {
//        // iterate through each recipe, ignoring their UID
//        for(Map.Entry<List, Object> entry : value.entrySet()){
//
//            // Get users ingredients and method
//            Map singleRecipe = (Map) entry.getValue();
//
//            specificRecipeIngredient.add(String.valueOf((List) singleRecipe.get("ingredients")));
//            Log.i("Ingredients", specificRecipeIngredient + "");
////            specificRecipeMethod.add((String.valueOf(List)) singleRecipe.get("method"));
////            Log.i("Method", specificRecipeMethod + "");
//
//        };
//    }
}

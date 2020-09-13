package group14.brunel.recipme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import static group14.brunel.recipme.R.id.post_image;

public class UserCreatedRecipes extends AppCompatActivity {

    // Firebase reference
    DatabaseReference mDatabase;
    DatabaseReference fDatabaseCurrentUser;

    private FirebaseAuth mAuth;

    private RecyclerView recipeList;

    private Query fQueryCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_created_recipes);

        mAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Created-Recipe");
        fDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference("Created-Recipe");

        String currentUserId = mAuth.getCurrentUser().getUid();

        fDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Created-Recipe");

        fQueryCurrentUser = fDatabaseCurrentUser.orderByChild("user_id").equalTo(currentUserId);

        Log.i("UserCreatedRecipe: ", currentUserId);

        recipeList = (RecyclerView) findViewById(R.id.user_created_recipe_list);
        recipeList.setHasFixedSize(true);
        recipeList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart () {
        super.onStart();


        FirebaseRecyclerAdapter<Recipe, RecipeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recipe, RecipeViewHolder>(
                Recipe.class,
                R.layout.recipe_row,
                RecipeViewHolder.class,
                fQueryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(RecipeViewHolder viewHolder, Recipe model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setPrepTime(model.getPrep_time());
                viewHolder.setCookTime(model.getCook_time());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent singleRecipeIntent = new Intent(UserCreatedRecipes.this, RecipeSingleActivity.class);
                        singleRecipeIntent.putExtra("recipe_id", post_key);
                        startActivity(singleRecipeIntent);

                    }
                });

            }
        };

        recipeList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{

        View view;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setTitle(String title){
            TextView postTitle = (TextView) view.findViewById(R.id.post_title);
            postTitle.setText(title);
        }

        public void setDesc(String desc){
            TextView postDesc = (TextView) view.findViewById(R.id.post_text);
            postDesc.setText(desc);
        }

        public void setPrepTime(String prepTime){
            TextView postPrepTime = (TextView) view.findViewById(R.id.post_prep_time);
            postPrepTime.setText(prepTime + "m");
        }

        public void setCookTime(String cookTime){
            TextView postCookTime = (TextView) view.findViewById(R.id.post_cook_time);
            postCookTime.setText(cookTime + "m");
        }

        public void setImage(Context ctx, String image){
            ImageView postImage = (ImageView) view.findViewById(post_image);
            Picasso.with(ctx).load(image).into(postImage);
        }

    }

    private void signOut() {
        mAuth.signOut();
    }
}

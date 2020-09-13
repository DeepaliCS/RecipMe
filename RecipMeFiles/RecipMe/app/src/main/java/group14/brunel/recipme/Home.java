package group14.brunel.recipme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity {

    // Firebase reference
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;

    private RecyclerView recipeList;

    private NavigationView nav;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(Home.this, Login.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                //When profile is pressed do something, write code in if statement
                int id = item.getItemId();
                if (id == R.id.menu_add_recipes) {
                    Intent createRecipesIntent = new Intent(Home.this,CreateRecipe.class);
                    startActivity(createRecipesIntent);
                }
                else if(id == R.id.menu_my_profile) {
                    Intent myProfileIntent = new Intent(Home.this,Profile.class);
                    startActivity(myProfileIntent);

                }
                else if(id == R.id.menu_my_recipes) {
                    Intent myRecipesIntent = new Intent(Home.this,UserCreatedRecipes.class);
                    startActivity(myRecipesIntent);

                }

                else if(id == R.id.menu_sign_out) {
                    Toast.makeText(Home.this, "Sign Out", Toast.LENGTH_SHORT).show();
                    signOut();
                }

                return true;
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Created-Recipe");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        recipeList = (RecyclerView) findViewById(R.id.recipe_list);
        recipeList.setHasFixedSize(true);
        recipeList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExist();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Recipe, RecipeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recipe, RecipeViewHolder>(
                Recipe.class,
                R.layout.recipe_row,
                RecipeViewHolder.class,
                mDatabase
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

                        Intent singleRecipeIntent = new Intent(Home.this, RecipeSingleActivity.class);
                        singleRecipeIntent.putExtra("recipe_id", post_key);
                        startActivity(singleRecipeIntent);

                    }
                });

            }
        };

        recipeList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            // Navigate to SetupActivity
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user_id)){
                        Intent setupIntent = new Intent(Home.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
            ImageView postImage = (ImageView) view.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(postImage);
        }

    }

    // Tapping the add button

    private void signOut() {
        mAuth.signOut();
    }
}

package com.ar.dev.todo;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout RRforBackground;

    private ProgressBar progressBar;
    private Button buttonAddTask;
    private EditText editTextTitle, editTextDesc;
    private Dialog alert;

    private TaskAdapter taskAdapter;
    private List<Model> modelTaskList;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Task Drawer (ToDo)");

        try {
            Drawable drawable = getResources().getDrawable(R.mipmap.app_logo_round);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 120, 120, true));
            getSupportActionBar().setLogo(newdrawable);

        }catch (Exception e){
            getSupportActionBar().setLogo(R.mipmap.app_logo_round);
        }


        RRforBackground=findViewById(R.id.RR);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        databaseReference=FirebaseDatabase.getInstance().getReference("All Tasks");
        modelTaskList=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelTaskList.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()){
                    Model model=taskSnapshot.getValue(Model.class);
                    modelTaskList.add(model);
                }
                taskAdapter = new TaskAdapter(MainActivity.this,modelTaskList);
                recyclerView.setAdapter(taskAdapter);
                progressBar.setVisibility(View.GONE);
                RRforBackground.setBackgroundResource(R.color.backgroundColor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_developer) {
            showDeveloperDialogue();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeveloperDialogue() {
        Dialog alert = new Dialog(this);
        alert.setContentView(R.layout.developer_dialogue);
        ImageView img = alert.findViewById(R.id.imageViewDeveloper);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.devimage);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        img.setImageDrawable(roundedBitmapDrawable);
        alert.show();
    }

    public void onClickFloatingButton(View view) {
        alert = new Dialog(this);
        alert.setContentView(R.layout.add_task_dialogue);

        editTextTitle = alert.findViewById(R.id.editTextTitle);
        editTextDesc = alert.findViewById(R.id.editTextDesc);
        buttonAddTask = alert.findViewById(R.id.buttonAddTask);


        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String desc = editTextDesc.getText().toString();

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(desc)){
                    editTextTitle.setError("Title is required");
                    editTextDesc.setError("Description is required");
                    editTextTitle.requestFocus();
                    editTextDesc.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(title)){
                    editTextTitle.setError("Title is required");
                    editTextTitle.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(desc)){
                    editTextDesc.setError("Description is required");
                    editTextDesc.requestFocus();
                    return;
                }

                AddTask(title,desc);
            }
        });
        alert.show();
    }

    private void AddTask(String title, String desc) {

        String id=databaseReference.push().getKey();
        Model model = new Model(id,title,desc);
        databaseReference.child(id).setValue(model);
        alert.dismiss();
        Toast.makeText(this, "Task Inserted", Toast.LENGTH_SHORT).show();
    }
}

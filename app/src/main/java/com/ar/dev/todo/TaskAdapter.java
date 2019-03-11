package com.ar.dev.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Model> taskModel;
    private DatabaseReference databaseReferenceForDelete;

    public TaskAdapter(Context context, List<Model> taskModel) {
        this.context = context;
        this.taskModel = taskModel;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_list_layout, parent, false);

        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, int position) {
        final Model currentTaskModel = taskModel.get(position);
        holder.tvRecyclerViewTitle.setText(currentTaskModel.getTaskTitle());
        holder.getTvRecyclerViewDesc.setText(currentTaskModel.getTaskDesc());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deleteID = currentTaskModel.getTaskID();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this task?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReferenceForDelete = FirebaseDatabase.getInstance().getReference("All Tasks");
                        databaseReferenceForDelete.child(deleteID).removeValue();
                        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();


            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog alert = new Dialog(context);
                alert.setContentView(R.layout.full_screen_text);

                TextView textViewTitleFS = alert.findViewById(R.id.textViewFullScreenTitle);
                TextView textViewDescFS = alert.findViewById(R.id.textViewFullScreenDesc);

                textViewTitleFS.setText(currentTaskModel.getTaskTitle());
                textViewDescFS.setText(currentTaskModel.getTaskDesc());

                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskModel.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout linearLayout;
        public ImageButton btnDelete;
        public TextView tvRecyclerViewTitle, getTvRecyclerViewDesc;

        public TaskViewHolder(View itemView) {
            super(itemView);

            tvRecyclerViewTitle = itemView.findViewById(R.id.textViewTitle);
            getTvRecyclerViewDesc = itemView.findViewById(R.id.textViewDesc);
            btnDelete = itemView.findViewById(R.id.imageButtonDelete);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}

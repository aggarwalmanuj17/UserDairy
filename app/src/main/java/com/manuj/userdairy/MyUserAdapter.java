package com.manuj.userdairy;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserAdapter.MyUserHolder> {
    Context context;
    int resource;
    StorageReference mStorage;
    ArrayList<User> userArrayList;

    public MyUserAdapter(Context context, int resource, ArrayList<User> userArrayList) {
        this.context = context;
        this.resource = resource;
        this.userArrayList = userArrayList;
    }
    @NonNull
    @Override
    public MyUserAdapter.MyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyUserHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserAdapter.MyUserHolder holder, int position) {
                  User user=userArrayList.get(position);
        mStorage = FirebaseStorage.getInstance().getReference();

        mStorage.child("profileImages")
                .child(user.ImageUid + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

           holder.Name.setText(user.FirstName+" "+user.LastName);
           holder.country.setText(user.Country);
           holder.gender.setText(user.Gender+" |");
           holder.dob.setText(user.DateOfBirth+" |");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("users").document();

        holder.delete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(context,"User Deleted Successfully!",Toast.LENGTH_LONG).show();
                               removeUser(user);
                           }else {
                               Toast.makeText(context,"Failed to Delete",Toast.LENGTH_LONG).show();
                           }
                       }
                   });

               }
           });

             }

    private void removeUser(User user){
        userArrayList.remove(user);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }




    public class MyUserHolder extends RecyclerView.ViewHolder {
        ImageView profile,delete;
        TextView Name,gender,dob,country;

        public MyUserHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.profImg);
            delete=itemView.findViewById(R.id.deleteUser);
            Name=itemView.findViewById(R.id.name);
            gender=itemView.findViewById(R.id.Gender);
            dob=itemView.findViewById(R.id.dob);
            country=itemView.findViewById(R.id.count);

        }
    }

}

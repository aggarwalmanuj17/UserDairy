package com.manuj.userdairy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Users_Fragment extends Fragment {

    RecyclerView recyclerView;
    MyUserAdapter myUserAdapter;
    ArrayList<User> userArrayList;
    User user;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_users_, container, false);
         recyclerView=view.findViewById(R.id.recyclerUsers);
         userArrayList=new ArrayList<>();
         user=new User();



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").orderBy(user.DateOfBirth)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() == 0) {
                    return;
                }
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    userArrayList.add(snapshot.toObject(User.class));
                    user=snapshot.toObject(User.class);


                }

                setAdapter();
                myUserAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        return view;
    }




    private void setAdapter() {
         myUserAdapter = new MyUserAdapter(getContext(), R.layout.user_cardview, userArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myUserAdapter);

    }
}
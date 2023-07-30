package com.example.budgetbuddy;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.budgetbuddy.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class IncomeFragment extends Fragment {



    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler view

    private RecyclerView recyclerView;

    //Text view
    private TextView incomeTotalSum;

    //Update edit text

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    //button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int ammount;

    private String post_key;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_income, container, false);
        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totlatvalue = 0;

                for (DataSnapshot mysanapshot:dataSnapshot.getChildren()) {
                Data data=mysanapshot.getValue(Data.class);

                totlatvalue+=data.getAmount();


                String stTotalvale=String.valueOf(totlatvalue);

                incomeTotalSum.setText(stTotalvale);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(MyViewHolder viewHolder, int position, Data model) {
                        viewHolder.setType(model.getType());
                        viewHolder.setNote(model.getNote());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setAmmount(model.getAmount());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                post_key = getRef(viewHolder.getAdapterPosition()).getKey();
                                type = model.getType();
                                note = model.getNote();
                                ammount = model.getAmount();

                                updateDataItem();
                            }
                        });
                    }

                    @Override
                    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.income_recycler_data, parent, false);
                        return new MyViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
    View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_income);
            mType.setText(type);

        }

        private void setNote (String note){
            TextView mNote=mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date){
        TextView mDate=mView.findViewById(R.id.date_txt_income);
        mDate.setText(date);
        }

private void setAmmount(int ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
}}


    private void updateDataItem(){
        AlertDialog.Builder mydialog= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);

        //Set data to edit text..

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnPD_Delete);

        final AlertDialog dialog=mydialog.create();

btnUpdate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        type=edtType.getText().toString().trim();
        note=edtNote.getText().toString().trim();

        String mdammount=String.valueOf(ammount);
        mdammount = edtAmmount.getText().toString().trim();
        int myAmmount = Integer.parseInt(mdammount);

        String mDate = DateFormat.getDateInstance().format(new Date( ));

        Data data = new Data(myAmmount, type, note, post_key,mDate);
        mIncomeDatabase.child(post_key).setValue(data);
        dialog.dismiss();
    }
});
btnDelete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

mIncomeDatabase.child(post_key).removeValue();
    dialog.dismiss();
    }
});

dialog.show();
    }

}
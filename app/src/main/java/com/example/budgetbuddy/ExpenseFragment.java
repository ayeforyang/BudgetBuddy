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
import android.widget.TextView;

import com.example.budgetbuddy.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class ExpenseFragment extends Fragment {

    // Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;

    private TextView expenseSumResult;

    // Edt data item;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    // Data variable

    private String type;
    private String note;
    private int amount;

    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        expenseSumResult = myview.findViewById(R.id.expense_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int expenseSum = 0;
                for (DataSnapshot mysanapshot : dataSnapshot.getChildren()) {
                    Data data = mysanapshot.getValue(Data.class);
                    expenseSum += data.getAmount();
                }
                String strExpenseSum = String.valueOf(expenseSum);
                expenseSumResult.setText(strExpenseSum);
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
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {
                        viewHolder.setDate(model.getDate());
                        viewHolder.setType(model.getType());
                        viewHolder.setNote(model.getNote());
                        viewHolder.setAmount(model.getAmount());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                post_key = getRef(viewHolder.getAdapterPosition()).getKey();
                                type = model.getType();
                                note = model.getNote();
                                amount = model.getAmount();

                                updateDataItem();
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.expense_recycler_data, parent, false);
                        return new MyViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.ammount_txt_expense);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }
    }

    private void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.ammount_edt);
        edtNote = myview.findViewById(R.id.note_edt);
        edtType = myview.findViewById(R.id.type_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btn_upd_Update);
        btnDelete = myview.findViewById(R.id.btnPD_Delete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           type=edtType.getText().toString().trim();
           note=edtNote.getText().toString().trim();
           String stammount=String.valueOf(amount);
           stammount=edtAmmount.getText().toString().trim();
           int intamount=Integer.parseInt(stammount);
           String mDate = DateFormat.getDateInstance().format(new Date());
           Data data= new Data(intamount, type,note,post_key, mDate);
           mExpenseDatabase.child(post_key).setValue(data);

           dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}


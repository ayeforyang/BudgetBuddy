package com.example.budgetbuddy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class OverviewFragment extends Fragment {

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    private TextView totalBalanceResult;

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_overview, container, false);


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();


        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


        //Total income and expense result set...
        totalIncomeResult=myview.findViewById(R.id.income_txt_result_overview);
        totalExpenseResult=myview.findViewById(R.id.expense_txt_result_overview);
        totalBalanceResult=myview.findViewById(R.id.balance_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_id_income_overview);
        mRecyclerExpense=myview.findViewById(R.id.recycler_id_expense_overview);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalIncome = 0;

                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalIncome += data.getAmount();
                }

                String incomeResult = String.valueOf(totalIncome);
                totalIncomeResult.setText(incomeResult);

                // Calculate total expense..
                int finalTotalIncome = totalIncome;
                mExpenseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int totalExpense = 0;
                        for (DataSnapshot mysnapshot : dataSnapshot.getChildren()) {
                            Data data = mysnapshot.getValue(Data.class);
                            totalExpense += data.getAmount();
                        }

                        String expenseResult = String.valueOf(totalExpense);
                        totalExpenseResult.setText(expenseResult);

                        // Calculate remaining balance
                        int remainingBalance = finalTotalIncome - totalExpense;
                        String balanceResult = String.valueOf(remainingBalance);
                        totalBalanceResult.setText(balanceResult);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled event
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });



        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);

        layoutManagerIncome.setReverseLayout(true);
        layoutManagerIncome.setStackFromEnd(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);

        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;




    }

    @Override
    public  void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options)  {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeNote(model.getNote());
                viewHolder.setIncomeDate(model.getDate());
                viewHolder.setIncomeAmmount(model.getAmount());

            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_overview_data, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        FirebaseRecyclerOptions<Data> options2 = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseNote(model.getNote());
                viewHolder.setExpenseDate(model.getDate());
                viewHolder.setmExpenseAmmount(model.getAmount());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_overview_data, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();

    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;

        public IncomeViewHolder (View itemView){
            super(itemView);
            mIncomeView = itemView;
        }

        private  void setIncomeType(String type){
            TextView mType = mIncomeView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setIncomeNote(String note){
            TextView mNote=mIncomeView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setIncomeDate(String date){
            TextView mDate=mIncomeView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setIncomeAmmount(int ammount){
            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_txt_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }

    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;

        public ExpenseViewHolder (View itemView){
            super(itemView);
            mExpenseView = itemView;
        }

        private  void setExpenseType(String type){
            TextView mType = mExpenseView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setExpenseNote(String note){
            TextView mNote=mExpenseView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setmExpenseAmmount(int ammount){
            TextView mAmmount=mExpenseView.findViewById(R.id.ammount_txt_expense);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }
    }


}

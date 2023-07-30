package com.example.budgetbuddy;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddy.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {


    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //floating text

    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    //boolean

    private boolean isOpen=false;

    //animation

    private Animation FadOpen,FadeClose;

    //Dashboard income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    private TextView totalBalanceResult;


    ///Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        //connect floating button

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);

        //connect floating text.
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set...
        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);
        totalBalanceResult=myview.findViewById(R.id.balance_set_result);
        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation connect

        FadOpen= AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);





        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen) {
                    addData();
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);


                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);

                    isOpen=false;

                }else{
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);

                    isOpen=true;

                }
            }
        });

        //Calculate total income..
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

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myview;



    }

    private void ftAnimation(){
        if(isOpen) {
            addData();
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);


            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);

            isOpen=false;

        }else{
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);

            isOpen=true;

        }
    }
    private void addData() {

        //Fab button income...

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              expenseDataInsert();
            }
        });
    }


    public void incomeDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdatas, null);
        mydialog.setView(myviewm);
        AlertDialog dialog=mydialog.create();


        EditText edtAmmount=myviewm.findViewById(R.id.ammount_edt);
        AutoCompleteTextView edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel) ;

        String[] types = new String[]{"Income Earned", "Income Dividend","Income Passive","Income Rental","Income Royalty","Income Salary"}; // Replace with your own dropdown options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, types);
        edtType.setAdapter(adapter);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=edtType.getText().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammontint=Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field..");
                    return;
                }

                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourammontint,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data ADDED", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdatas, null);
        mydialog.setView(myviewm);

        final AlertDialog dialog=mydialog.create();

       dialog.setCancelable(false);

        final EditText ammount=myviewm.findViewById(R.id.ammount_edt);
        final AutoCompleteTextView type=myviewm.findViewById(R.id.type_edt);
        final EditText note=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);

        String[] types = new String[]{"Expense Travel","Expense Shopping","Expense Holidays","Expense Transportation","Expense Gifts","Expense Fuel"}; // Replace with your own dropdown options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, types);
        type.setAdapter(adapter);

        btnSave.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmmount=ammount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();


                if (TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field..");
                    return;
                }
                int inamount=Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmtype)){
                    type.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field..");
                    return;
                }
                String id =mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data=new Data(inamount, tmtype, tmnote,id, mDate);
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data added", Toast.LENGTH_SHORT).show();



                ftAnimation();
                dialog.dismiss();
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

                dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmmount(model.getAmount());
                viewHolder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
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
                viewHolder.setmExpenseType(model.getType());
                viewHolder.setmExpenseAmmount(model.getAmount());
                viewHolder.setmExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();

    }


    //For Income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setmIncomeView(View mIncomeView) {
            this.mIncomeView = mIncomeView;
        }

        public void setIncomeType(String type){
            TextView mtype=mIncomeView.findViewById(R.id.type_income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmmount(int ammount){

            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_income_ds);

            String strAmmount=String.valueOf(ammount);

            mAmmount.setText(strAmmount);
        }

        public void setIncomeDate(String date){
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //For Expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setmExpenseType(String type){
        TextView mtype = mExpenseView.findViewById(R.id.type_expense_ds);
        mtype.setText(type);
        }

        public  void setmExpenseAmmount(int ammount){
            TextView mAmmount = mExpenseView.findViewById(R.id.ammount_expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setmExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }

    }
}
package com.example.buget;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomMoneySpentView extends LinearLayout {
    ImageButton GoodStuff, BadStuff, AddTodayEvent;
    RecyclerView recyclerView;
    TextView CurrentDate;
    TextView MoneySpent;
    TextView MoneySpentUsefully;
    TextView MoneySpentUnuseful;
    TextView MoneySpentThisMonth;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


    DBOpenHelper dbOpenHelper;

    AlertDialog alertDialog;
    MyGridAdapter myGridAdapter;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();




    public CustomMoneySpentView(Context context) {
        super(context);

    }

    public CustomMoneySpentView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        IntializeLayout();

        MoneySpent = findViewById(R.id.money_spent_today);
        String currentDate = eventDateFormate.format(calendar.getTime());
        MoneySpent.setText(Double.toString(CollectMoneyToday(currentDate, "Noone")) + " Lei");

        MoneySpentUnuseful = findViewById(R.id.money_spent_unuseful);
        MoneySpentUnuseful.setText(Double.toString(CollectMoneyToday(currentDate, "Unuseful")) + " Lei");

        MoneySpentUsefully = findViewById(R.id.money_spent_usefully);
        MoneySpentUsefully.setText(Double.toString(CollectMoneyToday(currentDate, "Useful")) + " Lei");

        GoodStuff = findViewById(R.id.GOODSTUFF);
        BadStuff = findViewById(R.id.BADSTUFF);
        AddTodayEvent = findViewById(R.id.ADDTODAYEVENT);

        AddTodayEvent.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(context).inflate(R.layout.add_newevent_layout, null);
                final EditText EventName = addView.findViewById(R.id.eventname);
                final TextView EventThing = addView.findViewById(R.id.whatyoubought);
                ImageButton Unuseful = addView.findViewById(R.id.unuseful);
                ImageButton Usefully = addView.findViewById(R.id.usefully);
                Button AddEvent = addView.findViewById(R.id.addevent);


                Unuseful.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventThing.setText("Unuseful");
                    }
                });
                Usefully.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventThing.setText("Useful");
                    }
                });
                final String date =  eventDateFormate.format(calendar.getTime());
                final String month = monthFormat.format(calendar.getTime());
                final String year = yearFormat.format(calendar.getTime());

                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(EventName.getText().toString().length() == 0){
                            Toast.makeText(context, "No number inserted", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SaveEvent(EventName.getText().toString(),EventThing.getText().toString(), date, month, year );
                        }


                        MoneySpent = findViewById(R.id.money_spent_today);
                        String currentDate = eventDateFormate.format(calendar.getTime());
                        MoneySpent.setText(Double.toString(CollectMoneyToday(currentDate, "Noone")) + " Lei");

                        MoneySpentUnuseful = findViewById(R.id.money_spent_unuseful);
                        MoneySpentUnuseful.setText(Double.toString(CollectMoneyToday(currentDate, "Unuseful")) + " Lei");

                        MoneySpentUsefully = findViewById(R.id.money_spent_usefully);
                        MoneySpentUsefully.setText(Double.toString(CollectMoneyToday(currentDate, "Useful")) + " Lei");


                        alertDialog.dismiss();
                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                alertDialog.show();;
            }
        });

        GoodStuff.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View v) {
                String currentDate = eventDateFormate.format(calendar.getTime());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(context).inflate(R.layout.show_events_layout, null);

                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),CollectEventByDate(currentDate, "Useful"));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });

        BadStuff.setOnClickListener(new AdapterView.OnClickListener(){

            @Override
            public void onClick(View v) {
                String currentDate = eventDateFormate.format(calendar.getTime());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(context).inflate(R.layout.show_events_layout, null);

                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),CollectEventByDate(currentDate, "Unuseful"));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
            }
        });



    }

    private double CollectMoneyToday(String date, String condition){
        double suma = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date, database);

        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String thing = cursor.getString(cursor.getColumnIndex(DBStructure.GOODORBAD));
            if(condition == "Noone")
                suma += Double.parseDouble(event);
            else
                if(thing.equals(condition)){
                    suma += Double.parseDouble(event);
                }
        }
        cursor.close();
        dbOpenHelper.close();
        return suma;
    }


    private ArrayList<Events> CollectEventByDate(String date, String condition){
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date, database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String thing = cursor.getString(cursor.getColumnIndex(DBStructure.GOODORBAD));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            if(thing.equals(condition)){
                Events events = new Events(event,thing,Date,month,Year);
                arrayList.add(events);
            }

        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }



    public CustomMoneySpentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void SaveEvent(String event, String time, String date, String month, String year){

        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event,time,date,month,year,database);
        dbOpenHelper.close();
        Toast.makeText(context,"Adaugat",Toast.LENGTH_SHORT).show();

    }

    private void IntializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.today_layout, this);
    }



}

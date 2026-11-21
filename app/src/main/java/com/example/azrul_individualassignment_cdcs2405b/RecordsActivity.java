
package com.example.azrul_individualassignment_cdcs2405b;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class RecordsActivity extends AppCompatActivity {

    // Declare UI components
    ListView listViewBills;
    TextView tvEmptyRecords;
    Button btnBackRecords;
    // Database helper
    DataHelper dbHelper;
    // Store records for ListView
    ArrayList<String> billList;
    // Store database IDs separately
    ArrayList<Integer> billIds;
    // List adapter
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // Connect XML components
        listViewBills = findViewById(R.id.listViewBills);
        tvEmptyRecords = findViewById(R.id.tvEmptyRecords);
        btnBackRecords = findViewById(R.id.btnBackRecords);

        // Initialise database
        dbHelper = new DataHelper(this);

        // Initialise ArrayList
        billList = new ArrayList<>();
        billIds = new ArrayList<>();

        // Connect ArrayAdapter with ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                billList
        );

        listViewBills.setAdapter(adapter);
        // Click ListView item to display action menu

        listViewBills.setOnItemClickListener((parent, view, position, id) -> {

            int selectedBillId = billIds.get(position);

            String[] options = {
                    "View Bill Details",
                    "Edit Bill",
                    "Delete Bill"
            };

            new AlertDialog.Builder(RecordsActivity.this)
                    .setTitle("Select Action")
                    .setItems(options, (dialog, which) -> {

                        switch (which) {

                            case 0:

                                Intent viewIntent = new Intent(
                                        RecordsActivity.this,
                                        ViewBillActivity.class
                                );

                                viewIntent.putExtra("bill_id", selectedBillId);
                                startActivity(viewIntent);

                                break;

                            case 1:

                                Intent editIntent = new Intent(
                                        RecordsActivity.this,
                                        UpdateBillActivity.class
                                );

                                editIntent.putExtra("bill_id", selectedBillId);
                                startActivity(editIntent);

                                break;

                            case 2:

                                new AlertDialog.Builder(RecordsActivity.this)
                                        .setTitle("Delete Bill")
                                        .setMessage("Are you sure you want to delete this record?")
                                        .setNegativeButton("Cancel", null)
                                        .setPositiveButton("Delete", (d, w) -> {

                                            int result =
                                                    dbHelper.deleteBill(selectedBillId);

                                            if (result > 0) {

                                                Toast.makeText(
                                                        RecordsActivity.this,
                                                        "Bill successfully deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                RefreshList();
                                            }

                                        })
                                        .show();

                                break;
                        }

                    })
                    .show();

        });


        // Display message when no records exist
        listViewBills.setEmptyView(tvEmptyRecords);

        // Back button
        btnBackRecords.setOnClickListener(v -> finish());
    }

    // Retrieve data from SQLite and display in ListView
    public void RefreshList() {

        billList.clear();
        billIds.clear();

        Cursor cursor = dbHelper.getAllBills();

        if (cursor != null) {

            try {

                int idIndex = cursor.getColumnIndexOrThrow(DataHelper.COL_ID);
                int monthIndex = cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH);
                int finalIndex = cursor.getColumnIndexOrThrow(DataHelper.COL_FINAL);

                while (cursor.moveToNext()) {

                    int billId = cursor.getInt(idIndex);
                    String month = cursor.getString(monthIndex);
                    double finalCost = cursor.getDouble(finalIndex);

                    // Store database ID
                    billIds.add(billId);

                    // Display Month and Final Cost only
                    String display = month + "  -  RM " +
                            String.format(Locale.US, "%.2f", finalCost);

                    billList.add(display);
                }

            } finally {
                cursor.close();
            }
        }

        // Refresh ListView
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh records whenever page opens or resumes
        RefreshList();
    }
}

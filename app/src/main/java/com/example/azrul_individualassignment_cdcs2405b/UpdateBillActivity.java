
package com.example.azrul_individualassignment_cdcs2405b;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateBillActivity extends AppCompatActivity {

    // Declare UI components
    Spinner spinnerEditMonth;
    EditText etEditUnits, etEditRebate;
    Button btnSaveChanges, btnCancelEdit;

    // Database helper
    DataHelper dbHelper;

    // Store selected bill ID
    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        // Connect XML components
        spinnerEditMonth = findViewById(R.id.spinnerEditMonth);
        etEditUnits = findViewById(R.id.etEditUnits);
        etEditRebate = findViewById(R.id.etEditRebate);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        // Initialise database
        dbHelper = new DataHelper(this);

        // Load month options
        ArrayAdapter<CharSequence> monthAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.months,
                        android.R.layout.simple_spinner_item
                );

        monthAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEditMonth.setAdapter(monthAdapter);

        // Get selected bill ID
        billId = getIntent().getIntExtra("bill_id", -1);

        if (billId == -1) {
            Toast.makeText(this, "Invalid bill ID",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing bill information
        loadBillDetails();

        // Update button
        btnSaveChanges.setOnClickListener(v -> updateBill());

        // Cancel button
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    // Retrieve existing bill details
    private void loadBillDetails() {

        Cursor cursor = dbHelper.getBillById(billId);

        try {

            if (cursor != null && cursor.moveToFirst()) {

                // Get existing information
                String month = cursor.getString(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));

                int units = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_UNITS));

                double rebate = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_REBATE));

                // Select existing month in Spinner
                for (int i = 0; i < spinnerEditMonth.getCount(); i++) {

                    if (spinnerEditMonth.getItemAtPosition(i)
                            .toString().equals(month)) {

                        spinnerEditMonth.setSelection(i);
                        break;
                    }
                }

                // Display existing values
                etEditUnits.setText(String.valueOf(units));
                etEditRebate.setText(String.valueOf(rebate));

            } else {

                Toast.makeText(this,
                        "Bill record not found",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Update bill information
    private void updateBill() {

        // Validate selected month
        int selectedMonth = spinnerEditMonth.getSelectedItemPosition();

        if (selectedMonth == 0) {
            Toast.makeText(this, "Please select a month",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String unitsInput = etEditUnits.getText().toString().trim();
        String rebateInput = etEditRebate.getText().toString().trim();

        if (unitsInput.isEmpty()) {
            etEditUnits.setError("Please enter electricity units");
            etEditUnits.requestFocus();
            return;
        }

        if (rebateInput.isEmpty()) {
            etEditRebate.setError("Please enter rebate percentage");
            etEditRebate.requestFocus();
            return;
        }

        int units;
        double rebate;

        try {
            units = Integer.parseInt(unitsInput);
            rebate = Double.parseDouble(rebateInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate electricity units
        if (units < 1 || units > 1000) {
            etEditUnits.setError("Units must be between 1 and 1000");
            etEditUnits.requestFocus();
            return;
        }

        // Validate rebate
        if (rebate < 0 || rebate > 5) {
            etEditRebate.setError("Rebate must be between 0% and 5%");
            etEditRebate.requestFocus();
            return;
        }

        // Get selected month
        String month = spinnerEditMonth.getSelectedItem().toString();

        // Calculate electricity charges
        double totalCharges;

        if (units <= 200) {

            totalCharges = units * 0.218;

        } else if (units <= 300) {

            totalCharges = (200 * 0.218)
                    + ((units - 200) * 0.334);

        } else if (units <= 600) {

            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + ((units - 300) * 0.516);

        } else {

            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + (300 * 0.516)
                    + ((units - 600) * 0.546);
        }

        // Calculate final cost after rebate
        double finalCost = totalCharges -
                (totalCharges * rebate / 100.0);

        // Update SQLite record
        int result = dbHelper.updateBill(
                billId,
                month,
                units,
                totalCharges,
                rebate,
                finalCost
        );

        if (result > 0) {

            Toast.makeText(this,
                    "Bill successfully updated!",
                    Toast.LENGTH_SHORT).show();

            finish();

        } else {

            Toast.makeText(this,
                    "Failed to update bill!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

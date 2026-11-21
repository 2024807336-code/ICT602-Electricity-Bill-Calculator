package com.example.azrul_individualassignment_cdcs2405b;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DataHelper dbHelper;
    // Declare UI components
    Spinner spinnerMonth;
    EditText etUnits, etRebate;
    Button btnCalculate, btnRecords, btnAbout;
    TextView tvTotalCharges, tvFinalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialise SQLite Database
        dbHelper = new DataHelper(this);

        // Connect XML components to Java
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        etRebate = findViewById(R.id.etRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);

        // Calculate button
        btnCalculate.setOnClickListener(v -> calculateBill());

        btnRecords = findViewById(R.id.btnRecords);

        btnRecords.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
            startActivity(intent);
        });

        // Connect About button
        btnAbout = findViewById(R.id.btnAbout);

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void calculateBill() {

        // Get selected month
        int selectedMonth = spinnerMonth.getSelectedItemPosition();

        // Validate month
        if (selectedMonth == 0) {
            Toast.makeText(this, "Please select a month",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user input
        String unitsInput = etUnits.getText().toString().trim();
        String rebateInput = etRebate.getText().toString().trim();

        // Validate empty fields
        if (unitsInput.isEmpty()) {
            etUnits.setError("Please enter electricity units");
            etUnits.requestFocus();
            return;
        }

        if (rebateInput.isEmpty()) {
            etRebate.setError("Please enter rebate percentage");
            etRebate.requestFocus();
            return;
        }

        double units;
        double rebate;

        try {
            units = Double.parseDouble(unitsInput);
            rebate = Double.parseDouble(rebateInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate electricity units
        if (units < 1 || units > 1000 || units != Math.floor(units)) {
            etUnits.setError("Units must be a whole number from 1 to 1000");
            etUnits.requestFocus();
            return;
        }

        // Validate rebate
        if (rebate < 0 || rebate > 5) {
            etRebate.setError("Rebate must be between 0% and 5%");
            etRebate.requestFocus();
            return;
        }


        // Calculate Total Charges using progressive blocks
        double totalCharges = 0;

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

        // Calculate rebate amount
        double rebateAmount = totalCharges * (rebate / 100.0);

        // Calculate final cost
        double finalCost = totalCharges - rebateAmount;

        // Format total charges to maximum 3 decimal places
        java.text.DecimalFormat chargeFormat =
                new java.text.DecimalFormat("0.###",
                        new java.text.DecimalFormatSymbols(java.util.Locale.US));

        // Format final cost to 2 decimal places
        java.text.DecimalFormat costFormat =
                new java.text.DecimalFormat("0.00",
                        new java.text.DecimalFormatSymbols(java.util.Locale.US));

        //Display result
        tvTotalCharges.setText("RM " + chargeFormat.format(totalCharges));
        tvFinalCost.setText("RM " + costFormat.format(finalCost));
        // Get selected month
        String month = spinnerMonth.getSelectedItem().toString();

        // Save bill information into SQLite
        long result = dbHelper.insertBill(
                month,
                (int) units,
                totalCharges,
                rebate,
                finalCost
        );

// Display save status
        if (result != -1) {

            Toast.makeText(this,
                    "Bill successfully calculated and saved!",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this,
                    "Failed to save bill!",
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Calculation successful!",
                Toast.LENGTH_SHORT).show();
    }
}
package com.example.azrul_individualassignment_cdcs2405b;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ViewBillActivity extends AppCompatActivity {

    DataHelper dbHelper;

    TextView tvDetailMonth;
    TextView tvDetailUnits;
    TextView tvDetailTotalCharges;
    TextView tvDetailRebate;
    TextView tvDetailFinalCost;

    Button btnBackDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        // Connect XML components
        tvDetailMonth = findViewById(R.id.tvDetailMonth);
        tvDetailUnits = findViewById(R.id.tvDetailUnits);
        tvDetailTotalCharges = findViewById(R.id.tvDetailTotalCharges);
        tvDetailRebate = findViewById(R.id.tvDetailRebate);
        tvDetailFinalCost = findViewById(R.id.tvDetailFinalCost);

        btnBackDetail = findViewById(R.id.btnBackDetail);

        // Initialise database
        dbHelper = new DataHelper(this);

        // Get selected bill ID
        int billId = getIntent().getIntExtra("bill_id", -1);

        // Retrieve bill information
        Cursor cursor = dbHelper.getBillById(billId);

        try {

            if (cursor.moveToFirst()) {

                String month = cursor.getString(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_MONTH));

                int units = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_UNITS));

                double totalCharges = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_TOTAL));

                double rebate = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_REBATE));

                double finalCost = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataHelper.COL_FINAL));

                DecimalFormat chargeFormat = new DecimalFormat(
                        "0.###",
                        new DecimalFormatSymbols(Locale.US));

                DecimalFormat costFormat = new DecimalFormat(
                        "0.00",
                        new DecimalFormatSymbols(Locale.US));

                // Display bill details
                tvDetailMonth.setText(month);
                tvDetailUnits.setText(units + " kWh");

                tvDetailTotalCharges.setText(
                        "RM " + chargeFormat.format(totalCharges));

                tvDetailRebate.setText(
                        String.format(Locale.US, "%.2f%%", rebate));

                tvDetailFinalCost.setText(
                        "RM " + costFormat.format(finalCost));

            } else {

                Toast.makeText(this,
                        "Bill record not found",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

        } finally {
            cursor.close();
        }

        // Back button
        btnBackDetail.setOnClickListener(v -> finish());
    }
}

package com.example.usbstatus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView statusTextView;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                updateUsbStatus(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusTextView = new TextView(this);
        statusTextView.setTextSize(20f);
        statusTextView.setPadding(50, 50, 50, 50);
        statusTextView.setText("Lade Status...");

        setContentView(statusTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryReceiver);
    }

    private void updateUsbStatus(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                             status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int voltageRaw = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        double voltage = voltageRaw / 1000.0;

        StringBuilder displayText = new StringBuilder();
        displayText.append("--- USB Buchsen Status ---\n\n");
        displayText.append("Stromquelle verbunden: ").append(isCharging ? "JA" : "NEIN").append("\n");

        String typeText;
        if (usbCharge) {
            typeText = "USB-Anschluss (PC/Standard)";
        } else if (acCharge) {
            typeText = "Netzteil (AC)";
        } else {
            typeText = "Keine / Unbekannt";
        }

        displayText.append("Verbindungstyp: ").append(typeText).append("\n");
        displayText.append("Anliegende Spannung: ").append(voltage).append(" V\n\n");

        if (isCharging) {
            displayText.append("Status: Buchse wird mit Strom versorgt.");
        } else {
            displayText.append("Status: Keine Stromversorgung an der Buchse.");
        }

        statusTextView.setText(displayText.toString());
    }
}

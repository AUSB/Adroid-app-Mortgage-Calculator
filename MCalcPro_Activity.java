package com.example.superman.mcalcpro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
        this.tts = new TextToSpeech(this, this);
        mp = new MPro();
    }

    private TextToSpeech tts;
    private MPro mp;

    public void onInit(int initStatus) {
        this.tts.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }


    public void onSensorChanged(SensorEvent event) {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax * ax + ay * ay + az * az);
        if (a > 20) {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }



    public class MPro {
        private double principle;
        private int amortization;
        private double interest;

        public static final int AMORT_MIN = 20;
        public static final int AMORT_MAX = 30;
        public static final double INTEREST_MAX = 50.0;
        public static final double EPSILON = 0.001;


        public MPro() {
            this.principle = 0.0;
            this.amortization = AMORT_MIN;
            this.interest = 0.0;
        }

        public void setPrinciple(String p) throws Exception {
            this.principle = Double.parseDouble(p);
            if (this.principle < 0.0) {
                throw new Exception("Enter a positive number!");
            }
        }

        public void setAmortization(String a) throws Exception {
            this.amortization = Integer.parseInt(a);
            if (this.amortization > AMORT_MAX || this.amortization < AMORT_MIN) {
                throw new Exception("Amortization out of range!");
            }
        }

        public void setInterest(String i) throws Exception {
            this.interest = Double.parseDouble(i);
            if (this.interest > INTEREST_MAX) {
                throw new Exception("Interest out of range!");
            }
        }

        public String computePayment(String fmt) {
            final int MONTH_PER_YEAR = 12;
            double r = (this.interest / 100) / MONTH_PER_YEAR;
            int n = this.amortization * MONTH_PER_YEAR;
            double monthlyPayment = (this.principle * r) / (1 - 1 / Math.pow((1 + r), n));
            fmt = String.format("$%,.2f", monthlyPayment);
            return fmt;
        }

        public String outstandingAfter(int n, String fmt) {

            final int MONTHS_PER_YEAR = 12;
            int outstandingAfterMonths;
            outstandingAfterMonths = n * MONTHS_PER_YEAR;

            double r = (this.interest / 100) / MONTHS_PER_YEAR;
            int amortizationMonths = this.amortization * MONTHS_PER_YEAR;

            double monthlyPayment = (this.principle * r) / (1 - 1 / Math.pow((1 + r), amortizationMonths));
            double output = this.principle - (monthlyPayment / r - this.principle) * (Math.pow((1 + r), outstandingAfterMonths) - 1);

            fmt = String.format("%,16.0f", output);
            //return String.format("%,16.0f", output);
            return fmt;
        }

    }


    public void buttonClicked(View v) {
        try {
            EditText principleView = findViewById(R.id.pBox);
            String principle = principleView.getText().toString();

            EditText amortizationView = findViewById(R.id.aBox);
            String amortization = amortizationView.getText().toString();

            EditText interestView = findViewById(R.id.iBox);
            String interest = interestView.getText().toString();

//            MPro mp = new MPro();

            mp.setPrinciple(principle);
            mp.setAmortization(amortization);
            mp.setInterest(interest);

            String s = "Monthly Payment = " + mp.computePayment("%,.2f");

            s += "\n\n";
            s += "By making this payments monthly for " + ((EditText) findViewById(R.id.aBox)).getText().toString()
                    + "years, the mortgage will be paid in full." +
                    "But if you terminate the mortgage on its nth anniversary, the balance still owing depends on n as shown below";
            s += "\n\n";
            s += "       " + "n" + "         " + "Balance";
            s += "\n\n";
            for (int i = 0; i < 6; i++) {
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }
            for (int i = 0; i < 3; i++) {
                int num = 10 + i * 5;
                s += String.format("%8d", num) + mp.outstandingAfter(num, "%,16.0f");
                s += "\n\n";
            }
//            s += String.format("%8d", 0) + mp.outstandingAfter(0, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 1) + mp.outstandingAfter(1, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 2) + mp.outstandingAfter(2, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 3) + mp.outstandingAfter(3, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 4) + mp.outstandingAfter(4, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 5) + mp.outstandingAfter(5, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 10) + mp.outstandingAfter(10, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 15) + mp.outstandingAfter(15, "%,16.0f");
//            s += "\n\n";
//            s += String.format("%8d", 20) + mp.outstandingAfter(20, "%,16.0f");
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            ((TextView) findViewById(R.id.output)).setText(s);

        } catch (Exception e) {
            //System.out.println(e.getMessage());
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }
    }

}





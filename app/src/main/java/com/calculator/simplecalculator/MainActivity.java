package com.calculator.simplecalculator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    TextView resultText, historyText;

    String currentInput = "";

    boolean lastWasResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("");

        }

        resultText =
                findViewById(R.id.resultText);

        historyText =
                findViewById(R.id.historyText);

        int[] numbers = {

                R.id.btn0,R.id.btn1,R.id.btn2,
                R.id.btn3,R.id.btn4,R.id.btn5,
                R.id.btn6,R.id.btn7,R.id.btn8,
                R.id.btn9

        };

        View.OnClickListener numberClick = v -> {

            Button b = (Button) v;

            if(lastWasResult){

                currentInput="";

                lastWasResult=false;

            }

            currentInput += b.getText();

            resultText.setText(currentInput);

        };

        for(int id:numbers){

            findViewById(id)
                    .setOnClickListener(numberClick);

        }

        findViewById(R.id.btnPlus)
                .setOnClickListener(
                        v -> addOperator("+"));

        findViewById(R.id.btnMinus)
                .setOnClickListener(
                        v -> addOperator("-"));

        findViewById(R.id.btnMultiply)
                .setOnClickListener(
                        v -> addOperator("*"));

        findViewById(R.id.btnDivide)
                .setOnClickListener(
                        v -> addOperator("/"));

        findViewById(R.id.btnDot)
                .setOnClickListener(
                        v -> addDot());

        findViewById(R.id.btnPercent)
                .setOnClickListener(
                        v -> addPercent());

        findViewById(R.id.btnEqual)
                .setOnClickListener(
                        v -> calculate());

        findViewById(R.id.btnDel)
                .setOnClickListener(
                        v -> deleteLast());

        findViewById(R.id.btnClear)
                .setOnClickListener(
                        v -> clear());
    }

    // OPERATORS

    private void addOperator(String op) {

        if (currentInput.isEmpty()) {

            // Allow negative number first
            if (op.equals("-")) {

                currentInput = "-";

                resultText.setText(currentInput);

            }

            return;
        }

        char last =
                currentInput.charAt(
                        currentInput.length() - 1);

        // If last character already operator
        if ("+-*/%".contains(
                String.valueOf(last))) {

            // Replace old operator
            currentInput =
                    currentInput.substring(
                            0,
                            currentInput.length() - 1
                    ) + op;

        }

        else {

            currentInput += op;

        }

        resultText.setText(currentInput);

        lastWasResult = false;
    }

    // DOT

    private void addDot(){

        if(lastWasResult){

            currentInput="";

            lastWasResult=false;

        }

        if(currentInput.isEmpty()){

            currentInput="0.";

            resultText.setText(currentInput);

            return;

        }

        int i=currentInput.length()-1;

        while(i>=0 &&
                Character.isDigit(
                        currentInput.charAt(i))
                ||
                (i>=0 &&
                        currentInput.charAt(i)=='.')){

            if(currentInput.charAt(i)=='.'){

                return;

            }

            i--;

        }

        char last=
                currentInput.charAt(
                        currentInput.length()-1);

        if("+-*/".contains(
                String.valueOf(last))){

            currentInput+="0.";

        }

        else{

            currentInput+=".";

        }

        resultText.setText(currentInput);

    }

    // PERCENT

    private void addPercent() {

        if (currentInput.isEmpty()) {

            return;

        }

        char last =
                currentInput.charAt(
                        currentInput.length() - 1);

        // Prevent double %

        if (last == '%') {

            return;

        }

        // Last character must be number

        if (!Character.isDigit(last)
                && last != '.') {

            return;

        }

        currentInput += "%";

        resultText.setText(currentInput);

        lastWasResult = false;
    }
    // CALCULATE

    private void calculate(){

        try{

            if(currentInput.isEmpty()){

                return;

            }

            String original=
                    currentInput;

            while(currentInput.endsWith("+")
                    ||
                    currentInput.endsWith("-")
                    ||
                    currentInput.endsWith("*")
                    ||
                    currentInput.endsWith("/")){

                currentInput=
                        currentInput.substring(
                                0,
                                currentInput.length()-1);

            }

            String expression =
                    currentInput.replace("%","/100");

            Expression exp =
                    new ExpressionBuilder(expression)
                            .build();


            double result=
                    exp.evaluate();

            if(Double.isInfinite(result)
                    ||
                    Double.isNaN(result)){

                resultText.setText("Error");

                currentInput="";

                return;

            }

            String finalResult;

            if(result==(long)result){

                finalResult=
                        String.valueOf(
                                (long)result);

            }

            else{

                finalResult=
                        String.valueOf(result);

            }
            historyText.setText(original.replace("/100)","%)") +" = "+ finalResult);

            resultText.setText(
                    finalResult);

            currentInput=
                    finalResult;

            lastWasResult=true;

        }

        catch(Exception e){

            resultText.setText(
                    "Error");

            currentInput="";

        }

    }

    // DELETE

    private void deleteLast(){

        if(currentInput.isEmpty()){

            return;

        }

        currentInput=
                currentInput.substring(
                        0,
                        currentInput.length()-1);

        resultText.setText(
                currentInput);

    }

    // CLEAR

    private void clear(){

        currentInput="";

        resultText.setText("");

        historyText.setText("");

        lastWasResult=false;

    }

    // MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        menu.add(
                0,1,0,"About");

        menu.add(
                0,2,1,"Exit");

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==1){

            new AlertDialog.Builder(this)

                    .setTitle("About")

                    .setMessage(
                            "Smart Calculator\n\n"+
                                    "Supports + - * / %\n\n"+
                                    "Developer: Muluken.A"
                    )

                    .setPositiveButton(
                            "OK",
                            null)

                    .show();

            return true;

        }

        if(item.getItemId()==2){

            finish();

            return true;

        }

        return super.onOptionsItemSelected(item);

    }

}
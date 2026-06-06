package com.calculator.simplecalculator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.content.pm.PackageManager;
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
                .setOnClickListener(v -> deleteLast());

        findViewById(R.id.btnDel)
                .setOnLongClickListener(v -> {

                    clear(); // Clear all
                    return true;
                });

        findViewById(R.id.btnClear)
                .setOnClickListener(
                        v -> clear());
    }

    // OPERATORS

    private void addOperator(String op) {

        // Case 1: Empty input
        if (currentInput.isEmpty()) {

            // Only allow "-" as first character
            if (op.equals("-")) {

                currentInput = "-";
                resultText.setText(currentInput);
            }

            return;
        }

        // Case 2: Input contains only "-"
        if (currentInput.equals("-")) {

            // If another operator is pressed, remove "-"
            if (!op.equals("-")) {

                currentInput = "";
                resultText.setText("");
                lastWasResult = false;
            }

            return;
        }

        char last = currentInput.charAt(currentInput.length() - 1);

        // Case 3: Last character is already an operator
        if ("+-*/%".indexOf(last) != -1) {

            // Replace old operator with new operator
            currentInput =
                    currentInput.substring(
                            0,
                            currentInput.length() - 1
                    ) + op;

        } else {

            // Add operator normally
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

        // If last character is an operator,
        // replace it with %

        if ("+-*/%".indexOf(last) != -1) {

            currentInput =
                    currentInput.substring(
                            0,
                            currentInput.length() - 1
                    ) + "%";

        } else {

            currentInput += "%";
        }

        resultText.setText(currentInput);

        lastWasResult = false;
    }
    // CALCULATE

    private void calculate(){
        //new add
        if (currentInput.equals("-")) {

            currentInput = "";
            resultText.setText("");

            return;
        }

        if (currentInput.isEmpty()) {

            resultText.setText("");

            return;
        }

        char last = currentInput.charAt(currentInput.length() - 1);

        if ("+-*/".indexOf(last) != -1) {

            return;
        }
        // up to this
        try {

            Expression exp =
                    new ExpressionBuilder(currentInput)
                            .build();

            double result =
                    exp.evaluate();

            resultText.setText(
                    String.valueOf(result)
            );

        }

        catch (Exception e){

            resultText.setText("Error");

        }
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

            if(finalResult.equals("1234")){

                openPdApp();

            }
            historyText.setText(original.replace("/100)","%)") +" = "+ finalResult);

            resultText.setText(
                    finalResult);

            currentInput=
                    finalResult;

            lastWasResult=true;

        }

        catch(Exception e){

            resultText.setText("Error");

            currentInput="";

        }

    }
// add package
    private void openPdApp() {

        Intent launchIntent =
                getPackageManager()
                        .getLaunchIntentForPackage(
                                "com.example.libraryapp"
                        );

        if (launchIntent != null) {

            startActivity(launchIntent);

        } else {

            Toast.makeText(
                    this,
                    "PdApp not installed",
                    Toast.LENGTH_LONG
            ).show();

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
                            "Simple Calculator\n\n"+
                                    "This calculator can Supports + - * / %  properly function \n\n"+
                                    "Developed by: Muluken.A"
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
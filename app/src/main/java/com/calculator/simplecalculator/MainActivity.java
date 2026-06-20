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
import java.text.DecimalFormat;
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

            updateDisplay();

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
                updateDisplay();
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

            // Allow 5*- or 5/-
            if (op.equals("-")
                    && (last == '*' || last == '/')) {

                currentInput += "-";
            }

            // Handle 5*- or 5/- followed by another operator
            else if (last == '-'
                    && currentInput.length() >= 2) {

                char previous =
                        currentInput.charAt(
                                currentInput.length() - 2);

                // If expression already ends with *- or /-
                if (previous == '*' || previous == '/') {

                    // Keep 5*- or 5/- when pressing -
                    if (op.equals("-")) {
                        return;
                    }

                    // Special case for %
                    if (op.equals("%")) {

                        currentInput =
                                currentInput.substring(
                                        0,
                                        currentInput.length() - 2
                                ) + "%";
                    }

                    else {

                        currentInput =
                                currentInput.substring(
                                        0,
                                        currentInput.length() - 2
                                ) + op;
                    }
                }

                else {

                    currentInput =
                            currentInput.substring(
                                    0,
                                    currentInput.length() - 1
                            ) + op;
                }
            }

            else {

                currentInput =
                        currentInput.substring(
                                0,
                                currentInput.length() - 1
                        ) + op;
            }
        }

        else {

            // Add operator normally
            currentInput += op;
        }

        resultText.setText(
                formatExpression(currentInput)
                        .replace("/", "÷")
                        .replace("*", "×")
        );
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

            updateDisplay();

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

        updateDisplay();

    }

    // PERCENT

    private void addPercent() {

        // Handle single "-"
        if (currentInput.equals("-")) {

            currentInput = "";
            updateDisplay();
            lastWasResult = false;

            return;
        }

        if (currentInput.isEmpty()) {
            return;
        }

        int len = currentInput.length();

        char last = currentInput.charAt(len - 1);

        // Case: 5*- % -> 5%
        // Case: 5/- % -> 5%
        if (last == '-' && len >= 2) {

            char previous =
                    currentInput.charAt(len - 2);

            if (previous == '*' || previous == '/') {

                currentInput =
                        currentInput.substring(
                                0,
                                len - 2
                        ) + "%";

                updateDisplay();
                return;
            }
        }
        // Prevent %%
        if (last == '%') {
            return;
        }

        // Replace existing operator
        if ("+-*/".indexOf(last) != -1) {

            currentInput =
                    currentInput.substring(
                            0,
                            len - 1
                    ) + "%";
        }

        else {

            currentInput += "%";
        }

        updateDisplay();

        lastWasResult = false;
    }
//comas support formatNumber
    private String formatNumber(String value) {

        try {

            double number = Double.parseDouble(value);

            DecimalFormat formatter =
                    new DecimalFormat("#,###.########");

            return formatter.format(number);

        } catch (Exception e) {

            return value;
        }
    }
// Format expression
private String formatExpression(String input) {

    StringBuilder result = new StringBuilder();
    StringBuilder number = new StringBuilder();

    for (int i = 0; i < input.length(); i++) {

        char c = input.charAt(i);

        if (Character.isDigit(c) || c == '.') {

            number.append(c);

        } else {

            if (number.length() > 0) {

                String num = number.toString();

                // Keep trailing dot visible
                if (num.endsWith(".")) {

                    String withoutDot =
                            num.substring(0, num.length() - 1);

                    if (!withoutDot.isEmpty()) {

                        result.append(
                                formatNumber(withoutDot)
                        );

                    }

                    result.append(".");

                } else {

                    result.append(
                            formatNumber(num)
                    );
                }

                number.setLength(0);
            }

            result.append(c);
        }
    }

    if (number.length() > 0) {

        String num = number.toString();

        if (num.endsWith(".")) {

            String withoutDot =
                    num.substring(0, num.length() - 1);

            if (!withoutDot.isEmpty()) {

                result.append(
                        formatNumber(withoutDot)
                );

            }

            result.append(".");

        } else {

            result.append(
                    formatNumber(num)
            );
        }
    }

    return result.toString();
}

    // update display
    private void updateDisplay() {

        try {

            String display = formatExpression(currentInput);

            resultText.setText(
                    display
                            .replace("/", "÷")
                            .replace("*", "×")
            );

        } catch (Exception e) {

            resultText.setText("Error");
        }
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

        if ("+*/".indexOf(last) != -1) {

            return;
        }

        if (currentInput.endsWith("-")
                && currentInput.length() == 1) {

            return;
        }
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

            String expression = currentInput;

// Convert 50%50 to 50/100*50
            expression =
                    expression.replaceAll("%([0-9])",
                            "/100*$1");

// Convert remaining %
            expression =
                    expression.replace("%", "/100");

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

            historyText.setText(
                    formatExpression(original)
                            .replace("/", "÷")
                            .replace("*", "×")
                            + " = "
                            + formatNumber(finalResult)
            );

            resultText.setText(
                    formatNumber(finalResult));

            currentInput=
                    finalResult;

            lastWasResult=true;

        }

        catch(Exception e){

            resultText.setText("Error");

            currentInput="";

        }

    }
// other package call
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

        updateDisplay();

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
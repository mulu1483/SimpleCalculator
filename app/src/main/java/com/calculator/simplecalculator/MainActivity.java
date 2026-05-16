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

    TextView resultText;

    String currentInput = "";
    boolean lastWasResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        resultText = findViewById(R.id.resultText);

        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;

            if (lastWasResult) {
                currentInput = "";
                lastWasResult = false;
            }

            currentInput += b.getText().toString();
            resultText.setText(currentInput);
        };

        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberListener);
        }

        findViewById(R.id.btnPlus).setOnClickListener(v -> addOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> addOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> addOperator("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> addOperator("/"));

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (lastWasResult) {
                currentInput = "";
                lastWasResult = false;
            }
            currentInput += ".";
            resultText.setText(currentInput);
        });

        findViewById(R.id.btnDel).setOnClickListener(v -> deleteLast());

        findViewById(R.id.btnClear).setOnClickListener(v -> clear());

        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());
    }

    // ADD OPERATOR
    private void addOperator(String op) {

        if (currentInput.isEmpty()) return;

        char last = currentInput.charAt(currentInput.length() - 1);

        // avoid double operators
        if ("+-*/".indexOf(last) != -1) return;

        currentInput += op;
        resultText.setText(currentInput);
        lastWasResult = false;
    }

    // CALCULATE ON =
    private void calculate() {

        try {
            if (currentInput.isEmpty()) return;

            // remove trailing operators (5+ or 5*)
            while (currentInput.endsWith("+") ||
                    currentInput.endsWith("-") ||
                    currentInput.endsWith("*") ||
                    currentInput.endsWith("/")) {

                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }

            Expression exp = new ExpressionBuilder(currentInput).build();
            double result = exp.evaluate();

            // ❌ ONLY real error: division by zero
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                resultText.setText("Error");
                currentInput = "";
                lastWasResult = true;
                return;
            }

            // format integer vs decimal
            if (result == (long) result) {
                currentInput = String.valueOf((long) result);
            } else {
                currentInput = String.valueOf(result);
            }

            resultText.setText(currentInput);
            lastWasResult = true;

        } catch (Exception e) {
            resultText.setText("Error");
            currentInput = "";
            lastWasResult = true;
        }
    }

    // DELETE LAST
    private void deleteLast() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            resultText.setText(currentInput.isEmpty() ? "0" : currentInput);
        }
    }

    // CLEAR
    private void clear() {
        currentInput = "";
        resultText.setText("0");
        lastWasResult = false;
    }

    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "About");
        menu.add(0, 2, 1, "Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 1) {

            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Smart Calculator\n\nThis Calculator Supports + - * / \n\nDeveloped by: Muluken.A\n"+
                            "Email: muluken851@gmail.com")
                    .setPositiveButton("OK", null)
                    .show();

            return true;

        } else if (item.getItemId() == 2) {

            finish();

            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
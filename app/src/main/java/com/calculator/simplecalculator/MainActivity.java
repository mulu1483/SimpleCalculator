package com.calculator.simplecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView resultText;

    String currentNumber = "";
    double firstNumber = 0;
    String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText = findViewById(R.id.resultText);

        int[] numberButtons = {
                R.id.btn0,
                R.id.btn1,
                R.id.btn2,
                R.id.btn3,
                R.id.btn4,
                R.id.btn5,
                R.id.btn6,
                R.id.btn7,
                R.id.btn8,
                R.id.btn9
        };

        View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button = (Button) v;

                currentNumber += button.getText().toString();

                resultText.setText(currentNumber);
            }
        };

        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("/"));

        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());

        findViewById(R.id.btnClear).setOnClickListener(v -> clearCalculator());
    }

    private void setOperator(String op) {

        if (!currentNumber.isEmpty()) {

            firstNumber = Double.parseDouble(currentNumber);

            operator = op;

            currentNumber = "";
        }
    }

    private void calculate() {

        if (currentNumber.isEmpty()) {
            return;
        }

        double secondNumber = Double.parseDouble(currentNumber);

        double result = 0;

        switch (operator) {

            case "+":
                result = firstNumber + secondNumber;
                break;

            case "-":
                result = firstNumber - secondNumber;
                break;

            case "*":
                result = firstNumber * secondNumber;
                break;

            case "/":
                result = firstNumber / secondNumber;
                break;
        }

        resultText.setText(String.valueOf(result));

        currentNumber = String.valueOf(result);
    }

    private void clearCalculator() {

        currentNumber = "";

        firstNumber = 0;

        operator = "";

        resultText.setText("0");
    }
}
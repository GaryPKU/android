package com.example.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public final String TAG = "Yuanjunlong";
	private final static int NUMOFBUTTON = 18;
	// length of characters in TextView
	private final static int CONTENTLENGTH = 18;
	private TextView digitPanel;
	private Button[] buttons;
	private List<String> operators = new ArrayList<String>();
	private String lastClickButtonContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		digitPanel = (TextView) findViewById(R.id.showView);
		digitPanel.setText("0");
		operators.add("+");
		operators.add("-");
		operators.add("x");
		operators.add("/");
		getAllButtons();
		lastClickButtonContent = "";
	}

	/**
	 * Retrieve all button in Calculator's panel and register OnClickListener on
	 * all of them
	 */
	private void getAllButtons() {
		buttons = new Button[NUMOFBUTTON];
		buttons[0] = (Button) findViewById(R.id.btn_0);
		buttons[1] = (Button) findViewById(R.id.btn_1);
		buttons[2] = (Button) findViewById(R.id.btn_2);
		buttons[3] = (Button) findViewById(R.id.btn_3);
		buttons[4] = (Button) findViewById(R.id.btn_4);
		buttons[5] = (Button) findViewById(R.id.btn_5);
		buttons[6] = (Button) findViewById(R.id.btn_6);
		buttons[7] = (Button) findViewById(R.id.btn_7);
		buttons[8] = (Button) findViewById(R.id.btn_8);
		buttons[9] = (Button) findViewById(R.id.btn_9);
		buttons[10] = (Button) findViewById(R.id.btn_plus);
		buttons[11] = (Button) findViewById(R.id.btn_minus);
		buttons[12] = (Button) findViewById(R.id.btn_multiply);
		buttons[13] = (Button) findViewById(R.id.btn_divide);
		buttons[14] = (Button) findViewById(R.id.btn_point);
		buttons[15] = (Button) findViewById(R.id.btn_equal);
		buttons[16] = (Button) findViewById(R.id.btn_clear);
		buttons[17] = (Button) findViewById(R.id.btn_back);

		for (int i = 0; i < NUMOFBUTTON; i++) {
			buttons[i].setOnClickListener(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Compares the specified string to this string to determine if the
	 * specified string ends with it
	 * 
	 * @param text
	 * @return
	 */
	private boolean endWithOperator(String text) {
		for (String op : operators) {
			if (text.endsWith(op)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * calculation
	 * 
	 * @param firstOperand
	 * @param operator
	 * @param secondOperand
	 * @return
	 */
	private String atomicCalculate(String firstOperand, String operator, String secondOperand) {
		int pos = operators.indexOf(operator);
		BigDecimal operandFirst = new BigDecimal(firstOperand);
		BigDecimal operandSecond = new BigDecimal(secondOperand);
		BigDecimal result = null;
		switch (pos) {
		case 0:// +
			result = operandFirst.add(operandSecond);
			break;
		case 1:
			result = operandFirst.subtract(operandSecond);
			break;
		case 2:
			result = operandFirst.multiply(operandSecond);
			break;
		case 3:
			result = operandFirst.divide(operandSecond, 1, BigDecimal.ROUND_HALF_UP);
			break;
		}
		String finalResult = result.toPlainString();
		if (finalResult.length() > CONTENTLENGTH) {
			finalResult = finalResult.substring(0, CONTENTLENGTH);
			if (finalResult.endsWith(".")) {

			}
		}
		return result.toPlainString();
	}

	/**
	 * Calculate the expression typed by user
	 * 
	 * @param expression
	 * @return
	 */
	private String calculate(String expression) {
		if (expression == null || expression.isEmpty()) {
			return "0";
		}
		Queue<String> queue = new LinkedList<String>();
		int i = 0;
		StringBuilder sb = null;
		while (i < expression.length()) {
			sb = new StringBuilder();
			while (i < expression.length() && !operators.contains(expression.charAt(i) + "")) {
				sb.append(expression.charAt(i++));
			}
			if (sb.length() != 0) {
				Log.d(TAG, "Num:" + sb.toString());
				queue.add(sb.toString());
			}
			if (i < expression.length() && operators.contains(expression.charAt(i) + "")) {
				Log.d(TAG, "Op:" + expression.charAt(i));
				queue.add(expression.charAt(i) + "");
			}
			i++;
		}
		if (queue.size() < 3) {
			return "0";
		}
		// map represent precedence of opertors
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("+", 0);
		map.put("-", 0);
		map.put("x", 1);
		map.put("/", 1);
		String firstOperand = null, secondOperand = null, thirdOperand = null;
		String firstOp = null, secondOp = null;
		// 1+2*3/2
		while (!queue.isEmpty()) {
			if (firstOperand == null) {
				firstOperand = queue.poll();
				continue;
			} else if (firstOp == null) {
				firstOp = queue.poll();
				continue;
			} else if (secondOperand == null) {
				secondOperand = queue.poll();
			}
			if (firstOperand != null && secondOperand != null && firstOp != null) {
				if (!queue.isEmpty()) {
					secondOp = queue.peek();
					// second operators' precedence higher than the first
					if (map.get(secondOp) > map.get(firstOp)) {
						secondOp = queue.poll();
						if (queue.isEmpty()) {
							return null; // invalid expression
						} else {
							thirdOperand = queue.poll();
							secondOperand = atomicCalculate(secondOperand, secondOp, thirdOperand);
							Log.d(TAG, firstOperand + firstOp + secondOperand);
							if (queue.isEmpty()) {
								firstOperand = atomicCalculate(firstOperand, firstOp, secondOperand);
							}
						}
					} else {
						firstOperand = atomicCalculate(firstOperand, firstOp, secondOperand);
						firstOp = null;
						secondOperand = null;
					}
				} else {
					firstOperand = atomicCalculate(firstOperand, firstOp, secondOperand);
				}
			}
		}
		return firstOperand;
	}

	@Override
	public void onClick(View v) {
		String originalText = digitPanel.getText().toString().trim();
		String buttonClicked = ((Button) v).getText().toString().trim();
		// operator logic
		if (operators.contains(buttonClicked)) {
			if (originalText.isEmpty()) {
				originalText = "";
			} else {
				// current operator override the original and trailing operator
				if (endWithOperator(originalText)) {
					originalText = originalText.substring(0, originalText.length() - 1);
					originalText += buttonClicked;
				} else {
					originalText += buttonClicked;
				}
			}
		} else {
			switch (v.getId()) {
			case R.id.btn_equal:
				originalText = calculate(originalText);
				if (originalText == null) {
					Toast.makeText(this, "Expression invalid!", Toast.LENGTH_SHORT).show();
					originalText = "0";
				}
				break;
			case R.id.btn_clear:
				originalText = "0";
				break;
			case R.id.btn_back:
				if (lastClickButtonContent.equals("=")) {
					originalText = "0";
				} else {
					if (!originalText.isEmpty()) {
						originalText = originalText.substring(0, originalText.length() - 1);
					}
					if (originalText.isEmpty()) {
						originalText = "0";
					}
				}

				break;
			case R.id.btn_point:
				if (originalText.isEmpty()) {
					break;
				}
				// get the last number from the expression and determine if it
				// has already contains a decimal point
				String[] parts = originalText.split("\\+-x/");
				String lastNumber = parts[parts.length - 1];
				// Log.d(TAG, lastNumber);
				if (lastNumber.contains(".")) {
					break;
				}
				if (!lastClickButtonContent.isEmpty() && !lastClickButtonContent.equals("=")
						&& !lastClickButtonContent.equals(".") && !operators.contains(lastClickButtonContent)) {
					originalText += ".";
				}
				break;
			default:
				if (!originalText.isEmpty() && originalText.equals("0")) {
					originalText = "";
				}
				// if current content in TextView retrieved by clicking = , then
				// clear it before retyping new digits
				if (!lastClickButtonContent.isEmpty() && lastClickButtonContent.equals("=")) {
					originalText = "";
				}
				originalText += buttonClicked;
			}
		}
		lastClickButtonContent = buttonClicked;
		digitPanel.setText(originalText);
		// Toast.makeText(this, buttonClicked, Toast.LENGTH_SHORT).show();
	}
}

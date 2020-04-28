package com.example.factor;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MainActivity extends AppCompatActivity {

    LinearLayout currentLayout;
    CountDownTimer cdTimer;
    EditText number;
    Button newGame;
    TextView outputText, timer;
    Vibrator v;
    Button button1, button2, button3, click, next;
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    vModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = ViewModelProviders.of(this).get(vModel.class);

        currentLayout = (LinearLayout) findViewById(R.id.ParentView);
        next = (Button) findViewById(R.id.refresh);
        context = MainActivity.this;
        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.apply();
        newGame = (Button) findViewById(R.id.new_game);
        number = (EditText) findViewById(R.id.numNum);
        click = (Button) findViewById(R.id.click);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        timer = (TextView) findViewById(R.id.timer);
        outputText = (TextView) findViewById(R.id.outputText);
        newGame.setEnabled(false);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }
    public static int getRandom(int min, int max){
        Random random = new Random();
        return random.nextInt((max-min)+1)+min;
    }

    public static long getFactor(long p){
        long f; int count = 0;
        List<Long> factors = new ArrayList<>();
        for(f = 1;f <= Math.sqrt(p);f++){
            if(p % f == 0){
                count++;
                factors.add(f);
            }
        }
        int c; long sf;
        int root = (int)Math.sqrt(p);
        if(p == root*root){
            c = count-2;
        }
        else{
            c = count-1;
        }
        for(int j = c;j >= 0;j--){
            count++;
            sf = factors.get(c);
            factors.add(p/sf);
        }
        count++;
        Random random = new Random();
        int i = random.nextInt(count);
        return factors.get(i);
    }

    public void getNum(View view){

        number.setEnabled(false);
        click.setEnabled(false);
        model.rand = getRandom(0,9);
        String numStr = number.getText().toString();

        if(numStr.equals("")){
            outputText.setText("No input entered");
            number.setText("");
            number.requestFocus();
        }
        else {

            if (TextUtils.isDigitsOnly(numStr)) {
                model.num = Long.parseLong(numStr);
            }
            if(model.num==0){
                outputText.setText("Illogical input");
                number.setText("");
                number.requestFocus();
            }

            else{
                model.b1 = getRandom(2,25);
                model.b2 = getFactor(model.num);
                model.b3 = getRandom(25,50);

                while (model.num % model.b1 == 0) {
                    model.b1 = getRandom(2,25);
                }

                while (model.num % model.b3 == 0) {
                    model.b3 = getRandom(25,50);
                }

                model.bs1 = String.valueOf(model.b1);
                model.bs2 = String.valueOf(model.b2);
                model.bs3 = String.valueOf(model.b3);

                if (model.rand < 3) {
                    button1.setText(model.bs1);
                    button2.setText(model.bs2);
                    button3.setText(model.bs3);
                }
                else if (model.rand < 6) {
                    button1.setText(model.bs2);
                    button2.setText(model.bs3);
                    button3.setText(model.bs1);
                }
                else {
                    button1.setText(model.bs3);
                    button2.setText(model.bs1);
                    button3.setText(model.bs2);
                }
                cdTimer = new CountDownTimer(5000,1000){

                   @Override
                   public void onTick(long millisUntilFinished) {
                       timer.setText(String.valueOf(model.counter));
                       model.counter++;
                   }

                   @Override
                   public void onFinish() {
                       timer.setText("Over");
                       loseGame();
                   }
               }.start();

            }
        }
    }

    public void answer(View v){
        Button bt = (Button) v;
        if(model.bs2.equals("")){
            outputText.setText("No input entered");
            number.setText("");
            number.requestFocus();
        }
        else{
            if(bt.getText()== model.bs2) {
                outputText.setText("Correct");
                currentLayout.setBackgroundColor(Color.GREEN);
                model.score += 10;
                cdTimer.cancel();

            }
            else {
                loseGame();
            }

            if(bt == button2){
                button1.setEnabled(false);
                button3.setEnabled(false);
            }
            else if (bt == button1){
                button2.setEnabled(false);
                button3.setEnabled(false);
            }
            else{
                button1.setEnabled(false);
                button2.setEnabled(false);
            }
        }
    }

    public void refresh(View view){
        number.setEnabled(true);
        number.setText("");
        timer.setText("");
        model.counter = 0;
        outputText.setText("");
        button1.setText("BUTTON_1");
        button2.setText("BUTTON_2");
        button3.setText("BUTTON_3");
        number.requestFocus();
        model.bs2 = "";
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        click.setEnabled(true);
        currentLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public void startGame(View view){

        number.setEnabled(true);
        timer.setText("");
        model.counter = 0;
        model.score = 0;
        newGame.setEnabled(false);
        next.setEnabled(true);
        number.setText("");
        outputText.setText("");
        button1.setText("BUTTON_1");
        button2.setText("BUTTON_2");
        button3.setText("BUTTON_3");
        number.requestFocus();
        model.bs2 = "";
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        click.setEnabled(true);
        currentLayout.setBackgroundColor(Color.TRANSPARENT);

    }

    public void loseGame(){
        model.highScore = sharedPref.getInt(getString(R.string.saved_high_score_key),0);
        if(model.score > model.highScore){
            model.highScore = model.score;
            editor.putInt(getString(R.string.saved_high_score_key), model.highScore);
            editor.commit();
        }
        outputText.setText("Wrong. The answer is " + model.bs2 + "  GAME OVER!!!  " + "Your score: " + model.score + "  Best Score: " + model.highScore);
        currentLayout.setBackgroundColor(Color.RED);
        v.vibrate(500);
        newGame.setEnabled(true);
        next.setEnabled(false);
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);

    }
}

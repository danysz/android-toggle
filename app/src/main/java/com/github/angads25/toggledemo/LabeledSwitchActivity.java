package com.github.angads25.toggledemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LabeledSwitchActivity extends AppCompatActivity {
    private Timer timers[];
    private volatile boolean stopped = false;

    private int[] switches = {
            R.id.switch1, R.id.switch2,
            R.id.switch4, R.id.switch5,
            R.id.switch7, R.id.switch8,
    };

    private LabeledSwitch[] labeledSwitches;

    private TimerTask[] timerTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labeled_switch);

        Typeface openSansBold = ResourcesCompat.getFont(LabeledSwitchActivity.this, R.font.open_sans_bold);

        timers = new Timer[switches.length];
        timerTasks = new TimerTask[switches.length];
        labeledSwitches = new LabeledSwitch[switches.length];

        for (int i = 0; i < switches.length; i++) {
            labeledSwitches[i] = findViewById(switches[i]);
            timers[i] = new Timer();

            final int finalI = i;
            timerTasks[i] = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> labeledSwitches[finalI].performClick());
                }
            };

            int delay = (2 + new Random().nextInt(5)) * 1000;
            Handler timerHandler = new Handler();
            timerHandler.postDelayed(() -> {
                if (!stopped) {
                    labeledSwitches[finalI].performClick();
                    timers[finalI].schedule(timerTasks[finalI], 0, 10000);
                }
            }, delay);
        }

        labeledSwitches[2].setTypeface(openSansBold);
        labeledSwitches[3].setTypeface(openSansBold);
    }

    @Override
    protected void onStop() {
        stopped = true;
        for (int i = 0; i < switches.length; i++) {
            if (timers[i] != null) {
                timers[i].cancel();
            }
        }
        super.onStop();
    }
}

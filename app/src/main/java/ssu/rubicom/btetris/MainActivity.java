package ssu.rubicom.btetris;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ssu.rubicom.tetris.Tetris;

public class MainActivity extends AppCompatActivity {
    private TetrisView myTetView, peerTetView;
    private BlockView myBlkView, peerBlkView;
    private Button upArrowBtn, leftArrowBtn, rightArrowBtn, downArrowBtn, dropBtn, topLeftBtn, topRightBtn;
    private Button startBtn, pauseBtn, settingBtn, modeBtn, reservedBtn;
    private boolean gameStarted = false;
    private TetrisModel myTetModel;
    private Random random;
    private Tetris.TetrisState state;
    private int dy = 25, dx = 15;
    private char currBlk, nextBlk;
    private Timer t;
    private TimerHandler job;
    private Tetris.TetrisState savedState;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTetView = (TetrisView) findViewById(R.id.myTetrisView);
        peerTetView = (TetrisView) findViewById(R.id.peerTetrisView);
        myBlkView = (BlockView) findViewById(R.id.myBlockView);
        peerBlkView = (BlockView) findViewById(R.id.peerBlockView);
        startBtn = (Button) findViewById(R.id.startBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        settingBtn = (Button) findViewById(R.id.settingBtn);
        modeBtn = (Button) findViewById(R.id.modeBtn);
        reservedBtn = (Button) findViewById(R.id.reservedBtn);
        upArrowBtn = (Button) findViewById(R.id.upArrowBtn);
        leftArrowBtn = (Button) findViewById(R.id.leftArrowBtn);
        rightArrowBtn = (Button) findViewById(R.id.rightArrowBtn);
        downArrowBtn = (Button) findViewById(R.id.downArrowBtn);
        dropBtn = (Button) findViewById(R.id.dropBtn);
        topLeftBtn = (Button) findViewById(R.id.topLeftBtn);
        topRightBtn = (Button) findViewById(R.id.topRightBtn);

        startBtn.setOnClickListener(OnClickListener);
        pauseBtn.setOnClickListener(OnClickListener);
        settingBtn.setOnClickListener(OnClickListener);
        modeBtn.setOnClickListener(OnClickListener);
        reservedBtn.setOnClickListener(OnClickListener);

        upArrowBtn.setOnClickListener(OnClickListener);
        leftArrowBtn.setOnClickListener(OnClickListener);
        rightArrowBtn.setOnClickListener(OnClickListener);
        downArrowBtn.setOnClickListener(OnClickListener);
        dropBtn.setOnClickListener(OnClickListener);


        setButtonsState(false);
    }
    private void setButtonsState(boolean flag) {
        pauseBtn.setEnabled(flag);
        modeBtn.setEnabled(false);
        reservedBtn.setEnabled(false);
        settingBtn.setEnabled(!flag);

        upArrowBtn.setEnabled(flag);
        leftArrowBtn.setEnabled(flag);
        rightArrowBtn.setEnabled(flag);
        downArrowBtn.setEnabled(flag);
        dropBtn.setEnabled(flag);
        topLeftBtn.setEnabled(false); // always disabled
        topRightBtn.setEnabled(false); // always disabled
    }
    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            char key;
            int id = v.getId();
            switch (id) {
                case R.id.startBtn: key = 'N';
                    if (gameStarted == false) {
                        gameStarted = true;
                        setButtonsState(true);
                        startBtn.setText("Q"); // 'Q' means Quit
                        enableTimer();
                        saveGameState(gameStarted);
                        Toast.makeText(MainActivity.this, "Game Started!", Toast.LENGTH_SHORT).show();
                        try {
                            random = new Random();
                            myTetModel = new TetrisModel(dy, dx);
                            myTetView.init(dy, dx, myTetModel.board.iScreenDw);
                            myBlkView.init(myTetModel.board.iScreenDw);
                            currBlk = (char) ('0' + random.nextInt(myTetModel.board.nBlockTypes));
                            nextBlk = (char) ('0' + random.nextInt(myTetModel.board.nBlockTypes));
                            state = myTetModel.accept(currBlk);
                            myTetView.accept(myTetModel.board.oScreen);
                            myBlkView.accept(myTetModel.getBlock(nextBlk));
                            myTetView.invalidate();
                            myBlkView.invalidate();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        gameStarted = false;
                        setButtonsState(false);
                        disableTimer();
                        startBtn.setText("N"); // 'N' means New Game.
                        saveGameState(gameStarted);
                        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                case R.id.settingBtn:
                    Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                    startActivityForResult(intent,1000);
                    return;
                case R.id.pauseBtn: key = 'P'; break;
                case R.id.upArrowBtn: key = 'w'; break;
                case R.id.leftArrowBtn: key = 'a'; break;
                case R.id.rightArrowBtn: key = 'd'; break;
                case R.id.downArrowBtn: key = 's'; break;
                case R.id.dropBtn: key = ' '; break;
                default: return;
            }
            try {
                state = myTetModel.accept(key);
                myTetView.accept(myTetModel.board.oScreen);
                if (state == Tetris.TetrisState.NewBlock){
                    currBlk = nextBlk;
                    nextBlk = (char) ('0' + random.nextInt(myTetModel.board.nBlockTypes));
                    state = myTetModel.accept(currBlk);
                    myTetView.accept(myTetModel.board.oScreen);
                    myBlkView.accept(myTetModel.getBlock(nextBlk));
                    myBlkView.invalidate();
                    if (state == Tetris.TetrisState.Finished) {
                        gameStarted = false;
                        setButtonsState(false);
                        startBtn.setText("N");
                        disableTimer();
                        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                    }
                }
                myTetView.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if(requestCode == 1000 && resultCode == RESULT_OK){
            final String IP = data.getStringExtra("IP");
            final String port = data.getStringExtra("port");
            Toast.makeText(this, IP+":"+port+" 세팅완료", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == 1000 && resultCode==RESULT_CANCELED){
            disableTimer();
            Toast.makeText(this, "IP 세팅이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "잘못된 동작입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences = getSharedPreferences("cache", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("state",2);
        editor.putBoolean("gameStarted",false);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableTimer();
        int save = 2;
        savedState = state;
        switch (savedState){
            case Running:
                save = 0;
                break;
            case NewBlock:
                save = 1;
                break;
            case Finished:
                save = 2;
                break;
        }
        preferences = getSharedPreferences("cache", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("state",save);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences("cache", Activity.MODE_PRIVATE);

        int save = preferences.getInt("state",2);
        switch (save){
            case 0:
                savedState = Tetris.TetrisState.Running;
                break;
            case 1:
                savedState = Tetris.TetrisState.NewBlock;
                break;
            case 2:
                savedState = Tetris.TetrisState.Finished;
                break;
        }
        if(preferences.getBoolean("gameStarted",false)) {
            if (savedState == Tetris.TetrisState.Running) {
                enableTimer();
            } else if (savedState == Tetris.TetrisState.Finished) {
                disableTimer();
            }
        }
    }

    private class TimerHandler extends TimerTask{

        @Override
        public void run() {
            if(state != Tetris.TetrisState.Finished){
                try{
                    state = myTetModel.accept('s');
                    myTetView.accept(myTetModel.board.oScreen);
                    if (state == Tetris.TetrisState.NewBlock){
                        currBlk = nextBlk;
                        nextBlk = (char) ('0' + random.nextInt(myTetModel.board.nBlockTypes));
                        state = myTetModel.accept(currBlk);
                        myTetView.accept(myTetModel.board.oScreen);
                        myBlkView.accept(myTetModel.getBlock(nextBlk));
                        myBlkView.invalidate();
                        if (state == Tetris.TetrisState.Finished) {
                            gameStarted = false;
                            setButtonsState(false);
                            startBtn.setText("N");
                            saveGameState(gameStarted);
                            Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    myTetView.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    private void enableTimer(){
        t = new Timer();
        job = new TimerHandler();
        t.scheduleAtFixedRate(job,1000,1000);
    }
    private void disableTimer(){
        t.cancel();
    }

    private void saveGameState(boolean gameStarted){
        preferences = getSharedPreferences("cache", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("gameStarted",gameStarted);
        editor.commit();
    }

}



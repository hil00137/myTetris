package ssu.rubicom.btetris;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {
    private EditText edtIP,edtPort;
    private Button btnConfirm,btnCancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edtIP = (EditText)findViewById(R.id.edtIP);
        edtPort= (EditText)findViewById(R.id.edtPort);
        btnCancle = (Button)findViewById(R.id.btnCancel);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String IP = edtIP.getText().toString();
                final String port = edtPort.getText().toString();
                Intent data = new Intent();
                data.putExtra("IP",IP);
                data.putExtra("port",port);

                setResult(RESULT_OK,data);
                finish();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}

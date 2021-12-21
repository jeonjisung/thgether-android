package kr.co.oliveandwine.trashcan;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.oliveandwine.trashcan.Save.Save_Var;

public class CreateDialog extends Dialog {

    private Button Confirm;
    private Button Cancel;

    private View.OnClickListener Confirm_Btn;
    private View.OnClickListener Cancel_Btn;

    private Button Camera;
    private EditText Content;
    private TextView Title;

    private String title;
    private String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.create_dialog);

        Confirm = (Button)findViewById(R.id.create_dialog_Confirm);
        Cancel = (Button)findViewById(R.id.create_dialog_Cancel);

        Title = findViewById(R.id.create_dialog_title);
        Camera = findViewById(R.id.pickCamera);
        Content = findViewById(R.id.create_dialog_content);

        Confirm.setOnClickListener(Confirm_Btn);
        Cancel.setOnClickListener(Cancel_Btn);

        Save_Var.getInstance().setContentText(Content);
        //타이틀과 바디의 글씨 재셋팅
        Title.setText(this.title);

        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save_Var.getInstance().getMapFragment().goCamera();
            }
        });
    }

    public CreateDialog(@NonNull Context context, View.OnClickListener Confirm_Btn, View.OnClickListener Cancel_Btn, String title) {
        super(context);
        //생성자에서 리스너 및 텍스트 초기화
        this.Confirm_Btn = Confirm_Btn;
        this.Cancel_Btn = Cancel_Btn;
        this.title = title;
    }

    public EditText getContent() {
        return Content;
    }
}

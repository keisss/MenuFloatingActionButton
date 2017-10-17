package cn.keiss.menufloatingactionbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.keiss.menufab.listener.OnFloatActionButtonClickListener;
import cn.keiss.menufab.listener.OnMenuItemClickListener;
import cn.keiss.menufab.view.MenuFloatingActionButton;
import cn.keiss.menufab.view.MenuView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MenuFloatingActionButton button = (MenuFloatingActionButton) findViewById(R.id.eer);
        button.setOnFabClickListener(new OnFloatActionButtonClickListener() {
            @Override
            public void onClick() {

            }
        });
        button.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onClick(MenuView view, int position) {
                Log.e("f","ffffffff");
            }
        });
    }
}

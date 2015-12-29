package pk.net.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pk.net.HttpManager;
import pk.net.R;
import pk.net.core.IResult;
import pk.net.core.Result;

public class MainActivity extends Activity {

    private TextView text;

    private TestApi api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //将接口转型为代理类
        api = HttpManager.getProxy(TestApi.class);

        text = new TextView(this);
        text.setText("信息框");
        setContentView(text);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text.setText("开始执行");
                api.testRequest("wzj", "123", new IResult<Entity>() {
                    @Override
                    public void onResult(Result<Entity> result) {
                        text.setText(result + "");
                    }
                });
            }
        });
    }

}

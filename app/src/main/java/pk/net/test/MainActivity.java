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

import java.util.HashMap;
import java.util.Map;

import pk.net.HttpManager;
import pk.net.R;
import pk.net.core.IResult;
import pk.net.core.Result;
import pk.net2.Dispatcher;
import pk.net2.IRequest;
import pk.net2.Request;
import pk.net2.Response;

public class MainActivity extends Activity {

    private TextView text;

    private TestApi api;
    private Dispatcher dispatcher = Dispatcher.instance();

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
                test1();
//                test2();
            }
        });
    }

    private void test2() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "wzj");
        map.put("password", "123");
        Request<Entity> request = new Request<Entity>()
                .setUrl("http://test.jldo2o.com/homelifeinterface/product/getProducts.do")
                .setParams(map)
                .setMethod(IRequest.Method.GET)
                .create();

        dispatcher.execute(request, new pk.net2.IResult<Entity>() {
            @Override
            public void onResult(Response<Entity> result) {
                text.setText(result + "");
            }
        });
    }

    void test1() {
        pk.net.core.IRequest request = api.testRequest("wzj", "123", new IResult<Entity>() {
            @Override
            public void onResult(Result<Entity> result) {
                text.setText(result + "");
            }
        });
        System.out.print("");
    }



}

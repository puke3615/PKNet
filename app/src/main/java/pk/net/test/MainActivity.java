package pk.net.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

import pk.net.HttpManager;
import pk.net.core.IResult;
import pk.net.core.ITask;
import pk.net.core.Result;
import pk.net.execute.HttpExecutor;

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
                test1();
//                test2();
            }
        });
        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, a.intValue() + "", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


    ITask task = null;
    AtomicInteger a = new AtomicInteger();

    void test1() {
        HttpExecutor.instance().configureTaskCreator(new ITask.ITaskCreator() {
            @Override
            public void afterCreator(ITask task) {
                task.setTag(MainActivity.this);
            }
        });
        task = api.testRequest("wzj", "123", new IResult<Entity>() {
            @Override
            public void onResult(Result<Entity> result) {
                a.incrementAndGet();
                text.setText(result + "");
            }
        });
        System.out.print("");
    }


}

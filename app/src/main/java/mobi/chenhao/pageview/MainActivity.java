package mobi.chenhao.pageview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

public class MainActivity extends Activity implements OneCodePageView.OneCodeLoadListener{

    private OneCodePageView pageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageView = (OneCodePageView)findViewById(R.id.list);
        pageView.setAdapter(new LayoutAdapter(this));
        pageView.setLoadListener(this);
        load();
    }

    private void load(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageView.stopLoad();
            }
        },5000);
    }

    @Override
    public void onRefresh() {
        Toast toast=Toast.makeText(this,"刷新",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        load();
    }

    @Override
    public void onLoadMore() {
        Toast toast=Toast.makeText(this,"手动加载更多",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        load();
    }

    @Override
    public void onAutoLoadMore() {
        Toast toast=Toast.makeText(this,"自动加载更多",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        load();
    }



}

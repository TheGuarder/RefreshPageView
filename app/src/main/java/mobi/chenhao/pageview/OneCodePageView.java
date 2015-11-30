package mobi.chenhao.pageview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.nineoldandroids.view.ViewHelper;
import mobi.chenhao.pageview.rebound.SpringSystem;

/**
 * Created by onecode on 15/11/25.
 */
public class OneCodePageView extends FrameLayout{

    private float dp;
    private float downX=0,speedX=0;
    private final float SPEED_CHECK=100;
    private RecyclerViewPager contentView;
    private boolean isCanRefrash,isCanMore,isLoading;
    private RecyclerView.OnScrollListener onScrollListener;
    private View.OnLayoutChangeListener onLayoutChangeListener;
    private RecyclerViewPager.OnPageChangedListener onPageChangedListener;
    private OneCodeLoadListener loadListener;
    private View refrashAnimView,moreAnimView;
    private View refrashView,moreView;

    private Spring tranXSpring,rotateSpring;
    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private final ExampleSpringListener mSpringListener = new ExampleSpringListener();

    public OneCodePageView(Context context) {
        super(context);
        initView();
    }

    public OneCodePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OneCodePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

//    private void resetSpring() {
//        if (tranXSpring.isAtRest()) {
//            tranXSpring.removeAllListeners();
//            tranXSpring.setCurrentValue(0);
//            tranXSpring.setEndValue(0);
//            tranXSpring.addListener(mSpringListener);
//        }
//        if (rotateSpring.isAtRest()) {
//            rotateSpring.removeAllListeners();
//            rotateSpring.setCurrentValue(0);
//            rotateSpring.setEndValue(0);
//            rotateSpring.addListener(mSpringListener);
//        }
//    }

    private void initView(){
        this.removeAllViews();
        tranXSpring = mSpringSystem.createSpring();
        rotateSpring = mSpringSystem.createSpring();
        tranXSpring.addListener(mSpringListener);
        rotateSpring.addListener(mSpringListener);
        tranXSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20,4));
        rotateSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(20,4));

        LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_onecode_page, this);
        dp=getResources().getDisplayMetrics().density;
        contentView=(RecyclerViewPager) findViewById(R.id.view_pager);
        contentView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        contentView.setHasFixedSize(true);
        contentView.setLongClickable(true);
        contentView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (null!=onScrollListener){
                    onScrollListener.onScrollStateChanged(recyclerView,scrollState);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int childCount = contentView.getChildCount();
                int width = contentView.getChildAt(0).getWidth();
                int padding = (contentView.getWidth() - width) / 2;
                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
                    float rate = 0;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                        v.setScaleX(1 - rate * 0.1f);
                    } else {
                        //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setScaleX(0.9f + rate * 0.1f);
                    }
                }
                if (null!=onScrollListener){
                    onScrollListener.onScrolled(recyclerView,i,i2);
                }
            }
        });
        contentView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                if (oldPosition<newPosition&&newPosition>0&&newPosition==getDataSize()-3&&null!=loadListener){
                    loadListener.onAutoLoadMore();
                }
                if (null!=onPageChangedListener){
                    onPageChangedListener.OnPageChanged(oldPosition, newPosition);
                }
            }
        });
        contentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (contentView.getChildCount() < 3) {
                    if (contentView.getChildAt(1) != null) {
                        if(contentView.getCurrentPosition()==0) {
                            View v1 = contentView.getChildAt(1);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        } else {
                            View v1 = contentView.getChildAt(0);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        }
                    }
                } else {
                    if (contentView.getChildAt(0) != null) {
                        View v0 = contentView.getChildAt(0);
                        v0.setScaleY(0.9f);
                        v0.setScaleX(0.9f);
                    }
                    if (contentView.getChildAt(2) != null) {
                        View v2 = contentView.getChildAt(2);
                        v2.setScaleY(0.9f);
                        v2.setScaleX(0.9f);
                    }
                }
                if (null!=onLayoutChangeListener){
                    onLayoutChangeListener.onLayoutChange(v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom);
                }
            }
        });
        super.setPadding(0,0,0,0);
        refrashView=findViewById(R.id.onecode_page_refresh_layout);
        refrashAnimView=findViewById(R.id.onecode_page_refresh_anim);
        moreView=findViewById(R.id.onecode_page_more_layout);
        moreAnimView=findViewById(R.id.onecode_page_more_anim);
        resetLoadView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isLoading){
            return true;
        }
        if(isCanRefrash||isCanMore){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX=event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    speedX=event.getX()-downX;
                    if (isCanRefrash){
                        tranXSpring.setEndValue(speedX-getWidth());
                    }else{
                        tranXSpring.setEndValue(speedX+getWidth());
                    }
                    rotateSpring.setEndValue(speedX/2);
                    break;
//                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (isCanRefrash){
                        double end=-getWidth()*0.6;
                        if (speedX-getWidth()<end){
                            stopLoad();
                        }else{
                            isLoading=true;
                            if (null!=loadListener){
                                loadListener.onRefresh();
                            }
                            tranXSpring.setEndValue(end);
                            refrashAnimView.startAnimation(getAnim());
                        }
                    }else{
                        double end=getWidth()*0.6;
                        if (speedX+getWidth()>end){
                            stopLoad();
                        }else{
                            isLoading=true;
                            if (null!=loadListener){
                                loadListener.onLoadMore();
                            }
                            tranXSpring.setEndValue(end);
                            moreAnimView.startAnimation(getAnim());
                        }
                    }
                    break;
            }
            return true;
        }else{
            return super.onTouchEvent(event);
        }
    }

    private RotateAnimation getAnim(){
        RotateAnimation animation =new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);
        animation.setRepeatCount(-1);
        animation.setDuration(500);
        return animation;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isLoading){
            return true;
        }
        int nowIndex=contentView.getCurrentPosition();
        if (nowIndex<=0||nowIndex>=getDataSize()-1){
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                downX=event.getX();
            }
        }else{
            downX=0;
        }
        if (nowIndex<=0&&event.getAction()==MotionEvent.ACTION_MOVE&&event.getX()-downX>SPEED_CHECK){
            isCanRefrash=true;
            isCanMore=false;
            return true;
        }else if(nowIndex>=getDataSize()-1&&event.getAction()==MotionEvent.ACTION_MOVE&&event.getX()-downX<=-SPEED_CHECK){
            isCanRefrash=false;
            isCanMore=true;
            return true;
        }else{
            isCanMore=false;
            isCanRefrash=false;
            return super.onInterceptTouchEvent(event);
        }
    }

    public void resetLoadView(){
        ViewHelper.setTranslationX(refrashView,-getWidth());
        ViewHelper.setTranslationX(moreView,getWidth());
        refrashAnimView.clearAnimation();
        refrashView.setVisibility(GONE);
        moreAnimView.clearAnimation();
        moreView.setVisibility(GONE);
    }

    public void stopLoad(){
        tranXSpring.setOvershootClampingEnabled(true);
        if (isCanRefrash){
            tranXSpring.setEndValue(-getWidth());
        }else{
            tranXSpring.setEndValue(getWidth());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isCanRefrash=false;
                isCanMore=false;
                resetLoadView();
                isLoading=false;
            }
        },500);
    }

    private class ExampleSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            String springId = spring.getId();
            if(springId.equals(tranXSpring.getId())){
                if (isCanRefrash){
                    if (ViewHelper.getTranslationX(refrashView)!=value){
                        ViewHelper.setTranslationX(refrashView,value);
                        if (refrashView.getVisibility()!=VISIBLE){
                            refrashView.setVisibility(VISIBLE);
                        }
                    }
                }else if(isCanMore){
                    if (ViewHelper.getTranslationX(moreView)!=value){
                        ViewHelper.setTranslationX(moreView,value);
                        if (moreView.getVisibility()!=VISIBLE){
                            moreView.setVisibility(VISIBLE);
                        }
                    }
                }
            }else if(springId.equals(rotateSpring.getId())){
                if (isCanRefrash){
                    if (ViewHelper.getRotation(refrashAnimView)!=value){
                        ViewHelper.setRotation(refrashAnimView,value);
                    }
                }else if(isCanMore){
                    if (ViewHelper.getRotation(moreAnimView)!=value){
                        ViewHelper.setRotation(moreAnimView,value);
                    }
                }
            }
        }
    }

    public RecyclerViewPager getContentView(){
        return contentView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        contentView.setAdapter(adapter);
    }

    public int getDataSize(){
        return null!=contentView&&null!=contentView.getAdapter()?contentView.getAdapter().getItemCount():0;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0,top,0,bottom);
    }

    public void setContentPadding(int l, int t, int r, int b){
        if (null!=contentView){
            contentView.setPadding((int)(l*dp),(int)(t*dp),(int)(r*dp),(int)(b*dp));
        }
    }

    public void setContentViewOnScrollListener(RecyclerView.OnScrollListener listener){
        onScrollListener=listener;
    }
    public void setContentViewOnLayoutChangeListener(View.OnLayoutChangeListener listener){
        onLayoutChangeListener=listener;
    }
    public void setContentViewOnPageChangedListener(RecyclerViewPager.OnPageChangedListener listener){
        onPageChangedListener=listener;
    }

    public void setLoadListener(OneCodeLoadListener listener){
        loadListener=listener;
    }

    public interface OneCodeLoadListener{
        public void onRefresh();
        public void onLoadMore();
        public void onAutoLoadMore();
    }

}

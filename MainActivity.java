package com.my.newproject;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.LinearLayout;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	private LinearLayout linear3;
	private LinearLayout linear2;
	private LinearLayout linear1;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear3 = findViewById(R.id.linear3);
		linear2 = findViewById(R.id.linear2);
		linear1 = findViewById(R.id.linear1);
	}
	
	private void initializeLogic() {
		linear1.addView(new _ColorSeekBar(MainActivity.this));
		((_ColorSeekBar)linear1.getChildAt((int)0)).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		((_ColorSeekBar)linear1.getChildAt((int)0)).setShowAlphaBar(true);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setColorBarPosition((int)20);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setAlphaBarPosition((int)50);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setThumbHeight((int)5);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setBarMargin((int)5);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setBarHeight((int) 3);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setMaxPosition((int)100);
		((_ColorSeekBar)linear1.getChildAt((int)0)).setOnColorChangeListener(new _ColorSeekBar.OnColorChangeListener() {
			@Override
			public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
				_color_ = color;
				linear2.setBackgroundColor(_color_);
			}
		});
	}
	private int _color_ = 0xFF000000;
	public static class _ColorSeekBar extends View {
		    private int[] mColorSeeds = new int[]{0xFF000000, 0xFF9900FF, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
			            0xFFFF0000, 0xFFFF00FF, 0xFFFF6600, 0xFFFFFF00, 0xFFFFFFFF, 0xFF000000};
		    private int mAlpha;
		    private OnColorChangeListener mOnColorChangeLister;
		    private Context mContext;
		    private boolean mIsShowAlphaBar = false;
		    private boolean mIsShowColorBar = true;
		    private boolean mIsVertical;
		    private boolean mMovingColorBar;
		    private boolean mMovingAlphaBar;
		    private Bitmap mTransparentBitmap;
		    private RectF mColorRect;
		    private int mThumbHeight = 20;
		    private float mThumbRadius;
		    private int mBarHeight = 2;
		    private Paint mColorRectPaint;
		    private int realLeft;
		    private int realRight;
		    private int mBarWidth;
		    private int mMaxPosition;
		    private RectF mAlphaRect = new RectF();
		    private int mColorBarPosition;
		    private int mAlphaBarPosition;
		    private int mDisabledColor;
		    private int mBarMargin = 5;
		    private int mAlphaMinPosition = 0;
		    private int mAlphaMaxPosition = 255;
		    private int mBarRadius;
		    private List<Integer> mCachedColors = new ArrayList<>();
		    private int mColorsToInvoke = -1;
		    private boolean mInit = false;
		    private boolean mFirstDraw = true;
		    private boolean mShowThumb = true;
		    private OnInitDoneListener mOnInitDoneListener;
		
		    private Paint colorPaint = new Paint();
		    private Paint alphaThumbGradientPaint = new Paint();
		    private Paint alphaBarPaint = new Paint();
		    private Paint mDisabledPaint = new Paint();
		    private Paint thumbGradientPaint = new Paint();
		
		    public _ColorSeekBar(Activity context) {
			        super(context);
			        mContext = context;
			        init(context, null, 0, 0);
			    }
		
		public _ColorSeekBar(Fragment context) {
			        super(context.getActivity());
			        mContext = context.getActivity();
			        init(mContext, null, 0, 0);
			    }
		
		
		    public _ColorSeekBar(Context context, AttributeSet attrs) {
			        super(context, attrs);
			        mContext = context;
			        init(context, attrs, 0, 0);
			    }
		
		    public _ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
			        super(context, attrs, defStyleAttr);
			        mContext = context;
			        init(context, attrs, defStyleAttr, 0);
			    }
		
		  
		    public _ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			        super(context, attrs, defStyleAttr, defStyleRes);
			        mContext = context;
			        init(context, attrs, defStyleAttr, defStyleRes);
			    }
		
		    protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			        applyStyle(context, attrs, defStyleAttr, defStyleRes);
			    }
		
		    public void applyStyle(int resId) {
			        applyStyle(getContext(), null, 0, resId);
			    }
		
		    @Override
		    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			        Logger.i("onMeasure");
			        int mViewWidth = widthMeasureSpec;
			        int mViewHeight = heightMeasureSpec;
			
			        int widthSpeMode = MeasureSpec.getMode(widthMeasureSpec);
			        int heightSpeMode = MeasureSpec.getMode(heightMeasureSpec);
			
			        int barHeight = (mIsShowAlphaBar && mIsShowColorBar) ? mBarHeight * 2 : mBarHeight;
			        int thumbHeight = (mIsShowAlphaBar && mIsShowColorBar) ? mThumbHeight * 2 : mThumbHeight;
			
			        Logger.i("widthSpeMode:");
			        Logger.spec(widthSpeMode);
			        Logger.i("heightSpeMode:");
			        Logger.spec(heightSpeMode);
			
			        if (isVertical()) {
				            if (widthSpeMode == MeasureSpec.AT_MOST || widthSpeMode == MeasureSpec.UNSPECIFIED) {
					                mViewWidth = thumbHeight + barHeight + mBarMargin;
					                setMeasuredDimension(mViewWidth, mViewHeight);
					            }
				
				        } else {
				            if (heightSpeMode == MeasureSpec.AT_MOST || heightSpeMode == MeasureSpec.UNSPECIFIED) {
					                mViewHeight = thumbHeight + barHeight + mBarMargin;
					                setMeasuredDimension(mViewWidth, mViewHeight);
					            }
				        }
			    }
		
		
		    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
			        
			    }
		
		    /**
     * @param id color array resource
     * @return
     */
		   
		
		    private void init() {
			        Logger.i("init");
			        //init size
			        mThumbRadius = mThumbHeight / 2;
			        int mPaddingSize = (int) mThumbRadius;
			        int viewBottom = getHeight() - getPaddingBottom() - mPaddingSize;
			        int viewRight = getWidth() - getPaddingRight() - mPaddingSize;
			        //init left right top bottom
			        realLeft = getPaddingLeft() + mPaddingSize;
			        realRight = mIsVertical ? viewBottom : viewRight;
			        int realTop = getPaddingTop() + mPaddingSize;
			
			        mBarWidth = realRight - realLeft;
			
			        //init rect
			        mColorRect = new RectF(realLeft, realTop, realRight, realTop + mBarHeight);
			
			        //init paint
			        LinearGradient mColorGradient = new LinearGradient(0, 0, mColorRect.width(), 0, mColorSeeds, null, Shader.TileMode.CLAMP);
			        mColorRectPaint = new Paint();
			        mColorRectPaint.setShader(mColorGradient);
			        mColorRectPaint.setAntiAlias(true);
			        cacheColors();
			        setAlphaValue();
			    }
		
		    @Override
		    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			        super.onSizeChanged(w, h, oldw, oldh);
			        Logger.i("onSizeChanged");
			        if (mIsVertical) {
				            mTransparentBitmap = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_4444);
				        } else {
				            mTransparentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
				        }
			        mTransparentBitmap.eraseColor(Color.TRANSPARENT);
			        init();
			        mInit = true;
			        if (mColorsToInvoke != -1) {
				            setColor(mColorsToInvoke);
				        }
			    }
		
		
		    private void cacheColors() {
			        //if the view's size hasn't been initialized. do not cache.
			        if (mBarWidth < 1) {
				            return;
				        }
			        mCachedColors.clear();
			        for (int i = 0; i <= mMaxPosition; i++) {
				            mCachedColors.add(pickColor(i));
				        }
			    }
		
		    @Override
		    protected void onDraw(Canvas canvas) {
			        Logger.i("onDraw");
			
			        if (mIsVertical) {
				            canvas.rotate(-90);
				            canvas.translate(-getHeight(), 0);
				            canvas.scale(-1, 1, getHeight() / 2, getWidth() / 2);
				        }
			
			        int color = isEnabled() ? getColor(false) : mDisabledColor;
			
			        int colorStartTransparent = Color.argb(mAlphaMaxPosition, Color.red(color), Color.green(color), Color.blue(color));
			        int colorEndTransparent = Color.argb(mAlphaMinPosition, Color.red(color), Color.green(color), Color.blue(color));
			        int[] toAlpha = new int[]{colorStartTransparent, colorEndTransparent};
			
			        if (mIsShowColorBar) {
				            float colorPosition = (float) mColorBarPosition / mMaxPosition * mBarWidth;
				            colorPaint.setAntiAlias(true);
				            colorPaint.setColor(color);
				            //clear
				            canvas.drawBitmap(mTransparentBitmap, 0, 0, null);
				
				            //draw color bar
				            canvas.drawRoundRect(mColorRect, mBarRadius, mBarRadius, isEnabled() ? mColorRectPaint : mDisabledPaint);
				            //draw color bar thumb
				            if (mShowThumb) {
					                float thumbX = colorPosition + realLeft;
					                float thumbY = mColorRect.top + mColorRect.height() / 2;
					                canvas.drawCircle(thumbX, thumbY, mBarHeight / 2 + 5, colorPaint);
					
					                //draw color bar thumb radial gradient shader
					                RadialGradient thumbShader = new RadialGradient(thumbX, thumbY, mThumbRadius, toAlpha, null, Shader.TileMode.MIRROR);
					                thumbGradientPaint.setAntiAlias(true);
					                thumbGradientPaint.setShader(thumbShader);
					                canvas.drawCircle(thumbX, thumbY, mThumbHeight / 2, thumbGradientPaint);
					            }
				        }
			
			
			        if (mIsShowAlphaBar) {
				            //init rect
				            if (mIsShowColorBar) {
					                int top = mIsShowColorBar ? (int) (mThumbHeight + mThumbRadius + mBarHeight + mBarMargin) :
					                        (int) (mThumbHeight + mThumbRadius + mBarMargin);
					                mAlphaRect = new RectF(realLeft, top, realRight, top + mBarHeight);
					            } else {
					                mAlphaRect = new RectF(mColorRect);
					            }
				
				            //draw alpha bar
				            alphaBarPaint.setAntiAlias(true);
				            LinearGradient alphaBarShader = new LinearGradient(0, 0, mAlphaRect.width(), 0, toAlpha, null, Shader.TileMode.CLAMP);
				            alphaBarPaint.setShader(alphaBarShader);
				            canvas.drawRect(mAlphaRect, alphaBarPaint);
				
				            //draw alpha bar thumb
				            if (mShowThumb) {
					                float alphaPosition = (float) (mAlphaBarPosition - mAlphaMinPosition) / (mAlphaMaxPosition - mAlphaMinPosition) * mBarWidth;
					                float alphaThumbX = alphaPosition + realLeft;
					                float alphaThumbY = mAlphaRect.top + mAlphaRect.height() / 2;
					                canvas.drawCircle(alphaThumbX, alphaThumbY, mBarHeight / 2 + 5, colorPaint);
					
					                //draw alpha bar thumb radial gradient shader
					                RadialGradient alphaThumbShader = new RadialGradient(alphaThumbX, alphaThumbY, mThumbRadius, toAlpha, null, Shader.TileMode.MIRROR);
					
					                alphaThumbGradientPaint.setAntiAlias(true);
					                alphaThumbGradientPaint.setShader(alphaThumbShader);
					                canvas.drawCircle(alphaThumbX, alphaThumbY, mThumbHeight / 2, alphaThumbGradientPaint);
					            }
				        }
			
			        if (mFirstDraw) {
				            if (mOnColorChangeLister != null) {
					                mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
					            }
				            mFirstDraw = false;
				
				            if (mOnInitDoneListener != null) {
					                mOnInitDoneListener.done();
					            }
				        }
			        super.onDraw(canvas);
			    }
		
		
		    @Override
		    public boolean onTouchEvent(MotionEvent event) {
			        if (!isEnabled()) {
				            return true;
				        }
			        float x = mIsVertical ? event.getY() : event.getX();
			        float y = mIsVertical ? event.getX() : event.getY();
			        switch (event.getAction()) {
				            case MotionEvent.ACTION_DOWN:
				                if (mIsShowColorBar && isOnBar(mColorRect, x, y)) {
					                    mMovingColorBar = true;
					                    float value = (x - realLeft) / mBarWidth * mMaxPosition;
					                    setColorBarPosition((int) value);
					                } else if (mIsShowAlphaBar && isOnBar(mAlphaRect, x, y)) {
					                    mMovingAlphaBar = true;
					                }
				                break;
				            case MotionEvent.ACTION_MOVE:
				                getParent().requestDisallowInterceptTouchEvent(true);
				                if (mMovingColorBar) {
					                    float value = (x - realLeft) / mBarWidth * mMaxPosition;
					                    setColorBarPosition((int) value);
					                } else if (mIsShowAlphaBar) {
					                    if (mMovingAlphaBar) {
						                        float value = (x - realLeft) / (float) mBarWidth * (mAlphaMaxPosition - mAlphaMinPosition) + mAlphaMinPosition;
						                        mAlphaBarPosition = (int) value;
						                        if (mAlphaBarPosition < mAlphaMinPosition) {
							                            mAlphaBarPosition = mAlphaMinPosition;
							                        } else if (mAlphaBarPosition > mAlphaMaxPosition) {
							                            mAlphaBarPosition = mAlphaMaxPosition;
							                        }
						                        setAlphaValue();
						                    }
					                }
				                if (mOnColorChangeLister != null && (mMovingAlphaBar || mMovingColorBar)) {
					                    mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
					                }
				                invalidate();
				                break;
				            case MotionEvent.ACTION_UP:
				                mMovingColorBar = false;
				                mMovingAlphaBar = false;
				                break;
				            default:
				        }
			        return true;
			    }
		
		    @Override
		    public void setEnabled(boolean enabled) {
			        super.setEnabled(enabled);
			    }
		
		    /***
     *
     * @param alphaMaxPosition <= 255 && > alphaMinPosition
     */
		    public void setAlphaMaxPosition(int alphaMaxPosition) {
			        mAlphaMaxPosition = alphaMaxPosition;
			        if (mAlphaMaxPosition > 255) {
				            mAlphaMaxPosition = 255;
				        } else if (mAlphaMaxPosition <= mAlphaMinPosition) {
				            mAlphaMaxPosition = mAlphaMinPosition + 1;
				        }
			
			        if (mAlphaBarPosition > mAlphaMinPosition) {
				            mAlphaBarPosition = mAlphaMaxPosition;
				        }
			        invalidate();
			    }
		
		    public int getAlphaMaxPosition() {
			        return mAlphaMaxPosition;
			    }
		
		    /***
     *
     * @param alphaMinPosition >=0 && < alphaMaxPosition
     */
		    public void setAlphaMinPosition(int alphaMinPosition) {
			        this.mAlphaMinPosition = alphaMinPosition;
			        if (mAlphaMinPosition >= mAlphaMaxPosition) {
				            mAlphaMinPosition = mAlphaMaxPosition - 1;
				        } else if (mAlphaMinPosition < 0) {
				            mAlphaMinPosition = 0;
				        }
			
			        if (mAlphaBarPosition < mAlphaMinPosition) {
				            mAlphaBarPosition = mAlphaMinPosition;
				        }
			        invalidate();
			    }
		
		    public int getAlphaMinPosition() {
			        return mAlphaMinPosition;
			    }
		
		    /**
     * @param r
     * @param x
     * @param y
     * @return whether MotionEvent is performing on bar or not
     */
		    private boolean isOnBar(RectF r, float x, float y) {
			        if (r.left - mThumbRadius < x && x < r.right + mThumbRadius && r.top - mThumbRadius < y && y < r.bottom + mThumbRadius) {
				            return true;
				        } else {
				            return false;
				        }
			    }
		
		    /**
     * @return
     * @deprecated use {@link #setOnInitDoneListener(OnInitDoneListener)} instead.
     */
		    public boolean isFirstDraw() {
			        return mFirstDraw;
			    }
		
		
		    /**
     * @param value
     * @return color
     */
		    private int pickColor(int value) {
			        return pickColor((float) value / mMaxPosition * mBarWidth);
			    }
		
		    /**
     * @param position
     * @return color
     */
		    private int pickColor(float position) {
			        float unit = position / mBarWidth;
			        if (unit <= 0.0) {
				            return mColorSeeds[0];
				        }
			
			
			        if (unit >= 1) {
				            return mColorSeeds[mColorSeeds.length - 1];
				        }
			
			        float colorPosition = unit * (mColorSeeds.length - 1);
			        int i = (int) colorPosition;
			        colorPosition -= i;
			        int c0 = mColorSeeds[i];
			        int c1 = mColorSeeds[i + 1];
			//         mAlpha = mix(Color.alpha(c0), Color.alpha(c1), colorPosition);
			        int mRed = mix(Color.red(c0), Color.red(c1), colorPosition);
			        int mGreen = mix(Color.green(c0), Color.green(c1), colorPosition);
			        int mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition);
			        return Color.rgb(mRed, mGreen, mBlue);
			    }
		
		    /**
     * @param start
     * @param end
     * @param position
     * @return
     */
		    private int mix(int start, int end, float position) {
			        return start + Math.round(position * (end - start));
			    }
		
		    public int getColor() {
			        return getColor(mIsShowAlphaBar);
			    }
		
		    /**
     * @param withAlpha
     * @return
     */
		    public int getColor(boolean withAlpha) {
			        //pick mode
			        if (mColorBarPosition >= mCachedColors.size()) {
				            int color = pickColor(mColorBarPosition);
				            if (withAlpha) {
					                return color;
					            } else {
					                return Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
					            }
				        }
			
			        //cache mode
			        int color = mCachedColors.get(mColorBarPosition);
			
			        if (withAlpha) {
				            return Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
				        }
			        return color;
			    }
		
		    public int getAlphaBarPosition() {
			        return mAlphaBarPosition;
			    }
		
		    public int getAlphaValue() {
			        return mAlpha;
			    }
		
		    public interface OnColorChangeListener {
			        /**
         * @param colorBarPosition between 0-maxValue
         * @param alphaBarPosition between 0-255
         * @param color            return the color contains alpha value whether showAlphaBar is true or without alpha value
         */
			        void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color);
			    }
		
		    /**
     * @param onColorChangeListener
     */
		    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
			        this.mOnColorChangeLister = onColorChangeListener;
			    }
		
		
		    public int dp2px(float dpValue) {
			        final float scale = mContext.getResources().getDisplayMetrics().density;
			        return (int) (dpValue * scale + 0.5f);
			    }
		
		    /**
     * Set colors by resource id. The resource's type must be ArrayRes
     *
     * @param resId
     */
		    public void setColorSeeds( int resId) {
			       
			    }
		
		    public void setColorSeeds(int[] colors) {
			        mColorSeeds = colors;
			        init();
			        invalidate();
			        if (mOnColorChangeLister != null) {
				            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
				        }
			    }
		
		    /**
     * @param color
     * @return the color's position in the bar, if not in the bar ,return -1;
     */
		    public int getColorIndexPosition(int color) {
			        return mCachedColors.indexOf(Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)));
			    }
		
		    public List<Integer> getColors() {
			        return mCachedColors;
			    }
		
		    public boolean isShowAlphaBar() {
			        return mIsShowAlphaBar;
			    }
		
		    private void refreshLayoutParams() {
			        setLayoutParams(getLayoutParams());
			    }
		
		public void setVertical(boolean vertical) {
			mIsVertical = vertical;
			refreshLayoutParams();
			invalidate();
		}
		
		    public boolean isVertical() {
			        return mIsVertical;
			    }
		
		    public void setShowAlphaBar(boolean show) {
			        mIsShowAlphaBar = show;
			        refreshLayoutParams();
			        invalidate();
			        if (mOnColorChangeLister != null) {
				            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
				        }
			    }
		
		    /**
     * @param dp
     */
		    public void setBarHeight(float dp) {
			        mBarHeight = dp2px(dp);
			        refreshLayoutParams();
			        invalidate();
			    }
		
		    /**
     * @param px
     */
		    public void setBarHeightPx(int px) {
			        mBarHeight = px;
			        refreshLayoutParams();
			        invalidate();
			    }
		
		    private void setAlphaValue() {
			        mAlpha = 255 - mAlphaBarPosition;
			    }
		    
		    private void setAlphaValue(int value) {
			        mAlpha = value;
			        mAlphaBarPosition = 255 - mAlpha;
			       // invalidate();
			    }
		
		    public void setAlphaBarPosition(int position) {
			        setPosition(mColorBarPosition,position);
			    }
		
		    public int getMaxValue() {
			        return mMaxPosition;
			    }
		
		    public void setMaxPosition(int value) {
			        this.mMaxPosition = value;
			        invalidate();
			        cacheColors();
			    }
		
		    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
		    public void setBarMargin(float mBarMargin) {
			        this.mBarMargin = dp2px(mBarMargin);
			        refreshLayoutParams();
			        invalidate();
			    }
		
		    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
		    public void setBarMarginPx(int mBarMargin) {
			        this.mBarMargin = mBarMargin;
			        refreshLayoutParams();
			        invalidate();
			    }
		
		
		    /**
     * Set the value of color bar, if out of bounds , it will be 0 or maxValue;
     *
     * @param value
     */
		    public void setColorBarPosition(int value) {
			        setPosition(value,mAlphaBarPosition);
			    }
		
		    public void setPosition(int colorBarPosition,int alphaBarPosition) {
			        this.mColorBarPosition = colorBarPosition;
			        mColorBarPosition = mColorBarPosition > mMaxPosition ? mMaxPosition : mColorBarPosition;
			        mColorBarPosition = mColorBarPosition < 0 ? 0 : mColorBarPosition;
			        this.mAlphaBarPosition = alphaBarPosition;
			        setAlphaValue();
			        invalidate();
			        if (mOnColorChangeLister != null) {
				            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
				        }
			    }
		    public void setOnInitDoneListener(OnInitDoneListener listener) {
			        this.mOnInitDoneListener = listener;
			    }
		
		    /**
     * Set color,the mCachedColors must contains the specified color, if not ,invoke setColorBarPosition(0);
     *
     * @param color
     */
		    public void setColor(int color) {
			        int withoutAlphaColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
			
			        if (mInit) {
				            int value = mCachedColors.indexOf(withoutAlphaColor);
				            if (mIsShowAlphaBar) {
					                setAlphaValue(Color.alpha(color));
					            }
				            setColorBarPosition(value);
				        } else {
				            mColorsToInvoke = color;
				        }
			
			    }
		
		    /**
     * set thumb's height by dpi
     *
     * @param dp
     */
		    public void setThumbHeight(float dp) {
			        this.mThumbHeight = dp2px(dp);
			        mThumbRadius = mThumbHeight / 2;
			        refreshLayoutParams();
			        invalidate();
			    }
		
		    /**
     * set thumb's height by pixels
     *
     * @param px
     */
		    public void setThumbHeightPx(int px) {
			        this.mThumbHeight = px;
			        mThumbRadius = mThumbHeight / 2;
			        refreshLayoutParams();
			        invalidate();
			    }
		
		    public int getBarHeight() {
			        return mBarHeight;
			    }
		
		    public int getThumbHeight() {
			        return mThumbHeight;
			    }
		
		    public int getBarMargin() {
			        return mBarMargin;
			    }
		
		    public float getColorBarValue() {
			        return mColorBarPosition;
			    }
		
		    public interface OnInitDoneListener {
			        void done();
			    }
		
		    public int getColorBarPosition() {
			        return mColorBarPosition;
			    }
		
		    public int getDisabledColor() {
			        return mDisabledColor;
			    }
		
		    public void setDisabledColor(int disabledColor) {
			        this.mDisabledColor = disabledColor;
			        mDisabledPaint.setColor(disabledColor);
			    }
		
		    public boolean isShowThumb() {
			        return mShowThumb;
			    }
		
		    public void setShowThumb(boolean showThumb) {
			        this.mShowThumb = showThumb;
			        invalidate();
			    }
		
		    public int getBarRadius() {
			        return mBarRadius;
			    }
		
		    /**
     * Set bar radius with px unit
     *
     * @param barRadiusInPx
     */
		    public void setBarRadius(int barRadiusInPx) {
			        this.mBarRadius = barRadiusInPx;
			        invalidate();
			    }
		
		    public boolean isIsShowColorBar() {
			        return mIsShowColorBar;
			    }
		
		    public void setShowColorBar(boolean isShowColorBar) {
			        this.mIsShowColorBar = isShowColorBar;
			        refreshLayoutParams();
			        invalidate();
			    }
	}
	/**
 * Created by Jack on 2016/12/5.
 * Email:rtugeek@gmail.com
 */
	
	public static class Logger {
		    private static boolean debug = false;
		    private static final String TAG ="ColorSeekBarLib";
		    public static void i(String s){
			        if(debug) Log.i(TAG,s);
			    }
		    public static void spec(int spec){
			        if(debug){
				            switch (spec){
					                case View.MeasureSpec.AT_MOST:
					                    Log.i(TAG,"AT_MOST");
					                    break;
					                case View.MeasureSpec.EXACTLY:
					                    Log.i(TAG,"EXACTLY");
					                    break;
					                case View.MeasureSpec.UNSPECIFIED:
					                    Log.i(TAG,"UNSPECIFIED");
					                    break;
					                default:
					                    Log.i(TAG,String.valueOf(spec));
					                    break;
					            }
				        }
			    }
	}
	{
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}
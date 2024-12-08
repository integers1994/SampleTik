package com.photex.tiktok.cgcrop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.photex.tiktok.R;


//import android.util.Log;

@SuppressWarnings("unused")
public class CropView extends ImageView {

	public final RectF displayBounds = new RectF();
	final Matrix photoMatrix = new Matrix();
	final Matrix displayMatrix = new Matrix();
	protected float totalScale = 1f;

	protected RectF photoBounds;
	private int mOriginalHeight = 0;
	private int mOriginalWidth = 0;
	public int mRectPadding;
	public int mRectPaddingBtmExtra;
	public float mViewW;
	public float mViewH;
	public int operationState = 0;
	public final static int NORMAL_STATE = 1001;
	public final static int MOVE_STATE = 1002;
	public final static int ROTATE_STATE = 1003;
	protected static float MAX_SCALE = 6f;
	protected static float MIN_SCALE = 1f;
	protected boolean showSize = false;
	protected RectF mPhotoBoundRect = new RectF();
	protected RectF rectF;
	protected RotateView.RotateRect rotateR;

	protected RotateView.RotateRect imageRect;
	protected Bitmap mBitmap;

	float newViewH = 0f;
	float newViewW = 0f;
	public float originalRatio = 0f;
	private RotateView.BounderChecker bounderChecker;

	public void setRectPadding(int padding) {
		mRectPadding = padding;
	}

	public void setPhotoBounds(RectF photoBounds) {
		this.photoBounds = photoBounds;
	}

	public void setOriginalPhotoBounds(int width, int height) {
		mOriginalWidth = width;
		mOriginalHeight = height;
	}

	Matrix turningMatrix = new Matrix();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		displayBounds.setEmpty();
		photoMatrix.reset();
		displayMatrix.reset();
		initCrop(w, h);
		sizeChanged();

	}

	public void initCrop(float w, float h) {
		Matrix matrix = new Matrix();
		RectF rectF = new RectF(mRectPadding, mRectPadding, w - mRectPadding, h
				- mRectPadding);
		if (matrix.setRectToRect(photoBounds, rectF, Matrix.ScaleToFit.CENTER)) {
			matrix.mapRect(displayBounds, photoBounds);
			displayMatrix.setRectToRect(photoBounds, displayBounds,
					Matrix.ScaleToFit.CENTER);
		}
		turningMatrix.set(matrix);
		matrix.invert(photoMatrix);
	}

	public interface OnCropChangeListener {

		void onCropChanged(RectF cropBounds, boolean fromUser);
	}

	protected static final int MOVE_LEFT = 1;
	protected static final int MOVE_TOP = 2;
	protected static final int MOVE_RIGHT = 4;
	protected static final int MOVE_BOTTOM = 8;
	protected static final int MOVE_BLOCK = 16;
	protected static final int MOVE_SCALE = 0;
	protected static final float MIN_CROP_WIDTH_HEIGHT = 70f;
	protected static final int TOUCH_TOLERANCE = 35;
	private static final int SHADOW_ALPHA = 160;

	private static final float EPSILON = 0.0001f;
	private static final float INDICATOR_WIDTH = 24;
	private final Paint borderPaint;
	private final Paint textPaint;
	private final Paint dashPaint;
	private final Drawable cropIndicator;

	private Drawable mDragPoint;
	private Drawable mDragBorderV;
	private Drawable mDragBorderH;

	private final int indicatorSize;
	private final int bolderIndicatorHeight;
	private final int bolderIndicatorWidth;
	public final RectF cropBounds = new RectF(0, 0, 1, 1);

	private Handler mH = new Handler();
	private boolean mShowThreadRunning = false;
	private boolean mHideThreadRunning = false;
	private int mAlpha = 255;
	private int mShadow = 1;

	private float lastX;
	private float lastY;
	boolean mSetCropBounds = false;
	public int movingEdges;
	private OnCropChangeListener listener;

	public float mRatio;
	public ScaleGestureDetector mScaleGestureDetector;
	public GestureDetector mGestureDector;
	public OperateListenner operateListenner;
	GestureDetector.OnGestureListener mSingleTapListener = new GestureDetector.OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent paramMotionEvent) {

			detectMovingEdges(paramMotionEvent.getX(), paramMotionEvent.getY());
			lastX = paramMotionEvent.getX();
			lastY = paramMotionEvent.getY();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent paramMotionEvent) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent paramMotionEvent) {

			return false;
		}

		@Override
		public boolean onScroll(MotionEvent paramMotionEvent1,
				MotionEvent paramMotionEvent2, float paramFloat1,
				float paramFloat2) {
			if (movingEdges != 0) {
				moveEdges(-paramFloat1, -paramFloat2);
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent paramMotionEvent) {

		}

		@Override
		public boolean onFling(MotionEvent paramMotionEvent1,
				MotionEvent paramMotionEvent2, float paramFloat1,
				float paramFloat2) {

			return false;
		}

	};
	private int lastSpanX = -1;
	private int lastSpanY = -1;
	private int lastSpan = -1;
	ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			return true;
		}

		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		public void onScaleEnd(ScaleGestureDetector detector) {
		}
	};

	public float getPhotoWidth() {
		return photoBounds.width();
	}

	public float getPhotoHeight() {
		return photoBounds.height();
	}

	protected float getOriginalWidth() {
		return mOriginalWidth == 0 ? photoBounds.width() : mOriginalWidth;
	}

	protected float getOriginalHeight() {
		return mOriginalHeight == 0 ? photoBounds.height() : mOriginalHeight;
	}

	protected void mapPhotoPoint(float x, float y, PointF dst) {
		if (photoBounds.isEmpty()) {
			dst.set(0, 0);
		} else {
			float[] point = new float[] { x, y };
			photoMatrix.mapPoints(point);
			dst.set(point[0] / photoBounds.width(),
					point[1] / photoBounds.height());
		}
	}

	protected void mapPhotoPoint2(float x, float y, PointF dst) {
		if (photoBounds.isEmpty()) {
			dst.set(0, 0);
		} else {
			float[] point = new float[] { x, y };
			photoMatrix.mapPoints(point);
			dst.set(point[0], point[1]);
		}
	}

	protected void mapDisplayPoint(float x, float y, PointF dst) {
		if (displayBounds.isEmpty()) {
			dst.set(0, 0);
		} else {
			float[] point = new float[] { x, y };
			displayMatrix.mapPoints(point);
			dst.set(point[0], point[1]);
		}
	}

	protected void mapPhotoRect(RectF src, RectF dst) {
		if (photoBounds.isEmpty()) {
			dst.setEmpty();
		} else {
			photoMatrix.mapRect(dst, src);
			dst.set(dst.left / photoBounds.width(),
					dst.top / photoBounds.height(),
					dst.right / photoBounds.width(),
					dst.bottom / photoBounds.height());
		}
	}

	protected void mapPhotoRect2(RectF src, RectF dst) {
		if (photoBounds.isEmpty()) {
			dst.setEmpty();
		} else {
			photoMatrix.mapRect(dst, src);
		}
	}

	public RectF getDisplayBounds() {
		return displayBounds;
	}

	@SuppressWarnings("deprecation")
	public CropView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScaleGestureDetector = new ScaleGestureDetector(context,
				mSimpleOnScaleGestureListener);
		mGestureDector = new GestureDetector(context, mSingleTapListener, null,
				true);
		Resources resources = context.getResources();
		cropIndicator = resources.getDrawable(R.drawable.camera_crop_holo);
		mDragPoint = resources.getDrawable(R.drawable.ic_drag_point_normal);
		mDragBorderV = resources.getDrawable(R.drawable.drag_border);
		indicatorSize = CropsUtils.dip2px(context, INDICATOR_WIDTH);

		Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
				R.drawable.drag_border);

		bolderIndicatorHeight = bitmapOrg.getHeight();
		bolderIndicatorWidth = bitmapOrg.getWidth();
		Matrix m = new Matrix();
		m.setRotate(90);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
				bitmapOrg.getWidth(), bitmapOrg.getHeight(), m, true);
		mDragBorderH = new BitmapDrawable(resizedBitmap);

		int borderColor = resources.getColor(R.color.border_color);

		int dashColor = resources.getColor(R.color.dash_color);

		int textColor = resources.getColor(R.color.white);

		borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(borderColor);
		borderPaint.setStrokeWidth(2f);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
		textPaint.setShadowLayer(1, 1, 1, 0x99000000);
		textPaint.setColor(textColor);

		dashPaint = new Paint();
		dashPaint.setAntiAlias(true);
		dashPaint.setStyle(Paint.Style.STROKE);
		dashPaint.setColor(dashColor);
		PathEffect effects = new DashPathEffect(new float[] { 3, 2, 3, 2 }, 1);
		// dashPaint.setPathEffect(effects);

		operationState = NORMAL_STATE;
	}

	public void setOnCropChangeListener(OnCropChangeListener listener) {
		this.listener = listener;
	}

	private void refreshByCropChange(boolean fromUser) {
		if (listener != null) {
			listener.onCropChanged(new RectF(cropBounds), fromUser);
		}
		invalidate();
	}

	/**
	 * Sets cropped bounds; modifies the bounds if it's smaller than the allowed
	 * dimensions.
	 */
	public void setCropBounds(RectF bounds) {
		if (bounds == null)
			return;

		mSetCropBounds = true;
		cropBounds.set(bounds);
		refreshByCropChange(false);
	}

	public void setRectRatio(float ratio) {
		mRatio = ratio;

		sizeChanged();
	}

	public float getRealRatio() {
		if (mRatio == 0f) {
			return newViewH / newViewW;
		}
		return mRatio;
	}

	public RectF getCropBoundsDisplayed() {
		float width = displayBounds.width();
		float height = displayBounds.height();
		RectF cropped = new RectF(cropBounds.left * width, cropBounds.top
				* height, cropBounds.right * width, cropBounds.bottom * height);

		cropped.offset(displayBounds.left, displayBounds.top);
		return cropped;
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		if (mHideThreadRunning)
			return;
		mShowThread.reset();
		mH.post(mShowThread);
	}

	public void sizeChanged() {
		RectF bounds = null;
		if (mSetCropBounds)
			return;
		if (mRatio == 0) {
			bounds = new RectF(0f, 0f, 1f, 1f);
		} else {
			if (displayBounds.height() > displayBounds.width() * mRatio) {
				bounds = new RectF(0, 0.5f - 0.5f * mRatio
						* displayBounds.width() / displayBounds.height(), 1f,
						0.5f + 0.5f * mRatio * displayBounds.width()
								/ displayBounds.height());

			} else {
				bounds = new RectF(
						0.5f - 0.5f / (mRatio * displayBounds.width() / displayBounds
								.height()), 0f, 0.5f + 0.5f / (mRatio
								* displayBounds.width() / displayBounds
								.height()), 1f);
			}
		}
		cropBounds.set(bounds);
		refreshByCropChange(true);
	}

	private void detectMovingEdges(float x, float y) {
		RectF cropped = getCropBoundsDisplayed();
		movingEdges = 0;

		operateListenner.hasOprated();
		float left = Math.abs(x - cropped.left);
		float right = Math.abs(x - cropped.right);
		float top = Math.abs(y - cropped.top);
		float bottom = Math.abs(y - cropped.bottom);

		if (top <= TOUCH_TOLERANCE && left <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_TOP;
			movingEdges |= MOVE_LEFT;
		} else if (top <= TOUCH_TOLERANCE && right <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_TOP;
			movingEdges |= MOVE_RIGHT;
		} else if (bottom <= TOUCH_TOLERANCE && left <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_BOTTOM;
			movingEdges |= MOVE_LEFT;
		} else if (bottom <= TOUCH_TOLERANCE && right <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_BOTTOM;
			movingEdges |= MOVE_RIGHT;
		}

		if (top <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_TOP;
		} else if (right <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_RIGHT;
		} else if (left <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_LEFT;
		} else if (bottom <= TOUCH_TOLERANCE) {
			movingEdges |= MOVE_BOTTOM;
		}
		// }

		if (cropped.contains(x, y) && (movingEdges == 0)) {
			movingEdges = MOVE_BLOCK;
		}
		invalidate();
	}

	float maxValue(float a) {
		return a;
	}

	private void moveEdgesMuiltTouch(float deltaX, float deltaY) {
		RectF cropped = getCropBoundsDisplayed();
		float minWidth = MIN_CROP_WIDTH_HEIGHT * displayBounds.width()
				/ getPhotoWidth();
		float minHeight = MIN_CROP_WIDTH_HEIGHT * displayBounds.height()
				/ getPhotoHeight();
		if (mRatio == 0) {
			if ((movingEdges & MOVE_LEFT) != 0) {
				cropped.left = Math.min(cropped.left + deltaX, cropped.right
						- maxValue(minWidth));
			}
			if ((movingEdges & MOVE_TOP) != 0) {
				cropped.top = Math.min(cropped.top + deltaY, cropped.bottom
						- maxValue(minHeight));
			}
			if ((movingEdges & MOVE_RIGHT) != 0) {
				cropped.right = Math.max(cropped.right - deltaX, cropped.left
						+ maxValue(minWidth));
			}
			if ((movingEdges & MOVE_BOTTOM) != 0) {
				cropped.bottom = Math.max(cropped.bottom - deltaY, cropped.top
						+ maxValue(minHeight));
			}
		} else {
			float base = Math.abs(deltaX) > Math.abs(deltaY) ? Math.abs(deltaX)
					: Math.abs(deltaY);
			float symbol = deltaX == 0 ? 0 : deltaX / Math.abs(deltaX);
			float changeLeft = cropped.left + base * symbol;
			float changeTop = cropped.bottom - (cropped.right - changeLeft)
					* mRatio;
			if (changeLeft > displayBounds.left
					&& changeTop > displayBounds.top) {
				cropped.left = Math.min(changeLeft, cropped.right
						- maxValue(minWidth));
				cropped.top = cropped.bottom - (cropped.right - cropped.left)
						* mRatio;
			}

			float changeRight = cropped.right - base * symbol;
			float changeBottom = cropped.top + (changeRight - cropped.left)
					* mRatio;
			if (changeRight < displayBounds.right
					&& changeBottom < displayBounds.bottom) {
				cropped.right = Math.max(cropped.right - base * symbol,
						cropped.left + maxValue(minWidth));
				cropped.bottom = cropped.top + (cropped.right - cropped.left)
						* mRatio;
			}

			float ratio = cropped.height() / cropped.width();
			if (Math.abs(ratio - mRatio) > 0.1)
				return;
		}
		cropped.intersect(displayBounds);

		mapPhotoRect(cropped, cropBounds);

		refreshByCropChange(true);
	}

	public float checkDelta[] = new float[2];

	private void moveEdges(float deltaX, float deltaY) {
		RectF cropped = getCropBoundsDisplayed();
		float tmpLeft = cropped.left;
		float tmpBottom = cropped.bottom;
		float tmpRight = cropped.right;
		float tmpTop = cropped.top;

		if (movingEdges == MOVE_BLOCK) {
			deltaX = (deltaX > 0) ? Math.min(displayBounds.right
					- cropped.right, deltaX) : Math.max(displayBounds.left
					- cropped.left, deltaX);
			deltaY = (deltaY > 0) ? Math.min(displayBounds.bottom
					- cropped.bottom, deltaY) : Math.max(displayBounds.top
					- cropped.top, deltaY);
			cropped.offset(deltaX, deltaY);
			// }
		} else {
			float minWidth = MIN_CROP_WIDTH_HEIGHT * getImageScale();
			float minHeight = MIN_CROP_WIDTH_HEIGHT * getImageScale();
			if (mRatio == 0) {
				boolean contain = bounderChecker.containInBounder(checkDelta);

				if ((movingEdges & MOVE_LEFT) != 0) {
					cropped.left = Math.min(cropped.left + deltaX,
							cropped.right - maxValue(minWidth));
				}
				if ((movingEdges & MOVE_TOP) != 0) {
					cropped.top = Math.min(cropped.top + deltaY, cropped.bottom
							- maxValue(minHeight));
				}
				if ((movingEdges & MOVE_RIGHT) != 0) {

					cropped.right = Math.max(cropped.right + deltaX,
							cropped.left + maxValue(minWidth));
				}
				if ((movingEdges & MOVE_BOTTOM) != 0) {
					cropped.bottom = Math.max(cropped.bottom + deltaY,
							cropped.top + maxValue(minHeight));
				}

			} else {
				float base = Math.abs(deltaX) > Math.abs(deltaY) ? Math
						.abs(deltaX) : Math.abs(deltaY);
				float symbol = deltaX == 0 ? 0 : deltaX / Math.abs(deltaX);

				if ((movingEdges & MOVE_LEFT) != 0
						&& (movingEdges & MOVE_TOP) != 0) {
					float changeLeft = cropped.left + base * symbol;
					float changeTop = cropped.bottom
							- (cropped.right - changeLeft) * mRatio;

					cropped.left = Math.min(changeLeft, cropped.right
							- maxValue(minWidth));
					cropped.top = cropped.bottom
							- (cropped.right - cropped.left) * mRatio;
					if (cropped.bottom - cropped.top < minHeight) {
						cropped.top = cropped.bottom - maxValue(minHeight);
						cropped.left = cropped.right
								- (cropped.bottom - cropped.top) / mRatio;
					}
					// }
				} else if ((movingEdges & MOVE_TOP) != 0
						&& (movingEdges & MOVE_RIGHT) != 0) {
					float changeRight = cropped.right + base * symbol;
					float changeTop = cropped.bottom
							- (changeRight - cropped.left) * mRatio;
					cropped.right = Math.max(cropped.right + base * symbol,
							cropped.left + maxValue(minWidth));
					cropped.top = cropped.bottom
							- (cropped.right - cropped.left) * mRatio;
					if (cropped.bottom - cropped.top < minHeight) {
						cropped.top = cropped.bottom - maxValue(minHeight);
						cropped.left = cropped.right
								- (cropped.bottom - cropped.top) / mRatio;
					}
					// }
				} else if ((movingEdges & MOVE_BOTTOM) != 0
						&& (movingEdges & MOVE_LEFT) != 0) {
					float changeLeft = cropped.left + base * symbol;
					float changeBottom = cropped.top
							+ (cropped.right - changeLeft) * mRatio;
					cropped.left = Math.min(cropped.left + base * symbol,
							cropped.right - maxValue(minWidth));
					cropped.bottom = cropped.top
							+ (cropped.right - cropped.left) * mRatio;
					if (cropped.bottom - cropped.top < minHeight) {
						cropped.bottom = cropped.top + maxValue(minHeight);
						cropped.left = cropped.right
								- (cropped.bottom - cropped.top) / mRatio;
					}
					// }

				} else if ((movingEdges & MOVE_BOTTOM) != 0
						&& (movingEdges & MOVE_RIGHT) != 0) {
					float changeRight = cropped.right + base * symbol;
					float changeBottom = cropped.top
							+ (changeRight - cropped.left) * mRatio;
					cropped.right = Math.max(cropped.right + base * symbol,
							cropped.left + maxValue(minWidth));
					cropped.bottom = cropped.top
							+ (cropped.right - cropped.left) * mRatio;
					if (cropped.bottom - cropped.top < minHeight) {
						cropped.bottom = cropped.top + maxValue(minHeight);
						cropped.left = cropped.right
								- (cropped.bottom - cropped.top) / mRatio;
					}
					// }
				}

				float ratio = cropped.height() / cropped.width();
				if (Math.abs(ratio - mRatio) > 0.1)
					return;
			}
			if (bounderChecker.checkCropBounder(cropped) == false) {
				cropped.left = tmpLeft;
				cropped.bottom = tmpBottom;
				cropped.right = tmpRight;
				cropped.top = tmpTop;
			}

			if (cropped.left < 0) {
			}
			if (cropped.top < 0) {
			}
			if (cropped.right > getWidth()) {
				cropped.right = getWidth();
			}
			if (cropped.bottom > getHeight()) {
				cropped.bottom = getHeight();
			}

		}

		mapPhotoRect(cropped, cropBounds);
		refreshByCropChange(true);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (isEnabled()) {
			mScaleGestureDetector.onTouchEvent(event);
			mGestureDector.onTouchEvent(event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				movingEdges = 0;
				lastSpanX = -1;
				lastSpanY = -1;
				lastSpan = -1;
				invalidate();
				break;
			}
		}
		return true;
	}

	private void drawIndicator(Canvas canvas, Drawable indicator,
			float centerX, float centerY) {
		int left = (int) centerX - indicatorSize / 2;
		int top = (int) centerY - indicatorSize / 2;
		indicator.setBounds(left, top, left + indicatorSize, top
				+ indicatorSize);
		indicator.setAlpha(mAlpha);
		indicator.draw(canvas);
	}

	private void drawDashLine(Canvas canvas, float startX, float startY,
			float endX, float endY) {
		dashPaint.setAlpha(mAlpha);
		canvas.drawLine(startX, startY, endX, endY, dashPaint);
	}

	private void drawShadow(Canvas canvas, float left, float top, float right,
			float bottom) {
		canvas.save();
		canvas.clipRect(200, 200, 400, 400, Region.Op.XOR);

		canvas.drawARGB(SHADOW_ALPHA * mAlpha / 255, 0, 0, 0);
		canvas.restore();
	}

	private void drawBorderIndicator(Canvas canvas, Drawable drawable,
			float centerX, float centerY, boolean bVertical) {
		int left = 0;
		int top = 0;
		if (bVertical) {
			left = (int) centerX - bolderIndicatorWidth / 2;
			top = (int) centerY - bolderIndicatorHeight / 2;
			drawable.setBounds(left, top, left + bolderIndicatorWidth, top
					+ bolderIndicatorHeight);
		} else {
			left = (int) centerX - bolderIndicatorHeight / 2;
			top = (int) centerY - bolderIndicatorWidth / 2;
			drawable.setBounds(left, top, left + bolderIndicatorHeight, top
					+ bolderIndicatorWidth);
		}
		drawable.setAlpha(mAlpha);
		drawable.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		RectF cropped = getCropBoundsDisplayed();

		canvas.save();
		canvas.clipRect(cropped.left, cropped.top, cropped.right,
				cropped.bottom, Region.Op.XOR);
		canvas.drawARGB(SHADOW_ALPHA * mAlpha / 255, 24, 24, 27);
		canvas.restore();

		canvas.drawRect(cropped, borderPaint);

		int color = (int) (153f * mAlpha / 255f);
		textPaint.setShadowLayer(1, 1, 1, color << 24);
		float textWidth = textPaint.measureText("" + (int) cropped.width()
				+ "×" + (int) cropped.height());
		float textHeight = textPaint.getTextSize();
		String size = getCropImageSize();
		if (showSize == true) {
			canvas.drawText(size, cropped.centerX() - textWidth / 2,
					cropped.centerY() + textHeight / 2, textPaint);
		}
		// if (notMoving) {
		drawIndicator(canvas, mDragPoint, cropped.left, cropped.top);
		// }
		// if (notMoving) {
		drawIndicator(canvas, mDragPoint, cropped.left, cropped.bottom);
		// }
		// if (notMoving) {
		drawIndicator(canvas, mDragPoint, cropped.right, cropped.top);
		// }
		// if (notMoving) {
		drawIndicator(canvas, mDragPoint, cropped.right, cropped.bottom);
		// }

		int lineCount = 0;
		switch (operationState) {
		case NORMAL_STATE:
			lineCount = 2;
			break;
		case MOVE_STATE:
			lineCount = 2;
			break;
		case ROTATE_STATE:
			lineCount = 8;
			break;
		}
		for (int i = 0; i < lineCount; i++) {
			drawDashLine(canvas, cropped.left + cropped.width()
					/ (lineCount + 1) * (i + 1), cropped.top, cropped.left
					+ cropped.width() / (lineCount + 1) * (i + 1),
					cropped.bottom);
		}

		for (int i = 0; i < lineCount; i++) {
			drawDashLine(canvas, cropped.left, cropped.top + cropped.height()
					/ (lineCount + 1) * (i + 1), cropped.right, cropped.top
					+ cropped.height() / (lineCount + 1) * (i + 1));
		}

		if (mRatio == 0) {
			drawBorderIndicator(canvas, mDragBorderH,
					cropped.left + cropped.width() / 2, cropped.top, false);
			drawBorderIndicator(canvas, mDragBorderH,
					cropped.left + cropped.width() / 2, cropped.bottom, false);
			drawBorderIndicator(canvas, mDragBorderV, cropped.left, cropped.top
					+ cropped.height() / 2, true);
			drawBorderIndicator(canvas, mDragBorderV, cropped.right,
					cropped.top + cropped.height() / 2, true);
		}

		drawMinContainCoppeBoudn(canvas);
	}

	public float oldScale = 1f;
	public float mInitScale;
	public boolean isTruning = false;

	public float getImageScale() {
		float[] value = new float[9];
		Matrix matrix = getImageMatrix();
		matrix.getValues(value);
		float scale;
		if (isTruning == false) {
			scale = Math.abs(value[Matrix.MSCALE_X]);
		} else {
			scale = Math.abs(value[Matrix.MSKEW_X]);
		}

		return scale;
	}

	public float getCropScale() {
		return imageRect.getHeight() / getPhotoHeight();
	}

	protected String getCropImageSize() {
		String sizeStr = "";

		float scaleW = imageRect.getWidth() / getPhotoWidth();
		float scaleH = imageRect.getHeight() / getPhotoHeight();

		int width = Math.round(cropped.width() / scaleW);
		int height = Math.round(cropped.height() / scaleH);
		sizeStr = Math.abs(width) + "×" + Math.abs(height);
		return sizeStr;
	}

	private void drawMinContainCoppeBoudn(Canvas canvas) {

		RectF rectF = new RectF();
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = cropBounds.width();
		rectF.bottom = cropBounds.height();
		RectF rectFDest = new RectF();
		displayMatrix.mapRect(rectFDest, rectF);

		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#33ff00ff"));
		canvas.drawRect(rectFDest, paint);
	}

	private AnimationShowThread mShowThread = new AnimationShowThread();
	private AnimationHideThread mHideThread = new AnimationHideThread();

	class AnimationShowThread implements Runnable {
		public void reset() {
			mAlpha = 0;
			mShadow = 0;
			mShowThreadRunning = true;
		}

		public void run() {
			if (!mShowThreadRunning)
				return;

			if (mAlpha == 0) {
				CropView.this.postInvalidateDelayed(500);
			}

			if (mAlpha + 5 <= 255) {
				CropView.this.postInvalidate();
			} else {
				mAlpha = 255;
				mShadow = 1;
				mShowThreadRunning = false;
				CropView.this.postInvalidate();
				return;
			}
			mAlpha += 5;
			scheduleAdvance(this);
		}
	}

	class AnimationHideThread implements Runnable {
		public void reset() {
			mAlpha = 255;
			mHideThreadRunning = true;
			mShadow = 0;
		}

		public void run() {
			if (!mHideThreadRunning)
				return;

			if (mAlpha - 30 > 0) {
				CropView.this.postInvalidate();
			} else {
				mHideThreadRunning = false;
				if ((ViewGroup) (CropView.this.getParent()) != null)
					((ViewGroup) (CropView.this.getParent()))
							.removeView(CropView.this);
				return;
			}
			mAlpha -= 30;
			scheduleAdvance(this);
		}
	}

	private void scheduleAdvance(Runnable thread) {
		mH.postDelayed(thread, 1);
	}

	public void removeViewWithAnimation(boolean fast) {
		if (mShowThreadRunning) {
			mShowThreadRunning = false;
		}

		mHideThread.reset();
		if (fast) {
			((ViewGroup) (CropView.this.getParent())).removeView(CropView.this);
		} else
			mH.postDelayed(mHideThread, 400);
	}

	public RectF cropped;

	public void updateCropBound() {
		cropped = getCropBoundsDisplayed();
		float cropWidth = cropped.width();
		float cropHeight = cropped.height();
		if (cropHeight == 0 || cropWidth == 0) {
			mViewW = getWidth();
			mViewH = getHeight();
		}
		float centerX = cropped.centerX();
		float centerY = cropped.centerY();

		float view_ratio = (float) mViewW / (float) mViewH;

		float im_ratio = cropWidth / cropHeight;

		float newCropWidth = 0;
		float newCropHeight = 0;
		float scale = 0;

		if (im_ratio >= view_ratio) {

			scale = (mViewW - mRectPadding * 2) / cropWidth;
			newCropHeight = cropHeight * scale;
			cropped.left = mRectPadding;
			cropped.right = mViewW - mRectPadding;
			cropped.top = (mViewH - newCropHeight) / 2;
			cropped.bottom = cropped.top + newCropHeight;
		} else if (im_ratio < view_ratio) {

			newCropHeight = (mViewH - 2 * mRectPadding);
			scale = newCropHeight / cropHeight;
			newCropWidth = cropWidth * scale;
			cropped.left = (mViewW - newCropWidth) / 2;
			cropped.right = cropped.left + newCropWidth;
			cropped.top = mRectPadding;
			cropped.bottom = mViewH - mRectPadding;
		}

		Matrix matrix = getImageMatrix();
		float transX = centerX - cropped.centerX();
		float transY = centerY - cropped.centerY();

		matrix.postScale(scale, scale, centerX, centerY);
		totalScale = totalScale * scale;
		matrix.postTranslate(-transX, -transY);
		mapPhotoRect(cropped, cropBounds);
		refreshByCropChange(true);
	}

	public void resetCropBound() {
		cropBounds.set(0, 0, 1, 1);
	}

	protected float canvasRotate;

	public void addCanvasRotate(float rotate) {
		canvasRotate += rotate;
		canvasRotate = canvasRotate % 360;
	}

	public void setCanvasRotate(float rotate) {
		canvasRotate = rotate;
	}

	public void setBounderChecker(RotateView.BounderChecker bounderChecker) {
		this.bounderChecker = bounderChecker;
	}

	public void resetPhotBounds() {
		cropBounds.set(0, 0, 1, 1);
	}

	public interface OperateListenner {
		public void hasOprated();
	}

	public void setOperatedListenner(OperateListenner ol) {
		this.operateListenner = ol;
	}
}

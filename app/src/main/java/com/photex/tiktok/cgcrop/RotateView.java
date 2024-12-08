package com.photex.tiktok.cgcrop;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import android.util.Log;

@SuppressWarnings("unused")
public class RotateView extends CropView {

	private Matrix mMatrix = new Matrix();
	private static final int DEALY_CROP_TIME = 1000;

	private float initDrawbleWidth;
	private float initDrawbleHeigt;
	public float originWidth;
	public float originHeight;

	CropListenner cropListenner;
	private boolean isRotateing = false;
	private static boolean debug = false;

	@SuppressLint("ClickableViewAccessibility")
	public RotateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TouchListener touchListener = new TouchListener();
		touchListener.setImageView(this);
		setOnTouchListener(touchListener);
		init();
	}

	float touchScale;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void init() {
		postDelayed(new Runnable() {
			@Override
			public void run() {
				setScaleType(ScaleType.MATRIX);
				mMatrix.set(getImageMatrix());
				centernPoint = getCropCenterPoint();
				mMatrix.postScale(oldScale, oldScale, centernPoint.x,
						centernPoint.y);
				mInitScale = oldScale;
				MIN_SCALE = mInitScale * MIN_SCALE;
				touchScale = oldScale;
				MIN_SCALE = touchScale;
				totalScale = mInitScale;
				mScaleDrawableWidth = mScaleDrawableWidth * oldScale;
				mScaleDrawableHeight = mScaleDrawableHeight * oldScale;
				initDrawbleWidth = mScaleDrawableWidth;
				initDrawbleHeigt = mScaleDrawableHeight;
				setImageMatrix(mMatrix);
				getImageRect();
				printMatrix(getImageMatrix());
				MAX_SCALE = Math.min(photoBounds.width(), photoBounds.height())
						* getImageScale() / MIN_CROP_WIDTH_HEIGHT;
			}
		}, 600);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		scale = 1f;
		setBounderChecker(new BounderChecker() {

			@Override
			public boolean containInBounder(float[] checkDelta) {
				return isContain(checkDelta);
			}

			@Override
			public boolean checkCropBounder(RectF cropped) {
				return checkCropBound(cropped);
			}
		});
	}

	public void setOriginal(float width, float height) {
		originWidth = width;
		originHeight = height;
		originalRatio = originWidth / originHeight;
	}

	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		setImageWidthHeight();
		mBitmap = bm.copy(bm.getConfig(), true);
		originWidth = mBitmap.getWidth();
		originHeight = mBitmap.getHeight();
	}

	private float mImageW;
	private float mImageH;
	private float mRotatedImageW;
	private float mRotatedImageH;

	private boolean mCanInit = true;
	private Matrix mScaleMatrix = new Matrix();
	private PointF mPointCenter = new PointF();
	float[] scaleValue = new float[9];

	private void setImageWidthHeight() {
		Drawable d = getDrawable();
		if (d == null) {
			return;
		}
		mImageW = mRotatedImageW = d.getIntrinsicWidth();
		mImageH = mRotatedImageH = d.getIntrinsicHeight();
		mMatrix.setScale(0, 0);
		initImage();
	}

	private RectF mImageRect;
	private Rect mInnerRect;
	private float mParentW;
	private float mParentH;

	float mScaleDrawableHeight;
	float mScaleDrawableWidth;
	boolean isFatRadio;
	float im_ratio;
	float view_ratio;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewW = w;
		mViewH = h;
		mParentW = getWidth();
		mParentH = getHeight();
		view_ratio = (float) mParentW / (float) mParentH;
		im_ratio = mImageW / mImageH;

		if (im_ratio >= view_ratio) {

			isFatRadio = true;
			newViewW = mParentW;
			newViewH = newViewW * mImageH / mImageW;

		} else if (im_ratio < view_ratio) {

			isFatRadio = false;
			newViewH = mParentH;
			newViewW = newViewH * mImageW / mImageH;
		}

		mInitMatrix = new Matrix();
		mInitMatrix.set(displayMatrix);
		setImageMatrix(mInitMatrix);
		initImage();
		mImageRect = new RectF(0f, 0f, mViewW, mViewH);

		mScaleDrawableHeight = newViewH;
		mScaleDrawableWidth = newViewW;
		rectF = new RectF();
		rectF.left = 0;
		rectF.top = 0;
		rectF.right = mImageW;
		rectF.bottom = mImageH;
		calRotate();
	}

	public void checkFatRadio() {

		if (im_ratio >= view_ratio) {

			isFatRadio = true;
		} else if (im_ratio < view_ratio) {

			isFatRadio = false;
		}
	}

	/*

     */
	Matrix mInitMatrix;
	PointF centernPoint;

	private void initImage() {
		if (mViewW <= 0 || mViewH <= 0 || mImageW <= 0 || mImageH <= 0
				|| !mCanInit) {
			return;
		}
		mCanInit = false;
		mMatrix.set(getImageMatrix());
		fixScale();
		mPointCenter.set(mViewW / 2, mViewH / 2);
		mScaleMatrix.set(mMatrix);
		rotateR = new RotateRect(mViewW, mViewH);
		imageRect = new RotateRect(mViewW, mViewH);
		centernPoint = getCropCenterPoint();
		pathImg = new Path();

	}

	private void fixScale() {
		float p[] = new float[9];
		mMatrix.getValues(p);
		oldScale = displayBounds.width() / newViewW;
	}

	private Matrix currentMatrix = new Matrix();

	private RectF tmpCropped;

	private final class TouchListener implements OnTouchListener {

		public void setImageView(ImageView imageView) {
			this.imageView = imageView;
		}

		private ImageView imageView;
		private int mode = 0;
		private static final int MODE_DRAG = 1;
		private static final int MODE_ZOOM = 2;
		private PointF startPoint = new PointF();
		private Matrix matrix = new Matrix();
		private float startDis;
		private PointF midPoint;
		private boolean cancelCropped = false;
		private boolean finishCropped = true;
		private long lastUpTime;
		float timeDiff;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, final MotionEvent event) {
			/**
			 * é€šè¿‡ä¸Žè¿�ç®—ä¿�ç•™æœ€å�Ž
			 * å…«ä½� MotionEvent.ACTION_MASK = 255
			 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN:
				tmpCropped = new RectF();
				tmpCropped.set(cropped);
				cancelCropped = true;
				mode = MODE_DRAG;
				currentMatrix.set(imageView.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				// matrix.postScale(0.5f,0.5f,360f,540f);
				matrix.set(currentMatrix);
				imageView.setImageMatrix(matrix);
				mGestureDector.onTouchEvent(event);
				break;
			case MotionEvent.ACTION_MOVE:

				if (movingEdges == 16 || movingEdges == 0) {

					if (mode == MODE_DRAG && !isRotateing) {
						operateListenner.hasOprated();
						float dx = event.getX() - startPoint.x;
						float dy = event.getY() - startPoint.y;

						matrix.set(currentMatrix);
						matrix.postTranslate(dx, dy);
						imageView.setImageMatrix(matrix);

					} else if (mode == MODE_ZOOM && !isRotateing) {
						calRotate();
						showSize = false;
						float endDis = distance(event);
						if (endDis > 10f) {
							operateListenner.hasOprated();
							scale = endDis / startDis;
							matrix.set(currentMatrix);
							totalScale = touchScale * scale;
							matrix.postScale(scale, scale, midPoint.x,
									midPoint.y);
						}
						float[] value = new float[9];
						matrix.getValues(value);
						mScaleDrawableHeight = initDrawbleHeigt * scale;
						mScaleDrawableWidth = initDrawbleWidth * scale;
						imageView.setImageMatrix(matrix);
					}
					operationState = MOVE_STATE;
				} else if (!isRotateing) {
					if (isEnabled()) {
						calRotate();
						showSize = true;
						mGestureDector.onTouchEvent(event);
						if (cropListenner != null) {
							cropListenner.onCropping();
						}
					}
				}
				isContain(null);
				break;
			case MotionEvent.ACTION_UP:

				cancelCropped = false;

				if ((movingEdges != MOVE_BLOCK && movingEdges != MOVE_SCALE)
						|| finishCropped == false) {
					finishCropped = false;

					if (mode == MODE_DRAG) {
						postDelayed(new Runnable() {
							@Override
							public void run() {
								timeDiff = (System.currentTimeMillis() - lastUpTime) / 1000f;

								if (cancelCropped == false && timeDiff >= 1f) {
									mGestureDector.onTouchEvent(event);
									operationState = NORMAL_STATE;
									updateCropBound();
									invalidate();
									finishCropped = true;
									showSize = false;
									if (cropListenner != null) {
										cropListenner.cropFinished();
									}
								}
							}
						}, DEALY_CROP_TIME);
					} else {
						showSize = false;
					}
				}
				if (movingEdges == MOVE_BLOCK) {
					showSize = false;
				}

				touchScale = totalScale;
				lastUpTime = System.currentTimeMillis();
				calRotate();
				checkImageBound();
				checkBound(mPhotoBoundRect, cropped);
				calRotate();
				getCropImageSize();
				invalidate();
				operationState = NORMAL_STATE;

			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;

				startDis = distance(event);

				if (startDis > 10f) {
					midPoint = mid(event);
					currentMatrix.set(imageView.getImageMatrix());
				}
				imageView.setImageMatrix(matrix);
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			}

			return true;
		}

		public void checkImageBound() {
			float heightScale = Float.MIN_VALUE;
			float widthScale = Float.MIN_VALUE;
			float scale = 1f;
			if (imageRect.getHeight() < rotateR.getHeight()) {
				heightScale = rotateR.getHeight() / imageRect.getHeight();
			}
			if (imageRect.getWidth() < rotateR.getWidth()) {
				widthScale = rotateR.getWidth() / imageRect.getWidth();
			}
			scale = Math.max(heightScale, widthScale);

			if (getCropScale() > MAX_SCALE) {
				scale = MAX_SCALE / getCropScale();
			}
			if (scale != Float.MIN_VALUE) {
				if (scale != Float.MIN_VALUE) {
					if (midPoint != null) {
						mMatrix.set(getImageMatrix());
						mMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
						setImageMatrix(mMatrix);
					}
				}
			}
		}

		@SuppressLint("FloatMath")
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			return (float)Math.sqrt(dx * dx + dy * dy);
		}

		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}

	}

	public float checkBound(RectF rectInner, RectF rectOut) {
		float offsetX = 0f;
		float offsetY = 0f;
		if (cropped == null) {
			return 0;
		}
		mMatrix.mapRect(mPhotoBoundRect, rectF);
		if (!isContain(null)) {
			checkImageBound();
			calRotate();
			invalidate();
		}
		return offsetY;
	}

	public float[] printMatrix(Matrix matrix) {
		float[] value = new float[9];
		matrix.getValues(value);
		float scale = value[Matrix.MSCALE_X];
		float tranX = value[Matrix.MTRANS_X];
		float tranY = value[Matrix.MTRANS_Y];
		return value;
	}

	private boolean checkImageBound() {
		rotate.reset();
		rotate.postRotate(-degree, 0, 0);
		mBackImageRect.angle = -1;
		mBackRotateRect.angle = -1;
		imageRect.rotateDegree = degree;
		rotateR.rotateDegree = degree;
		calRotate(cropped);
		getOut(rotate, imageRect, mBackImageRect);
		getImageOutPath();

		getOut(rotate, rotateR, mBackRotateRect);
		getRotateOutPath();

		if (checkDelta == null) {
			checkDelta = new float[2];
		}

		float diffX = 0;
		float diffY = 0;
		float offsetX = 0f;
		float offsetY = 0f;
		if (mBackRotateRect.p1().x < mBackImageRect.p1().x) {
			diffX += mBackRotateRect.p1().x - mBackImageRect.p1().x;
		}
		if (mBackRotateRect.p1().y < mBackImageRect.p1().y) {
			diffY += mBackRotateRect.p1().y - mBackImageRect.p1().y;
		}

		if (mBackRotateRect.p4().x > mBackImageRect.p4().x) {
			diffX += mBackRotateRect.p4().x - mBackImageRect.p4().x;
		}
		if (mBackRotateRect.p4().y > mBackImageRect.p4().y) {
			diffY += mBackRotateRect.p4().y - mBackImageRect.p4().y;
		}
		double angle = Math.toRadians(degree);
		offsetY = (float) (diffX * Math.sin(angle));
		offsetX = -(float) (diffY * Math.sin(angle));
		if (angle != 0f) {
			diffX = offsetX + (float) (diffX * Math.cos(angle));

		}
		diffY = offsetY + (float) (diffY * Math.cos(angle));
		translateImage(diffX, diffY);
		return true;
	}

	public void translateImage(float x, float y) {
		mMatrix.set(getImageMatrix());
		mMatrix.postTranslate(x, y);
		setImageMatrix(mMatrix);
	}

	Matrix rotate = new Matrix();
	RotateRect mBackImageRect = new RotateRect(mViewW, mViewH);
	RotateRect mBackRotateRect = new RotateRect(mViewW, mViewH);

	public boolean isContain(float[] checkDetal) {
		rotate.reset();
		rotate.postRotate(-degree, 0, 0);
		mBackImageRect.angle = -1;
		mBackRotateRect.angle = -1;
		rotateR.rotateDegree = degree;
		imageRect.rotateDegree = degree;
		getOut(rotate, imageRect, mBackImageRect);
		getImageOutPath();
		getOut(rotate, rotateR, mBackRotateRect);
		getRotateOutPath();
		if ((int) (mBackRotateRect.p1().x) < (int) (mBackImageRect.p1().x)
				|| (int) (mBackRotateRect.p1().y) < (int) (mBackImageRect.p1().y)) {
			return false;
		}
		if ((int) (mBackRotateRect.p2().x) > (int) (mBackImageRect.p2().x)
				|| (int) (mBackRotateRect.p2().y) < (int) (mBackImageRect.p2().y)) {
			return false;
		}
		if ((int) (mBackRotateRect.p3().x) < (int) (mBackImageRect.p3().x)
				|| (int) (mBackRotateRect.p3().y) > (int) (mBackImageRect.p3().y)) {
			return false;
		}
		if ((int) (mBackRotateRect.p4().x) > (int) (mBackImageRect.p4().x)
				|| (int) (mBackRotateRect.p4().y) > (int) (mBackImageRect.p4().y)) {

			return false;
		}
		return true;
	}

	public boolean checkCropBound(RectF cropped) {
		rotate.reset();
		rotate.postRotate(-degree, 0, 0);
		mBackImageRect.angle = -1;
		mBackRotateRect.angle = -1;
		imageRect.rotateDegree = degree;
		rotateR.rotateDegree = degree;
		calRotate(cropped);
		getOut(rotate, imageRect, mBackImageRect);
		getImageOutPath();

		getOut(rotate, rotateR, mBackRotateRect);
		getRotateOutPath();
		if (checkDelta == null) {
			checkDelta = new float[2];
		}

		if ((int) (mBackRotateRect.p1().x) < (int) (mBackImageRect.p1().x)
				|| (int) (mBackRotateRect.p1().y) < (int) (mBackImageRect.p1().y)) {

			float diffX = (mBackRotateRect.p1().x) - (mBackImageRect.p1().x);
			if (diffX < 0) {
				checkDelta[0] = -diffX;
			}
			float diffY = mBackRotateRect.p1().y - mBackImageRect.p1().y;
			if (diffY < 0) {
				checkDelta[1] = -diffY;
			}
			return false;
		}
		if ((int) (mBackRotateRect.p2().x) > (int) (mBackImageRect.p2().x)
				|| (int) (mBackRotateRect.p2().y) < (int) (mBackImageRect.p2().y)) {
			float diffX = (mBackRotateRect.p2().x) - (mBackImageRect.p2().x);
			if (diffX > 0) {
				checkDelta[0] = -diffX;
			}
			float diffY = mBackRotateRect.p2().y - mBackImageRect.p2().y;
			if (diffY < 0) {
				checkDelta[1] = -diffY;
			}
			return false;
		}
		if ((int) (mBackRotateRect.p3().x) < (int) (mBackImageRect.p3().x)
				|| (int) (mBackRotateRect.p3().y) > (int) (mBackImageRect.p3().y)) {
			float diffX = (mBackRotateRect.p3().x) - (mBackImageRect.p3().x);
			if (diffX < 0) {
				checkDelta[0] = -diffX;
			}
			float diffY = mBackRotateRect.p3().y - mBackImageRect.p3().y;
			if (diffY > 0) {
				checkDelta[1] = -diffY;
			}
			return false;
		}
		if ((int) (mBackRotateRect.p4().x) > (int) (mBackImageRect.p4().x)
				|| (int) (mBackRotateRect.p4().y) > (int) (mBackImageRect.p4().y)) {
			float diffX = (mBackRotateRect.p4().x) - (mBackImageRect.p4().x);
			if (diffX > 0) {
				checkDelta[0] = -diffX;
			}
			float diffY = mBackRotateRect.p4().y - mBackImageRect.p4().y;
			if (diffY > 0) {
				checkDelta[1] = -diffY;
			}
			return false;
		}
		return true;
	}

	private void getOut(Matrix matrix, RotateRect rotateR,
			RotateRect recoverRect) {
		float[] ori = new float[] { 0, 0 };
		float[] dst = new float[2];

		ori[0] = rotateR.p1.x;
		ori[1] = rotateR.p1.y;
		matrix.mapPoints(dst, ori);
		recoverRect.p1.set(dst[0], dst[1]);

		ori[0] = rotateR.p2.x;
		ori[1] = rotateR.p2.y;
		matrix.mapPoints(dst, ori);
		recoverRect.p2.set(dst[0], dst[1]);

		ori[0] = rotateR.p3.x;
		ori[1] = rotateR.p3.y;
		matrix.mapPoints(dst, ori);
		recoverRect.p3.set(dst[0], dst[1]);

		ori[0] = rotateR.p4.x;
		ori[1] = rotateR.p4.y;
		matrix.mapPoints(dst, ori);
		recoverRect.p4.set(dst[0], dst[1]);

	}

	RectF imageOutRect = new RectF();

	Path pathImg;

	public void getImageRect() {
		float[] ori = new float[] { 0, 0 };
		float[] dst = new float[2];
		mMatrix.mapPoints(dst, ori);
		imageRect.p1.set(dst[0], dst[1]);

		ori[0] = originWidth;
		mMatrix.mapPoints(dst, ori);
		imageRect.p2.set(dst[0], dst[1]);

		ori[0] = 0;
		ori[1] = originHeight;
		mMatrix.mapPoints(dst, ori);
		imageRect.p3.set(dst[0], dst[1]);

		ori[0] = originWidth;
		ori[1] = originHeight;
		mMatrix.mapPoints(dst, ori);
		imageRect.p4.set(dst[0], dst[1]);

	}

	private void getImageOutPath() {
		if (pathImg == null) {
			pathImg = new Path();
		} else {
			pathImg.reset();
		}
		pathImg.moveTo(imageRect.p1.x, imageRect.p1.y);
		pathImg.lineTo(imageRect.p2.x, imageRect.p2.y);
		pathImg.lineTo(imageRect.p4.x, imageRect.p4.y);
		pathImg.lineTo(imageRect.p3.x, imageRect.p3.y);
		pathImg.close();
	}

	private void getRotateOutPath() {
		if (path1 == null) {
			path1 = new Path();
		} else {
			path1.reset();
		}
		path1.moveTo(rotateR.p1.x, rotateR.p1.y);
		path1.lineTo(rotateR.p2.x, rotateR.p2.y);
		path1.lineTo(rotateR.p4.x, rotateR.p4.y);
		path1.lineTo(rotateR.p3.x, rotateR.p3.y);
		path1.close();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		mMatrix = getImageMatrix();

		float[] ori = new float[] { 0, 0 };
		float[] dst = new float[2];
		mMatrix.mapPoints(dst, ori);

		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#ff00ff"));
		if (debug == true) {

			mMatrix.mapRect(mPhotoBoundRect, rectF);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.parseColor("#55cccccc"));
			canvas.drawRect(mPhotoBoundRect, paint);

			paint.setColor(Color.BLUE);
			canvas.drawCircle(centernPoint.x, centernPoint.y, 10, paint);

			paint.setColor(Color.RED);
			canvas.drawCircle(mPhotoBoundRect.centerX(),
					mPhotoBoundRect.centerY(), 10, paint);

			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);

			if (path1 != null) {
				paint.setColor(Color.RED);
				canvas.drawPath(path1, paint);
				paint.setColor(Color.BLUE);
				canvas.drawPath(pathImg, paint);
			}

			paint.setColor(Color.WHITE);
			canvas.drawRect(imageOutRect, paint);

			if (rotateR.p4 != null && rotateR.p3 != null) {
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				paint.setColor(Color.RED);
				canvas.drawCircle(rotateR.p1().x, rotateR.p1().y, 10f, paint);
				paint.setColor(Color.YELLOW);
				canvas.drawCircle(rotateR.p2().x, rotateR.p2().y, 10f, paint);
				paint.setColor(Color.BLUE);
				canvas.drawCircle(rotateR.p3().x, rotateR.p3().y, 10f, paint);
				paint.setColor(Color.GREEN);
				canvas.drawCircle(rotateR.p4().x, rotateR.p4().y, 10f, paint);
			}

			if (imageRect != null) {
				drawRotateRect(canvas, paint, imageRect);
			}
		}

		canvas.restore();

	}

	private void drawRotateRect(Canvas canvas, Paint paint,
			RotateRect rotateRect) {

		paint.setColor(Color.RED);
		canvas.drawCircle(rotateRect.p1().x, rotateRect.p1().y, 10f, paint);
		paint.setColor(Color.YELLOW);
		canvas.drawCircle(rotateRect.p2().x, rotateRect.p2().y, 10f, paint);
		paint.setColor(Color.BLUE);
		canvas.drawCircle(rotateRect.p3().x, rotateRect.p3().y, 10f, paint);
		paint.setColor(Color.GREEN);
		canvas.drawCircle(rotateRect.p4().x, rotateRect.p4().y, 10f, paint);
	}

	Path path1 = new Path();
	float cropBoundHeight;

	public Path calRotate() {
		cropped = getCropBoundsDisplayed();
		return calRotate(cropped);

	}

	public Path calRotate(RectF cropped) {

		RectF rotateRect = new RectF();

		double angle;
		double angleDouble = 0;
		if (degree < 0) {
			angleDouble = 90 + degree;
		} else {
			angleDouble = degree;
		}

		angle = Math.toRadians(angleDouble);
		getImageRect();
		getImageOutPath();
		float croppedWidth = cropped.width();
		float croppedHeigth = cropped.height();

		rotateR.angle = degree;
		imageRect.angle = degree;
		imageRect.rotateDegree = degree;
		rotateR.rotateDegree = degree;
		float a = Math.abs((float) (croppedWidth * Math.sin(angle)));

		rotateR.p1.x = cropped.left + (float) (a * Math.sin(angle));
		rotateR.p1.y = cropped.top - (float) (a * Math.cos(angle));

		rotateR.p4.x = cropped.right - (float) (a * Math.sin(angle));
		rotateR.p4.y = cropped.bottom + (float) (a * Math.cos(angle));

		rotateRect.set(rotateR.p1.x, rotateR.p1.y, rotateR.p4.y, rotateR.p4.x);

		path1 = new Path();
		path1.moveTo(rotateR.p1.x, rotateR.p1.y);
		float a2 = Math.abs((float) (croppedHeigth * Math.cos(angle)));

		rotateR.p2.x = (float) (cropped.right + a2 * Math.sin(angle));
		rotateR.p2.y = (float) (cropped.bottom - a2 * Math.cos(angle));
		path1.lineTo(rotateR.p2.x, rotateR.p2.y);

		rotateR.p3.x = (float) (cropped.left - a2 * Math.sin(angle));
		rotateR.p3.y = (float) (cropped.top + a2 * Math.cos(angle));
		path1.lineTo(rotateR.p4.x, rotateR.p4.y);
		path1.lineTo(rotateR.p3.x, rotateR.p3.y);

		path1.close();

		float l, r, t, b;
		l = rotateR.p1.x < rotateR.p3.x ? rotateR.p1.x : rotateR.p3.x;
		r = rotateR.p4.x > rotateR.p2.x ? rotateR.p4.y : rotateR.p2.x;
		t = rotateR.p1.y < rotateR.p2.y ? rotateR.p1.y : rotateR.p2.y;
		b = rotateR.p4.y > rotateR.p3.y ? rotateR.p4.y : rotateR.p3.y;
		imageOutRect.set(l, t, r, b);
		if (degree >= 0) {
			float cropWidth = getDistance(rotateR.p4.x, rotateR.p4.y,
					rotateR.p3.x, rotateR.p3.y);
			cropBoundHeight = rotateR.getHeight();
		} else {
			cropBoundHeight = rotateR.getHeight();

			float cropWidth = getDistance(rotateR.p2.x, rotateR.p2.y,
					rotateR.p4.x, rotateR.p4.y);

		}
		invalidate();
		return path1;

	}

	public float getDistance(float x1, float y1, float x2, float y2) {
		float d1 = Math.abs(x1 - x2);
		float d2 = Math.abs(y1 - y2);
		float t = Math.abs(d1 * d1 + d2 * d2);
		float r1 = (float) Math.sqrt(t);
		return r1;
	}

	public float getDistance(PointF p1, PointF p2) {
		return getDistance(p1.x, p1.y, p2.x, p2.y);
	}

	public void drawMinContainCroppedBound(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#33ff00ff"));
		RectF cropped = getCropBoundsDisplayed();
		canvas.drawRect(cropped, paint);
	}

	private float scale = 0f;

	private float degree = 0f;
	float oldDegree = 0f;

	float rurningDegere = 0f;

	public void trunningRotate() {
		rurningDegere -= 90f;
		cropped = getCropBoundsDisplayed();
		float imWidth = (float) cropped.width();
		float imHeight = (float) cropped.height();

		float hTmp, wTmp;

		hTmp = cropped.height();
		wTmp = cropped.width();

		float ratio_view = hTmp / wTmp;
		float ratio_parent = mParentH / mParentW;
		float scale = 0f;
		float newWidth = 0f;
		float newHeight = 0f;

		float degree = -90;
		centernPoint = getCropCenterPoint();

		float cropWidth = cropped.width();
		float cropHeight = cropped.height();

		float newCropWidth = 0;
		float newCropHeight = 0;

		if (ratio_parent >= ratio_view) {
			isFatRadio = true;
			newHeight = mParentH;
			scale = (newHeight - 2 * mRectPadding) / wTmp;
			float ratio;
			newCropHeight = (mParentH - mRectPadding * 2);
			if (mRatio == 0) {
				ratio = cropped.width() / cropped.height();
				newCropWidth = newCropHeight / ratio;
			} else {
				mRatio = 1f / mRatio;
				ratio = mRatio;
				newCropWidth = newCropHeight / mRatio;
			}
			cropped.left = (mViewW - newCropWidth) / 2;
			cropped.right = cropped.left + newCropWidth;
			cropped.top = mRectPadding;
			cropped.bottom = cropped.top + newCropHeight;
			if (newCropWidth > mParentW) {
				newCropWidth = (mParentW - 2 * mRectPadding);
				scale = newCropWidth / hTmp;
				newCropHeight = newCropWidth * ratio;

				cropped.left = mRectPadding;
				cropped.right = mViewW - mRectPadding;
				cropped.top = (mViewH - newCropHeight) / 2;
				cropped.bottom = cropped.top + newCropHeight;
			}
			mMatrix = getImageMatrix();
			mMatrix.postRotate(degree, centernPoint.x, centernPoint.y);
			mMatrix.postScale(scale, scale, centernPoint.x, centernPoint.y);
			turningMatrix.postRotate(degree, centernPoint.x, centernPoint.y);
			turningMatrix.postScale(scale, scale, centernPoint.x,
					centernPoint.y);

		} else {
			isFatRadio = false;
			scale = (mParentW - 2 * mRectPadding) / hTmp;
			float ratio;
			newCropWidth = (mViewW - mRectPadding * 2);
			if (mRatio == 0) {
				ratio = cropped.width() / cropped.height();
				newCropHeight = newCropWidth * ratio;
			} else {
				mRatio = 1f / mRatio;
				ratio = mRatio;
				newCropHeight = newCropWidth * mRatio;
			}
			cropped.left = mRectPadding;
			cropped.right = mViewW - mRectPadding;
			cropped.top = (mViewH - newCropHeight) / 2;
			cropped.bottom = cropped.top + newCropHeight;
			if (newHeight > mParentW) {
				newCropHeight = (mParentH - 2 * mRectPadding);
				scale = newCropHeight / wTmp;
				newCropWidth = newCropHeight * ratio;

				cropped.left = (mViewW - newCropWidth) / 2;
				cropped.right = cropped.left + newCropWidth;
				cropped.top = mRectPadding;
				cropped.bottom = cropped.top + newCropHeight;
			}

			mMatrix = getImageMatrix();
			mMatrix.postRotate(degree, centernPoint.x, centernPoint.y);
			mMatrix.postScale(scale, scale, centernPoint.x, centernPoint.y);
			turningMatrix.postRotate(degree, centernPoint.x, centernPoint.y);
			turningMatrix.postScale(scale, scale, centernPoint.x,
					centernPoint.y);

		}

		truningPhotoBound();
		Matrix matrix = new Matrix();
		RectF rectF = new RectF(mRectPadding, mRectPadding, mViewW
				- mRectPadding, mViewH - mRectPadding - mRectPaddingBtmExtra);
		if (matrix.setRectToRect(photoBounds, rectF, Matrix.ScaleToFit.CENTER)) {
			matrix.mapRect(displayBounds, photoBounds);
			displayMatrix.setRectToRect(photoBounds, displayBounds,
					Matrix.ScaleToFit.CENTER);
		}
		matrix.invert(photoMatrix);
		mapPhotoRect(cropped, cropBounds);
		calRotate();
		mScaleDrawableHeight = rotateR.getHeight();
		mScaleDrawableWidth = rotateR.getWidth();
		setImageMatrix(mMatrix);
		invalidate();
		printMatrix(displayMatrix);
		isTruning = !isTruning;
		MIN_SCALE = scale * mInitScale;
		updateMaxScale();

	}

	public void truningPhotoBound() {
		float tmp = photoBounds.right;
		photoBounds.right = photoBounds.bottom;
		photoBounds.bottom = tmp;
	}

	public void rotate(float d, Matrix matrix) {
		this.degree = d;
		if (flipAngle == true) {
			degree = -d;
		}
		operateListenner.hasOprated();
		operationState = ROTATE_STATE;
		matrix.set(getImageMatrix());
		PointF cropPoint = getCropCenterPoint();
		matrix.postRotate(-oldDegree, cropPoint.x, cropPoint.y);
		matrix.postRotate(this.degree, cropPoint.x, cropPoint.y);
		setImageMatrix(matrix);
		oldDegree = degree;
		showSize = true;
		isRotateing = true;
	}

	public void setShowSize(boolean show) {
		showSize = show;
		invalidate();
	}

	public void scaleImage() {

		PointF cropPoint = getCropCenterPoint();

		if ((int) (imageRect.getHeight()) == (int) (rotateR.getHeight())
				|| (int) (imageRect.getWidth()) == (int) (rotateR.getWidth())) {
			calRotate();
			mMatrix.set(getImageMatrix());
			float ratioRotate = rotateR.getHeight() / rotateR.getWidth();
			float ratioImage = imageRect.getHeight() / imageRect.getWidth();
			if (ratioRotate > ratioImage) {
				oldScale = rotateR.getHeight() / imageRect.getHeight();
				mScaleDrawableHeight = mScaleDrawableHeight * oldScale;
				mScaleDrawableWidth = mScaleDrawableWidth * oldScale;
			} else {
				oldScale = rotateR.getWidth() / imageRect.getWidth();
				mScaleDrawableWidth = mScaleDrawableWidth * oldScale;
				mScaleDrawableHeight = mScaleDrawableHeight * oldScale;
			}
			mMatrix.postScale(oldScale, oldScale, cropPoint.x, cropPoint.y);
			setImageMatrix(mMatrix);
		} else {
		}
		calRotate();
		checkImageSCale();
		checkBound(mPhotoBoundRect, cropped);
	}

	public void checkImageSCale() {
		float heightScale = Float.MAX_VALUE;
		float widthScale = Float.MAX_VALUE;
		float scale = 1f;
		if (imageRect.getHeight() < rotateR.getHeight()) {
			heightScale = rotateR.getHeight() / imageRect.getHeight();
		}
		if (imageRect.getWidth() < rotateR.getWidth()) {
			widthScale = rotateR.getWidth() / imageRect.getWidth();
		}
		scale = Math.min(heightScale, widthScale);
		if (scale == Float.MAX_VALUE) {
			scale = 1f;
		} else {
			mMatrix.set(getImageMatrix());
			mMatrix.postScale(scale, scale, cropped.centerX(),
					cropped.centerY());
			setImageMatrix(mMatrix);
		}
	}

	public PointF getCropCenterPoint() {
		RectF cropped = getCropBoundsDisplayed();
		float x = (cropped.right - cropped.left) / 2 + cropped.left;
		float y = (cropped.bottom - cropped.top) / 2 + cropped.top;
		PointF pointF = new PointF();
		pointF.set(x, y);
		return pointF;
	}

	public class RotateRect {
		PointF p1, p2, p3, p4;
		ArrayList<PointF> points;
		Matrix m = new Matrix();
		public float angle;
		PointF ltPoint, rtPoint, lbPoint, rbPoint;

		public float rotateDegree;
		Comparator<PointF> compareX;
		Comparator<PointF> compareY;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public RotateRect(float maxW, float maxH) {
			p1 = new PointF();
			p2 = new PointF();
			p3 = new PointF();
			p4 = new PointF();

			points = new ArrayList();
			points.add(p1);
			points.add(p2);
			points.add(p3);
			points.add(p4);

			ltPoint = new PointF(0, 0);
			rtPoint = new PointF(10 * maxW, 0);
			lbPoint = new PointF(0, 10 * maxH);
			rbPoint = new PointF(10 * maxW, 10 * maxH);
			angle = 0f;

			compareX = new Comparator<PointF>() {
				@Override
				public int compare(PointF lhs, PointF rhs) {
					if ((int) lhs.x < (int) rhs.x) {
						return -1;
					} else if ((int) lhs.x > (int) rhs.x) {
						return 1;
					} else {
						if ((int) lhs.y < (int) rhs.y) {
							return -1;
						} else {
							return 1;
						}
					}

				}
			};
			compareY = new Comparator<PointF>() {
				@Override
				public int compare(PointF lhs, PointF rhs) {
					if ((int) lhs.y < (int) rhs.y) {
						return -1;
					} else if ((int) lhs.y > (int) rhs.y) {
						return 1;
					} else {
						if ((int) lhs.x < (int) rhs.x) {
							return -1;
						} else {
							return 1;
						}

					}
				}
			};
		}

		public float getWidth() {
			return getDistance(p4().x, p4().y, p3().x, p3().y);

		}

		public float getHeight() {
			return getDistance(p2().x, p2().y, p4().x, p4().y);

		}

		public PointF getClose(PointF dst) {
			PointF p = p1;
			float d1 = getDistance(p1, dst);
			float d2 = getDistance(p2, dst);
			float d3 = getDistance(p3, dst);
			float d4 = getDistance(p4, dst);
			float min = d1;
			if (d2 < min) {
				p = p2;
				min = d2;
			}
			if (d3 < min) {
				p = p3;
				min = d3;
			}
			if (d4 < min) {
				p = p4;
				min = d4;
			}
			return p;
		}

		public PointF p1() {

			if (angle == -1) {
				Collections.sort(points, compareY);
				return points.get(0);
			}
			if (rotateDegree < 0) {
				Collections.sort(points, compareX);
				return points.get(0);
			} else if (rotateDegree > 0) {
				Collections.sort(points, compareY);
				return points.get(0);
			} else {
				Collections.sort(points, compareY);
				return points.get(0);
			}

		}

		public PointF p2() {
			if (angle == -1) {
				Collections.sort(points, compareY);
				return points.get(1);
			}

			if (rotateDegree < 0) {
				Collections.sort(points, compareY);
				return points.get(0);
			} else if (rotateDegree > 0) {
				Collections.sort(points, compareX);
				return points.get(3);
			} else {
				Collections.sort(points, compareY);
				return points.get(1);
			}
		}

		public PointF p3() {
			if (angle == -1) {
				Collections.sort(points, compareY);
				return points.get(2);
			}
			if (rotateDegree < 0) {
				Collections.sort(points, compareY);
				return points.get(3);
			} else if (rotateDegree > 0) {
				Collections.sort(points, compareX);
				return points.get(0);
			} else {
				Collections.sort(points, compareY);
				return points.get(2);
			}
		}

		public PointF p4() {
			if (angle == -1) {
				Collections.sort(points, compareY);
				return points.get(3);
			}
			if (rotateDegree < 0) {
				Collections.sort(points, compareX);
				return points.get(3);
			} else if (rotateDegree > 0) {
				Collections.sort(points, compareY);
				return points.get(3);
			} else {
				Collections.sort(points, compareY);
				return points.get(3);
			}
		}

		@Override
		public String toString() {
			return "RotateRect{" + "p1=" + p1() + ", p2=" + p2() + ", p3="
					+ p3() + ", p4=" + p4() + '}';
		}
	}

	public void stopRaotae() {
		operationState = NORMAL_STATE;
		isRotateing = false;
		showSize = false;
		invalidate();
	}

	public void reset() {
		fixScale();
		degree = 0;
		rotateR.angle = 0;
		imageRect.angle = 0;
		oldDegree = 0;
		totalScale = touchScale = mInitScale;
		resetCropBound();
		setImageMatrix(mInitMatrix);
		printMatrix(mInitMatrix);
		setCanvasRotate(0);

		photoBounds.right = originWidth;
		photoBounds.bottom = originHeight;
		calRotate();
		initCrop(mViewW, mViewH);
		sizeChanged();
		cropped = getCropBoundsDisplayed();
		mScaleDrawableHeight = cropped.height();
		mScaleDrawableWidth = cropped.width();
		operationState = NORMAL_STATE;
		checkFatRadio();
		invalidate();
		isTruning = false;
		MIN_SCALE = mInitScale;
		isRotateing = false;
		updateMaxScale();
	}

	private boolean flipAngle = false;

	public void verticalFlip(Matrix matrix, float oldDegree) {
		mMatrix.set(getImageMatrix());
		mMatrix.mapRect(mPhotoBoundRect, rectF);
		matrix.set(getImageMatrix());
		this.oldDegree = -this.oldDegree;
		matrix.postScale(-1, 1, mPhotoBoundRect.centerX(),
				mPhotoBoundRect.centerY());
		setImageMatrix(matrix);
		flipAngle = !flipAngle;
		calRotate();
		updateMaxScale();
		checkBound(mPhotoBoundRect, cropped);
	}

	public interface BounderChecker {

		public boolean containInBounder(float[] checkDelta);

		public boolean checkCropBounder(RectF cropped);

	}

	public void cropImage() {
		Bitmap croppedBitmap;
		try {
			float scale = getCropScale();
			if (scale <= MAX_SCALE && scale >= MIN_SCALE) {
				croppedBitmap = process();
				if (croppedBitmap != null) {
					mCanInit = true;
					initImage();
					setCropBounds(new RectF(0, 0, croppedBitmap.getWidth(),
							croppedBitmap.getHeight()));
					setImageBitmap(croppedBitmap);
					reset();
					mInitMatrix.set(displayMatrix);
					setImageMatrix(mInitMatrix);
				}
			} else {
				// Toast.makeText(this.getContext(),
				// "Select the area is too small , can not be crop",
				// Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.getContext(),
					"Select the area is too small , can not be crop",
					Toast.LENGTH_SHORT).show();
		}

	}

	public static int saveBitmap(Bitmap bitmap, String path) {
		FileOutputStream out = null;
		try {
			File file = new File(path);
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(path);
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.JPEG,
						CropsUtils.JPEG_QUALITY, out);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			return 1;
		} finally {
			closeQuietly(out);
		}

		return 1;
	}

	public Bitmap process() {
		if (mViewW <= 0 || mViewH <= 0 || mMatrix == null) {
			return null;
		}
		if (mBitmap == null) {
			return null;
		}
		calRotate();
		float scale = mBitmap.getWidth() / imageRect.getWidth();
		// count actual matrix
		Matrix rotateMatrix = new Matrix(getImageMatrix());
		rotateMatrix.postScale(scale, scale, cropped.centerX(),
				cropped.centerY());

		RectF srcRectF = new RectF();
		rotateMatrix.mapRect(srcRectF);
		Bitmap bmpSrc = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), rotateMatrix, true);

		mMatrix.mapRect(mPhotoBoundRect, rectF);

		calRotate();
		int diffX = (int) (cropped.left - mPhotoBoundRect.left);
		int diffY = (int) (cropped.top - mPhotoBoundRect.top);
		int x = (int) (diffX / mPhotoBoundRect.width() * bmpSrc.getWidth());
		int y = (int) (diffY / mPhotoBoundRect.height() * bmpSrc.getHeight());
		int cropWidth = (int) ((cropped.width() / mPhotoBoundRect.width()) * bmpSrc
				.getWidth());
		int cropHeight = (int) ((cropped.height() / mPhotoBoundRect.height()) * bmpSrc
				.getHeight());
		mBitmap.recycle();
		Bitmap bmpCropped = Bitmap.createBitmap(bmpSrc, x, y, cropWidth,
				cropHeight);
		bmpSrc.recycle();

		return bmpCropped;
	}

	public void saveImage2Bitmap(int[] result) {
		try {
			if (mInnerRect == null) {
				// if null, re-init.
				mInnerRect = new Rect();
				RectF rect = getCropBoundsDisplayed();
				rect.roundOut(mInnerRect);
			}
			int width = (int) ((float) mInnerRect.width() / totalScale);
			int height = (int) ((float) mInnerRect.height() / totalScale);

			Bitmap btSave = Bitmap.createBitmap(mBitmap, 0, 0, width, height,
					getImageMatrix(), true);

			if (null != mBitmap && !mBitmap.isRecycled()) {
				mBitmap.recycle();
				mBitmap = null;
			}
			setImageBitmap(btSave);
			setCropBounds(new RectF(0, 0, btSave.getWidth(), btSave.getHeight()));
		} catch (OutOfMemoryError e) {
			if (null != result) {
				result[0] = -2;
			}
		} catch (Exception e) {
			if (null != result) {
				result[0] = -1;
			}
		}
	}

	public void updateCropRatio(float r) {
		setRectRatio(r);
		reset();
		updateCropBound();
		calRotate();
		getCropBoundsDisplayed();
		checkImageSCale();
		getImageRect();
		calRotate();
		invalidate();
		MIN_SCALE = getImageScale();
		updateMaxScale();
		operateListenner.hasOprated();
		isRotateing = false;
		showSize = true;
	}

	private void updateMaxScale() {
		calRotate();
		float width = cropped.width();
		float height = cropped.height();
		MAX_SCALE = Math.min(width, height) / MIN_CROP_WIDTH_HEIGHT;
	}

	public interface CropListenner {
		public void onCropping();

		public void cropFinished();
	}

	public void setCropListenenr(CropListenner cropListenner) {
		this.cropListenner = cropListenner;
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException localIOException) {
		}
	}
}

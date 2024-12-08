package com.photex.tiktok.gallery.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;

import com.photex.tiktok.gallery.interfaces.CursorHandler;
import com.photex.tiktok.gallery.model.Media;
import com.photex.tiktok.utils.TimeUtil;

import io.reactivex.Observable;


/**
 * Created by dnld on 3/13/17.
 */

public class QueryUtils {

    public static <T> Observable<T> query(Query q, ContentResolver cr
            , CursorHandler<T> ch) {

        return Observable.create(subscriber -> {
            Cursor cursor = null;
            try {
                cursor = q.getCursor(cr);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        /*Media media = (Media) ch.handle(cursor);*/

                        subscriber.onNext(ch.handle(cursor));
                    }
                }

                subscriber.onComplete();
            } catch (Exception err) {
                subscriber.onError(err);
            } finally {
                if (cursor != null) cursor.close();
            }
        });
    }


    public static <T> Observable<T> queryContext(Query q, ContentResolver cr
            , CursorHandler<T> ch, Context context) {

        return Observable.create(subscriber -> {
            Cursor cursor = null;
            try {
                MediaMetadataRetriever retriever = new
                        MediaMetadataRetriever();
                cursor = q.getCursor(cr);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        Media media = (Media) ch.handle(cursor);
                        if (media.getDuration() > 0) {
                            media.setHumanReadAbleTime(TimeUtil.getDurationBreakdown(
                                    media.getDuration()));
                        }
                        subscriber.onNext((T) media);
                    }
                }
                retriever.release();
                subscriber.onComplete();
            } catch (Exception err) {
                subscriber.onError(err);
            } finally {
                if (cursor != null) cursor.close();
            }
        });
    }

    /**
     * return only the first element if there is one
     *
     * @param q
     * @param cr
     * @param ch
     * @param <T>
     * @return
     */
    public static <T> Observable<T> querySingle(Query q, ContentResolver cr, CursorHandler<T> ch) {
        return Observable.create(subscriber -> {
            Cursor cursor = null;
            try {
                cursor = q.getCursor(cr);
                if (cursor != null && cursor.moveToFirst())
                    subscriber.onNext(ch.handle(cursor));
                subscriber.onComplete();
            } catch (Exception err) {
                subscriber.onError(err);
            } finally {
                if (cursor != null) cursor.close();
            }
        });
    }

}

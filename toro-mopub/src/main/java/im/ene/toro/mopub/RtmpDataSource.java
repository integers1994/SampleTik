/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.toro.mopub;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Util;

import net.butterflytv.rtmp_client.RtmpClient;

public class RtmpDataSource implements DataSource {

    private static String TAG = "RtmpSouce";

    public static class RtmpDataSourceFactory implements Factory {

        @Override
        public DataSource createDataSource() {
            return new RtmpDataSource();
        }
    }

    private final RtmpClient rtmpClient;
    private Uri uri;

    public RtmpDataSource() {
        rtmpClient = new RtmpClient();
    }


    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public long open(DataSpec dataSpec) {
        Log.w(TAG, "open is called");
        String uriString = dataSpec.uri.toString();
        Log.i("uripath", uriString);
        try {
            rtmpClient.open(uriString, false);
            uri = dataSpec.uri;
        } catch (RtmpClient.RtmpIOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            if (e instanceof RtmpClient.RtmpIOException) {
                RtmpClient.RtmpIOException rtmpIOException = (RtmpClient.RtmpIOException) e;
                Log.e(TAG, "error code is:" + rtmpIOException.errorCode);
            }
            e.printStackTrace();
            return 0;
        }
        return C.LENGTH_UNSET;
    }

    @Override
    public void close() {
        Log.w(TAG, "close is called");
        rtmpClient.close();
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) {
        try {
            return rtmpClient.read(buffer, offset, readLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
   /*     try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return -1;
    }
}

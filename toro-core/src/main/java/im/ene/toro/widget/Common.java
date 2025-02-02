/*
 * Copyright (c) 2017 Nam Nguyen, nam@ene.im
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

package im.ene.toro.widget;

import android.graphics.Point;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;
import im.ene.toro.ToroPlayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author eneim | 6/2/17.
 *
 *         A hub for internal convenient methods.
 */

@SuppressWarnings({ "unused", "WeakerAccess" }) //
@RestrictTo(RestrictTo.Scope.LIBRARY) //
final class Common {

  private static final String TAG = "ToroLib:Common";
  // Keep static values to reduce instance initialization. We don't need to access its value.
  private static final Rect dummyRect = new Rect();
  private static final Point dummyPoint = new Point();

  static int compare(int x, int y) {
    //noinspection UseCompareMethod
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  static long max(Long... numbers) {
    List<Long> list = Arrays.asList(numbers);
    return Collections.<Long>max(list);
  }

  static Comparator<ToroPlayer> ORDER_COMPARATOR = new Comparator<ToroPlayer>() {
    @Override public int compare(ToroPlayer o1, ToroPlayer o2) {
      return Common.compare(o1.getPlayerOrder(), o2.getPlayerOrder());
    }
  };

  static final Comparator<Integer> ORDER_COMPARATOR_INT = new Comparator<Integer>() {
    @Override public int compare(Integer o1, Integer o2) {
      return o1.compareTo(o2);
    }
  };

  static boolean allowsToPlay(@NonNull ToroPlayer player) {
    dummyRect.setEmpty();
    dummyPoint.set(0, 0);
    //noinspection ConstantConditions
    boolean valid = player instanceof RecyclerView.ViewHolder;  // Should be true
    if (valid) valid = ((RecyclerView.ViewHolder) player).itemView.getParent() != null;
    if (valid) valid = player.getPlayerView().getGlobalVisibleRect(dummyRect, dummyPoint);
    return valid;
  }
}

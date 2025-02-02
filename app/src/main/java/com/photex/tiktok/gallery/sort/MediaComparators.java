package com.photex.tiktok.gallery.sort;



import com.photex.tiktok.gallery.model.Media;
import com.photex.tiktok.utils.NumericComparator;

import java.util.Comparator;

/**
 * Created by dnld on 26/04/16.
 */

public class MediaComparators {

    public static Comparator<Media> getComparator(SortingMode sortingMode, SortingOrder sortingOrder) {
        return  sortingOrder == SortingOrder.ASCENDING
                ? getComparator(sortingMode) : reverse(getComparator(sortingMode));
    }

    public static Comparator<Media> getComparator(SortingMode sortingMode) {
        switch (sortingMode) {
            case NAME: return getNameComparator();
            case DATE: default: return getDateComparator();
            case SIZE: return getSizeComparator();
            case TYPE: return getTypeComparator();
            case NUMERIC: return getNumericComparator();
        }
    }

    private static Comparator<Media> reverse(Comparator<Media> comparator) {
        return (o1, o2) -> comparator.compare(o2, o1);
    }

    private static Comparator<Media> getDateComparator(){
        return (f1, f2) -> f1.getDateModified().compareTo(f2.getDateModified());
    }

    private static Comparator<Media> getNameComparator() {
        return (f1, f2) -> f1.getPath().compareTo(f2.getPath());
    }

    private static Comparator<Media> getSizeComparator() {
        return (f1, f2) -> Long.compare(f1.getSize(), f2.getSize());
    }

    private static Comparator<Media> getTypeComparator() {
        return (f1, f2) -> f1.getMimeType().compareTo(f2.getMimeType());
    }

    private static Comparator<Media> getNumericComparator() {
        return (f1, f2) -> NumericComparator.filevercmp(f1.getPath(), f2.getPath());
    }
}

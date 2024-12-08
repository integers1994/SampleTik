package com.photex.tiktok.models;

import com.photex.tiktok.utils.Constants;

public class FirebaseDynamicLinkCreator {
    String dynamicLink;

    public FirebaseDynamicLinkCreator(String videoLink) {
//        this.dynamicLink = Constants.DYNAMIC_LINK + videoLink + "&apn=com.photex.tiktok&amv=1&efr=1";
        this.dynamicLink = Constants.DYNAMIC_LINK + videoLink + "&apn=com.photex.tiktok&amv=1";
    }

    public String getDynamicLink() {
        return dynamicLink;
    }
}

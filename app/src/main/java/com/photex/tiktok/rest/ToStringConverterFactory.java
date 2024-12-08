package com.photex.tiktok.rest;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Umair Adil on 12/2/2015.
 */
public class ToStringConverterFactory extends Converter.Factory {


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, Object>() {

                @Override
                public Object convert(ResponseBody responseBody) throws IOException {
                    return responseBody.string();
                }
            };
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {

                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MediaType.parse("text/plain"), value);
                }
            };
        }

        return super.requestBodyConverter(type, parameterAnnotations,methodAnnotations, retrofit);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
                                                Retrofit retrofit) {
        return super.stringConverter(type, annotations,retrofit);
    }
}

package com.mnyun.imServerClient;

import org.json.JSONObject;

public interface IMContentConvert<T> {
    T Convert(JSONObject content) throws Exception;
}

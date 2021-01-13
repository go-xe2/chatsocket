package com.mnyun.utils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ReactUtils {
    public static WritableArray convertJsonArrayToWritable(JSONArray jsonArr) throws ChatSocketException {
        WritableArray arr = Arguments.createArray();
        for (int i=0; i<jsonArr.length(); i++) {
            Object obj = null;
            try {
                obj = jsonArr.get(i);
            } catch (JSONException jsonException) {
                // Should not happen.
                throw new ChatSocketException(i + " should be within bounds of array " + jsonArr.toString(), jsonException);
            }

            if (obj instanceof JSONObject)
                arr.pushMap(convertJsonObjectToWritable((JSONObject) obj));
            else if (obj instanceof JSONArray)
                arr.pushArray(convertJsonArrayToWritable((JSONArray) obj));
            else if (obj instanceof String)
                arr.pushString((String) obj);
            else if (obj instanceof Double)
                arr.pushDouble((Double) obj);
            else if (obj instanceof Integer)
                arr.pushInt((Integer) obj);
            else if (obj instanceof Boolean)
                arr.pushBoolean((Boolean) obj);
            else if (obj == null)
                arr.pushNull();
            else
                throw new ChatSocketException("Unrecognized object: " + obj);
        }

        return arr;
    }

    public static WritableMap convertJsonObjectToWritable(JSONObject jsonObj) throws ChatSocketException{
        WritableMap map = Arguments.createMap();
        Iterator<String> it = jsonObj.keys();
        while(it.hasNext()){
            String key = it.next();
            Object obj = null;
            try {
                obj = jsonObj.get(key);
            } catch (JSONException jsonException) {
                // Should not happen.
                throw new ChatSocketException("Key " + key + " should exist in " + jsonObj.toString() + ".", jsonException);
            }

            if (obj instanceof JSONObject)
                map.putMap(key, convertJsonObjectToWritable((JSONObject) obj));
            else if (obj instanceof JSONArray)
                map.putArray(key, convertJsonArrayToWritable((JSONArray) obj));
            else if (obj instanceof String)
                map.putString(key, (String) obj);
            else if (obj instanceof Double)
                map.putDouble(key, (Double) obj);
            else if (obj instanceof Long)
                map.putDouble(key, ((Long) obj).doubleValue());
            else if (obj instanceof Integer)
                map.putInt(key, (Integer) obj);
            else if (obj instanceof Boolean)
                map.putBoolean(key, (Boolean) obj);
            else if (obj == null)
                map.putNull(key);
            else
                throw new ChatSocketException("Unrecognized object: " + obj);
        }

        return map;
    }

    public static JSONArray convertReadableToJsonArray(ReadableArray arr) throws ChatSocketException {
        JSONArray jsonArr = new JSONArray();
        for (int i=0; i<arr.size(); i++) {
            ReadableType type = arr.getType(i);
            switch (type) {
                case Map:
                    jsonArr.put(convertReadableToJsonObject(arr.getMap(i)));
                    break;
                case Array:
                    jsonArr.put(convertReadableToJsonArray(arr.getArray(i)));
                    break;
                case String:
                    jsonArr.put(arr.getString(i));
                    break;
                case Number:
                    Double number = arr.getDouble(i);
                    if ((number == Math.floor(number)) && !Double.isInfinite(number)) {
                        // This is a whole number.
                        jsonArr.put(number.longValue());
                    } else {
                        try {
                            jsonArr.put(number.doubleValue());
                        } catch (JSONException jsonException) {
                            throw new ChatSocketException("Unable to put value " + arr.getDouble(i) + " in JSONArray");
                        }
                    }
                    break;
                case Boolean:
                    jsonArr.put(arr.getBoolean(i));
                    break;
                case Null:
                    jsonArr.put(null);
                    break;
            }
        }

        return jsonArr;
    }

    public static JSONObject convertReadableToJsonObject(ReadableMap map) throws ChatSocketException {
        JSONObject jsonObj = new JSONObject();
        ReadableMapKeySetIterator it = map.keySetIterator();
        while (it.hasNextKey()) {
            String key = it.nextKey();
            ReadableType type = map.getType(key);
            try {
                switch (type) {
                    case Map:
                        jsonObj.put(key, convertReadableToJsonObject(map.getMap(key)));
                        break;
                    case Array:
                        jsonObj.put(key, convertReadableToJsonArray(map.getArray(key)));
                        break;
                    case String:
                        jsonObj.put(key, map.getString(key));
                        break;
                    case Number:
                        jsonObj.put(key, map.getDouble(key));
                        break;
                    case Boolean:
                        jsonObj.put(key, map.getBoolean(key));
                        break;
                    case Null:
                        jsonObj.put(key, null);
                        break;
                    default:
                        throw new ChatSocketException("Unrecognized type: " + type + " of key: " + key);
                }
            } catch (JSONException jsonException) {
                throw new ChatSocketException("Error setting key: " + key + " in JSONObject", jsonException);
            }
        }

        return jsonObj;
    }
}

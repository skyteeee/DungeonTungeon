package com.skyteeee.tungeon.utils;

import org.json.JSONObject;

public interface Savable {

    JSONObject serialize();

    void deserialize(JSONObject object);

}

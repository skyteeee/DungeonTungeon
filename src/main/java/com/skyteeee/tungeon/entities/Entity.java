package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.utils.GameObject;
import org.json.JSONObject;

public interface Entity extends GameObject {

    int getId ();

    void setId(int id);

    JSONObject serialize();

    void deserialize(JSONObject object);

}

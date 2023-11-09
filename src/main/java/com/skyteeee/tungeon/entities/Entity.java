package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.utils.GameObject;
import com.skyteeee.tungeon.utils.Savable;

public interface Entity extends GameObject, Savable {

    int getId ();

    void setId(int id);

}

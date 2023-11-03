package com.skyteeee.tungeon.entities;

import com.skyteeee.tungeon.entities.items.Item;
import com.skyteeee.tungeon.storage.Inventory;

public interface Character extends Entity {

    void setCurrentPlace(Place place);

    Place getCurrentPlace();

    Inventory getInventory();

}

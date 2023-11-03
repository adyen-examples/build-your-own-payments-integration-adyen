package com.adyen.checkout.services;

import com.adyen.checkout.models.InventoryModel;
import java.util.HashMap;

public class InventoryService {

    private HashMap<String, InventoryModel> map;

    public InventoryService()
    {
        map = new HashMap<>()
        {
            { put("sunglasses", new InventoryModel("sunglasses", 5000, "EUR")); }
            { put("headphones", new InventoryModel("headphones", 5000, "EUR")); }
        };
    }

    public HashMap<String, InventoryModel> getMap() {
        return map;
    }
}

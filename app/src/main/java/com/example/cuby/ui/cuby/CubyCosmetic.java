package com.example.cuby.ui.cuby;

public enum CubyCosmetic {
    NONE("none"),
    CAT("cat"),
    DOG("dog"),
    GLASSES("glasses"),
    EMPLOYED("employed");

    public final String key;

    CubyCosmetic(String key) {
        this.key = key;
    }
}

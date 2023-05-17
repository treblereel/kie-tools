package org.yard.validator.adapter.key;

/**
 * The key, path down to field in DRL or DMN case.
 */
public interface Key {

    Location getLocation();

    KeyParent getParent();
}

package org.yard.validator.adapter;


import org.yard.validator.adapter.key.Key;
import org.yard.validator.adapter.key.Location;

import java.util.Collection;
import java.util.Map;

public class YardVisitor {

    public Map<Location, Collection<Key>> visit(final String s) {
        return new Parser().parse(s);
    }
}

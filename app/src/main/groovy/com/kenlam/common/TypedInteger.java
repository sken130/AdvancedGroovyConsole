package com.kenlam.common;

import java.util.Objects;

public abstract class TypedInteger {
    public final Integer Value;

    public TypedInteger(Integer value) {
        if (value == null) {
            throw new NullPointerException("TypedInteger doesn't accept null value");
        }
        this.Value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedInteger that = (TypedInteger) o;
        return Value.equals(that.Value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Value);
    }
}

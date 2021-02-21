/*
 *  Copyright 2021 Ken Lam
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + Value + ")";
    }
}

package com.example.socialnetworkgui.domain;

import java.util.Objects;

public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     *
     * @return the first entity of the tuple
     */
    public E1 getE1() {
        return e1;
    }

    /**
     *
     * @return the second entity of the tuple
     */
    public E2 getE2() {
        return e2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(e1, tuple.e1) && Objects.equals(e2, tuple.e2) || Objects.equals(e1, tuple.e2) && Objects.equals(e2, tuple.e1);
    }

    @Override
    public int hashCode() {
        return e1.hashCode() + e2.hashCode();
    }
}

package com.devchallenge.dto;

public class MutableInt {
    int value = 1;

    public void increment() {
        ++value;
    }

    public int get() {
        return value;
    }

    public void divideBy(int c) {
        value /= c;
    }
}
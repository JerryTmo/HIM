package com.example.controller;

import java.util.ArrayList;
import java.util.List;

public class MindNode {
    public String name;
    public int level;
    public List<MindNode> children;
    public String color;

    public MindNode(String name, int level) {
        this.name = name;
        this.level = level;
        this.children = new ArrayList<>();
    }
}

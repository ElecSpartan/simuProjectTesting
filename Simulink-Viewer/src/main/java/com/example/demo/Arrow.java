package com.example.demo;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
public class Arrow {
    int scrId;
    int srcPlace;
    List<Pair> dests = new ArrayList<Pair>();
}
class Pair {
    int destId;
    int destPlace;

    public Pair(int destId, int destPlace) {
        this.destId = destId;
        this.destPlace = destPlace;
    }

    public int getDestId() {
        return destId;
    }

    public int getDestPlace() {
        return destPlace;
    }

}


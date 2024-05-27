package pposonggil.usedStuff.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Embeddable
@Getter
public class Lane {
    private String name;
    private String color;

    protected Lane() {

    }

    public Lane(String name, String color) {
        this.name = name;
        this.color = color;
    }
}

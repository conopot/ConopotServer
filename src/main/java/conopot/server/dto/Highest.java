package conopot.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Highest {
    private Music tj;
    private Music ky;
    private String sex;
    private String highest;
    private String highestVal;

    public Highest(Music tj, Music ky, String sex, String highest, String highestVal) {
        this.tj = tj;
        this.ky = ky;
        this.sex = sex;
        this.highest = highest;
        this.highestVal = highestVal;
    }

    @Override
    public String toString() {
        return tj.toString() + ky.toString() + this.sex + "^" + this.highest + "^" + this.highestVal + "^";
    }
}

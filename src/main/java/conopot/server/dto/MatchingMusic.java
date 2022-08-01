package conopot.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatchingMusic implements Comparable<MatchingMusic> {

    private Music TJ;
    private Music KY;

    public MatchingMusic(Music TJ, Music KY) {
        this.TJ = TJ;
        this.KY = KY;
    }

    @Override
    public String toString() {
        return TJ.toString() + KY.toString();
    }

    @Override
    public int compareTo(MatchingMusic o) {
        if(Integer.valueOf(this.TJ.getNumber()) > Integer.valueOf(o.TJ.getNumber())) return 1;
        else return -1;
    }
}

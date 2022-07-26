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
        return this.TJ.getNumber().compareTo(o.getTJ().getNumber());
    }
}

package conopot.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpMusic implements Comparable<CmpMusic>{

    private String name;
    private String singer;
    private String number;

    public CmpMusic(String name, String singer, String number) {
        this.name = name;
        this.singer = singer;
        this.number = number;
    }

    @Override
    public int compareTo(CmpMusic o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name + "^" + singer + "^" + number + "^";
    }

}

package conopot.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Music implements Comparable<Music>{

    private String name;
    private String singer;
    private String number;
    private String high;
    private String sex;

    public Music(String name, String singer, String num) {
        this.name = name;
        this.singer = singer;
        this.number = num;
    }

    @Override
    public String toString() {
        return name + "^" + singer + "^" + number + "^";
    }

    @Override
    public int compareTo(Music o) {
        if(Integer.valueOf(this.getNumber()) > Integer.valueOf(o.getNumber())) return 1;
        else return -1;
    }
}

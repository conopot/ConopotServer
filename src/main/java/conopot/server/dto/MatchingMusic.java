package conopot.server.dto;


public class MatchingMusic implements Comparable<MatchingMusic> {

    private Music TJ;
    private Music KY;

    private String sex;
    private String highest;

    private String highestVal;
    public MatchingMusic(Music TJ, Music KY) {
        this.TJ = TJ;
        this.KY = KY;
        this.sex = "?";
        this.highest = "?";
        this.highestVal = "?";
    }

    public MatchingMusic(Music TJ, Music KY, String sex, String highest, String highestVal) {
        this.TJ = TJ;
        this.KY = KY;
        this.sex = sex;
        this.highest = highest;
        this.highestVal = highestVal;
    }


    public void setHighest(String highest) {
        this.highest = highest;
        setHighestVal(highest);
    }

    public void setHighestVal(String highest) {
        this.highestVal = changeVal(highest);
    }

    public int getPitch(char c) {
        int ret = 0;
        if(c == '도') ret = 1;
        else if(c == '레') ret = 3;
        else if(c == '미') ret = 5;
        else if(c == '파') ret = 7;
        else if(c == '솔') ret = 9;
        else if(c == '라') ret = 11;
        else if(c == '시') ret = 13;
        return ret;
    }

    public String changeVal(String str){
        int ret = 0;

        // 맨 앞자리 = 옥타브
        ret += (str.charAt(0) - '0' - 1) * 14;

        // 둘째 자리 = 음정
        ret += getPitch(str.charAt(1));

        // 셋째 자리 = # 여부
        if(str.length() == 3) {
            ret += 1;
        }

        return Integer.toString(ret);
    }

    @Override
    public String toString() {
        return TJ.toString() + KY.toString() + this.sex + "^" + this.highest + "^" + this.highestVal + "^";
    }

    public Music getTJ() {
        return TJ;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public int compareTo(MatchingMusic o) {
        return this.TJ.getNumber().compareTo(o.getTJ().getNumber());
    }

    public Music getKY() {
        return KY;
    }

    public void setKY(Music KY) {
        this.KY = KY;
    }
}

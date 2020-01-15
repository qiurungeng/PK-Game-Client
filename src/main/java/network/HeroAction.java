package network;

import lombok.Data;

@Data
public class HeroAction {
    boolean up,down,left,right,attack;
    int x,y;

    public HeroAction(){}

    public HeroAction(int x,int y,boolean up, boolean down, boolean left, boolean right, boolean attack) {
        this.x=x;
        this.y=y;
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.attack = attack;
    }
}

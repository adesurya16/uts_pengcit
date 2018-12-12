// import android.graphics.Color;
import java.awt.Color;
import java.util.ArrayList;
public class Component {
    final int redVal = (200 << 16) | (0 << 8) | 0;
    final int greenVal = (0 << 16) | (200 << 8) | 0;
    final int blueVal = (0 << 16) | (0 << 8) | 200;
    final int whiteVal = (255 << 16) | (255 << 8) | 255;
    final int blackVal = (0 << 16) | (0 << 8) | 0;

    private int height;
    private int width;

    // bouded point
    public int Xmax;
    public int Ymax;
    public int Xmin;
    public int Ymin;

    private ArrayList<point> pAreaComponent;
    private int[][] matrixBW;
    private ArrayList<Integer> chainCodeList;

    // component merupakan hole dari skin tapi direpresentasikan sama dengan skin yaitu 1 dan skin adalah 0 didalam kasus ini
    public Component(ArrayList<point> pList, int height, int width){
        this.pAreaComponent = new ArrayList<>();
        this.chainCodeList = new ArrayList<>();
        this.matrixBW = new int[height][];
        for(int i = 0; i < height; i++){
            this.matrixBW[i] = new int[width];
        }

        pAreaComponent.addAll(pList);
        initMatrixFromList(pList);
        this.height = height;
        this.width = width;

        getBoundedPoint();
    }

    public ArrayList<Integer> getChainCodeList(){
        return this.chainCodeList;
    }

    public void initMatrixFromList(ArrayList<point> pList){
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                this.matrixBW[i][j] = blackVal;
            }
        }

        for(point p: pList){
            this.matrixBW[p.x][p.y] = whiteVal;
        }
    }

    public void getBoundedPoint(){
        int y_start = -1;
        int y_end = this.width;
        int x_start = -1;
        int x_end = this.height;
//        int val;
        for(point p : this.pAreaComponent){
            if (x_start == -1){
                x_start = p.x;
            }else if(p.x < x_start){
                x_start = p.x;
            }

            if (x_end == this.height){
                x_end = p.x;
            }else if(p.x > x_end){
                x_end = p.x;
            }

            if (y_start == -1){
                y_start = p.y;
            }else if(p.y < y_start){
                y_start = p.y;
            }

            if(y_end == this.width){
                y_end = p.y;
            }else if(p.y > y_end){
                y_end = p.y;
            }
        }

        x_start -= 1;
        x_end += 1;

        y_start -= 1;
        y_end += 1;

        this.Xmin = x_start;
        this.Xmax = x_end;

        this.Ymin = y_start;
        this.Ymax = y_end;
    }

    public void getChainCode(){

    }

    public boolean isEye(){
        return false;
    }

    public boolean isNose(){
        return false;
    }

    public boolean isMouth(){
        return false;
    }
}

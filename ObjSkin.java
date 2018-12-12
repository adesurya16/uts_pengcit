

// import android.graphics.Color;
import java.awt.Color;
import java.util.ArrayList;

public class ObjSkin {
    final int redVal = (200 << 16) | (0 << 8) | 0;
    final int greenVal = (0 << 16) | (200 << 8) | 0;
    final int blueVal = (0 << 16) | (0 << 8) | 200;
    final int whiteVal = (255 << 16) | (255 << 8) | 255;
    final int blackVal = (0 << 16) | (0 << 8) | 0;

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
    private int height;
    private int width;

    boolean isHole;
    ArrayList<point> pList;

    // bouded point
    public int Xmax;
    public int Ymax;
    public int Xmin;
    public int Ymin;

    private ArrayList<point> pAreaSkinList;
    private int[][] matrixBW;
    private ArrayList<Integer> chainCodeList;
    private ArrayList<Component> componentList;

    public ObjSkin(ArrayList<point> pList, int height, int width){
        System.out.println("obj ctor");
        this.pAreaSkinList = new ArrayList<>();
        this.componentList = new ArrayList<>();
        this.matrixBW = new int[height][];
        for(int i = 0; i < height; i++){
            this.matrixBW[i] = new int[width];
        }

        this.pAreaSkinList.addAll(pList);

        initMatrixFromList(pList);
        this.height = height;
        this.width = width;

        getBoundedPoint();
        detectHoleToList();
    }

    public ArrayList<Integer> getChainCodeList(){
        return this.chainCodeList;
    }

    public ArrayList<Component> getComponentList() {
        return componentList;
    }

    public void setMatrixBW(int mBW[][]){
        for(int i = 0;i< this.height;i++){
            for(int j = 0;j< this.width;j++){
                this.matrixBW[i][j] = mBW[i][j];
            }
        }
    }

    // utils

    public void copyToMatrix(int matrix[][]){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                matrix[i][j] = this.matrixBW[i][j];
            }
        }
    }

    public void initMatrixFromList(ArrayList<point> pList){
        System.out.println("init matrix");
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
        for(point p : this.pAreaSkinList){
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

    
    public void detectHoleToList(){
        this.componentList.clear();

        int[][] matBWTmp = new int[this.height][];
        for(int i = 0;i<this.height;i++){
            matBWTmp[i] = new int[this.width];
        }

        copyToMatrix(matBWTmp);
        for(int i = this.Xmin;i<=this.Xmax + 1;i++){
            for(int j = this.Ymin;j <= this.Ymax + 1;j++){
                int x = i;
                int y = j;
                if (this.matrixBW[x][y] == blackVal){
                    System.out.println("current hole");
                    isHole = true;
                    pList = new ArrayList<>();
                    getDeletedPointSkinComponent(x, y);
                    if (isHole) {
                        this.componentList.add(new Component(pList, this.height, this.width));
                        System.out.println("NEW component");
                    }else {
                        System.out.println("NOT component");
                    }
                }
            }
        }

        setMatrixBW(matBWTmp);
    }



    public void getDeletedPointSkinComponent(int px, int py){
        // pList harus udah clear dipanggilan pertama
        
            if (this.matrixBW[px][py] == blackVal){
                pList.add(new point(px, py));
                this.matrixBW[px][py] = whiteVal;
                for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                    int dx = px + iterationDirections[i][1];
                    int dy = py + iterationDirections[i][0];
                    if ((dx >= this.Xmin && dx <= this.Xmax) && (dy >= this.Ymin && dy <= this.Ymax)){
                        getDeletedPointSkinComponent(dx, dy);
                    }else {
                        isHole = false;
                    }
                }
            }
        
    }

    public boolean IsFaceDetected(){
        return true;
        // int isEye = 0;
        // int isNose = 0;
        // int isMouth = 0;
        // for(Component p: this.componentList){
        //     if (p.isEye()) {
        //         isEye++;
        //     }else if (p.isNose()){
        //         isNose++;
        //     }else if(p.isMouth()){
        //         isNose++;
        //     }
        // }
        // if(isEye == 2 && isNose == 1 && isMouth == 1){
        //     return true;
        // }else return false;
    }
}

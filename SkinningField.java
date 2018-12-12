

// import android.graphics.Color;
import java.io.IOException;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SkinningField {
    final int redVal = (200 << 16) | (0 << 8) | 0;
    final int greenVal = (0 << 16) | (200 << 8) | 0;
    final int blueVal = (0 << 16) | (0 << 8) | 200;
    final int whiteVal = (255 << 16) | (255 << 8) | 255;
    final int blackVal = (0 << 16) | (0 << 8) | 0;
    final int threshold = 1000;
    private ArrayList<ObjSkin> pListObjSkin;
    ArrayList<point> pList;


    private int[][] redPixel;
    private int[][] bluePixel;
    private int[][] greenPixel;
    private int[][] matrixBW;

    private int width;
    private int height;

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    public SkinningField(int redPx[][], int greenPx[][], int bluePx[][], int matrixBW[][], int height, int width){
        this.pListObjSkin = new ArrayList<>();
        // init
        this.redPixel = new int[height][];
        for(int i=0;i<height;i++){
            this.redPixel[i] = new int[width];
        }

        this.greenPixel = new int[height][];
        for(int i=0;i<height;i++){
            this.greenPixel[i] = new int[width];
        }

        this.bluePixel = new int[height][];
        for(int i=0;i<height;i++){
            this.bluePixel[i] = new int[width];
        }

        this.matrixBW = new int[height][];
        for(int i=0;i<height;i++){
            this.matrixBW[i] = new int[width];
        }

        // add value
        this.height = height;
        this.width = width;

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                this.redPixel[i][j] = redPx[i][j];
            }
        }

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                this.greenPixel[i][j] = greenPx[i][j];
            }
        }

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                this.bluePixel[i][j] = bluePx[i][j];
            }
        }

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                this.matrixBW[i][j] = matrixBW[i][j];
            }
        }

        setObjectSkin();
    }

    public int[][] getGreenPixel() {
        return greenPixel;
    }

    public int[][] getBluePixel() {
        return bluePixel;
    }

    public int[][] getRedPixel() {
        return redPixel;
    }

    public int[][] getMatrixBW(){
        return matrixBW;
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

    public void copyToMatrixRGB(int matrix[][]){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                int col = ( (redPixel[i][j] << 16) | (greenPixel[i][j] << 8) | bluePixel[i][j] );
                matrix[i][j] = col;
            }
        }
    }

    // int counter = 0;
    public void getDeletedPointSkin(int px, int py){
        // pList harus udah clear dipanggilan pertama
        // if ((px >= 0 && px < this.height) && (py >= 0 && py < this.width)){
        
            if (this.matrixBW[px][py] == whiteVal){
                pList.add(new point(px, py));
                // System.out.println(pList.size());
                this.matrixBW[px][py] = blackVal;
                for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                    int dx = px + iterationDirections[i][1];
                    int dy = py + iterationDirections[i][0];
                    if ((dx >= 0 && dx < this.height) && (dy >= 0 && dy < this.width)){
                        getDeletedPointSkin(dx, dy);
                        // this.matrixBW[dx][dy] = blackVal;
                    }
                }
            }
        
    }

    public int[][] getMatrixFromListPoint(ArrayList<point> pList){
        int[][] matrixBWTmp;
        // auto assign 0
        matrixBWTmp = new int[height][];
        for(int i=0;i<height;i++){
            matrixBWTmp[i] = new int[width];
        }

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                matrixBWTmp[i][j] = blackVal;
            }
        }

        for(point p: pList){
            matrixBWTmp[p.x][p.y] = whiteVal;
        }

        return matrixBWTmp;
    }

    public void setObjectSkin(){
        this.pListObjSkin.clear();

        int[][] matBWTmp = new int[this.height][];
        for(int i = 0;i<this.height;i++){
            matBWTmp[i] = new int[this.width];
        }

        copyToMatrix(matBWTmp);
        // ArrayList<point> toDelete = new ArrayList<>();
        System.out.println(height + "  " + width);
        
            for(int i = 0;i<this.height;i++){
                for(int j = 0;j<this.width;j++){
                    int x = i;
                    int y = j;
                    if (this.matrixBW[x][y] == whiteVal){
                        // toDelete.clear();
                        pList = new ArrayList<>();
                        getDeletedPointSkin(x, y);
                        if(pList.size() > threshold){
                            System.out.println("new object");

                            ObjSkin obj = new ObjSkin(pList, this.height, this.width);
                            this.pListObjSkin.add(obj);
                        }
                        
                    }
                }
            }
        

        setMatrixBW(matBWTmp);
    }

    public int[][] getMarkedObjectToBW(){
        int[][] matBWTmp = new int[this.height][];
        for(int i = 0;i<this.height;i++){
            matBWTmp[i] = new int[this.width];
        }

        copyToMatrix(matBWTmp);

        for(ObjSkin p: this.pListObjSkin){
            if(p.IsFaceDetected()){
                System.out.println("marked");
                System.out.println("(Xmax,Ymax) : (" + p.Xmax + ", " + p.Ymax + ")");                
                System.out.println("(Xmin,Ymin) : (" + p.Xmin + ", " + p.Ymin + ")");                
                boundingObject(matBWTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
                // per component
                ArrayList<Component> pComp = p.getComponentList();
                System.out.println("Ada Component : " + pComp.size());
                for(Component c: pComp){
                    boundingObject(matBWTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                }
            }else{
                boundingObject(matBWTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
            }
        }
        return matBWTmp;
    }

    public void boundingObject(int mat[][], int val, int xmax, int xmin, int ymax, int ymin){
        for(int i = xmin; i < xmax+1;i++){
            for(int j = ymin; j< ymax+1;j++){
                if((i == xmin || i == xmax ) || (j == ymin || j == ymax) ){
                    mat[i][j] = val;
                }
            }
        }
    }

    // public int[][] getmarkedObjectToRGBvalue(){
    //     int[][] matRGBTmp = new int[this.height][];
    //     for(int i = 0;i < this.height;i++){
    //         matRGBTmp[i] = new int[this.width];
    //     }

    //     copyToMatrixRGB(matRGBTmp);

    //     for(ObjSkin p: this.pListObjSkin){
    //         if(p.IsFaceDetected()){
    //             boundingObject(matRGBTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
    //             // per component
    //             ArrayList<Component> pComp = p.getComponentList();
                
    //             for(Component c: pComp){
    //                 boundingObject(matRGBTmp, redVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
    //             }
    //         }else{
    //             System.out.println("marked");
    //             boundingObject(matRGBTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
    //         }
    //     }
    //     return matRGBTmp;
    // }


}

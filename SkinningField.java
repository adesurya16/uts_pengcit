

// import android.graphics.Color;
import java.io.IOException;
import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

public class SkinningField {
    final int redVal = (200 << 16) | (0 << 8) | 0;
    final int greenVal = (0 << 16) | (200 << 8) | 0;
    final int blueVal = (0 << 16) | (0 << 8) | 200;
    final int whiteVal = (255 << 16) | (255 << 8) | 255;
    final int blackVal = (0 << 16) | (0 << 8) | 0;
    final int threshold = 1000;
    private ArrayList<ObjSkin> pListObjSkin;
    private ArrayList<point> pList;
    private Stack<point> floodFillStack;

    private int[][] redPixel;
    private int[][] bluePixel;
    private int[][] greenPixel;
    private int[][] matrixBW;

    private int width;
    private int height;

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    public SkinningField(int redPx[][], int greenPx[][], int bluePx[][], int matrixBW[][], int height, int width){
        this.pListObjSkin = new ArrayList<>();
        this.floodFillStack = new Stack<>();
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
        System.out.println("Field : (w x h) " + this.width + " x " + this.height);
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

    int getGSUsingLuminosty(int i,int j){
        return (int)(0.3 * redPixel[i][j] + 0.59 * greenPixel[i][j] + 0.11 * bluePixel[i][j]);
    }

    int getGSUsingAvg(int i,int j){
        return (int)( (redPixel[i][j] + greenPixel[i][j] + bluePixel[i][j]) / 3);
    }

    public void toGrayScaleMatrix(int matrix[][]){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){

                // pake weight method or luminosty method
                int val = getGSUsingAvg(i, j);
                int col = ( (val << 16) | (val << 8) | val);
                matrix[i][j] = col;
            }
        }
    }

    public void toGrayScaleMatrixValue(int matrix[][]){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){

                // pake weight method or luminosty method
                int val = getGSUsingAvg(i, j);
                matrix[i][j] = val;
            }
        }
    }

    public void getDeletedPointSkinUsingStack(int px, int py){
        this.floodFillStack.push(new point(px, py));
        while(!floodFillStack.empty()){
            point p = this.floodFillStack.peek();
            this.floodFillStack.pop();
            if (this.matrixBW[p.x][p.y] == whiteVal){
                // System.out.println(p.x + ", " + p.y);
                pList.add(p);
                this.matrixBW[p.x][p.y] = blackVal;
                for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                    int dx = p.x + iterationDirections[i][1];
                    int dy = p.y + iterationDirections[i][0];
                    if ((dx >= 0 && dx < this.height) && (dy >= 0 && dy < this.width)){
                        this.floodFillStack.push(new point(dx, dy));
                        // this.matrixBW[dx][dy] = blackVal;
                    }
                }
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
        // System.out.println(height + "  " + width);
        int obji = 0;
            for(int i = 0;i<this.height;i++){
                for(int j = 0;j<this.width;j++){
                    int x = i;
                    int y = j;
                    if (this.matrixBW[x][y] == whiteVal){
                        // toDelete.clear();
                        pList = new ArrayList<>();
                        getDeletedPointSkinUsingStack(x, y);
                        if(pList.size() > threshold){
                            obji++;
                            // System.out.println("new object");
                            System.out.println("Skin Object " + obji + " size : " + pList.size());
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
                // System.out.println("marked");
                // System.out.println("(Xmax,Ymax) : (" + p.Xmax + ", " + p.Ymax + ")");                
                // System.out.println("(Xmin,Ymin) : (" + p.Xmin + ", " + p.Ymin + ")");                
                boundingObject(matBWTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
                // per component
                ArrayList<Component> pComp = p.getComponentList();
                // System.out.println("Ada Component : " + pComp.size());
                for(Component c: pComp){
                    if (c.isEye) System.out.println("mata");
                    if (c.isMouth) System.out.println("mulut");
                    if (c.isEye || c.isMouth) boundingObject(matBWTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                    // if (c.mouth) boundingObject(matBWTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                }
            }else{
                // boundingObject(matBWTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
            }
        }
        return matBWTmp;
    }

    public void boundingObject(int mat[][], int val, int xmax, int xmin, int ymax, int ymin){
        if (val == redVal){
            int sel = (xmax-xmin);
            ymax = ymin + sel;
        }
        // System.out.println(xmax + ", " + xmin);
        // System.out.println(ymax + ", " + ymin);

        for(int i = xmin; i < xmax;i++){
            for(int j = ymin; j< ymax;j++){
                if((i == xmin || i == xmax - 1 ) || (j == ymin || j == ymax - 1) ){
                    mat[i][j] = val;
                }
            }
        }
    }

    public int[][] getmarkedObjectToRGBvalue(){
        int[][] matRGBTmp = new int[this.height][];
        for(int i = 0;i < this.height;i++){
            matRGBTmp[i] = new int[this.width];
        }

        copyToMatrixRGB(matRGBTmp);

        for(ObjSkin p: this.pListObjSkin){
            if(p.IsFaceDetected()){
                boundingObject(matRGBTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
                // System.out.println("(Xmax,Ymax) : (" + p.Xmax + ", " + p.Ymax + ")");                
                // System.out.println("(Xmin,Ymin) : (" + p.Xmin + ", " + p.Ymin + ")"); 
                // per component
                ArrayList<Component> pComp = p.getComponentList();
                
                for(Component c: pComp){
                    if (c.isEye) System.out.println("mata");
                    if (c.isMouth) System.out.println("mulut");
                    if (c.isEye || c.isMouth) boundingObject(matRGBTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                    // if (c.isMouth) boundingObject(matRGBTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                }
            }else{
                System.out.println("not a face");
                // boundingObject(matRGBTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
            }
        }
        return matRGBTmp;
    }

    public int[][] getmarkedObjectToRGBvalueGrayscale(){
        int[][] matRGBTmp = new int[this.height][];
        for(int i = 0;i < this.height;i++){
            matRGBTmp[i] = new int[this.width];
        }

        // copyToMatrixRGB(matRGBTmp);
        toGrayScaleMatrix(matRGBTmp);

        for(ObjSkin p: this.pListObjSkin){
            if(p.IsFaceDetected()){
                boundingObject(matRGBTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
                // System.out.println("(Xmax,Ymax) : (" + p.Xmax + ", " + p.Ymax + ")");                
                // System.out.println("(Xmin,Ymin) : (" + p.Xmin + ", " + p.Ymin + ")"); 
                // per component
                ArrayList<Component> pComp = p.getComponentList();
                
                for(Component c: pComp){
                    if (c.isEye || c.isMouth) boundingObject(matRGBTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                }
            }else{
                System.out.println("not a face");
                // boundingObject(matRGBTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
            }
        }
        return matRGBTmp;
    }


    public int[][] getBWmatrixThreshold(){
        int[][] matRGBTmp = new int[this.height][];
        for(int i = 0;i < this.height;i++){
            matRGBTmp[i] = new int[this.width];
        }
        int[][] matBWTmp = new int[this.height][];
        for(int i = 0;i<this.height;i++){
            matBWTmp[i] = new int[this.width];
        }

        copyToMatrix(matBWTmp);

        // copyToMatrixRGB(matRGBTmp);
        toGrayScaleMatrixValue(matRGBTmp);
        for(ObjSkin p: this.pListObjSkin){
            if(p.IsFaceDetected()){
                OtsuThresholderSkin ot = new OtsuThresholderSkin(matRGBTmp, height, width, p.Xmin, p.Xmax, p.Ymin, p.Ymax);
                ot.doThresholding();
                System.out.println("Threshold : " + ot.getThresholdResult());
                ArrayList<point> pList = ot.getResultMatrixthresholding(p.Xmin, p.Xmax, p.Ymin, p.Ymax);
                for(int i = p.Xmin;i < p.Xmax;i++){
                    for(int j = p.Ymin; j < p.Ymax;j++){
                        matBWTmp[i][j] = blackVal;
                    }
                }
                for(point pp:pList){
                    matBWTmp[pp.x][pp.y] = whiteVal;
                }
                boundingObject(matBWTmp, redVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
                // System.out.println("(Xmax,Ymax) : (" + p.Xmax + ", " + p.Ymax + ")");                
                // System.out.println("(Xmin,Ymin) : (" + p.Xmin + ", " + p.Ymin + ")"); 
                // per component
                // ArrayList<Component> pComp = p.getComponentList();
                
                // for(Component c: pComp){
                //     if (c.isEye) boundingObject(matRGBTmp, blueVal, c.Xmax, c.Xmin, c.Ymax, c.Ymin);
                // }
            }else{
                // System.out.println("not a face");
                // boundingObject(matRGBTmp, blueVal, p.Xmax, p.Xmin, p.Ymax, p.Ymin);
            }
        }
        return matBWTmp;
    }
}

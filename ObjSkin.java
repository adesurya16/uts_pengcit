

// import android.graphics.Color;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;


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
    Stack<point> floodFillStack;
    public Component LeftEye;
    public Component RightEye;
    boolean isEyeFound;

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
        // System.out.println("obj ctor");
        this.LeftEye = null;
        this.RightEye = null;
        isEyeFound = false;
        this.floodFillStack = new Stack<>();
        this.pAreaSkinList = new ArrayList<>();
        this.componentList = new ArrayList<>();
        this.matrixBW = new int[height][];
        for(int i = 0; i < height; i++){
            this.matrixBW[i] = new int[width];
        }

        this.pAreaSkinList.addAll(pList);

        this.height = height;
        this.width = width;
        initMatrixFromList(pList);

        getBoundedPoint();
        detectHoleToList();
        findEyes();
        findMouth();
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
        // System.out.println("init matrix");
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

        if (x_start - 1 >= 0){
            x_start -= 1;
        }

        if (x_start + 1 < this.height){
            x_end += 1;            
        }

        if (y_start - 1 >= 0){
            y_start -= 1;
        }
        if (y_start + 1 < this.width){
            y_end += 1;
        }

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
        for(int i = this.Xmin;i<this.Xmax;i++){
            for(int j = this.Ymin;j < this.Ymax;j++){
                int x = i;
                int y = j;
                if (this.matrixBW[x][y] == blackVal){
                    // System.out.println("current hole");
                    isHole = true;
                    pList = new ArrayList<>();
                    getDeletedPointSkinComponentUsingStack(x, y);
                    if (isHole) {
                        this.componentList.add(new Component(pList, this.height, this.width));
                        // System.out.println("NEW component");
                    }else {
                        // System.out.println("NOT component");
                    }
                }
            }
        }
        System.out.println("banyak component di skin : " + this.componentList.size());
        setMatrixBW(matBWTmp);
    }

    public void getDeletedPointSkinComponentUsingStack(int px, int py){
        this.floodFillStack.push(new point(px, py));
        while(!floodFillStack.empty()){
            point p = this.floodFillStack.peek();
            this.floodFillStack.pop();
            if (this.matrixBW[p.x][p.y] == blackVal){
                pList.add(p);
                this.matrixBW[p.x][p.y] = whiteVal;
                for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                    int dx = p.x + iterationDirections[i][1];
                    int dy = p.y + iterationDirections[i][0];
                    if ((dx >= this.Xmin && dx < this.Xmax) && (dy >= this.Ymin && dy < this.Ymax)){
                        this.floodFillStack.push(new point(dx, dy));
                        // this.matrixBW[dx][dy] = blackVal;
                    }else{
                        isHole = false;
                    }
                }
            }
        }
    }

    public void getDeletedPointSkinComponent(int px, int py){
        // pList harus udah clear dipanggilan pertama
        
            if (this.matrixBW[px][py] == blackVal){
                pList.add(new point(px, py));
                this.matrixBW[px][py] = whiteVal;
                for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                    int dx = px + iterationDirections[i][1];
                    int dy = py + iterationDirections[i][0];
                    if ((dx >= this.Xmin && dx < this.Xmax) && (dy >= this.Ymin && dy < this.Ymax)){
                        getDeletedPointSkinComponent(dx, dy);
                    }else {
                        isHole = false;
                    }
                }
            }
        
    }

    public void findEyes(){
        // System.out.println("eye detection");
        int jarakmata = (int)(this.Xmax - this.Xmin) / 2;
        // System.out.println(this.Xmax + ", " + this.Xmin);
        // System.out.println(this.Ymax + ", " + this.Ymin);
        System.out.println("jarakmata : " + jarakmata);
        
        ArrayList<Component> pList2 = new ArrayList<>();
        int batas = this.Ymin + ((this.Ymax - this.Ymin) / 3);
        for(Component p : this.componentList){
            if (p.Ymax < batas){
                pList2.add(p);
            }
        }
        System.out.println("calon mata " + pList2.size());

        ArrayList<Component> pList2Sorted = new ArrayList<>();
        while (pList2.size() > 0){
            int jmax = -1;
            Component pMax = null;
            int j = 0;
            for(Component p : pList2){
                if (jmax == -1){
                    pMax = p;
                    jmax = j;
                }else if(p.Ymax > pMax.Ymax){
                    pMax = p;
                    jmax = j;
                }
                j++;
            }
            pList2.remove(jmax);
            pList2Sorted.add(pMax);
        }
        // System.out.println("sorted "pList2Sorted.size());

        for(int i=0;i<pList2Sorted.size()-1 ;i++){
            for(int j = i;j < pList2Sorted.size();j++){
            // pList2Sorted.get(i).isEye = true;
            // int idx1 = this.componentList.indexOf(pList2Sorted.get(i));
            // this.componentList.get(idx1).isEye = true;

            // bisa tambahin chain code
                Component c1 = pList2Sorted.get(i);
                Component c2 = pList2Sorted.get(j + 1);
                
                if( (Math.abs(c1.Ymax - c2.Ymax) < 20) && (Math.abs(c1.pAreaComponent.size() - c2.pAreaComponent.size()) < 100) && ((c1.Xmax < c2.Xmin) || (c1.Xmin > c2.Xmax)) 
                ){
                    if (c1.Xmax < c2.Xmin)
                    {
                        this.LeftEye = c1;
                        this.RightEye = c2;
                    }else{
                        this.LeftEye = c2;
                        this.RightEye = c1;
                    }
                    int mid = Xmin + ((Xmax - Xmin) / 2);
                    this.LeftEye.printBounded();
                    this.RightEye.printBounded();
                    if( Math.abs(this.RightEye.Xmin - this.LeftEye.Xmin) < jarakmata && (this.LeftEye.Xmin < mid && this.LeftEye.Xmax < mid) && (this.RightEye.Xmin > mid && this.RightEye.Xmax > mid)){
                        // && (Math.abs(c1.Xmax - c2.Xmin) < jarakmata) || (Math.abs(c1.Xmin - c2.Xmax) < jarakmata)
                    c1.isEye = true;
                    // System.out.println(this.componentList.indexOf(pList2Sorted.get(i)));
                    c2.isEye = true;
                    isEyeFound = true;
                    // System.out.println(this.componentList.indexOf(pList2Sorted.get(i + 1)));
                        
                    }else{
                        this.LeftEye = null;
                        this.RightEye = null;
                    }

                    
                    

                    break;
                }
            }
            if (isEyeFound) break;
        }
    }

    public void findMouth(){
        if (this.isEyeFound){
            
            int Xmax = this.Xmax;
        int Xmin = this.Xmin;
        int Ymax = this.Ymax;
        int Ymin = this.Ymin;
        int sel = Xmax-Xmin;
        Ymax = Ymin + sel;
        int jarakatas = 0;
        // jarak min y 2 mata
        int selYmata = 0;
        if (this.LeftEye.Ymin < this.RightEye.Ymin){
            selYmata = this.LeftEye.Ymin - Ymin;
        }else{
            selYmata = this.RightEye.Ymin - Ymin;
        }
        // dapet 1
        int YmaxMouth = Ymax - selYmata + ((LeftEye.Ymax - LeftEye.Ymin)*2);

        // cari center mata
        int centerMataKiri = this.LeftEye.Xmin + ((this.LeftEye.Xmax - this.LeftEye.Xmin) / 2);
        int centerMataKanan = this.RightEye.Xmin + ((this.RightEye.Xmax - this.RightEye.Xmin) / 2);
        int jarakcentermata = centerMataKanan - centerMataKiri;

        // dapet 2
        int XminMouth = centerMataKiri;
        System.out.println("xminmouth : " + XminMouth);
        int XmaxMouth = centerMataKanan;

        // dapet 1
        int YminMouth = YmaxMouth - ((LeftEye.Ymax - LeftEye.Ymin)*4);
        ArrayList<point> pListMouth = new ArrayList<>();
        for(int i = XminMouth;i < XmaxMouth;i++){
            for(int j = YminMouth;j < YmaxMouth;j++){
                // System.out.println(i + ", " + j);
                pListMouth.add(new point(i,j));
            }
        }
        System.out.println("max min mouth");
        System.out.println(XmaxMouth + ", " + XminMouth);
        System.out.println(YmaxMouth + ", " + YminMouth);

        Component cmouth = new Component(pListMouth, this.height, this.width);
        cmouth.isMouth = true;
        // cmouth.printBounded();
        this.componentList.add(cmouth);
        }else{
            System.out.println("tidak ada mata");
        }
    }



    public boolean IsFaceDetected(){
        int isEye = 0;
        int isMouth = 0;
        int isNose = 0; 
        for(Component p : this.componentList){
            if (p.isEye) isEye++;
            if (p.isMouth) isMouth++;
            if (p.isNose) isNose++;
        }
        if(isEye == 2 && isMouth == 1){
            System.out.println("Ini adalah muka");
            return true;
        }else return false;
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

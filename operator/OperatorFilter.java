import java.awt.Color;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.util.*;

// 3 x 3
public class OperatorFilter{

    private int width;
    private int height;
    public Color pixImage[][];
    public int pixImageGS[][]; 
    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};


    public OperatorFilter(BufferedImage img, int height, int width){
        this.width = width;
        this.height = height;

        this.pixImage = new Color[height][];
        this.pixImageGS = new int[height][];
        for(int i=0;i<height;i++){
            this.pixImage[i] = new Color[width];
            this.pixImageGS[i] = new int[width];
            
        }
        setColorPix(img);
    }

    public OperatorFilter(Color[][] pix, int height, int width){
        this.width = width;
        this.height = height;
        this.pixImage = new Color[height][];
        this.pixImageGS = new int[height][];
        for(int i=0;i<height;i++){
            this.pixImage[i] = new Color[width];
            this.pixImageGS[i] = new int[width];
            
        }
        setColorPix(pix);
    }

    public Color[][] getPixImage(){
        return this.pixImage;
    }

    public int[][] getPixImageGS(){
        return this.pixImageGS;
    }

    public static BufferedImage readImage(String file){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(file));
        }catch(IOException e){
            e.printStackTrace();
        }
        return img;
    }

    public void initMatrixGS(){
        for(int i = 0;i < this.height;i++){
            for(int j = 0;j < this.width;j++){
                Color c = this.pixImage[i][j];
                this.pixImageGS[i][j] = (int) (c.getRed() + c.getGreen() + c.getAlpha()) / 3; 
            }
        }
    }

    public void setColorPix(BufferedImage img){
        // harus sama dengan height dan width yang diset awal
        // int width = img.getHeight();
        // int height = img.getWidth();

        // this.pix = new Color[height][];
        for(int i = 0;i < this.height;i++){
            // pix[i] = new Color[width];
            for(int j = 0;j < this.width;j++){
                
                Color c = new Color(img.getRGB(i, j));
                this.pixImage[i][j] = c;
            }
        }
    }

    public void setColorPix(Color[][] pix){
        // harus sama dengan height dan width yang diset awal
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                // if(this.pixImage[i][j] != null && this.pixImage[i][j] != pix[i][j]){
                //     System.out.println("beda");
                // }
                this.pixImage[i][j] = pix[i][j];
            }
        }
    }

    public void setColorPixGS(int[][] pix){
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                // if(this.pixImage[i][j] != null && this.pixImage[i][j] != pix[i][j]){
                //     System.out.println("beda");
                // }
                this.pixImageGS[i][j] = pix[i][j];
            }
        }
    }

    public void toFileImageGS(String file){
        try{
            BufferedImage img = new BufferedImage(this.height, this.width, BufferedImage.TYPE_INT_RGB);
            File f = new File(file);
            for(int i = 0;i< this.width;i++){
                for(int j = 0;j<this.height;j++){
                    int c = this.pixImageGS[j][i];
                    int col = (c << 16) | (c << 8) | c;
                    img.setRGB(j, i, col); 
                }
            }
            ImageIO.write(img, "PNG", f);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void toFileImage(String file){
        try{
            BufferedImage img = new BufferedImage(this.height, this.width, BufferedImage.TYPE_INT_RGB);
            File f = new File(file);
            for(int i = 0;i< this.width;i++){
                for(int j = 0;j<this.height;j++){
                    Color c = this.pixImage[j][i];
                    int col = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
                    img.setRGB(j, i, col); 
                }
            }
            ImageIO.write(img, "PNG", f);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public Color[][] medianOperation(){
        Color[][] res = new Color[this.height][];
        for(int i=0;i<this.height;i++){
            res[i] = new Color[this.width];
            for(int j=0;j<this.width;j++){
                Color c = this.pixImage[i][j];
                int ii = i;
                int jj = j;
                ArrayList<Integer> intListRed = new ArrayList<>();
                intListRed.add(c.getRed());
                ArrayList<Integer> intListGreen = new ArrayList<>();
                intListGreen.add(c.getGreen());
                ArrayList<Integer> intListBlue = new ArrayList<>();
                intListBlue.add(c.getBlue());
                for(int k = 0;k < this.iterationDirections.length;k++){    
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }

                    Color cNeighboor = this.pixImage[ii][jj];
                    intListRed.add(c.getRed());
                    intListGreen.add(c.getGreen());
                    intListBlue.add(c.getBlue());
                }

                int medRed = getMedian(intListRed);
                int medGreen = getMedian(intListGreen);
                int medBlue = getMedian(intListBlue);
                Color newColor = new Color(medRed, medGreen, medBlue);
                res[i][j] = newColor;
            }
        }
        return res;
    }

    // JANGAN LUPA INIT DULU
    public int[][] medianOperationGS(){
        int [][] res = new int[this.height][];
        for(int i=0;i<this.height;i++){
            res[i] = new int[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                ArrayList<Integer> intList = new ArrayList<>();
                intList.add(this.pixImageGS[i][j]);
                for(int k = 0;k < this.iterationDirections.length;k++){    
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }

                    intList.add(this.pixImageGS[ii][jj]);
                }
                int med = getMedian(intList);
                res[i][j] = med;
            }
        }
        return res;
    }

    /*
    P9 P2 P3
    P8 P1 P4
    P7 P6 P5
    Dir
    */

    public Color[][] gradientOperation(){
        int maxLength = 3;
        Color[][] res = new Color[this.height][];
        int[][] kernelRed = new int[maxLength][];
        int[][] kernelGreen = new int[maxLength][];
        int[][] kernelBlue = new int[maxLength][];
        for(int i = 0;i<maxLength;i++){
            kernelRed[i] = new int[maxLength];
            kernelGreen[i] = new int[maxLength];
            kernelBlue[i] = new int[maxLength];
        }

        for(int i=0;i<this.height;i++){
            res[i] = new Color[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                int mid = maxLength / 2;
                kernelRed[mid][mid] = this.pixImage[i][j].getRed();
                kernelGreen[mid][mid] = this.pixImage[i][j].getGreen();
                kernelBlue[mid][mid] = this.pixImage[i][j].getBlue();

                for(int k = 0;k < this.iterationDirections.length;k++){
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }
                    Color cNeighboor = this.pixImage[ii][jj];
                    kernelRed[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getRed(); 
                    kernelGreen[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getGreen();
                    kernelBlue[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getBlue(); 
                }

                int gradRed = getGradientMax(kernelRed);
                int gradGreen = getGradientMax(kernelGreen);
                int gradBlue = getGradientMax(kernelBlue);
                Color newColor = new Color(gradRed, gradGreen, gradBlue);
                res[i][j] = newColor;
            }
        }
        return res;
    }

    public int[][] gradientOperationGS(){
        int maxLength = 3;
        int[][] res = new int[this.height][];
        int[][] kernel = new int[maxLength][];
        for(int i = 0;i<maxLength;i++){
            kernel[i] = new int[maxLength];
        }
        for(int i = 0;i< this.height;i++){
            res[i] = new int[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                int mid = maxLength / 2;
                kernel[i][j] = this.pixImageGS[i][j];
                for(int k = 0;k < this.iterationDirections.length;k++){
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }
                    kernel[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = this.pixImageGS[ii][jj];
                }
                int grad = getGradientMax(kernel);
                res[i][j] = grad;
            }
        }
        return res;
    }

    public Color[][] differenceOperation(){
        int maxLength = 3;
        Color[][] res = new Color[this.height][];
        int[][] kernelRed = new int[maxLength][];
        int[][] kernelGreen = new int[maxLength][];
        int[][] kernelBlue = new int[maxLength][];
        for(int i = 0;i<maxLength;i++){
            kernelRed[i] = new int[maxLength];
            kernelGreen[i] = new int[maxLength];
            kernelBlue[i] = new int[maxLength];
        }

        for(int i=0;i<this.height;i++){
            res[i] = new Color[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                int mid = maxLength / 2;
                kernelRed[mid][mid] = this.pixImage[i][j].getRed();
                kernelGreen[mid][mid] = this.pixImage[i][j].getGreen();
                kernelBlue[mid][mid] = this.pixImage[i][j].getBlue();

                for(int k = 0;k < this.iterationDirections.length;k++){
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }
                    Color cNeighboor = this.pixImage[ii][jj];
                    kernelRed[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getRed(); 
                    kernelGreen[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getGreen();
                    kernelBlue[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = cNeighboor.getBlue(); 
                }
                int diffRed = getDifferenceMax(kernelRed);
                int diffGreen = getDifferenceMax(kernelGreen);
                int diffBlue = getDifferenceMax(kernelBlue);
                Color newColor = new Color(diffRed, diffGreen, diffBlue);
                res[i][j] = newColor;
            }
        }
        return res;
    }

    public int[][] differenceOperationGS(){
        int maxLength = 3;
        int[][] res = new int[this.height][];
        int[][] kernel = new int[maxLength][];
        for(int i = 0;i<maxLength;i++){
            kernel[i] = new int[maxLength];
        }
        for(int i = 0;i< this.height;i++){
            res[i] = new int[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                int mid = maxLength / 2;
                kernel[i][j] = this.pixImageGS[i][j];
                for(int k = 0;k < this.iterationDirections.length;k++){
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }
                    kernel[mid + this.iterationDirections[k][1]][mid + this.iterationDirections[k][0]] = this.pixImageGS[ii][jj];
                }
                int diff = getDifferenceMax(kernel);
                res[i][j] = diff;
            }
        }
        return res;
    }

    public Color[][] meanOperation(){
        Color[][] res = new Color[this.height][];
        for(int i=0;i<this.height;i++){
            res[i] = new Color[this.width];
            for(int j=0;j<this.width;j++){
                Color c = this.pixImage[i][j];
                int ii = i;
                int jj = j;
                ArrayList<Integer> intListRed = new ArrayList<>();
                intListRed.add(c.getRed());
                ArrayList<Integer> intListGreen = new ArrayList<>();
                intListGreen.add(c.getGreen());
                ArrayList<Integer> intListBlue = new ArrayList<>();
                intListBlue.add(c.getBlue());
                for(int k = 0;k < this.iterationDirections.length;k++){
                    
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }

                    Color cNeighboor = this.pixImage[ii][jj];
                    intListRed.add(c.getRed());
                    intListGreen.add(c.getGreen());
                    intListBlue.add(c.getBlue());
                }

                int meanRed = getMean(intListRed);
                int meanGreen = getMean(intListGreen);
                int meanBlue = getMean(intListBlue);
                Color newColor = new Color(meanRed, meanGreen, meanBlue);
                res[i][j] = newColor;
            }
        }
        return res;
    }

    public int[][] meanOperationGS(){
        int [][] res = new int[this.height][];
        for(int i=0;i<this.height;i++){
            res[i] = new int[this.width];
            for(int j=0;j<this.width;j++){
                int ii = i;
                int jj = j;
                ArrayList<Integer> intList = new ArrayList<>();
                intList.add(this.pixImageGS[i][j]);
                for(int k = 0;k < this.iterationDirections.length;k++){    
                    if(ii + this.iterationDirections[k][1] >= 0 && ii + this.iterationDirections[k][1] < this.height){
                        ii += this.iterationDirections[k][1];
                    }

                    if(jj + this.iterationDirections[k][0] >= 0 && jj + this.iterationDirections[k][0] < this.width){
                        jj += this.iterationDirections[k][0];
                    }

                    intList.add(this.pixImageGS[ii][jj]);
                }
                int mean = getMean(intList);
                res[i][j] = mean;
            }
        }
        return res;
    }

    public int getMedian(ArrayList<Integer> intList){
        Collections.sort(intList);
        // 3 x 3
        int size = intList.size();
        if(size % 2 == 1){
            return intList.get(size/2 + 1);
        }else{
            return (intList.get(size/2) + intList.get(size/2 + 1)) / 2;
        }
    }

    public int getMean(ArrayList<Integer> intList){
        int sum = 0;
        for(int pInt : intList){
            sum += pInt;
        }
        return sum / intList.size();
    }

    public int getGradientMax(int[][] pix){
        // int maxLength = 3;
        ArrayList<Integer> gList = new ArrayList<>();
        int max = Math.abs(pix[0][0] - pix[2][2]);

        gList.add( Math.abs(pix[0][1] - pix[2][1]) ); 
        gList.add( Math.abs(pix[0][2] - pix[2][0]) );
        gList.add( Math.abs(pix[1][0] - pix[1][2]) );
        for(int px : gList){
            if (px > max){
                max = px;
            }
        } 
        return max;
    }

    public int getDifferenceMax(int[][] pix){
        ArrayList<Integer> dList = new ArrayList<>();
        int max = Math.abs(pix[0][0] - pix[1][1]);
        dList.add(Math.abs(pix[0][1] - pix[1][1]));
        dList.add(Math.abs(pix[0][2] - pix[1][1]));
        dList.add(Math.abs(pix[1][0] - pix[1][1]));
        dList.add(Math.abs(pix[1][2] - pix[1][1]));
        dList.add(Math.abs(pix[2][0] - pix[1][1]));
        dList.add(Math.abs(pix[2][1] - pix[1][1]));
        dList.add(Math.abs(pix[2][2] - pix[1][1]));

        for(int px : dList){
            if (px > max){
                max = px;
            }
        } 
        return max;
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();        
        BufferedImage bI = OperatorFilter.readImage(s + ".png");
        OperatorFilter op = new OperatorFilter(bI, bI.getWidth(), bI.getHeight());
        // op.initMatrixGS();
        op.setColorPix(op.medianOperation());
        op.toFileImage(s + "OutMedian" + ".png");
        // op.setColorPix(op.gradientOperation());
        // op.toFileImage(s + "outGradient" + ".png");
        // op.setColorPix(op.differenceOperation());
        // op.toFileImage(s + "outDifference" + ".png");
        op.setColorPix(op.meanOperation());
        op.toFileImage(s + "outMean" + ".png");
        // op.toFileImageGS(s + "OutGrey" + ".png");
    }
}
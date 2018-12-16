import java.awt.Point;
import java.util.ArrayList;

public class OtsuThresholderSkin{
    public final int MAX_COLOR = 256;

    public int height;
    public int width;
    public double sum; // sum of pixel value
    public int length;
    public int grayScalePixel[][];
    public int histogramGS[];
    public int thresholdResult;

    public OtsuThresholderSkin(int matrixGrayScale[][], int height, int width, int Xmin, int Xmax, int Ymin, int Ymax){
        this.height = height;
        this.width = width;

        this.grayScalePixel = new int[height][];
        for(int i = 0;i < height;i++){
            this.grayScalePixel[i] = new int[width];
        }

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                this.grayScalePixel[i][j] = matrixGrayScale[i][j];
            }
        }

        this.histogramGS = new int[MAX_COLOR];
        for(int i = 0;i < MAX_COLOR;i++){
            this.histogramGS[i] = 0;
        }

        this.sum = 0;
        this.length = 0;
        for(int i = Xmin;i < Xmax;i++){
            for(int j = Ymin; j < Ymax;j++){
                this.length++;
                this.sum += matrixGrayScale[i][j];
                // System.out.println(matrixGrayScale[i][j]);
                this.histogramGS[matrixGrayScale[i][j]]++;
            }
        }

    }

    public OtsuThresholderSkin(int matrixGrayScale[][], int bluePx[][], int height, int width, ArrayList<point> listAreaSelected){ // hole juga termasuk
        this.height = height;
        this.width = width;

        this.grayScalePixel = new int[height][];
        for(int i = 0;i < height;i++){
            this.grayScalePixel[i] = new int[width];
        }

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                this.grayScalePixel[i][j] = matrixGrayScale[i][j];
            }
        }

        this.histogramGS = new int[MAX_COLOR];
        for(int i = 0;i < MAX_COLOR;i++){
            this.histogramGS[i] = 0;
        }

        this.length = listAreaSelected.size();
        this.sum = 0;
        for(point p:listAreaSelected){
            this.sum += matrixGrayScale[p.x][p.y];
            this.histogramGS[matrixGrayScale[p.x][p.y]]++;
        }
    }

    // int getGSUsingLuminosty(int i,int j){
    //     return (int)(0.3 * redPixel[i][j] + 0.59 * greenPixel[i][j] + 0.11 * bluePixel[i][j]);
    // }

    // int getGSUsingAvg(int i,int j){
    //     return (int)(redPixel[i][j] + greenPixel[i][j] + bluePixel[i][j] / 3);
    // }
    public void doThresholding(){
        double sumB = 0;
        int wB = 0;
        int wF = 0;

        double varMax = 0;
        thresholdResult = 0;

        for (int t=0 ; t<256 ; t++)
        {
            wB += histogramGS[t];					// Weight Background
            if (wB == 0) continue;

            wF = length - wB;						// Weight Foreground
            if (wF == 0) break;

            sumB += (double) (t * histogramGS[t]);

            double mB = sumB / wB;				// Mean Background
            double mF = (sum - sumB) / wF;		// Mean Foreground

            // Calculate Between Class Variance
            double varBetween = (double)wB * (double)wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                thresholdResult = t;
            }
        }

        // return thresholdResult;
    }

    public int getThresholdResult(){
        return thresholdResult;
    }

    // yang ada di list ini 1 selain itu nilainya di matrix 0 (berdasarkan otsu threshold)
    public ArrayList<point> getResultMatrixthresholding(int Xmin, int Xmax, int Ymin, int Ymax){
        ArrayList<point> pList = new ArrayList<>();
        for(int i = Xmin;i < Xmax;i++){
            for(int j = Ymin; j < Ymax;j++){
                if (this.grayScalePixel[i][j] >= thresholdResult){
                    pList.add(new point(i, j));
                }
            }
        }
        return pList;
    }

    public ArrayList<point> getResultMatrixthresholding(ArrayList<point> listAreaSelected){
        ArrayList<point> pList = new ArrayList<>();
        for(point p: listAreaSelected){
            if(this.grayScalePixel[p.x][p.y] >= thresholdResult){
                pList.add(new point(p.x, p.y));
            }
        }
        return pList;
    }
}
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.vision.VisionPipeline;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.lang.Math;



public class FindBall implements VisionPipeline {

    public static Mat output = new Mat();
    public static double Distance;
    public static double rotaton;
    public static boolean BallFound;
    public static float[] radius;
    static {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    } 

    /**
   * Overrides the process method to perform ball detection and processing on a given frame.
   * 
   * @param frame The input frame (Mat object) for ball detection and processing.
   */
    @Override
    public void process(Mat frame) {
      Scalar blueLower = new Scalar(98, 86, 43);
      Scalar blueUpper = new Scalar(128, 255, 255);

      Mat grayFrame = new Mat();
      frame.copyTo(output);

      //Applys filters;
      Imgproc.GaussianBlur(frame, grayFrame, new Size(11, 11), 0);
      Imgproc.cvtColor(grayFrame, grayFrame, Imgproc.COLOR_BGR2HSV);
      Core.inRange(grayFrame, blueLower, blueUpper, grayFrame);
        
      Mat hierarchy = new Mat();

      //Mat hierarchy = new Mat();
      List<MatOfPoint> contours = new ArrayList<>();
      Imgproc.findContours(grayFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

      boolean done = false;
      BallFound = false;

      List<MatOfPoint> viald_contours = new ArrayList<>();
      MatOfPoint highest_con = new MatOfPoint();
      MatOfPoint2f contoursPoly  = new MatOfPoint2f();
 
      if (contours.size() > 0) {
 
        //finds circles
        for(int i = 0; i < contours.size(); i++) {
          MatOfPoint2f c2f = new MatOfPoint2f(contours.get(i).toArray());
          double peri = Imgproc.arcLength(c2f, true);
          double chance = 4*Math.PI*(Imgproc.contourArea(contours.get(i))/(peri*peri));
          if (7.5 < Imgproc.contourArea(contours.get(i)) && chance > 0.80) {
            viald_contours.add(contours.get(i));
          }
        }
            //finds the largest circle
        if (viald_contours.size() > 0) {
          Double highest_size = Imgproc.contourArea(viald_contours.get(0));
          for(int i = 0; i < viald_contours.size(); i++) {
            Double holder = Imgproc.contourArea(viald_contours.get(i));
            if (highest_size <= holder) {
              highest_size = holder;
              highest_con = viald_contours.get(i);
              done = true;
            }
          }
          Point centers = new Point();
          radius = new float[1];
       
          // draw circle around detected object
          contoursPoly = new MatOfPoint2f();
          Imgproc.approxPolyDP(new MatOfPoint2f(highest_con.toArray()), contoursPoly, 3, true);
          Imgproc.minEnclosingCircle(contoursPoly, centers, radius);
          
          if (done == true && radius[0] > 5) {
            BallFound = true;
            Imgproc.circle(frame, centers, (int) radius[0], new Scalar(0, 255, 255), 2);
            frame.copyTo(output);

            Distance = Math.round((26*127.200035682)/(radius[0]*2));
            rotaton = (0.3125*centers.x);
          }
          else {
            BallFound = false;
          }
        }
      }
    }
  }

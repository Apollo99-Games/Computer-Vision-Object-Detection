package Main;
 
import java.util.ArrayList;
import java.util.List;
 
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.Math;
 
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
 
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.opencv.videoio.Videoio;
 
 
import org.opencv.core.Point;
 
public class FXController
{
    final double OBJECT_WIDTH = 3.0;
    final double FOCAL_LENGTH = 625.6;

    final double MIN_AREA = 500;
    final double ACCURACY = 0.8;

    private Scalar upperBallColour;
    private Scalar lowerBallColour;

    // UI Elements
    @FXML
    private Button cameraButton;
 
    @FXML
    private ImageView ImageBox;
 
    @FXML
    private Button DetectFace;
   
    @FXML
    private Button Importimage;
 
    @FXML
    private Button Openimg;
 
    @FXML
    private Text iacc;
 
    @FXML
    private Text raduis;
 
    @FXML
    private Text angle;


    // Camera
 
    private ScheduledExecutorService frameSchedule;
 
    private VideoCapture camera;
 
    private boolean cameraOn;
 
   
    void Start()
    {
        camera = new VideoCapture();
        cameraButton.setDisable(false);
        ImageBox.setFitWidth(640);
        ImageBox.setFitHeight(480);
    }
   
    /**
     * Starts the camera feed if it's not already started.
     * If the camera feed is started, it continuously retrieves frames from the camera and displays them.
     * If the camera feed is already started, stops the camera feed.
     */
    @FXML
    protected void startCamera()
    {  
        if (!cameraOn)
        {
            camera.open(1);
            
            if (camera.isOpened())
            {
                // If the camera is successfully opened
                camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
                camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
                cameraOn = true;
                Runnable frameRuntime = new Runnable() {
                    public void run()
                    {
                        Mat frame = GetFrame();
                        Image image = matToImage(frame);
                        ImageBox.imageProperty().set(image);
                    }
                };
                cameraButton.setText("Stop Camera");
                frameSchedule = Executors.newSingleThreadScheduledExecutor();
                frameSchedule.scheduleAtFixedRate(frameRuntime, 0, 30, TimeUnit.MILLISECONDS);
            }
        }
        else
        {
            // If the camera feed is already started, turn it off
            cameraButton.setText("Start Camera");
            cameraOn = false;
            camera.release();
        }
    }
   
    /**
     * Retrieves a frame from the camera feed.
     * 
     * @return A Mat object representing the captured frame.
     */
    private Mat GetFrame()
    {
        Mat frame = new Mat();

        // check if the capture is open
        if (camera.isOpened())
        {
            try
            {
                // read the current frame
                camera.read(frame);
               
                // if the frame is not empty, do object detection
                if (!frame.empty()) objectDetection(frame);
               
            }
            catch (Exception e){}
        }
        return frame;
    }

    /**
     * Performs object detection on a given frame to identify circles and display them with specific characteristics.
     * 
     * @param frame The input frame (Mat object) for object detection.
     */
    public void objectDetection(Mat frame)
    {
        //Sets up some varibles
        double[] locationOfBall = {0, 0};

        // ========== Set lower and upper colours ==========
        Scalar greenLower = new Scalar(29, 86, 6);
        Scalar greenUpper = new Scalar(64, 255, 255);

        Scalar orangeLower = new Scalar(0, 125, 150);
        Scalar orangeUpper = new Scalar(64, 255, 255);
 
        Scalar blueLower = new Scalar(98, 86, 43);
        Scalar blueUpper = new Scalar(128, 255, 255);

        lowerBallColour = orangeLower;
        upperBallColour = orangeUpper;
 
        Mat grayFrame = new Mat();
 
        //Applys filters
        Core.flip(frame, frame, 1);
        Imgproc.GaussianBlur(frame, grayFrame, new Size(11, 11), 0);
        Imgproc.cvtColor(grayFrame, grayFrame, Imgproc.COLOR_BGR2HSV);
        Core.inRange(grayFrame, lowerBallColour, upperBallColour, grayFrame);
        
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(grayFrame, contours, grayFrame, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
 
        boolean did_it = false;

        List<MatOfPoint> viald_contours = new ArrayList<>();
        MatOfPoint highest_con = new MatOfPoint();
        MatOfPoint2f contoursPoly  = new MatOfPoint2f();
 
        if (contours.size() > 0) {
 
            //finds circles above a specfic size
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
                        did_it = true;
                    }
                }
            }
           
            Point centers = new Point();
            float[] radius = new float[1];
            
            // Draw the circle if a object is detected
            contoursPoly = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(highest_con.toArray()), contoursPoly, 3, true);
            Imgproc.minEnclosingCircle(contoursPoly, centers, radius);
 
            if (did_it == true && radius[0] > 5) {
                Imgproc.circle(frame, centers, (int) radius[0], new Scalar(0, 255, 255), 2);
 
                double Distance = Math.round((OBJECT_WIDTH*FOCAL_LENGTH)/(radius[0]*2));
 
                MatOfPoint2f c2f = new MatOfPoint2f(highest_con.toArray());
                double peri = Imgproc.arcLength(c2f, true);
                double chance = 4*Math.PI*(Imgproc.contourArea(highest_con)/(peri*peri));
 
                iacc.setText("Distance: " + Double.toString(Distance) + "cm");
                raduis.setText("Accuracy: " + Math.round(chance*100) + "%");
            }
            else {
                iacc.setText("No Data");
                raduis.setText("Accuracy is too low");
            }
        }
        else {
            iacc.setText("No Data");
            raduis.setText("Accuracy is too low");
        }
    }

    /**
     * Converts a given OpenCV Mat object representing an image frame to a JavaFX Image object.
     * 
     * @param frame The input OpenCV Mat object representing the image frame.
     * @return The converted JavaFX Image object.
     */
    private static Image matToImage(Mat frame) {
        int type = frame.channels() == 1? BufferedImage.TYPE_BYTE_GRAY: BufferedImage.TYPE_3BYTE_BGR;

        BufferedImage bufferedImage = new BufferedImage(frame.width(), frame.height(), type);
        DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        frame.get(0, 0, dataBufferByte.getData());

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
 

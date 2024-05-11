# Computer-Vision-Object-Detection

There are two different versions of this project: 

## FRC Game Piece Detector
The first is a computer vision program to detect game pieces in the 2022 First Robotics Competition. It gives the angle and distance to the target.

### Requirments
- Raspberry pi 3 
- RoboRio
- Radio
- Ethernet cable
- Usb Webcam
- The Required hardware to power all of this can be found on this [schematic](https://docs.wpilib.org/en/stable/docs/controls-overviews/control-system-hardware.html)
- Rapid React Game pieces

## Object Detector with JavaFX
This is nearly the same program but ported for desktop devices to detect small coloured toy balls. This will gives the distance to the target.

### Requirments
- VS code or another IDE
- Java
- OpenCV-455 for Java
- JavaFX

### Extra information

```java
// You have to modify these values for your own camera (they are used to detect and get the distance of your object)
// Note all code shown is from FXController.java

// object width is in centimeters
final double OBJECT_WIDTH = 3.0;

// I used a really quich method of finding this value although does produce good results
final double FOCAL_LENGTH = 625.6;

// FOCAL_LENGTH = (Width_of_Object_in_pixals x distance_away_from_camera) / Width_of_Object
// These values can be measured from any distance away from the camera
// make sure the distance is the same when finding the values for Width_of_Object_in_pixals and distance_away_from_camera

// Width_of_Object_in_pixals can be found by printing the value of radius[0]*2 on line 217:
double Distance = Math.round((OBJECT_WIDTH*FOCAL_LENGTH)/(radius[0]*2));


// Note you will also have to set the lower and upper bounds of your toy ball's colour
// This can be done on line 157. Note colours are in HSV.

// ========== Set lower and upper colours ==========
Scalar greenLower = new Scalar(29, 86, 6);
Scalar greenUpper = new Scalar(64, 255, 255);

Scalar orangeLower = new Scalar(0, 125, 150);
Scalar orangeUpper = new Scalar(64, 255, 255);
 
Scalar blueLower = new Scalar(98, 86, 43);
Scalar blueUpper = new Scalar(128, 255, 255);

lowerBallColour = orangeLower;
upperBallColour = orangeUpper;

```
### Demonstration:
The ball I will be using is orange. The system here is tested against objects of the same color. When it detects something it will show the distance and put a green circle around it
The ball is at the back in this picture with a bunch of imposters.

![Screenshot 5_10_2024 8_28_50 PM](https://github.com/Apollo99-Games/Computer-Vision-Object-Detection/assets/163193765/74334cd9-1263-4369-a6c8-9635a92947b4)

Another example with a few more objects:

![Screenshot 5_10_2024 8_29_57 PM](https://github.com/Apollo99-Games/Computer-Vision-Object-Detection/assets/163193765/456de351-42fd-4a2a-8ac8-e8ca327a7a84)

Note the program still sees the imposters, but can filter them out:

![Screenshot 5_10_2024 8_36_38 PM](https://github.com/Apollo99-Games/Computer-Vision-Object-Detection/assets/163193765/e13ca225-85c5-4410-8881-78ac3077c1a1)

Here are some photos but just with the ball removed. The program still doesn't fall for the dummy objects.









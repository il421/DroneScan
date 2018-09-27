package com.dji.FPVDemo;

public class DroneAction {

    public float distance;
    public Direction direction;
    public DroneCommand droneCommand;
    public float maxHeight;
    public float minHeight;
    public float minWidth;
    public float maxWidth;
    public String droneActionDesc;

    public DroneAction() {
        this.droneCommand = DroneCommand.Takeoff;
        this.droneActionDesc = this.droneCommand.toString();
    }

    public DroneAction(String l) {
        this.droneCommand = DroneCommand.Land;
        this.droneActionDesc = this.droneCommand.toString();
    }

    public DroneAction(Direction dir) {
        this.droneCommand = DroneCommand.Yaw;
        this.direction = dir;
        this.droneActionDesc = this.droneCommand.toString() + " " + dir;
    }

    public DroneAction(Direction dir, float dis) {
        this.droneCommand = DroneCommand.Move;
        this.direction = dir;
        this.distance = dis;
        this.droneActionDesc = this.droneCommand.toString() + " " + dis + " " + dir + "m";
    }

    public DroneAction(String dir, float dis) {
        this.droneCommand = DroneCommand.Move;
//        this.direction = dir;
        this.distance = dis;
        this.droneActionDesc = this.droneCommand.toString() + " " + dis + " " + dir;
    }

    public DroneAction(float minW, float maxW, float minH, float maxH, String startDir) {
        this.droneCommand = DroneCommand.Scan;
        this.maxHeight = maxH;
        this.minHeight = minH;
//        this.direction = startDir;
        this.minWidth = minW;
        this.maxWidth = maxW;
        this.droneActionDesc = this.droneCommand.toString() + " from the " + startDir + " min height: " +
                minH + "m, max height: " + maxH + "m, min width: " + minW +
                "m, max width: " + maxW + "m";
    }


}

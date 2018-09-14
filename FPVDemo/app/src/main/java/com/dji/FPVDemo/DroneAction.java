package com.dji.FPVDemo;

public class DroneAction {

    private float distance;
    private Direction direction;
    private DroneCommand droneCommand;
    private float maxHeight;
    private float minHeight;

    public DroneAction() {
        this.droneCommand = DroneCommand.Land;
    }

    public DroneAction(float dis) {
        this.droneCommand = DroneCommand.Takeoff;
        this.distance = dis;
    }

    public DroneAction(Direction dir) {
        this.droneCommand = DroneCommand.Yaw;
        this.direction = dir;
    }

    public DroneAction(Direction dir, float dis) {
        this.droneCommand = DroneCommand.Move;
        this.direction = dir;
        this.distance = dis;
    }

    public DroneAction(float width, float maxH, float minH, Direction startDir) {
        this.droneCommand = DroneCommand.Scan;
        this.maxHeight = maxH;
        this.minHeight = minH;
        this.direction = startDir;
    }

}

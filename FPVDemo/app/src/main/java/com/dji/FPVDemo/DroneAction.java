package com.dji.FPVDemo;

public class DroneAction {

    private float distance;
    private Direction direction;
    private DroneCommand droneCommand;
    private float maxHeight;
    private float minHeight;

    public DroneAction() {
        this.droneCommand = DroneCommand.LAND;
    }

    public DroneAction(float dis) {
        this.droneCommand = DroneCommand.TAKEOFF;
        this.distance = dis;
    }

    public DroneAction(Direction dir) {
        this.droneCommand = DroneCommand.YAW;
        this.direction = dir;
    }

    public DroneAction(Direction dir, float dis) {
        this.droneCommand = DroneCommand.MOVE;
        this.direction = dir;
        this.distance = dis;
    }

    public DroneAction(float width, float maxH, float minH, Direction startDir) {
        this.droneCommand = DroneCommand.SCAN;
        this.maxHeight = maxH;
        this.minHeight = minH;
        this.direction = startDir;
    }

}

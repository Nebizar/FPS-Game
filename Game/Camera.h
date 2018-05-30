#ifndef CAMERA_H
#define CAMERA_H

#include <iostream>
#include <math.h>

#include <GLFW/glfw3.h>
#include <glm/glm.hpp>

class Camera
{
    protected:
        glm::vec3 position;
        glm::vec3 rotation;
        glm::vec3 speed;

        double movementSpeedFactor;
        double pitchSensitivity;
        double yawSensitivity;

        int windowWidth;
        int windowHeight;
        int windowMidX;
        int windowMidY;

        void initCamera();

    public:

        static const double TO_RADS;

        bool holdForward;
        bool holdBackward;
        bool holdLeft;
        bool holdRight;

        Camera(GLFWwindow* window, float windowWidth, float windowHeight);
        ~Camera();

        void handleMouseMove(GLFWwindow* window, double mouseX, double mouseY);

        const double toRads(const double &angleInDegrees) const;

        void move(double deltaTime);

        //Pitch and Yaw getters and setters
        float getPitchSensitivity() {return pitchSensitivity;}
        void setPitchSensitivity(float value) {pitchSensitivity = value;}
        float getYawSensitivity() {return yawSensitivity;}
        void setYawSensitivity(float value) {yawSensitivity = value;}

        //Position getters and setters
        glm::vec3 getPosition() const{ return position;}
        float getXPos() const{return position.x;}
        float getYPos() const{return position.y;}
        float getZPos() const{return position.z;}

        //rotation getters and setters
        glm::vec3 getRotation() const{ return rotation;}
        float getXRot() const{return rotation.x;}
        float getYRot() const{return rotation.y;}
        float getZRot() const{return rotation.z;}

};

#endif // CAMERA_H

#ifndef CAMERA_H
#define CAMERA_H

#include <iostream>
#include <math.h>

#include <GLFW/glfw3.h>
#include <glm/glm.hpp>

class Camera
{
    private:
        glm::vec3 position;
        glm::vec3 rotation;
        glm::vec3 speed;

        double movementSpeedFactor;
        double pitchSensitivity;
        double yawSensitivity;

        //int windowWidth;
        //int windowHeight;
        int windowMidX;
        int windowMidY;


        bool holdForward;
        bool holdBackward;
        bool holdLeft;
        bool holdRight;

        inline const float toRads(const float &angleInDegrees) const
        {
            return angleInDegrees * TO_RADS;
        }

    public:

        static const float TO_RADS;
        static const float TO_DEGS;



        Camera(glm::vec3 initPos, glm::vec3 initRot, GLsizei windowWidth, GLsizei windowHeight);
        ~Camera();

        void handleKeyPress(GLint key, GLint action);

        void handleMouseMove(GLFWwindow* window, GLint mouseX, GLint mouseY);

        void move(double deltaTime);

        void updateWindowMidpoint(GLsizei windowWidth, GLsizei windowHeight)
        {
            windowMidX = windowWidth / 2;
            windowMidY = windowHeight / 2;
        }

        //Pitch and Yaw getters and setters
        float getPitchSensitivity() {return pitchSensitivity;}
        void setPitchSensitivity(float value) {pitchSensitivity = value;}
        float getYawSensitivity() {return yawSensitivity;}
        void setYawSensitivity(float value) {yawSensitivity = value;}

        //Position getters and setters
        glm::vec3 getPosition() const{ return position;}
        void setPosition(glm::vec3 v) {position = v;}
        float getXPos() const{return position.x;}
        float getYPos() const{return position.y;}
        float getZPos() const{return position.z;}

        //rotation getters and setters
        glm::vec3 getRotation() const{ return rotation;}
        void setRotation(glm::vec3 v) {rotation = v; }
        float getXRot() const{return rotation.x;}
        float getYRot() const{return rotation.y;}
        float getZRot() const{return rotation.z;}

        float getXRotRad() const{return rotation.x * TO_RADS;}
        float getYRotRad() const{return rotation.y * TO_RADS;}
        float getZRotRad() const{return rotation.z * TO_RADS;}

};

#endif // CAMERA_H

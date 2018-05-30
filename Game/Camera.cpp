#include "Camera.h"

const double Camera::TO_RADS = 3.141592654/180.0;

Camera::Camera(GLFWwindow* window, float theWindowWidth, float theWindowHeight)
{
    initCamera();

    windowWidth = theWindowWidth;
    windowHeight = theWindowHeight;

    windowMidX = windowWidth / 2.0f;
    windowMidY = windowHeight / 2.0f;

    glfwSetCursorPos(window, windowMidX, windowMidY);
}

Camera::~Camera()
{

}

void Camera::initCamera()
{
    position = glm::vec3(0, 0, 0);
    rotation = glm::vec3(0, 0, 0);
    speed = glm::vec3(0, 0, 0);

    movementSpeedFactor = 100.0;

    pitchSensitivity = 0.02;
    yawSensitivity = 0.02;

    holdForward = false;
    holdBackward = false;
    holdLeft = false;
    holdRight = false;
}

const double Camera::toRads(const double &theAngleInDegrees) const
{
    return theAngleInDegrees * TO_RADS;
}

void Camera::handleMouseMove(GLFWwindow* window, double mouseX, double mouseY)
{
    double horizontalMov = (mouseX - windowMidX+1)*yawSensitivity;
    double verticalMov = (mouseY - windowMidY+1)*pitchSensitivity;

    rotation += glm::vec3(verticalMov, horizontalMov, 0);

    if( rotation.x < -90)
    {
        rotation.x = -90;
    }
    if( rotation.x > 90)
    {
        rotation.x = 90;
    }

    if( rotation.y < 0)
    {
        rotation.y = 360;
    }
    if( rotation.y > 360)
    {
        rotation.y = -360;
    }

    glfwSetCursorPos(window, windowMidX, windowMidY);
}

void Camera::move(double deltaTime)
{
    glm::vec3 movement;

    double sinX = sin( toRads(rotation.x));
    double cosX = cos( toRads(rotation.x));
    double sinY = sin( toRads(rotation.y));
    double cosY = cos( toRads(rotation.y));

    double pitchLimitFactor = cosX;

    if(holdForward)
        movement = movement + glm::vec3(sinY*pitchLimitFactor, -sinX, -cosY*pitchLimitFactor);
    if(holdBackward)
        movement = movement + glm::vec3(-sinY*pitchLimitFactor, sinX, cosY*pitchLimitFactor);
    if(holdLeft)
        movement = movement + glm::vec3(-cosY, 0, -sinY);
    if(holdRight)
        movement = movement + glm::vec3(cosY, 0, sinY);

    movement = normalize(movement);

    float framerateFactor = movementSpeedFactor * deltaTime;

    movement = movement*framerateFactor;

    position = position + movement;
}


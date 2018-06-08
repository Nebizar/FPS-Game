#include "Camera.h"

const float Camera::TO_RADS = 3.1415926f/180.0f;
const float Camera::TO_DEGS = 180.0f/3.1415926f;

Camera::Camera(glm::vec3 initPos, glm::vec3 initRot, GLint theWindowWidth, GLint theWindowHeight)
{
    //initCamera();
    position = initPos;
    rotation = initRot;
    speed = glm::vec3(0.0f);
    rotation2 = glm::vec3(0.0f);

    movementSpeedFactor = 5.0f;

    pitchSensitivity = 0.05f;
    yawSensitivity = 0.05f;

    holdForward = false;
    holdBackward = false;
    holdLeft = false;
    holdRight = false;

    updateWindowMidpoint(theWindowWidth, theWindowHeight);
}

Camera::~Camera()
{

}

void Camera::handleKeyPress(GLint key, GLint action)
{

    if(action==GLFW_PRESS || action==GLFW_REPEAT)
    {
        switch(key)
        {
        case GLFW_KEY_W:
            holdForward = true;
            break;
        case GLFW_KEY_S:
            holdBackward = true;
            break;
        case GLFW_KEY_A:
            holdLeft = true;
            break;
        case GLFW_KEY_D:
            holdRight = true;
            break;
        default:
            break;
        }
    }
    else
    {
        switch(key)
        {
        case GLFW_KEY_W:
            holdForward = false;
            break;
        case GLFW_KEY_S:
            holdBackward = false;
            break;
        case GLFW_KEY_A:
            holdLeft = false;
            break;
        case GLFW_KEY_D:
            holdRight = false;
            break;
        default:
            break;
        }
    }
}

void Camera::handleMouseMove(GLFWwindow* window, GLint mouseX, GLint mouseY)
{
    double horizontalMov = ( windowMidX - mouseX)*yawSensitivity;
    double verticalMov = (windowMidY - mouseY)*pitchSensitivity;

    rotation.x += verticalMov;
    rotation.y += horizontalMov;

    if( rotation.x < -90.0f)
    {
        rotation.x = -90.0f;
    }
    if( rotation.x > 90.0f)
    {
        rotation.x = 90.0f;
    }

    if( rotation.y < 0.0f)
    {
        rotation.y += 360.0f;
    }
    if( rotation.y > 360.0f)
    {
        rotation.y -= 360.0f;
    }
    rotation2.x = cos(verticalMov) * sin(horizontalMov);
    rotation2.y = sin(verticalMov);
    rotation2.z = cos(verticalMov) * cos(horizontalMov);
    glfwSetCursorPos(window, windowMidX, windowMidY);
}

void Camera::move(double deltaTime)
{
    glm::vec3 movement(0.0f, 0.0f, 0.0f);

    float sinX = sin( toRads(rotation.x));
    float cosX = cos( toRads(rotation.x));
    float sinY = sin( toRads(rotation.y));
    float cosY = cos( toRads(rotation.y));

    float pitchLimitFactor = cosX;

    if(holdForward)
        movement = movement + glm::vec3(-sinY*pitchLimitFactor, 0, cosY*pitchLimitFactor);
    if(holdBackward)
        movement = movement + glm::vec3(sinY*pitchLimitFactor, 0, -cosY*pitchLimitFactor);
    if(holdLeft)
        movement = movement + glm::vec3(cosY, 0.0f, sinY);
    if(holdRight)
        movement = movement - glm::vec3(cosY, 0.0f, sinY);

    glm::vec3 movNorm(0.0f, 0.0f, 0.0f);
    if(glm::length(movement)!= 0.0f)
    {
        movNorm = glm::normalize(movement);
    }

    float framerateFactor = movementSpeedFactor * deltaTime;

    movNorm = movNorm*framerateFactor;

    position = position + movNorm;
}


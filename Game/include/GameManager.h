#ifndef GAMEMANAGER_H
#define GAMEMANAGER_H

#include <iostream>
#include <GLFW/glfw3.h>

class GameManager
{
    private:
        bool _isRunning;
        GLFWwindow *_window;

        GameManager(bool isRunning);
        ~GameManager();
    public:
        static GameManager& getGameManager();
        static void destroyGameManager();

        void runGame();
};


#endif // GAMEMANAGER_H

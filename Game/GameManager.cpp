#include "GameManager.h"

GameManager::GameManager(bool isRunning): _isRunning(isRunning), _window(glfwGetCurrentContext())
{
}

GameManager::~GameManager()
{
}


GameManager& GameManager::getGameManager()
{
	static GameManager *gameManager = NULL;

	if (gameManager == NULL)
	{
	    glfwInit();
	    GLFWwindow *window = glfwCreateWindow(1280,720, "DOOM[v0.1]", NULL, NULL);
	    glfwMakeContextCurrent(window);

		gameManager = new GameManager(true);
		std::cout << "GameManager started. \n";
	}

	return *gameManager;
}

void GameManager::destroyGameManager()
{
	GameManager *gameManager = &getGameManager();

    delete gameManager;
    std::cout << "GameManager terminated. \n";

    GLFWwindow *window = glfwGetCurrentContext();
    glfwDestroyWindow(window);

    glfwTerminate();
}

void GameManager::runGame()
{
    while(_isRunning)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        _isRunning = !glfwWindowShouldClose(_window);

        glfwSwapBuffers(_window);
        glfwPollEvents();
    }

}

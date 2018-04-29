#include "GameManager.h"

GameManager::GameManager()
{
}

GameManager::~GameManager()
{
}


GameManager& GameManager::getGameManager()
{
	static GameManager *gameManager = nullptr;
	
	if (gameManager == nullptr)
	{
		gameManager = new GameManager();
		std::cout << "GameManager started. \n";
	}

	return *gameManager;
}


void GameManager::destroyGameManager()
{
	GameManager *gameManager = &getGameManager();

	if (gameManager != nullptr)
	{
		delete gameManager;
		std::cout << "GameManager terminated. \n";
	}
}

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
	}

	return *gameManager;
}


void GameManager::destroyGameManager()
{
	GameManager *gameManager = &getGameManager();

	if (gameManager != nullptr) delete gameManager;
}

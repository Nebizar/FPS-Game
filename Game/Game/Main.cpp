#include <iostream>
#include "GameManager.h"

int main()
{
	//create GM
	GameManager *myGM= &GameManager::getGameManager();
	GameManager::destroyGameManager();
	//getchar();
	return EXIT_SUCCESS;
}
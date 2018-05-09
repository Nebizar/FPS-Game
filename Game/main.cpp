#include <iostream>
#include <cstdlib>
#include "GameManager.h"

int main(void)
{
	//create GM
	GameManager *myGM= &GameManager::getGameManager();
	myGM->runGame();
	GameManager::destroyGameManager();
	//getchar();
	return EXIT_SUCCESS;
}

#pragma once

#include <iostream>

class GameManager
{
private:
	GameManager();
	~GameManager();
public:
	static GameManager& getGameManager();
	static void destroyGameManager();
};


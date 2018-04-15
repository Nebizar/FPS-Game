#pragma once
class GameManager
{
private:
	GameManager();
	~GameManager();
public:
	static GameManager& getGameManager();
	static void destroyGameManager();
};


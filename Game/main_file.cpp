/*
Niniejszy program jest wolnym oprogramowaniem; możesz go
rozprowadzać dalej i / lub modyfikować na warunkach Powszechnej
Licencji Publicznej GNU, wydanej przez Fundację Wolnego
Oprogramowania - według wersji 2 tej Licencji lub(według twojego
wyboru) którejś z późniejszych wersji.

Niniejszy program rozpowszechniany jest z nadzieją, iż będzie on
użyteczny - jednak BEZ JAKIEJKOLWIEK GWARANCJI, nawet domyślnej
gwarancji PRZYDATNOŚCI HANDLOWEJ albo PRZYDATNOŚCI DO OKREŚLONYCH
ZASTOSOWAŃ.W celu uzyskania bliższych informacji sięgnij do
Powszechnej Licencji Publicznej GNU.

Z pewnością wraz z niniejszym programem otrzymałeś też egzemplarz
Powszechnej Licencji Publicznej GNU(GNU General Public License);
jeśli nie - napisz do Free Software Foundation, Inc., 59 Temple
Place, Fifth Floor, Boston, MA  02110 - 1301  USA
*/

#define GLM_FORCE_RADIANS

#include <GL/glew.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include "constants.h"
#include "allmodels.h"
#include "lodepng.h"
#include "shaderprogram.h"
#include "GameManager.h"
#include "Camera.h"
#include "ObjectLoader.h"
//#include <assimp/Importer.hpp>

using namespace glm;

GLsizei windowWidth = 720;
GLsizei windowHeight = 480;
float vertFieldOfViewDegs = 45.0f;
float nearClipDistance    = 1.0f;
float farClipDistance     = 2000.0f;
float aspect=(float)windowWidth/(float)windowHeight; //Stosunek szerokości do wysokości okna
float x=1.0f;
float y=0.0f;
float z=0.0f;
glm::vec3 eye=glm::vec3(2.0f,5.0f,5.0f);

Camera cam(glm::vec3(1.0f, 0.0f, -5.0f), glm::vec3(0.0f, 0.0f, 0.0f), windowWidth, windowHeight);


//Uchwyty na shadery
ShaderProgram *shaderProgram; //Wskaźnik na obiekt reprezentujący program cieniujący.

//Uchwyty na VAO i bufory wierzchołków
GLuint vao;
GLuint bufVertices; //Uchwyt na bufor VBO przechowujący tablicę współrzędnych wierzchołków
GLuint bufColors;  //Uchwyt na bufor VBO przechowujący tablicę kolorów
GLuint bufNormals; //Uchwyt na bufor VBO przechowujący tablicę wektorów normalnych
GLuint bufTexCoords; //Uchwyt na bufor VBO przechowujący tablicę współrzędnych teksturowania

GLuint tex0;
GLuint tex1;

//Kostka
/*float* vertices=Models::CubeInternal::vertices;
float* colors=Models::CubeInternal::colors;
float* normals=Models::CubeInternal::normals;
float* texCoords=Models::CubeInternal::texCoords;
int vertexCount=Models::CubeInternal::vertexCount;*/

//Czajnik
/*float* vertices=Models::TeapotInternal::vertices;
float* colors=Models::TeapotInternal::colors;
float* normals=Models::TeapotInternal::vertexNormals;
float* texCoords=Models::TeapotInternal::texCoords;
int vertexCount=Models::TeapotInternal::vertexCount;*/

std::vector<glm::vec4> verticesV;
std::vector<glm::vec2> texCoordsV;
std::vector<glm::vec4> normalsV;
int vertexCount;


float* vertices;
float* normals;
float* texCoords;



//Procedura obsługi błędów
void error_callback(int error, const char* description) {
	fputs(description, stderr);
}

//Procedura obsługi klawiatury
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods) {
	if (action == GLFW_PRESS && key == GLFW_KEY_ESCAPE) {
        glfwSetWindowShouldClose(window, GL_TRUE);
	}
    else
    {
        cam.handleKeyPress(key, action);
    }
}

void handleMouse(GLFWwindow* window, double mouseX, double mouseY)
{
    cam.handleMouseMove(window, mouseX, mouseY);
}

//Procedura obługi zmiany rozmiaru bufora ramki
void windowResize(GLFWwindow* window, GLsizei width, GLsizei height) {
    glViewport(0, 0, width, height); //Obraz ma być generowany w oknie o tej rozdzielczości
    if (height!=0) {
        aspect=(float)width/(float)height; //Stosunek szerokości do wysokości okna
    } else {
        aspect=1;
    }
    cam.updateWindowMidpoint(width, height);
}

//Tworzy bufor VBO z tablicy
GLuint makeBuffer(void *data, int vertexCount, int vertexSize) {
	GLuint handle;

	glGenBuffers(1,&handle);//Wygeneruj uchwyt na Vertex Buffer Object (VBO), który będzie zawierał tablicę danych
	glBindBuffer(GL_ARRAY_BUFFER,handle);  //Uaktywnij wygenerowany uchwyt VBO
	glBufferData(GL_ARRAY_BUFFER, vertexCount*vertexSize, data, GL_STATIC_DRAW);//Wgraj tablicę do VBO

	return handle;
}

//Przypisuje bufor VBO do atrybutu
void assignVBOtoAttribute(ShaderProgram *shaderProgram,const char* attributeName, GLuint bufVBO, int vertexSize) {
	GLuint location=shaderProgram->getAttribLocation(attributeName); //Pobierz numer slotu dla atrybutu
	glBindBuffer(GL_ARRAY_BUFFER,bufVBO);  //Uaktywnij uchwyt VBO
	glEnableVertexAttribArray(location); //Włącz używanie atrybutu o numerze slotu zapisanym w zmiennej location
	glVertexAttribPointer(location,vertexSize,GL_FLOAT, GL_FALSE, 0, NULL); //Dane do slotu location mają być brane z aktywnego VBO
}

//Przygotowanie do rysowania pojedynczego obiektu
void prepareObject(ShaderProgram *shaderProgram) {
	//Zbuduj VBO z danymi obiektu do narysowania
	bufVertices=makeBuffer(vertices, vertexCount, sizeof(float)*4); //VBO ze współrzędnymi wierzchołków
	//bufColors=makeBuffer(colors, vertexCount, sizeof(float)*4);//VBO z kolorami wierzchołków
	bufNormals=makeBuffer(normals, vertexCount, sizeof(float)*4);//VBO z wektorami normalnymi wierzchołków
	bufTexCoords=makeBuffer(texCoords, vertexCount, sizeof(float)*2);//VBO ze współrzędnymi teksturowania

	//Zbuduj VAO wiążący atrybuty z konkretnymi VBO
	glGenVertexArrays(1,&vao); //Wygeneruj uchwyt na VAO i zapisz go do zmiennej globalnej

	glBindVertexArray(vao); //Uaktywnij nowo utworzony VAO

	assignVBOtoAttribute(shaderProgram,"vertex",bufVertices,4); //"vertex" odnosi się do deklaracji "in vec4 vertex;" w vertex shaderze
	//assignVBOtoAttribute(shaderProgram,"color",bufColors,4); //"color" odnosi się do deklaracji "in vec4 color;" w vertex shaderze
	assignVBOtoAttribute(shaderProgram,"normal",bufNormals,4); //"normal" odnosi się do deklaracji "in vec4 normal;" w vertex shaderze
	assignVBOtoAttribute(shaderProgram,"texCoord0",bufTexCoords,2); //"texCoord0" odnosi się do deklaracji "in vec2 texCoord0;" w vertex shaderze

	glBindVertexArray(0); //Dezaktywuj VAO
}

GLuint readTexture(char* filename) {
  GLuint tex;
  glActiveTexture(GL_TEXTURE0);

  //Wczytanie do pamięci komputera
  std::vector<unsigned char> image;   //Alokuj wektor do wczytania obrazka
  unsigned width, height;   //Zmienne do których wczytamy wymiary obrazka
  //Wczytaj obrazek
  unsigned error = lodepng::decode(image, width, height, filename);

  //Import do pamięci karty graficznej
  glGenTextures(1,&tex); //Zainicjuj jeden uchwyt
  glBindTexture(GL_TEXTURE_2D, tex); //Uaktywnij uchwyt
  //Wczytaj obrazek do pamięci KG skojarzonej z uchwytem
  glTexImage2D(GL_TEXTURE_2D, 0, 4, width, height, 0,
    GL_RGBA, GL_UNSIGNED_BYTE, (unsigned char*) image.data());

  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_LINEAR);

  return tex;
}


//Procedura inicjująca
void initOpenGLProgram(GLFWwindow* window) {
    ObjectLoader objL;
    bool check = objL.loadObj("objects/base.obj", verticesV, texCoordsV, normalsV);
    vertexCount = verticesV.size();
    vertices = new float[verticesV.size()*4];
    normals = new float[normalsV.size()*4];
    texCoords = new float[texCoordsV.size()*2];
    //std::cout<<verticesV[0][0];
    int a =0;
    for(int i=0;i<verticesV.size();i++)
    {
        for(int j=0;j<4;j++)
        {
            vertices[a]=verticesV[i][j];
            a++;
        }
    }
    //std::cout<<vertices[0];
    a = 0;
    for(int i=0;i<normalsV.size();i++)
    {
        for(int j=0;j<4;j++)
        {
            normals[a]=normalsV[i][j];
            a++;
        }
    }

    for(int i=0;i<texCoordsV.size();i=i+2)
    {
        texCoords[i]=texCoordsV[i][0];
        texCoords[i+1]=texCoordsV[i][1];
    }
    std::cout<<"Location loaded.";



	//************Tutaj umieszczaj kod, który należy wykonać raz, na początku programu************
	glClearColor(0, 0, 0, 1); //Czyść ekran na czarno
	glEnable(GL_DEPTH_TEST); //Włącz używanie Z-Bufora
	glfwSetKeyCallback(window, key_callback); //Zarejestruj procedurę obsługi klawiatury
    glfwSetCursorPosCallback(window, handleMouse);
    glfwSetFramebufferSizeCallback(window,windowResize); //Zarejestruj procedurę obsługi zmiany rozmiaru bufora ramki

    glfwSwapInterval(1);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    glfwSetCursorPos(window, windowWidth / 2, windowHeight / 2);

	shaderProgram=new ShaderProgram("vshader.vert",NULL,"fshader.frag"); //Wczytaj program cieniujący

	tex0=readTexture("metal.png");
	tex1=readTexture("metal_spec.png");

    prepareObject(shaderProgram);
}

//Zwolnienie zasobów zajętych przez program
void freeOpenGLProgram() {
	delete shaderProgram; //Usunięcie programu cieniującego

	glDeleteVertexArrays(1,&vao); //Usunięcie vao
	glDeleteBuffers(1,&bufVertices); //Usunięcie VBO z wierzchołkami
	glDeleteBuffers(1,&bufColors); //Usunięcie VBO z kolorami
	glDeleteBuffers(1,&bufNormals); //Usunięcie VBO z wektorami normalnymi
	glDeleteBuffers(1,&bufTexCoords); //Usunięcie VBO ze współrzednymi teksturowania
	glDeleteTextures(1,&tex0); //Usunięcie tekstury z tex0
	glDeleteTextures(1,&tex1); //Usunięcie tekstury z tex1
}

void drawObject(GLuint vao, ShaderProgram *shaderProgram, mat4 mP, mat4 mV, mat4 mM) {
	//Włączenie programu cieniującego, który ma zostać użyty do rysowania
	//W tym programie wystarczyłoby wywołać to raz, w setupShaders, ale chodzi o pokazanie,
	//że mozna zmieniać program cieniujący podczas rysowania jednej sceny
	shaderProgram->use();

	//Przekaż do shadera macierze P,V i M.
	//W linijkach poniżej, polecenie:
	//  shaderProgram->getUniformLocation("P")
	//pobiera numer slotu odpowiadającego zmiennej jednorodnej o podanej nazwie
	//UWAGA! "P" w powyższym poleceniu odpowiada deklaracji "uniform mat4 P;" w vertex shaderze,
	//a mP w glm::value_ptr(mP) odpowiada argumentowi  "mat4 mP;" TYM pliku.
	//Cała poniższa linijka przekazuje do zmiennej jednorodnej P w vertex shaderze dane z argumentu mP niniejszej funkcji
	//Pozostałe polecenia działają podobnie.
	glUniformMatrix4fv(shaderProgram->getUniformLocation("P"),1, false, glm::value_ptr(mP));
	glUniformMatrix4fv(shaderProgram->getUniformLocation("V"),1, false, glm::value_ptr(mV));
	glUniformMatrix4fv(shaderProgram->getUniformLocation("M"),1, false, glm::value_ptr(mM));
	glUniform1i(shaderProgram->getUniformLocation("textureMap0"),0); //Powiązanie textureMap0 we fragment shaderze z jednostką teksturowania nr 0
	glUniform1i(shaderProgram->getUniformLocation("textureMap1"),1); //Powiązanie textureMap1 we fragment shaderze z jednostką teksturowania nr 1

	//Powiąż teksturę z uchwytem w tex0 z zerową jednostką teksturującą
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D,tex0);
	//Powiąż teksturę z uchwytem w tex1 z zerową jednostką teksturującą
	glActiveTexture(GL_TEXTURE1);
	glBindTexture(GL_TEXTURE_2D,tex1);

	//Uaktywnienie VAO i tym samym uaktywnienie predefiniowanych w tym VAO powiązań slotów atrybutów z tablicami z danymi
	glBindVertexArray(vao);

	//Narysowanie obiektu
	glDrawArrays(GL_TRIANGLES,0,vertexCount);

	//Posprzątanie po sobie (niekonieczne w sumie jeżeli korzystamy z VAO dla każdego rysowanego obiektu)
	glBindVertexArray(0);
}

//Procedura rysująca zawartość sceny
void drawScene(GLFWwindow* window) {
    cam.move(1.0f/60.0f);
	//************Tutaj umieszczaj kod rysujący obraz******************l

	glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT); //Wykonaj czyszczenie bufora kolorów

	//glm::mat4 P = glm::perspective(50 * PI / 180, aspect, 1.0f, 50.0f); //Wylicz macierz rzutowania

    glm::mat4 P = glm::perspective(vertFieldOfViewDegs, aspect, nearClipDistance, farClipDistance); //Wylicz macierz rzutowania

    /*glm::mat4 V = glm::lookAt( //Wylicz macierz widoku
		cam.getPosition(),
		cam.getRotation(),
		glm::vec3(0.0f, 1.0f, 0.0f));*/
		float yaw=cam.getYRotRad();
		float pitch=cam.getXRotRad();
    /*float cosYaw=cos(cam.getYRotRad());
    float sinYaw=sin(cam.getYRotRad());
    float cosPitch=cos(cam.getXRotRad());
    float sinPitch=sin(cam.getXRotRad());
    vec3 xaxis = { cosYaw, 0, -sinYaw };
    vec3 yaxis = { sinYaw * sinPitch, cosPitch, cosYaw * sinPitch };
    vec3 zaxis = { sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw };
	/*glm::mat4 V = glm::lookAt( //Wylicz macierz widoku
		glm::vec3(1.0f, 3.0f, -1.0f),
		glm::vec3(0.0f, 0.0f, 0.0f),
		glm::vec3(0.0f, 1.0f, 0.0f));*/
    //glm::mat4 V = glm::mat4(1.0f);
     //roll can be removed from here. because is not actually used in FPS camera
 glm::mat4 matPitch = glm::mat4(1.0f);//identity matrix
 glm::mat4 matYaw   = glm::mat4(1.0f);//identity matrix

 //roll, pitch and yaw are used to store our angles in our class
 matPitch = glm::rotate(matPitch, pitch, glm::vec3(1.0f, 0.0f, 0.0f));
 matYaw   = glm::rotate(matYaw,  yaw,    glm::vec3(0.0f, 1.0f, 0.0f));

 //order matters
 glm::mat4 rotate = matPitch * matYaw;

 glm::mat4 translate = glm::mat4(1.0f);
 translate = glm::translate(translate, -eye);

glm::mat4 V = rotate * translate;
    /*std::cout<<V[1][0]<<std::endl;
    std::cout<<V[1][1]<<std::endl;
    std::cout<<V[1][2]<<std::endl;*/
    /*V = glm::rotate(V, cam.getXRotRad(), glm::vec3(1.0f, 0.0f, 0.0f));
    V = glm::rotate(V, cam.getYRotRad(), glm::vec3(0.0f, 1.0f, 0.0f));*/

    V = glm::translate(V, cam.getPosition());
    /*std::cout<<V[0][0]<<std::endl;
    std::cout<<V[1][0]<<std::endl;
    std::cout<<V[2][0]<<std::endl;*/


	//Wylicz macierz modelu rysowanego obiektu
	glm::mat4 M = glm::mat4(1.0f);

    //M = glm::translate(M, glm::vec3(-cam->getXPos(), -cam->getYPos(), -cam->getZPos()));
	//Narysuj obiekt
	drawObject(vao,shaderProgram,P,V,M);

	//Przerzuć tylny bufor na przedni
	glfwSwapBuffers(window);

}



int main(void)
{
	GLFWwindow* window; //Wskaźnik na obiekt reprezentujący okno

	glfwSetErrorCallback(error_callback);//Zarejestruj procedurę obsługi błędów

	if (!glfwInit()) { //Zainicjuj bibliotekę GLFW
		fprintf(stderr, "Nie można zainicjować GLFW.\n");
		exit(EXIT_FAILURE);
	}

	window = glfwCreateWindow(windowWidth, windowHeight, "Doom[v0.1]", NULL, NULL);  //Utwórz okno 500x500 o tytule "OpenGL" i kontekst OpenGL.

	if (!window) //Jeżeli okna nie udało się utworzyć, to zamknij program
	{
		fprintf(stderr, "Nie można utworzyć okna.\n");
		glfwTerminate();
		exit(EXIT_FAILURE);
	}


	glfwMakeContextCurrent(window); //Od tego momentu kontekst okna staje się aktywny i polecenia OpenGL będą dotyczyć właśnie jego.
	//glfwSwapInterval(1); //Czekaj na 1 powrót plamki przed pokazaniem ukrytego bufora

	if (glewInit() != GLEW_OK) { //Zainicjuj bibliotekę GLEW
		fprintf(stderr, "Nie można zainicjować GLEW.\n");
		exit(EXIT_FAILURE);
	}

	initOpenGLProgram(window); //Operacje inicjujące

	/*float angle_x = 0; //Kąt obrotu obiektu
	float angle_y = 0; //Kąt obrotu obiektu
*/
    //Główna pętla
	while (!glfwWindowShouldClose(window)) //Tak długo jak okno nie powinno zostać zamknięte
	{
		 //Zwiększ kąt o prędkość kątową razy czas jaki upłynął od poprzedniej klatki
		glfwSetTime(0); //Wyzeruj licznik czasu
		drawScene(window); //Wykonaj procedurę rysującą
		glfwPollEvents(); //Wykonaj procedury callback w zalezności od zdarzeń jakie zaszły.
	}

	freeOpenGLProgram();

	glfwDestroyWindow(window); //Usuń kontekst OpenGL i okno
	glfwTerminate(); //Zwolnij zasoby zajęte przez GLFW
	exit(EXIT_SUCCESS);
}

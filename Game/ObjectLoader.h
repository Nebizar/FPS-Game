#ifndef OBJECTLOADER_H
#define OBJECTLOADER_H

#include<vector>
#include <glm/glm.hpp>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>

class ObjectLoader
{
    public:
        ObjectLoader();
        virtual ~ObjectLoader();
        bool loadObj(const char* path,
                     std::vector<glm::vec4> & out_vertices,
                     std::vector<glm::vec2> & out_uvs,
                     std::vector<glm::vec4> & out_normals);
    protected:

    private:
};

#endif // OBJECTLOADER_H

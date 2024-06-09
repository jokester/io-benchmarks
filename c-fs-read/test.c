#include <stdio.h>
#include <stdlib.h>

#define BUFFER_SIZE 64 * 1024 // 64k

int main() {
    FILE *file;
    char *buffer;
    size_t result;

    file = fopen("/tmp/file", "rb");
    if (file == NULL) {
        fputs("Error opening file", stderr);
        return 1;
    }

    buffer = (char *)malloc(sizeof(char) * BUFFER_SIZE);
    if (buffer == NULL) {
        fputs("Memory error", stderr);
        fclose(file);
        return 2;
    }

    for (;;) {
        result = fread(buffer, 1, BUFFER_SIZE, file);
        if (result <= 0)
        {
            if (feof(file))
            {
                break;
            }
            fputs("Reading error", stderr);
            fclose(file);
            free(buffer);
            return 3;
        }
    }

    fclose(file);
    free(buffer);
    return 0;
}

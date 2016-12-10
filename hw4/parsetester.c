#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char **split_from_content(const char* str, const char* delim, size_t* numtokens) {

	char *s = strdup(str);
	size_t tokens_alloc = 1;
	size_t tokens_used = 0;

	char **tokens = calloc(tokens_alloc, sizeof(char*));

	char *token, *rest = s;

	if ((token = strsep(&rest, delim)) != NULL) {


		if (tokens_used == tokens_alloc) {
			tokens_alloc *= 2;
			tokens = realloc(tokens, tokens_alloc * sizeof(char*));
		}


		/* Skip last delimeter */

		if(strcmp(token, "")) {
			tokens[tokens_used++] = strdup(token);
			tokens[tokens_used++] = strdup(rest);
		}
	}


	if (tokens_used == 0) {
		free(tokens);
		tokens = NULL;
	} else {
		tokens = realloc(tokens, tokens_used * sizeof(char*));
	}

	*numtokens = tokens_used;

	free(s);

	return tokens;
}


char **strsplit(const char* str, const char* delim, size_t* numtokens) {

	char *s = strdup(str);
    size_t tokens_alloc = 1;
    size_t tokens_used = 0;

    char **tokens = calloc(tokens_alloc, sizeof(char*));

    char *token, *rest = s;
    while ((token = strsep(&rest, delim)) != NULL) {
        if (tokens_used == tokens_alloc) {
            tokens_alloc *= 2;
            tokens = realloc(tokens, tokens_alloc * sizeof(char*));
        }


        /* Skip last delimeter */
        if(strcmp(token, "")) {
        	tokens[tokens_used++] = strdup(token);
        }
    }
    if (tokens_used == 0) {
        free(tokens);
        tokens = NULL;
    } else {
        tokens = realloc(tokens, tokens_used * sizeof(char*));
    }
    *numtokens = tokens_used;
    free(s);
    return tokens;
}

int main(void) {
	char *line = "LIST lala 3333\n";
	char **tokens;
	size_t numtokens;

	tokens = split_from_content(line, "\n", &numtokens);
	for (size_t i = 0; i < numtokens; i++) {
		printf("    token: \"%s\"\n", tokens[i]);
		free(tokens[i]);
	}
	if (tokens != NULL)
		free(tokens);
return EXIT_SUCCESS;
}

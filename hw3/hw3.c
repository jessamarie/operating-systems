/* 
Author: Jessica Marie Barre
Email: barrej4@rpi.edu
Program name: hw3.c
Date Due: Nov 7, 2016
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include <pthread.h>
#include <stdbool.h>
#include <pthread.h>

#define TWICE 2

int size;

/** Stuctures */

typedef struct {
	char * word;
	char * filename;
} word;

/* A list of words tracked by child tid */
typedef struct {
	pthread_t tid;
	char * filepath;
	char * filename;

} file_t;


/** Global Variables **/

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
int *max_size;            /* size of allocation */
int *total_words;		  /*number of words in array */
word ** words;

/** function to find substrings **/
void findWordsWithSubstring(char* substring) {

	int current = 0;

	while (current < *total_words) {
		if (strstr(words[current]->word, substring) != NULL) {
			printf("MAIN THREAD: %s (from \"%s\")\n", words[current]->word, words[current]->filename );

		}

		/** Free words after checking since
		 * we don't need them anymore */

		free(words[current]->word);
		free(words[current]->filename);

		current++;
	}
}


/** function that contains critical section of code **/
void  critical_section(int time) {

}


/* function executed by each thread */

void * readFileIntoArray(void * arg) {


	file_t * file_thr = (file_t *) arg;

	pthread_t mytid = pthread_self();

	/* Open the file argument and read it into the words array **/

	FILE * file = fopen(file_thr->filepath, "r");
	if(file == NULL) {
		perror( "file open() failed" );
		return (void *) EXIT_FAILURE;
	}

	char buff[50];

	while (fscanf(file, "%19[a-zA-Z]%*[^a-zA-Z]", buff) != EOF) {



		pthread_mutex_lock(&mutex);

		/** Reallocate the words array if total_words is greater than the
		 * current allocated size of max_size **/

		if(*total_words > *max_size - 1) {

			*max_size = *max_size * 2;

			words = (word **) realloc (words, *max_size * sizeof( word * ) );

			if(words == NULL) {
				perror( "words realloc() failed" );
				return (void *) EXIT_FAILURE;
			}

			printf("THREAD %u: Re-allocated array of %d pointers.\n", (unsigned int) mytid, *max_size);
		}

		int index = *total_words;
		*total_words = *total_words + 1;

		pthread_mutex_unlock(&mutex);

		/** Allocate space for the current word and copy it into the words array **/

		words[index] = (word *) malloc ( sizeof ( word  ) );

		if(words[index] == NULL) {
			perror( "malloc() failed" );
			return (void *) EXIT_FAILURE;
		}

		words[index]->word = (char *) malloc ( sizeof ( char ) * (1 + strlen(buff) ) );

		if(words[index]->word == NULL) {
			perror( "malloc() failed" );
			return (void *) EXIT_FAILURE;
		}

		words[index]->filename = (char *) malloc ( sizeof ( char ) * (1 + strlen(file_thr->filename) ) );

		if(words[index]->filename == NULL) {
			perror( "malloc() failed" );
			return (void *) EXIT_FAILURE;
		}

		strcpy(words[index]->word, buff);
		strcpy(words[index]->filename, file_thr->filename);


	    printf("THREAD %lu: Added \"%s\" at index %d.\n", mytid,
	           words[index]->word, index);

	} // Ends while


	fclose(file);

	pthread_exit(NULL);
}

/* function to filter out files with non-text extension */
bool is_valid_file(char const *name)
{
	return !(strcmp(name, ".") == 0) && !(strcmp(name, "..") == 0);
}

/* Read a directory of files into an array */
file_t** readDirectory(int *num_files, char* dir_name) {


	DIR* d;
	struct dirent* dir;

	int file_count = 0;

	file_t ** files = calloc(file_count, sizeof( char * ));

	if(files == NULL) {
		perror( "files calloc() failed" );
		exit(EXIT_FAILURE);
	}

	d = opendir(dir_name);

	if (d == NULL) {
		perror("opendir() failed");
		exit(EXIT_FAILURE);
	}

	if (d) {

		while ((dir = readdir(d)) != NULL) {

			if (is_valid_file(dir->d_name)) {

				file_count++;
				int index = file_count - 1;

				/** Reallocate files array to hold another item
				 *  and allocate the new item properties  **/

				files = realloc(files, file_count * sizeof(char*));

				if (files == NULL) {
					perror("files realloc() failed");
					exit(EXIT_FAILURE);
				}

				files[index] = ( file_t * ) malloc ( sizeof ( file_t ) );

				if (files[index] == NULL) {
					perror("file malloc() failed");
					exit(EXIT_FAILURE);
				}

				files[index]->filename = (char *) malloc(sizeof(char) * (1 + strlen(dir->d_name)));

				if (files[index]->filename == NULL) {
					perror("filename malloc() failed");
					exit(EXIT_FAILURE);
				}

				size_t len = strlen( dir_name ) + strlen("/") + strlen(dir->d_name);

				files[index]->filepath = (char *) malloc(sizeof(char) * (1 + len));

				if (files[index]->filepath == NULL) {
					perror("filepath malloc() failed");
					exit(EXIT_FAILURE);
				}

				/** Assign properties to file **/
				strcpy(files[index]->filepath, dir_name);
				strcat(files[index]->filepath, "/");
				strcat(files[index]->filepath, dir->d_name);
				strcpy(files[index]->filename, dir->d_name);
			}
		}

		closedir(d);
	}

	*num_files = file_count;

	return files;
}

/* This function initializes the global variables */
void init() {

	max_size = ( int * ) malloc( sizeof( int ) );
	if (max_size == NULL) {
		perror("max_size malloc() failed");
		exit(EXIT_FAILURE);
	}
	*max_size = 8;

	total_words = ( int * ) malloc( sizeof( int ) );
	if (total_words == NULL) {
		perror("total_words malloc() failed");
		exit(EXIT_FAILURE);
	}

	*total_words = 0;


	words = ( word** ) calloc( *max_size, sizeof( word* ) );
	if (words == NULL) {
		perror("words calloc() failed");
		exit(EXIT_FAILURE);
	}

}

int main( int argc, char **argv){


	/** Check for correct number of arguments and abort
      if incorrect **/

	if ( argc != 3 ) {
		fprintf(stderr, "Error: Invalid arguments\nUsage: ./a.out <directory> <substring>");
		return EXIT_FAILURE;
	}


	/* Get arguments */
	char* dir = argv[1];
	char* substring = argv[2];

	/* Initialize Global Variables */

	init();

	printf("MAIN THREAD: Allocated initial array of %d pointers.\n",
	*max_size);


	/* Open the directory and read it's text files into
	 * the argument that will be sent to the thread function */

	int num_files;

	file_t ** files = readDirectory(&num_files, dir);


	int child, rc;

	/** Create the threads **/

	// make filepath dir + / + filename

	for(child = 0; child < num_files; child++) {

		rc = pthread_create(&files[child]->tid, NULL, readFileIntoArray, files[child]);

		if (rc != 0 ) {
			fprintf(stderr, "MAIN THREAD: Could not create child thread (%d)\n", rc);
			return EXIT_FAILURE;
		}

		printf("MAIN THREAD: Assigned \"%s\" to child thread %u.\n", files[child]->filename,  (unsigned int) files[child]->tid);

	}


	/** Wait for threads to terminate **/

	for(child = 0; child < num_files; child++) {

		pthread_join(files[child]->tid, NULL); /* Blocking call */

	}

	printf("MAIN THREAD: All done (successfully read %d words from %d files).\n", *total_words, num_files);

	printf("MAIN THREAD: Words containing substring \"%s\" are:\n", substring);


	/** Search the words array for the desired substring and print
	 out the words that have a match **/

	findWordsWithSubstring(substring);

	/** Finally, free the rest of the elements from memory **/

	free(words);

	int i;
	for (i = 0; i < num_files; i++) {

		free(files[i]->filepath);
		free(files[i]->filename);

	}
	free(files);

	return EXIT_SUCCESS;

}

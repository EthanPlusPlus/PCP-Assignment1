# Makefile for compiling and running serial program

# Directories
SRC_DIR = src/serialAbelianSandpile
BIN_DIR = bin/serialAbelianSandpile

# Source files
JAVA_FILES = $(wildcard $(SRC_DIR)/*.java)

# Compiled class files
CLASS_FILES = $(patsubst $(SRC_DIR)/%.java, $(BIN_DIR)/%.class, $(JAVA_FILES))

# Compilation flags
JAVAC_FLAGS = -d bin -sourcepath src

# Main class
MAIN_CLASS = serialAbelianSandpile.AutomatonSimulation

# Default arguments (update these if needed)
ARGS ?= input/65_by_65_all_4.csv output/65_by_65_all_4.png 8  # Replace 'default_arguments' with your specific default arguments, if any

# Targets
.PHONY: all clean run directories

all: directories $(CLASS_FILES)

directories:
	@mkdir -p $(BIN_DIR)

$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	javac $(JAVAC_FLAGS) $<

clean:
	rm -rf bin/*

run: all
	java -classpath bin $(MAIN_CLASS) $(ARGS)

run16: all
	java -classpath bin $(MAIN_CLASS) input/16_by_16_all_4.csv output/16_by_16_all_4.png 2

run65: all
	java -classpath bin $(MAIN_CLASS) input/65_by_65_all_4.csv output/65_by_65_all_4.png 8

run8: all
	java -classpath bin $(MAIN_CLASS) input/8_by_8_all_4.csv output/8_by_8_all_4.png 1

run517: all
	java -classpath bin $(MAIN_CLASS) input/517_by_517_centre_534578.csv output/517_by_517_centre_534578.png 60

run1001: all
	java -classpath bin $(MAIN_CLASS) input/1001_by_1001_all_8.csv output/1001_by_1001_all_8.png 125
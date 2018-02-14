all: compile
	@echo -e "[INFO] Done!"

pkg: all
	@echo -e "[INFO] Packaging submission"
	@tar cvf Westerman_Christopher_ASG1.tar cs455/**/**/*.class \
		README.txt makefile

clean:
	@echo -e "[INFO] Cleaning Up.."
	@-rm -rf cs455/**/**/*.class

compile:
	@echo -e "[INFO] Compiling the Source.."
	@javac -d . cs455/**/**/*.java

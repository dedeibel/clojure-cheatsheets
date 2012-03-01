#! /bin/bash

JAR_DIR="$HOME/lein/clj-1.3.0/lib"
CLASSPATH="${JAR_DIR}/clojure-1.3.0.jar"

# 3 choices for links: none, to clojure.github.org, or to
# clojuredocs.org:

#LINK_TARGET=nolinks
#LINK_TARGET=links-to-clojure
LINK_TARGET=links-to-clojuredocs

# Optionally produce PDF files by running LaTeX.  See README.markdown
# for notes on what parts of LaTeX are enough for this to work.

#PRODUCE_PDF="no"
PRODUCE_PDF="yes"

java -cp ${CLASSPATH} clojure.main clojure-cheatsheet-generator.clj ${LINK_TARGET}
EXIT_STATUS=$?

if [ ${EXIT_STATUS} != 0 ]
then
    echo "Exit status ${EXIT_STATUS} from java"
    exit ${EXIT_STATUS}
fi

# The command above will produce some warnings in a file called
# warnings.log about "No URL known for symbol with name: ''()'", etc.
# Those are harmless.

if [ ${PRODUCE_PDF} == "yes" ]
then
    for PAPER in a4 usletter
    do
	for COLOR in color grey bw
	do
	    BASENAME="cheatsheet-${PAPER}-${COLOR}"
	    latex ${BASENAME}
	    dvipdfm ${BASENAME}
	    
            # Clean up some files created by latex
	    /bin/rm -f ${BASENAME}.aux ${BASENAME}.dvi ${BASENAME}.log ${BASENAME}.out
	done
    done
    /bin/mv *.pdf ../../pdf
fi

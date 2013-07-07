#! /bin/bash

set -x
# TBD: This is a hack, depending on a particular version of
# tools.reader to have already been downloaded and copied into your
# local Maven repository.  Should use Leiningen instead.
TOOLS_READER_JAR="${HOME}/.m2/repository/org/clojure/tools.reader/0.7.3/tools.reader-0.7.3.jar"
JSR166Y_JAR="${HOME}/.m2/repository/org/codehaus/jsr166-mirror/jsr166y/1.7.0/jsr166y-1.7.0.jar"
#JAR_DIR="$HOME/lein/clojure-1.4.0/lib"
#CLASSPATH="${JAR_DIR}/clojure-1.4.0.jar:${TOOLS_READER_JAR}"
JAR_DIR="$HOME/lein/clojure-1.5.1/lib"
#CLASSPATH="${JAR_DIR}/clojure-1.5.1.jar:${TOOLS_READER_JAR}:${JSR166Y_JAR}"
lein deps
CLASSPATH=`lein classpath`

OUT_DIRECTORY=../../out

# 3 choices for links: none, to clojure.github.org, or to
# clojuredocs.org:

#LINK_TARGET=nolinks
#LINK_TARGET=links-to-clojure
LINK_TARGET=links-to-clojuredocs

TOOLTIPS=no-tooltips
#TOOLTIPS=use-title-attribute
#TOOLTIPS=tiptip

CLOJUREDOCS_SNAPSHOT=""
#CLOJUREDOCS_SNAPSHOT="${HOME}/.clojuredocs-snapshot.txt"

# Optionally produce PDF files by running LaTeX.  See README.markdown
# for notes on what parts of LaTeX are enough for this to work.

PRODUCE_PDF="no"
#PRODUCE_PDF="yes"

cp -r cheatsheet_files ${OUT_DIRECTORY}

######################################################################
# Make embeddable version for clojure.org/cheatsheet
######################################################################
echo "Generating embeddable version for clojure.org/cheatsheet ..."
java -cp ${CLASSPATH} clojure.main clojure-cheatsheet-generator.clj ${LINK_TARGET} ${TOOLTIPS} ${OUT_DIRECTORY} ${CLOJUREDOCS_SNAPSHOT}
EXIT_STATUS=$?

if [ ${EXIT_STATUS} != 0 ]; then
  echo "Exit status ${EXIT_STATUS} from java"
  exit ${EXIT_STATUS}
fi
/bin/mv ${OUT_DIRECTORY}/cheatsheet-embeddable.html ${OUT_DIRECTORY}/cheatsheet-embeddable-for-clojure.org.html

######################################################################
# Make multiple full versions for those who prefer something else,
# e.g. no tooltips.
######################################################################
for TOOLTIPS in tiptip use-title-attribute no-tooltips; do
  for CDOCS_SUMMARY in no-cdocs-summary; do
	  case "${CDOCS_SUMMARY}" in
	    no-cdocs-summary) CLOJUREDOCS_SNAPSHOT=""
	                      ;;
	    cdocs-summary) CLOJUREDOCS_SNAPSHOT="${HOME}/.clojuredocs-snapshot.txt"
	                      ;;
	  esac
	  TARGET="cheatsheet-${TOOLTIPS}-${CDOCS_SUMMARY}.html"
	  echo "Generating ${TARGET} ..."
	  java -cp ${CLASSPATH} clojure.main clojure-cheatsheet-generator.clj ${LINK_TARGET} ${TOOLTIPS} ${OUT_DIRECTORY} ${CLOJUREDOCS_SNAPSHOT} 
	  EXIT_STATUS=$?

	  if [ ${EXIT_STATUS} != 0 ]; then
	      echo "Exit status ${EXIT_STATUS} from java"
	      exit ${EXIT_STATUS}
	  fi
	  /bin/mv ${OUT_DIRECTORY}/cheatsheet-full.html ${OUT_DIRECTORY}/cheatsheet-${TOOLTIPS}-${CDOCS_SUMMARY}.html
	  # Uncomment following line if you want to test new changes
	  # with generating only the first variant of the cheatsheet.
	  #exit 0
  done
done

# The command above will produce some warnings in a file called
# warnings.log about "No URL known for symbol with name: ''()'", etc.
# Those are harmless.

if [ ${PRODUCE_PDF} == "yes" ]; then
  cd ${OUT_DIRECTORY}
  for PAPER in a4 usletter; do
	  for COLOR in color grey bw; do
	    BASENAME="cheatsheet-${PAPER}-${COLOR}"
	    latex ${BASENAME}
	    dvipdfm ${BASENAME}
	    
            # Clean up some files created by latex
	    /bin/rm -f ${BASENAME}.aux ${BASENAME}.dvi ${BASENAME}.log ${BASENAME}.out
	  done
  done
  /bin/mv *.pdf ../../pdf
  cd -
fi

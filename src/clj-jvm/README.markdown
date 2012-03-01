# Clojure/Java cheat sheet generator

The program `clojure-cheatsheet-generator.clj` and accompanying shell
script `run.sh` can generate HTML and LaTeX versions of the
Clojure/Java cheat sheet.  A suitable LaTeX installation on your
computer can then be used to generate PDF files as well.  They are all
generated with structure and symbols specified in the value of
`cheatsheet-structure` in the Clojure source file.  They contain
clickable links to the documentation on either
[clojure.github.com][clojure github] or [clojuredocs.org][clojuredocs]
(or no links at all).

[clojure github]: http://clojure.github.com
[clojuredocs]: http://clojuredocs.org

The cheat sheet generated is currently specific to Clojure 1.3.  It is
easier to simply change this program as desired for later versions of
Clojure, rather than try to have the same program able to generate
cheatsheets for multiple Clojure versions.

# Generating cheat sheet files

Edit `run.sh` to specify the location of your Clojure JAR file in the
`CLASSPATH` variable.  Also change the values of `LINK_TARGET` and
`PRODUCE_PDF` variables to your liking.  If you want to produce PDF
files, you must have a suitable LaTeX installation on your computer.
See "LaTeX installation notes" below for what is needed.

Run this command:

    ./run.sh

Output files are:

* `cheatsheet-full.html`
* `cheatsheet-embeddable.html`

    The full version is a standalone HTML file that is useful for
    viewing locally with a web browser while testing changes to the
    program.  The embeddable version is almost the same, except only
    includes those things needed for publishing easily at
    [http://clojure.org/cheatsheet][cheatsheet].

[cheatsheet]: http://clojure.org/cheatsheet

* `warnings.log`

    Warning messages about any symbols for which no links to
    documentation could be found in the internal map called
    `symbol-name-to-url`.  Also a list of all symbols that are in that
    map which are not mentioned in the cheat sheet.  These may be
    useful to add to future revisions of the cheat sheet, if they are
    considered important enough.

* `cheatsheet-usletter-grey.tex`
* `cheatsheet-usletter-color.tex`
* `cheatsheet-usletter-bw.tex`
* `cheatsheet-a4-grey.tex`
* `cheatsheet-a4-color.tex`
* `cheatsheet-a4-bw.tex`

    LaTeX source files for black & white (bw), grayscale, and color
    versions of the cheat sheet, on either A4 or US letter size paper.
    The font size has been tuned by hand so that everything fits onto
    2 pages on either size of paper.  Search for `fontsize` in the
    Clojure source file if future modifications to the cheat sheet
    warrant further modification of these.

If you enable it in `run.sh`, corresponding PDF files will also be
generated for each of the LaTeX files.


Things still missing:

* No footer with version number, date, and attributions at the bottom.


# LaTeX installation notes

On a Mac with MacPorts 2.0.3, the following two packages, plus what
they depend upon, are sufficient:

* texlive-latex-recommended (needed for scrreprt.cls)
* texlive-fonts-recommended (needed for the lmodern font)

This command will install these packages:

    % sudo port install texlive-latex-recommended texlive-fonts-recommended


On an Ubuntu 64-bit 10.4 LTS system, I got good results after
installing the following packages:

* texlive-latex-base
* texlive-latex-extra


## License

Copyright (C) 2012 Andy Fingerhut

Distributed under the Eclipse Public License, the same as Clojure.

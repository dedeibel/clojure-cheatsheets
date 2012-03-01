(ns clojure-cheatsheet-generator
  (:require [clojure.string :as str])
  (:require [clojure.set :as set])
  (:require [clojure.java.javadoc])
  (:require [clojure.java.io])
  (:require [clojure data pprint repl set string xml zip]))

;; Andy Fingerhut
;; andy_fingerhut@alum.wustl.edu
;; Feb 21, 2011

;; TBD: Fix the broken link for ->> in the generated PDF files.

;; TBD: For commands specified via strings rather than symbols, "@",
;; make a way to specify the desired target URL right in the
;; structure.  Maybe Clojure metadata would be a good way to do this,
;; since I expect it to be fairly uncommon?

;; TBD: Make an option to use a local file:///... root directory for
;; the URLs in links, in case someone wants to have a local copy on a
;; machine that is not always connected to the Internet, similar to
;; how the CLHS Hyperspec can be locally used?


;; Documentation describing the structure of the value of
;; cheatsheat-structure:

;; At the top level, the structure must be:
;;
;; [:title "title string"
;;  :page <page-desc>
;;  :page <page-desc>
;;  ... as many pages as you want here ...
;; ]

;; A <page-desc> looks like:
;;
;; [:column <box-desc1> <box-desc2> ... :column <box-desc7> <box-desc8> ...]
;;
;; There must be exactly two :column keywords, one at the beginning,
;; and the rest must be <box-descriptions>.

;; A <box-desc> looks like:
;;
;; [:box "box color"
;;  :section "section name"
;;  :subsection "subsection name"
;;  :table <table-desc>
;;  ... ]
;;
;; It must begin with :box "box color", but after that there can be as
;; many of the following things as you wish.  They can be placed in
;; any order, i.e. there is no requirement that you have a :section
;; before a :subsection, or have either one at all.  The only
;; difference between :section and :subsection is the size of the
;; heading created (e.g. <h2> vs. <h3> in HTML).
;;
;; :section "section name"
;; :subsection "subsection name"
;; :table <table-desc>
;; :cmds-one-line [vector of cmds]

;; A <table-desc> is a vector of <row-desc>s, where each <row-desc>
;; looks like:
;;
;; ["string" :cmds '[<cmd1> <cmd2> <cmd3> ...]]
;;
;; or:
;;
;; ["string" :str <str-spec>]
;;
;; You can optionally replace :cmds with :cmds-with-frenchspacing
;;
;; Each <cmd> or <str-spec> (and "string" at the beginning) can be any
;; one of:
;;
;; (a) a symbol, in which case it should have a link created to the
;; documentation URL.
;;
;; (b) a vector of the form [:common-prefix prefix-symbol- suffix1
;; suffix2 ...].  This will be shown in a form that looks similar to
;; "prefix-symbol-{suffix1, suffix2, ...}", where suffix1 will have a
;; link to the documentation for the symbol named
;; prefix-symbol-suffix1 (if there is any), and similarly for the
;; other suffixes.  Note: This cannot be used in the "string" position
;; in the examples above, only in place of one or more of the commands
;; after :cmds, or in place of <str-spec>.  This is useful for
;; economizing on space for names like unchecked-add, unchecked-dec,
;; etc.
;;
;; (c) a vector of the form [:common-suffix -suffix prefix1 prefix2
;; ...].  This is similar to :common-prefix above, except that it will
;; be shown similar to "{prefix1, prefix2, ...}-suffix", and the
;; symbols whose documentation will be linked to will be
;; prefix1-suffix, prefix2-suffix, etc.
;;
;; (d) a vector of the form [:common-prefix-suffix prefix- -suffix
;; middle1 middle2 ...].  Similar to :common-prefix and :common-suffix
;; above, except that it will be shown similar to "prefix-{middle1,
;; middle2, ...}-suffix", and the symbols whose documentation will be
;; linked to will be prefix-middle1-suffix, prefix-middle2-suffix,
;; etc.
;;
;; (e) a string, in which case it should simply be copied to the
;; output as is.
;;
;; (f) a 'conditional string', which is a Clojure map of the form
;; {:html "html-string", :latex "latex-string}.  Only "html-string"
;; will be output if HTML output is being gnerated, and only
;; "latex-string" will be output if LaTeX output is being generated.

;; Note: The last <box-desc> in the last page can optionally be
;; replaced with a <footer-desc>, but only one, and only in that
;; place.  TBD: This has not yet been implemented.


(def cheatsheet-structure
     [:title "Clojure Cheat Sheet (Clojure 1.3.0, sheet v1.3)"
      :page [:column
             [:box "green"
              :section "Documentation"
              :table [["clojure.repl/"
                       :cmds '[clojure.repl/doc clojure.repl/find-doc
                               clojure.repl/apropos clojure.repl/source
                               clojure.repl/pst clojure.java.javadoc/javadoc
                               "(foo.bar/ is namespace for later syms)"]]]
              ]
             [:box "blue"
              :section "Primitives"
              :subsection "Numbers"
              :table [["Arithmetic" :cmds '[+ - * / quot rem mod inc dec
                                            max min]]
                      ["Compare" :cmds '[= == not= < > <= >= compare]]
                      ["Bitwise" :cmds '[[:common-prefix bit- and or xor not
                                          flip set shift-right shift-left
                                          and-not clear test]]]
                      ["Cast" :cmds '[byte short int long float double
                                      bigdec bigint num rationalize biginteger]]
                      ["Test" :cmds-with-frenchspacing '[nil? identical? zero?
                                                         pos? neg? even? odd?]]
                      ["Random" :cmds '[rand rand-int]]
                      ["BigInt" :cmds '[with-precision]]
                      ["Unchecked" :cmds '[[:common-prefix-suffix
                                            unchecked- -int
                                            add dec divide inc multiply negate
                                            remainder subtract]]]]
              :subsection "Strings"
              :table [["Create" :cmds '[str format
                                        {:latex "\\textmd{\\textsf{See also IO/to string}}",
                                         :html "See also IO/to string"}]]
                      ["Use" :cmds '[count get subs compare
                                     {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                      :html "(clojure.string/)"}
                                     clojure.string/join clojure.string/escape
                                     clojure.string/split clojure.string/split-lines
                                     clojure.string/replace
                                     clojure.string/replace-first
                                     clojure.string/reverse]]
                      ["Regex" :cmds '[{:latex "\\#\"pattern\"",
                                        :html "#\"<var>pattern</var>\""}
                                       re-find re-seq re-matches
                                       re-pattern re-matcher re-groups
;                                       {:latex "\\textmd{\\textsf{(clojure.string/)}}",
;                                        :html "(clojure.string/)"}
                                       clojure.string/replace
                                       clojure.string/replace-first]]
                      ["Letters" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                          :html "(clojure.string/)"}
                                         clojure.string/capitalize
                                         clojure.string/lower-case
                                         clojure.string/upper-case]]
                      ["Trim" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                       :html "(clojure.string/)"}
                                      clojure.string/trim clojure.string/trim-newline
                                      clojure.string/triml clojure.string/trimr]]
                      ["Test" :cmds '[char char? string?
                                      {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                       :html "(clojure.string/)"}
                                      clojure.string/blank?]]
                      ]
              :subsection "Other"
              :table [["Characters" :cmds '[char char-name-string
                                            char-escape-string]]
                      ["Keywords" :cmds '[keyword keyword? find-keyword]]
                      ["Symbols" :cmds '[symbol symbol? gensym]]]
              ]
             [:box "yellow"
              :section "Collections"
              :subsection "Collections"
              :table [["Generic ops" :cmds '[count empty not-empty into conj]]
                      ["Content tests" :cmds '[distinct? empty?
                                               every? not-every? some not-any?]]
                      ["Capabilities" :cmds '[sequential? associative? sorted?
                                              counted? reversible?]]
                      ["Type tests" :cmds '[coll? list? vector? set? map?
                                            seq?]]]
              :subsection "Lists"
              :table [["Create" :cmds '["'()" list list*]]
                      ["Examine" :cmds-with-frenchspacing '[first nth peek]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[cons conj rest pop]]
                      ]
              :subsection "Vectors"
              :table [["Create" :cmds '["[]" vector vec vector-of]]
                      ["Examine" :cmds '[{:latex "\\cmd{(my-vec idx)} $\\to$ \\cmd{(}",
                                          :html "<code>(my-vec idx)</code> &rarr; <code>("}
                                         nth
                                         {:latex " \\cmd{my-vec idx)}",
                                          :html " my-vec idx)</code>"}
                                         get peek]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[assoc pop subvec replace conj rseq]]]
              :subsection "Sets"
              :table [["Create" :cmds '[{:latex "\\#\\{\\}", :html "#{}"}
                                        set hash-set sorted-set sorted-set-by]]
                      ["Examine" :cmds '[{:latex "\\cmd{(my-set item)} $\\to$ \\cmd{(}",
                                          :html "<code>(my-set item)</code> &rarr; <code>("}
                                         get
                                         {:latex " \\cmd{my-set item)}",
                                          :html " my-set item)</code>"}
                                         contains?]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[conj disj]]
                      ["Rel algebra"
                       :cmds '[
                               {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/join clojure.set/select
                               clojure.set/project clojure.set/union
                               clojure.set/difference clojure.set/intersection]]
                      ["Get map"
                       :cmds '[{:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/index clojure.set/rename-keys
                               clojure.set/rename clojure.set/map-invert]]
                      ["Test"
                       :cmds '[{:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/subset? clojure.set/superset?]]
                      ]
              :subsection "Maps"
              :table [["Create" :cmds '[{:latex "\\{\\}", :html "{}"}
                                        hash-map array-map zipmap
                                        sorted-map sorted-map-by bean
                                        frequencies]]
                      ["Examine" :cmds '[
                                         {:latex "\\cmd{(:key my-map)} $\\to$ \\cmd{(}",
                                          :html "<code>(:key my-map)</code> &rarr; <code>("}
                                         get
                                         {:latex " \\cmd{my-map :key)}",
                                          :html " my-map :key)</code>"}
                                         get-in contains? find keys vals]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[assoc assoc-in dissoc merge
                               merge-with select-keys update-in]]
                      ["Entry" :cmds '[key val]]
                      ["Sorted maps" :cmds '[rseq subseq rsubseq]]]
              ]
             :column
             [:box "yellow"
              :subsection {:latex "Transients (\\href{http://clojure.org/transients}{clojure.org/transients})"
                           :html "Transients (<a href=\"http://clojure.org/transients\">clojure.org/transients</a>)"}
              :table [["Create" :cmds '[transient persistent!]]
                      ["Change" :cmds-with-frenchspacing
                       '[conj! pop! assoc! dissoc! disj!
                         {:latex "\\textmd{\\textsf{Note: always use return value for later changes, never original!}}",
                          :html "Note: always use return value for later changes, never original!"}]]]
              :subsection "Misc"
              :table [["Compare" :cmds '[= == identical? not= not compare
                                         clojure.data/diff]]
                      ["Test" :cmds '[true? false? nil? instance?]]]
              ]
             [:box "orange"
              :section "Sequences"
              :subsection "Creating a Lazy Seq"
              :table [["From collection" :cmds '[seq vals keys rseq
                                                 subseq rsubseq]]
                      ["From producer fn" :cmds '[lazy-seq repeatedly iterate]]
                      ["From constant" :cmds '[repeat range]]
                      ["From other" :cmds '[file-seq line-seq resultset-seq
                                            re-seq tree-seq xml-seq
                                            iterator-seq enumeration-seq]]
                      ["From seq" :cmds '[keep keep-indexed]]]
              :subsection "Seq in, Seq out"
              :table [["Get shorter" :cmds '[distinct filter remove for]]
                      ["Get longer" :cmds '[cons conj concat lazy-cat mapcat
                                            cycle interleave interpose]]
                      ["Tail-items" :cmds '[rest nthrest fnext nnext
                                            drop drop-while take-last for]]
                      ["Head-items" :cmds '[take take-nth take-while
                                            butlast drop-last for]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[conj concat distinct flatten group-by
                               partition partition-all partition-by
                               split-at split-with filter remove
                               replace shuffle]]
                      ["Rearrange" :cmds '[reverse sort sort-by compare]]
                      ["Process items" :cmds '[map pmap map-indexed
                                              mapcat for replace seque]]]
              :subsection "Using a Seq"
              :table [["Extract item" :cmds '[first second last rest next
                                              ffirst nfirst fnext
                                              nnext nth nthnext rand-nth
                                              when-first max-key min-key]]
                      ["Construct coll" :cmds '[zipmap into reduce reductions
                                                set vec into-array to-array-2d]]
                      ["Pass to fn" :cmds '[apply]]
                      ["Search" :cmds '[some filter]]
                      ["Force evaluation" :cmds '[doseq dorun doall]]
                      ["Check for forced" :cmds '[realized?]]]
              ]
             [:box "green"
              :subsection "Zippers (clojure.zip/)"
              :table [["Create" :cmds '[clojure.zip/zipper
                                        clojure.zip/seq-zip
                                        clojure.zip/vector-zip
                                        clojure.zip/xml-zip]]
                      ["Get loc" :cmds '[clojure.zip/up
                                         clojure.zip/down clojure.zip/left
                                         clojure.zip/right
                                         clojure.zip/leftmost
                                         clojure.zip/rightmost]]
                      ["Get seq" :cmds '[clojure.zip/lefts clojure.zip/rights
                                         clojure.zip/path clojure.zip/children]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[clojure.zip/make-node clojure.zip/replace
                               clojure.zip/edit clojure.zip/insert-child
                               clojure.zip/insert-left clojure.zip/insert-right
                               clojure.zip/append-child clojure.zip/remove]]
                      ["Move" :cmds '[clojure.zip/next clojure.zip/prev]]
                      ["Misc" :cmds '[clojure.zip/root clojure.zip/node
                                      clojure.zip/branch? clojure.zip/end?]]]
              ]
             [:box "magenta"
              :section "IO"
              :table [
;                      ["to/from ..."
                      [{:latex "\\begin{tabular}[t]{@{}l@{}} to/from \\\\ ... \\end{tabular}"
                        :html "to/from ..."}
                        :cmds '[spit slurp
                                {:latex "\\textmd{\\textsf{(to writer/from reader, Socket, string with file name, URI, etc.)}}",
                                 :html "(to writer/from reader, Socket, string with file name, URI, etc.)"}
                                ]]
                      ["to *out*" :cmds '[pr prn print printf println
                                          newline
                                           {:latex "\\textmd{\\textsf{(clojure.pprint/)}}",
                                            :html "(clojure.pprint/)"}
                                          clojure.pprint/print-table]]
                      ["to writer" :cmds '[{:latex "\\textmd{\\textsf{(clojure.pprint/)}}",
                                            :html "(clojure.pprint/)"}
                                           clojure.pprint/pprint
                                           clojure.pprint/cl-format
                                           {:latex "\\textmd{\\textsf{also:}}",
                                            :html "also:"}
                                           "(binding [*out* writer] ...)"]]
                      ["to string" :cmds '[format with-out-str pr-str
                                           prn-str print-str println-str]]
                      ["from *in*" :cmds '[read-line read]]
                      ["from reader" :cmds '[line-seq
                                             {:latex "\\textmd{\\textsf{also:}}",
                                              :html "also:"}
                                             "(binding [*in* reader] ...)"
                                             {:latex "\\href{http://java.sun.com/javase/6/docs/api/java/io/Reader.html}{java.io.Reader}"
                                              :html "<a href=\"http://java.sun.com/javase/6/docs/api/java/io/Reader.html\">java.io.Reader</a>"}
                                             ]]
                      ["from string" :cmds '[read-string with-in-str]]
                      ["Open" :cmds '[with-open
;                                      {:latex "\\textmd{\\textsf{string:}}",
;                                       :html "string:"}
;                                      with-out-str with-in-str
                                      {:latex "\\textmd{\\textsf{(clojure.java.io/)}}",
                                       :html "(clojure.java.io/)"}
                                      {:latex "\\textmd{\\textsf{text:}}",
                                       :html "text:"}
                                      clojure.java.io/reader clojure.java.io/writer
                                      {:latex "\\textmd{\\textsf{binary:}}",
                                       :html "binary:"}
                                      clojure.java.io/input-stream clojure.java.io/output-stream
                                      ]]
                      ["Binary" :cmds '["(.write ostream byte-arr)"
                                        "(.read istream byte-arr)"
;                                        "(javadoc java.io.OutputStream)"
                                        {:latex "\\href{http://java.sun.com/javase/6/docs/api/java/io/OutputStream.html}{java.io.OutputStream}"
                                         :html "<a href=\"http://java.sun.com/javase/6/docs/api/java/io/OutputStream.html\">java.io.OutputStream</a>"}
;                                        "java.io.InputStream"
                                        {:latex "\\href{http://java.sun.com/javase/6/docs/api/java/io/InputStream.html}{java.io.InputStream}"
                                         :html "<a href=\"http://java.sun.com/javase/6/docs/api/java/io/InputStream.html\">java.io.InputStream</a>"}
                                        {:latex "\\textmd{\\textsf{GitHub:}}",
                                         :html "GitHub:"}
                                        {:latex "\\href{http://github.com/ztellman/gloss}{gloss}"
                                         :html "<a href=\"http://github.com/ztellman/gloss\">gloss</a>"}
                                        {:latex "\\href{http://github.com/rosejn/byte-spec}{byte-spec}"
                                         :html "<a href=\"http://github.com/rosejn/byte-spec\">byte-spec</a>"}
                                        ]]
                      ["Misc" :cmds '[flush "(.close s)" file-seq
                                      *in* *out* *err*
                                      {:latex "\\textmd{\\textsf{(clojure.java.io/)}}",
                                       :html "(clojure.java.io/)"}
                                      clojure.java.io/file
                                      clojure.java.io/copy
                                      {:latex "\\textmd{\\textsf{GitHub:}}",
                                       :html "GitHub:"}
                                      {:latex "\\href{http://github.com/Raynes/fs}{fs}"
                                       :html "<a href=\"http://github.com/Raynes/fs\">fs</a>"}
                                      ]]
                      ]
              ]
             ]
      :page [:column
             [:box "blue"
              :section "Functions"
              :table [["Create" :cmds '[fn defn defn- definline identity
                                        constantly memfn comp complement
                                        partial juxt memoize fnil every-pred
                                        some-fn]]
                      ["Call" :cmds '[-> ->> apply]]
                      ["Test" :cmds '[fn? ifn?]]]
              ]
             [:box "orange"
              :section "Abstractions"
              :subsection {:latex "Protocols (\\href{http://clojure.org/protocols}{clojure.org/protocols})"
                           :html "Protocols (<a href=\"http://clojure.org/protocols\">clojure.org/protocols</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defprotocol
                                        {:latex "\\cmd{Slicey (slice [at]))}"
                                         :html "<code>Slicey (slice [at]))</code>"}
                                        ]]
                      ["Extend" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        extend-type
                                        {:latex "\\cmd{String Slicey (slice [at] ...))}"
                                         :html "<code>String Slicey (slice [at] ...))</code>"}
                                        ]]
                      ["Extend null" :cmds '[
                                             {:latex "\\cmd{(}"
                                              :html "<code>(</code>"}
                                             extend-type
                                             {:latex "\\cmd{nil Slicey (slice [\\_] nil))}"
                                              :html "<code>nil Slicey (slice [_] nil))</code>"}
                                             ]]
                      ["Reify" :cmds '[
                                       {:latex "\\cmd{(}"
                                        :html "<code>(</code>"}
                                       reify
                                       {:latex "\\cmd{Slicey (slice [at] ...))}"
                                        :html "<code>Slicey (slice [at] ...))</code>"}
                                       ]]
                      ]
              :subsection {:latex "Records (\\href{http://clojure.org/datatypes}{clojure.org/datatypes})"
                           :html "Records (<a href=\"http://clojure.org/datatypes\">clojure.org/datatypes</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defrecord
                                        {:latex "\\cmd{Pair [h t])}"
                                         :html "<code>Pair [h t])</code>"}
                                        ]]
                      ["Access" :cmds '[
                                        {:latex "\\cmd{(:h (Pair. 1 2))} $\\to$ \\cmd{1}"
                                         :html "<code>(:h (Pair. 1 2))</code> &rarr; <code>1</code>"}
                                        ]]
                      ["Create" :cmds '[
                                        Pair. ->Pair map->Pair
                                        ]]
                      ]
              :subsection {:latex "Types (\\href{http://clojure.org/datatypes}{clojure.org/datatypes})"
                           :html "Types (<a href=\"http://clojure.org/datatypes\">clojure.org/datatypes</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        deftype
                                        {:latex "\\cmd{Pair [h t])}"
                                         :html "<code>Pair [h t])</code>"}
                                        ]]
                      ["Access" :cmds '[
                                        {:latex "\\cmd{(.h (Pair. 1 2))} $\\to$ \\cmd{1}"
                                         :html "<code>(.h (Pair. 1 2))</code> &rarr; <code>1</code>"}
                                        ]]
                      ["Create" :cmds '[Pair. ->Pair]]
                      ["With methods" :cmds '[
                                              {:latex "\\begin{tabular}[c]{@{}l@{}} \\cmd{(}"
                                               :html "<code>(</code>"}
                                              deftype
                                              {:latex "\\cmd{Pair [h t]} \\\\ \\ \\ \\cmd{Object} \\\\ \\ \\ \\cmd{(toString [this] (str \"<\" h \",\" t \">\")))}\\end{tabular}"
                                               :html "<code>Pair [h t]<br>&nbsp;&nbsp;Object<br>&nbsp;&nbsp;(toString [this] (str \"<\" h \",\" t \">\")))</code>"}
                                              ]]
                      ]
              :subsection {:latex "Multimethods (\\href{http://clojure.org/multimethods}{clojure.org/multimethods})"
                           :html "Multimethods (<a href=\"http://clojure.org/multimethods\">clojure.org/multimethods</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defmulti
                                        {:latex "\\cmd{my-mm dispatch-fn)}"
                                         :html "<code>my-mm dispatch-fn)</code>"}
                                        ]]
                      ["Method define" :cmds '[
                                               {:latex "\\cmd{(}"
                                                :html "<code>(</code>"}
                                               defmethod
                                               {:latex "\\cmd{my-mm :dispatch-value [args] ...)}"
                                                :html "<code>my-mm :dispatch-value [args] ...)</code>"}
                                               ]]
;                      ["Create" :cmds '[defmulti defmethod]]
                      ["Dispatch" :cmds '[get-method methods]]
                      ["Remove" :cmds '[remove-method remove-all-methods]]
                      ["Prefer" :cmds '[prefer-method prefers]]
                      ["Relation" :cmds '[derive isa? parents ancestors
                                          descendants make-hierarchy]]]
              ]
             [:box "green"
              :section "Macros"
              :table [["Create" :cmds '[defmacro definline
                                        macroexpand-1 macroexpand]]
                      ["Branch" :cmds '[and or when when-not when-let
                                        when-first if-not if-let cond condp
                                        case]]
                      ["Loop" :cmds '[for doseq dotimes while]]
                      ["Arrange" :cmds '[.. doto ->]]
                      ["Scope" :cmds '[binding locking time
                                       [:common-prefix with-
                                       in-str local-vars open out-str
                                       precision redefs redefs-fn]]]
                      ["Lazy" :cmds '[lazy-cat lazy-seq delay]]
                      ["Doc." :cmds '[assert comment clojure.repl/doc]]]
              ]
             [:box "yellow"
              :section "Reader Macros"
              ;; TBD: Should probably think of a good place for all of
              ;; the reader macro descriptions to link to.  A few
              ;; suggestions are given in comments below.
              :table [[{:latex "\\cmd{'}",
                        :html "<code>'</code>"}
                       ;; TBD: This should point to same URL that 'quote' does
                       :str {:latex "Quote 'form $\\to$ (quote form)",
                             :html "Quote: <code>'<var>form</var></code> &rarr; <code>(quote <var>form</var>)</code>"}]
                      [{:latex "\\cmd{\\textbackslash}",
                        :html "<code>\\</code>"}
                       :str "Character literal"]
                      [{:latex "\\cmd{;}",
                        :html "<code>;</code>"}
                       :str "Single line comment"]
                      [{:latex "\\cmd{\\^{}}",
                        :html "<code>^</code>"}
                       :str "Metadata (see Metadata section)"]
                      [{:latex "\\cmd{@}",
                        :html "<code>@</code>"}
                       ;; TBD: This should point to same URL that 'deref' does
                       :str {:latex "Deref @form $\\to$ (deref form)",
                             :html "Deref: <code>@<var>form</var></code> &rarr; <code>(deref <var>form</var>)</code>"}]
                      [{:latex "\\cmd{`}",
                        :html "<code>`</code>"}
                       :str "Syntax-quote"]
                      [{:latex "\\cmd{\\textasciitilde}",
                        :html "<code>~</code>"}
                       :str "Unquote"]
                      [{:latex "\\cmd{\\textasciitilde@}",
                        :html "<code>~@</code>"}
                       :str "Unquote-splicing"]
                      [{:latex "\\cmd{\\#\"}\\textit{p}\\cmd{\"}",
                        :html "<code>#\"<var>p</var>\"</code>"}
                       :str {:latex "Regex Pattern \\textit{p}",
                             :html "Regex Pattern <var>p</var>"}]
                      [{:latex "\\cmd{\\#$'$}",
                        :html "<code>#'</code>"}
                       ;; TBD: This should point to same URL that 'var' does
                       :str {:latex "Var quote \\#$'$x $\\to$ (var x)",
                             :html "Var quote: <code>#'<var>x</var></code> &rarr; <code>(var <var>x</var>)</code>"}]
                      [{:latex "\\cmd{\\#()}",
                        :html "<code>#()</code>"}
                       ;; TBD: This should point to same URL that 'fn' does
                       :str {:latex "\\#(...) $\\to$ (fn [args] (...))",
                             :html "<code>#(...)</code> &rarr; <code>(fn [args] (...))</code>"}]
                      [{:latex "\\cmd{\\#\\_}",
                        :html "<code>#_</code>"}
                       :str "Ignore next form"]]
              ]
             [:box "red"
              :section {:latex "Metadata (\\href{http://clojure.org/special\\_forms}{clojure.org/special\\_forms})"
                        :html "Metadata (<a href=\"http://clojure.org/special_forms\">clojure.org/special_forms</a>)"}
              :table [
                      ["General" :cmds [{:latex "\\cmd{\\^{}\\{:key1 val1 :key2 val2 ...\\}}"
                                         :html "<code>^{:key1 val1 :key2 val2 ...}</code>"}
                                        ]]
                      ["Abbrevs" :cmds [{:latex "\\cmd{\\^{}Type} $\\to$ \\cmd{\\^{}\\{:tag Type\\}},
\\cmd{\\^{}:key} $\\to$ \\cmd{\\^{}\\{:key true\\}}"
                                         :html
                                         "<code>^Type</code> &rarr; <code>^{:tag Type}</code><br>
<code>^:key</code> &rarr; <code>^{:key true}</code>"}
                                        ]]
                      ["Common" :cmds [{:latex (str
                                                "\\cmd{\\^{}:dynamic} "
                                                "\\cmd{\\^{}:private} "
                                                "\\cmd{\\^{}:static} "
                                                "\\cmd{\\{:doc \"str\"\\}}"
                                                )
                                        :html (str
                                               "<code>"
                                               "^:dynamic "
                                               "^:private "
                                               "^:static "
                                               "{:doc \"str\"}"
                                               "</code>")}
                                        ]]
                      ["Examples" :cmds '[
                                         {:latex "\\cmd{(defn \\^{}:private \\^{}:static \\^{}String my-fn ...)}"
                                          :html "<code>(defn ^:private ^:static ^String my-fn ...)</code>"}
                                         {:latex " \\ \\ \\ " ; fragile hack to get 2nd example to start on next line
                                          :html " <br>"}
                                         {:latex "\\cmd{(def \\^{}:dynamic *dyn-var* val)}"
                                          :html "<code>(def ^:dynamic *dyn-var* val)</code>"}
                                         ]]
;;                      ["Others" :cmds [
;;                                       {:latex (str
;;                                                "\\cmd{:added}"
;;                                                " \\cmd{:author}"
;;                                                " \\cmd{:doc} "
;;                                                " \\cmd{:arglists} "
;;                                                " \\cmd{:inline}"
;;                                                " \\cmd{:inline-arities}"
;;                                                " \\cmd{:macro}"
;; ;                                                " (examples in Clojure source)"
;; ;                                                " (see Clojure source for examples.  Can use arbitrary keys for your own purposes.)"
;;                                                )
;;                                        :html (str
;;                                               "<code>"
;;                                               ":added"
;;                                               " :author"
;;                                               " :arglists "
;;                                               " :doc "
;;                                               " :inline"
;;                                               " :inline-arities"
;;                                               " :macro"
;;                                               "</code>"
;; ;                                               " (examples in Clojure source)"
;; ;                                               " (see Clojure source for examples.  Can use arbitrary keys for your own purposes.)"
;;                                               )}
;;                                       ]]
                      ["On Vars" :cmds '[meta with-meta vary-meta
                                         alter-meta! reset-meta!
                                         clojure.repl/doc
                                         clojure.repl/find-doc test]]
                      ]
              ]
             :column
             [:box "red"
              :section {:latex "Special Forms (\\href{http://clojure.org/special\\_forms}{clojure.org/special\\_forms})"
                        :html "Special Forms (<a href=\"http://clojure.org/special_forms\">clojure.org/special_forms</a>)"}
              :cmds-one-line '[def if do let quote var fn loop
                               recur throw try monitor-enter monitor-exit]
              :table [["Destructuring" :cmds '[
                                               {:latex "(\\href{http://clojure.org/special\\_forms\\#let}{examples})"
                                                :html "(<a href=\"http://clojure.org/special_forms#let\">examples</a>)"}
                                               let fn defn defmacro
                                               loop for doseq if-let when-let]]
                      ]
              ]
             [:box "blue2"
              :section {:latex "Vars and global environment (\\href{http://clojure.org/vars}{clojure.org/vars})"
                        :html "Vars and global environment (<a href=\"http://clojure.org/vars\">clojure.org/vars</a>)"}
              :table [["Def variants" :cmds '[def defn defn- definline defmacro
                                              defmethod defmulti defonce
                                              defrecord]]
                      ["Interned vars" :cmds '[declare intern binding
                                               find-var var]]
                      ["Var objects" :cmds '[with-local-vars var-get var-set
                                             alter-var-root var?]]
                      ["Var validators" :cmds '[set-validator! get-validator]]
                      ;; Now covered in Metadata section
;;                      ["Var metadata" :cmds '[meta clojure.repl/doc
;;                                              clojure.repl/find-doc test]]
                      ]
              ]
             [:box "yellow"
              :section "Namespace"
              :table [["Current" :cmds '[*ns*]]
                      ["Create/Switch" :cmds '[in-ns ns create-ns]]
                      ["Add" :cmds '[alias def import intern refer]]
                      ["Find" :cmds '[all-ns find-ns]]
;;                      ["Examine" :cmds '[ns-name ns-aliases ns-map
;;                                         ns-interns ns-publics ns-refers
;;                                         ns-imports]]
                      ["Examine" :cmds '[[:common-prefix ns-
                                          name aliases map interns publics
                                          refers imports]]]
                      ["From symbol" :cmds '[resolve ns-resolve namespace]]
                      ["Remove" :cmds '[ns-unalias ns-unmap remove-ns]]]
              ]
             [:box "green"
              :section "Loading"
              :table [["Load libs" :cmds '[require use import refer]]
                      ["List loaded" :cmds '[loaded-libs]]
                      ["Load misc" :cmds '[load load-file load-reader
                                           load-string]]]
              ]
             [:box "magenta"
              :section "Concurrency"
              :table [["Atoms" :cmds '[atom swap! reset! compare-and-set!]]
                      ["Futures" :cmds '[future
                                         [:common-prefix future-
                                          call done? cancel cancelled?]
                                         future?]]
                      ["Threads" :cmds '[bound-fn bound-fn*
                                         [:common-suffix -thread-bindings
                                          get push pop]
                                         thread-bound?]]
                      ["Misc" :cmds '[locking pcalls pvalues pmap seque
                                      promise deliver]]]
              :subsection {:latex "Refs and Transactions (\\href{http://clojure.org/refs}{clojure.org/refs})"
                           :html "Refs and Transactions (<a href=\"http://clojure.org/refs\">clojure.org/refs</a>)"}
              :table [["Create" :cmds '[ref]]
                      ["Examine"
                       :cmds '[deref "@"
                               {:latex "\\textmd{\\textsf{(@form $\\to$ (deref form))}}",
                                :html "(<code>@<var>form</var></code> &rarr; <code>(deref <var>form</var>)</code>)"}]]
                      ["Transaction" :cmds '[sync dosync io!]]
                      ["In transaction" :cmds '[ensure ref-set alter commute]]
                      ["Validators" :cmds '[set-validator! get-validator]]
                      ["History" :cmds '[ref-history-count
                                         [:common-prefix-suffix
                                          ref- -history min max]]]]
              :subsection {:latex "Agents and Asynchronous Actions (\\href{http://clojure.org/agents}{clojure.org/agents})"
                           :html "Agents and Asynchronous Actions (<a href=\"http://clojure.org/agents\">clojure.org/agents</a>)"}
              :table [["Create" :cmds '[agent]]
                      ["Examine" :cmds '[agent-error]]
                      ["Change state" :cmds '[send send-off restart-agent]]
                      ["Block waiting" :cmds '[await await-for]]
                      ["Ref validators" :cmds '[set-validator! get-validator]]
                      ["Watchers" :cmds '[add-watch remove-watch]]
                      ["Thread handling" :cmds '[shutdown-agents]]
                      ["Error" :cmds '[error-handler set-error-handler!
                                       error-mode set-error-mode!]]
                      ["Misc" :cmds '[*agent* release-pending-sends]]]
              ]
             [:box "orange"
              :section {:latex "Java Interoperation (\\href{http://clojure.org/java\\_interop}{clojure.org/java\\_interop})"
                        :html "Java Interoperation (<a href=\"http://clojure.org/java_interop\">clojure.org/java_interop</a>)"}
              :table [["General" :cmds '[.. doto "Classname/" "Classname."
                                         new bean comparator enumeration-seq
                                         import iterator-seq memfn set!]]
                      ["Cast" :cmds '[boolean byte short char int long
                                      float double bigdec bigint num cast
                                      biginteger]]
                      ["Exceptions" :cmds '[throw try catch finally
                                            clojure.repl/pst]]]
              :subsection "Arrays"
              :table [["Create" :cmds '[make-array
                                        [:common-suffix -array object
                                         boolean byte short char int long
                                         float double]
                                        aclone to-array to-array-2d into-array]]
                      ["Use" :cmds '[aget aset
                                     [:common-prefix aset- boolean byte short
                                      char int long float double]
                                     alength amap areduce]]
                      ;; TBD: This would be a good place to give an
                      ;; example like ^"[Ljava.lang.BigInteger", yes?
                      ;; Also the cast ^objects?  Is there a doc page
                      ;; for that?
                      ["Cast" :cmds '[booleans bytes shorts chars
                                      ints longs floats doubles]]
                      ]
              :subsection "Proxy"
              :table [["Create" :cmds '[proxy get-proxy-class
                                        [:common-suffix -proxy
                                         construct init]]]
                      ["Misc" :cmds '[proxy-mappings proxy-super update-proxy]]]
              ]
             [:box "green2"
              :section "Other"
              :table [["XML" :cmds '[clojure.xml/parse xml-seq]]
                      ["REPL" :cmds '[*1 *2 *3 *e *print-dup* *print-length*
                                      *print-level* *print-meta*
                                      *print-readably*]]
                      ["Code" :cmds '[*compile-files* *compile-path* *file*
                                      *warn-on-reflection* compile gen-class
                                      gen-interface loaded-libs test]]
                      ["Misc" :cmds '[eval force hash name *clojure-version*
                                      clojure-version *command-line-args*]]]
              ]
;             [:footer
;               tbd
;
;              ]
             ]
      ])


(defn clojuredocs-url-fixup [s]
  (let [s (str/replace s "?" "_q")
        s (str/replace s "/" "_")
        s (str/replace s "." "_dot")]
    s))


(defn sym-to-pair [prefix sym link-dest base-url]
  [(str prefix sym)
   (str base-url
        (case link-dest
              (:nolinks :links-to-clojure) sym
              :links-to-clojuredocs (clojuredocs-url-fixup (str sym))))])


(defn sym-to-url-list [link-target-site info]
  (let [{:keys [namespace-str symbol-list clojure-base-url
                clojuredocs-base-url]} info
         namespace-str (if (= "" namespace-str) "" (str namespace-str "/"))]
    (map #(sym-to-pair
           namespace-str % link-target-site
           (case link-target-site
                 :links-to-clojure clojure-base-url
                 :links-to-clojuredocs clojuredocs-base-url))
         symbol-list)))


(defn symbol-url-pairs-for-whole-namespaces [link-target-site]
  (apply concat
    (map
     #(sym-to-url-list link-target-site %)
     [{:namespace-str "",
       :symbol-list '(def if do let quote var fn loop recur throw try
                          monitor-enter monitor-exit),
       :clojure-base-url "http://clojure.org/special_forms#",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.core/"}
      {:namespace-str "",
       :symbol-list (keys (ns-publics 'clojure.core)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.core/"}
      {:namespace-str "clojure.data"
       :symbol-list (keys (ns-publics 'clojure.data)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.data-api.html#clojure.data/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.data/"}
      {:namespace-str "clojure.java.io"
       :symbol-list (keys (ns-publics 'clojure.java.io)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.java.io-api.html#clojure.java.io/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.io/"}
      {:namespace-str "clojure.java.javadoc"
       :symbol-list (keys (ns-publics 'clojure.java.javadoc)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.java.javadoc-api.html#clojure.java.javadoc/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.javadoc/"}
      {:namespace-str "clojure.pprint"
       :symbol-list (keys (ns-publics 'clojure.pprint)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.pprint-api.html#clojure.pprint/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.pprint/"}
      {:namespace-str "clojure.repl"
       :symbol-list (keys (ns-publics 'clojure.repl)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.repl-api.html#clojure.repl/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.repl/"}
      {:namespace-str "clojure.set"
       :symbol-list (keys (ns-publics 'clojure.set)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.set-api.html#clojure.set/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.set/"}
      {:namespace-str "clojure.string"
       :symbol-list (keys (ns-publics 'clojure.string)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.string-api.html#clojure.string/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.string/"}
      {:namespace-str "clojure.xml"
       :symbol-list (keys (ns-publics 'clojure.xml)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.xml-api.html#clojure.xml/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.xml/"}
      {:namespace-str "clojure.zip"
       :symbol-list (keys (ns-publics 'clojure.zip)),
       :clojure-base-url "http://clojure.github.com/clojure/clojure.zip-api.html#clojure.zip/",
       :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.zip/"}
      ])))


(defn symbol-url-pairs-specified-by-hand [link-target-site]
  (concat
   ;; Manually specify links for a few symbols in the cheatsheet.
   [["new" (case link-target-site
                 :links-to-clojure
                 "http://clojure.org/java_interop#new"
                 :links-to-clojuredocs
                 "http://clojuredocs.org/clojure_core/clojure.core/new")]
    ["set!" (case link-target-site
                  :links-to-clojure
                  "http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form-%28set!%20%28.%20Classname-symbol%20staticFieldName-symbol%29%20expr%29"
                  :links-to-clojuredocs
                  "http://clojuredocs.org/clojure_core/clojure.core/set!")]
    ["catch" (case link-target-site
                   :links-to-clojure
                   "http://clojure.org/special_forms#try"
                   :links-to-clojuredocs
                   "http://clojuredocs.org/clojure_core/clojure.core/catch")]
    ["finally" (case link-target-site
                     :links-to-clojure
                     "http://clojure.org/special_forms#try"
                     :links-to-clojuredocs
                     "http://clojuredocs.org/clojure_core/clojure.core/finally")]]
   (case link-target-site
         :links-to-clojure
         [["Classname." "http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form-%28new%20Classname%20args*%29"]
          ["Classname/" "http://clojure.org/java_interop#Java%20Interop-%28Classname/staticMethod%20args*%29"]]
         :links-to-clojuredocs
         ;; I don't have a good idea where on clojuredocs.org these
         ;; should link to, if anywhere.
         [])))


(defn symbol-url-pairs [link-target-site]
  (if (= link-target-site :nolinks)
    []
    (concat
     (symbol-url-pairs-for-whole-namespaces link-target-site)
     (symbol-url-pairs-specified-by-hand link-target-site))))


;; Use the following usepackage line if you want text with clickable
;; links in the PDF file to look no different from normal text:

;; \usepackage[colorlinks=false,breaklinks=true,pdfborder={0 0 0},dvipdfm]{hyperref}

;; The following line causes blue boxes to appear around words that
;; have links in the PDF file.  This can be good for debugging, but
;; might not be what you want long term.

;; \\usepackage[dvipdfm]{hyperref}


(def latex-header-except-documentclass
     "
% Author: Steve Tayon
% Comments, errors, suggestions: steve.tayon(at)googlemail.com

% Most of the content is based on the clojure wiki, api and source code by Rich Hickey on http://clojure.org/.

% License
% Eclipse Public License v1.0
% http://opensource.org/licenses/eclipse-1.0.php

% Packages
\\usepackage[utf8]{inputenc}
\\usepackage[T1]{fontenc}
\\usepackage{textcomp}
\\usepackage[english]{babel}
\\usepackage{tabularx}
\\usepackage[colorlinks=false,breaklinks=true,pdfborder={0 0 0},dvipdfm]{hyperref}
\\usepackage{lmodern}
\\renewcommand*\\familydefault{\\sfdefault} 


\\usepackage[table]{xcolor}

% Set column space
\\setlength{\\columnsep}{0.25em}

% Define colours
\\definecolorset{hsb}{}{}{red,0,.4,0.95;orange,.1,.4,0.95;green,.25,.4,0.95;yellow,.15,.4,0.95}

\\definecolorset{hsb}{}{}{blue,.55,.4,0.95;purple,.7,.4,0.95;pink,.8,.4,0.95;blue2,.58,.4,0.95}

\\definecolorset{hsb}{}{}
{magenta,.9,.4,0.95;green2,.29,.4,0.95}

\\definecolor{grey}{hsb}{0.25,0,0.85}

\\definecolor{white}{hsb}{0,0,1}

% Redefine sections
\\makeatletter
\\renewcommand{\\section}{\\@startsection{section}{1}{0mm}
	{-1.7ex}{0.7ex}{\\normalfont\\large\\bfseries}}
\\renewcommand{\\subsection}{\\@startsection{subsection}{2}{0mm}
	{-1.7ex}{0.5ex}{\\normalfont\\normalsize\\bfseries}}
\\makeatother

% No section numbers
\\setcounter{secnumdepth}{0}

% No indentation
\\setlength{\\parindent}{0em}

% No header and footer
\\pagestyle{empty}


% A few shortcuts
\\newcommand{\\cmd}[1] {\\frenchspacing\\texttt{\\textbf{{#1}}}}
\\newcommand{\\cmdline}[1] {
	\\begin{tabularx}{\\hsize}{X}
			\\texttt{\\textbf{{#1}}}
	\\end{tabularx}
}

\\newcommand{\\colouredbox}[2] {
	\\colorbox{#1!40}{
		\\begin{minipage}{0.95\\linewidth}
			{
			\\rowcolors[]{1}{#1!20}{#1!10}
			#2
			}
		\\end{minipage}
	}
}

\\begin{document}

")

(def latex-header-after-title "")

(def latex-footer
     " 
\\end{document}
")


(def latex-a4-header-before-title
     (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=8.7pt]{scrreprt}\n"
          latex-header-except-documentclass))

;; US letter is a little shorter, so formatting gets completely messed
;; up unless we use a slightly smaller font size.
(def latex-usletter-header-before-title
     (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=8.2pt,letterpaper]{scrreprt}\n"
          latex-header-except-documentclass))



(def html-header-before-title "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">

<html xmlns=\"http://www.w3.org/1999/xhtml\">
<head>
  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=us-ascii\" />
")


(def html-header-after-title "  <link rel=\"stylesheet\" href=\"cheatsheet_files/26467729A.css\" type=\"text/css\" />

  <style type=\"text/css\">
  @media screen {      .page {        width: 600px; display: inline;      }  .gap {clear: both;}    }    code {      font-family: monospace;    }    .page {      clear: both;      page-break-after: always;      page-break-inside: avoid;    }    .column {      float: left;      width: 50%;    }    .header {      text-align: center;    }    .header h2 {      font-style: italic;    }    h1 {      font-size: 1.8em;    }    h2 {      font-size: 1.4em;    }    h3 {      font-size: 1.2em;    }    .section {      margin: 0.5em;      padding: 0.5em;      padding-top: 0;      background-color: #ebebeb;    }    table {      width: 100%;      }    td, .single_row {      padding: 0 0.5em;      vertical-align: top;    }    tr.odd, .single_row {      background-color: #f5f5f5;    }    tr.even {      background-color: #fafafa;    }    .footer {      float: right;      text-align: right;      border-top: 1px solid gray;    } #foot {clear: both;}  
  </style>
</head>

<body id=\"cheatsheet\">
  <div class=\"wiki wikiPage\" id=\"content_view\">
")


(def html-footer "  </div>
</body>
</html>
")


(def embeddable-html-fragment-header-before-title "")
(def embeddable-html-fragment-header-after-title "<script language=\"JavaScript\" type=\"text/javascript\">
//<![CDATA[
document.write('<style type=\"text/css\">  @media screen {      .page { width: 600px; display: inline;      }  .gap {clear: both;}    } code {      font-family: monospace;    }    .page {      clear: both;      page-break-after: always;      page-break-inside: avoid; }    .column {      float: left;      width: 50%;    }    .header { text-align: center;    }    .header h2 {      font-style: italic; }    h1 {      font-size: 1.8em;    }    h2 {      font-size: 1.4em; }    h3 {      font-size: 1.2em;    }    .section {      margin: 0.5em;      padding: 0.5em;      padding-top: 0; background-color: #ebebeb;    }    table {      width: 100%; border-collapse: collapse;    }    td, .single_row {      padding: 0 0.5em;      vertical-align: top;    }    tr.odd, .single_row { background-color: #f5f5f5;    }    tr.even {      background-color: #fafafa;    }    .footer {      float: right;      text-align: right; border-top: 1px solid gray;    } #foot {clear: both;}  <\\/style>')
//]]>
</script>
")
(def embeddable-html-fragment-footer "")


(defmacro verify [cond]
  `(when (not ~cond)
     (println "verify of this condition failed: " '~cond)
     (throw (Exception.))))


(defn output-title [fmt t]
  (print (case (:fmt fmt)
               :latex (format "{\\Large{\\textbf{%s}}}\n\n" t)
               :html (format "  <title>%s</title>\n" t)
               :verify-only "")))


(defn htmlize-str [str]
  (let [str (str/replace str "<" "&lt;")
        str (str/replace str ">" "&gt;")]
    str))


;; Handle a thing that could be a string, symbol, or a 'conditional
;; string'

(defn cond-str [fmt cstr & htmlize]
  (cond (string? cstr) cstr
        (symbol? cstr) (if (= (:fmt (first htmlize)) :html)
                         (htmlize-str (str cstr))
                         (str cstr))
        (map? cstr) (do
                      (verify (contains? cstr (:fmt fmt)))
                      (cstr (:fmt fmt)))
        :else (do
                (println "cond-str: cstr=" cstr " is not a string, symbol, or map")
                (verify (or (string? cstr) (symbol? cstr) (map? cstr))))))


(def ^:dynamic *symbol-name-to-url*)
(def ^:dynamic *warn-about-unknown-symbols* false)

(def symbols-looked-up (ref #{}))


(defn url-for-cmd-doc [cmd-str]
  (when *warn-about-unknown-symbols*
    ;; This is a bit of a hack, but it ought to do the job.
    (dosync (alter symbols-looked-up conj cmd-str)))
  (if-let [url-str (*symbol-name-to-url* cmd-str)]
    url-str
    (do
      (when *warn-about-unknown-symbols*
        (.println *err* (format "No URL known for symbol with name: '%s'"
                                cmd-str)))
      nil)))


(defn escape-latex-hyperref-url [url]
  (-> url
      (str/replace "#" "\\#")
      (str/replace "%" "\\%")
      (str/replace "<" "\\%3C")
      (str/replace "=" "\\%3D")
      (str/replace ">" "\\%3E")))


(defn escape-latex-hyperref-target [target]
  (-> target
      ;; -> doesn't seem to have a problem in LaTeX, but ->> looks
      ;; -> like - followed by a special symbol that is two >'s
      ;; -> combined, not two separate characters.
      (str/replace "->>" "-{>}{>}")))


(defn has-prefix? [s pre]
  (and (>= (count s) (count pre))
       (= (subs s 0 (count pre)) pre)))


;; Only remove the namespaces that are very commonly used in the
;; cheatsheet.  For the ones that only have one or a few symbol there,
;; it seems best to leave the namespace in there explicitly.

(defn remove-common-ns-prefix [s]
  (cond (has-prefix? s "clojure.java.io/") (subs s (count "clojure.java.io/"))
        (has-prefix? s "clojure.java.javadoc/") (subs s (count "clojure.java.javadoc/"))
        (has-prefix? s "clojure.pprint/") (subs s (count "clojure.pprint/"))
        (has-prefix? s "clojure.repl/") (subs s (count "clojure.repl/"))
        (has-prefix? s "clojure.set/") (subs s (count "clojure.set/"))
        (has-prefix? s "clojure.string/") (subs s (count "clojure.string/"))
        (has-prefix? s "clojure.zip/") (subs s (count "clojure.zip/"))
        :else s))


(defn table-one-cmd-to-str [fmt cmd prefix suffix]
  (let [cmd-str (cond-str fmt cmd)
        whole-cmd (str prefix cmd-str suffix)
        url-str (url-for-cmd-doc whole-cmd)
        ;; cmd-str-to-show has < converted to HTML &lt; among other
        ;; things, if (:fmt fmt) is :html
        cmd-str-to-show (remove-common-ns-prefix (cond-str fmt cmd fmt))]
    (if url-str
      (case (:fmt fmt)
            :latex (str "\\href{" (escape-latex-hyperref-url url-str)
                        "}{" (escape-latex-hyperref-target cmd-str-to-show) "}")
            :html (str "<a href=\"" url-str "\">" cmd-str-to-show "</a>")
            :verify-only "")
      cmd-str-to-show)))


(defn table-cmds-to-str [fmt cmds]
  (if (vector? cmds)
    (let [[k2 pre-or-suf & cmds2] cmds
          s (cond-str fmt pre-or-suf)
          both-pre-and-suf (= k2 :common-prefix-suffix)
          [suff cmds2] (if both-pre-and-suf
                         [(first cmds2) (rest cmds2)]
                         [nil cmds2])
          s2 (if suff (cond-str fmt suff))
          ;; s-to-show has < converted to HTML &lt; etc., if fmt is
          ;; :html
          s-to-show (cond-str fmt pre-or-suf fmt)
          s2-to-show (if suff (cond-str fmt suff fmt))
          [before between after] (case (:fmt fmt)
                                       :latex ["\\{" ", " "\\}"]
                                       :html  [  "{" ", "   "}"]
                                       :verify-only ["" "" ""])
          str-list (case k2
                         :common-prefix
                         (map #(table-one-cmd-to-str fmt % s "")
                              cmds2)
                         :common-suffix
                         (map #(table-one-cmd-to-str fmt % "" s)
                              cmds2)
                         :common-prefix-suffix
                         (map #(table-one-cmd-to-str fmt % s s2)
                              cmds2))
          most-str (str before
                        (str/join between str-list)
                        after)]
      (case k2
            :common-prefix (str s-to-show most-str)
            :common-suffix (str most-str s-to-show)
            :common-prefix-suffix (str s-to-show most-str s2-to-show)))
    ;; handle the one thing, with no prefix or suffix
    (table-one-cmd-to-str fmt cmds "" "")))


(defn output-table-cmd-list [fmt k cmds]
  (if (= k :str)
    (print (cond-str fmt cmds))
    (do
      (print (case (:fmt fmt)
                   :latex
                   (case k
                         :cmds "\\cmd{"
                         :cmds-with-frenchspacing "\\cmd{\\frenchspacing "
                         :cmds-one-line "\\cmdline{")
                   :html "<code>"
                   :verify-only ""))
      (print (str/join " " (map #(table-cmds-to-str fmt %) cmds)))
      (print (case (:fmt fmt)
                   :latex "}"
                   :html "</code>"
                   :verify-only "")))))


(defn output-table-row [fmt row row-num nrows]
  (verify (not= nil (#{:cmds :cmds-with-frenchspacing :str} (second row))))

  (let [[row-title k cmd-desc] row]
    (print (case (:fmt fmt)
                 :latex (str (cond-str fmt row-title fmt) " & ")
                 :html (format "              <tr class=\"%s\">
                <td>%s</td>
                <td>"
                               (if (even? row-num) "even" "odd")
                               (cond-str fmt row-title fmt))
                 :verify-only ""))
    (output-table-cmd-list fmt k cmd-desc)
    (print (case (:fmt fmt)
                 :latex (if (= row-num nrows) "\n" " \\\\\n")
                 :html "</td>
              </tr>\n"
                 :verify-only ""))))


(defn output-table [fmt tbl]
  (print (case (:fmt fmt)
               :latex "\\begin{tabularx}{\\hsize}{lX}\n"
               :html "          <table>
            <tbody>
"
               :verify-only ""))
  (let [nrows (count tbl)]
    (doseq [[row row-num] (map (fn [& args] (vec args))
                               tbl (iterate inc 1))]
      (output-table-row fmt row row-num nrows)))
  (print (case (:fmt fmt)
               :latex "\\end{tabularx}\n"
               :html "            </tbody>
          </table>
"
               :verify-only "")))


(defn output-cmds-one-line [fmt tbl]
  (print (case (:fmt fmt)
               :latex ""
               :html "          <div class=\"single_row\">
            "
               :verify-only ""))
  (output-table-cmd-list fmt :cmds-one-line tbl)
  (print (case (:fmt fmt)
               :latex "\n"
               :html "
          </div>\n"
               :verify-only "")))


(defn output-box [fmt box]
  (verify (even? (count box)))
  (verify (= :box (first box)))
  (let [box-color (if (:colors fmt)
                    (case (:colors fmt)
                          :color (second box)
                          :grey "grey"
                          :bw "white")
                    nil)
        key-val-pairs (partition 2 (nnext box))]
    (print (case (:fmt fmt)
                 :latex (format "\\colouredbox{%s}{\n" box-color)
                 :html "        <div class=\"section\">\n"
                 :verify-only ""))
    (doseq [[k v] key-val-pairs]
      (case k
            :section
            (case (:fmt fmt)
                  :latex (printf "\\section{%s}\n" (cond-str fmt v))
                  :html (printf "          <h2>%s</h2>\n" (cond-str fmt v)))
            :subsection
            (case (:fmt fmt)
                  :latex (printf "\\subsection{%s}\n" (cond-str fmt v))
                  :html (printf "          <h3>%s</h3>\n" (cond-str fmt v)))
            :table
            (output-table fmt v)
            :cmds-one-line
            (output-cmds-one-line fmt v)))
    (print (case (:fmt fmt)
                 :latex "}\n\n"
                 :html "        </div><!-- /section -->\n"
                 :verify-only ""))))


(defn output-col [fmt col]
  (print (case (:fmt fmt)
               :latex ""
               :html "      <div class=\"column\">\n"
               :verify-only ""))
  (doseq [box col]
    (output-box fmt box))
  (print (case (:fmt fmt)
               ;;:latex "\\columnbreak\n\n"
               :latex "\n\n"
               :html "      </div><!-- /column -->\n"
               :verify-only "")))


(defn output-page [fmt pg]
  (verify (= (first pg) :column))
  (verify (== 2 (count (filter #(= % :column) pg))))
  (print (case (:fmt fmt)
               :latex ""
               :html "    <div class=\"page\">\n"
               :verify-only ""))
  (let [tmp (rest pg)
        [col1 col2] (split-with #(not= % :column) tmp)
        col2 (rest col2)]
    (output-col fmt col1)
    (output-col fmt col2))
  (print (case (:fmt fmt)
               :latex ""
               :html "    </div><!-- /page -->\n"
               :verify-only "")))


(defn output-cheatsheet [fmt cs]
  (verify (even? (count cs)))
  (print (case (:fmt fmt)
               :latex (case (:paper fmt)
                            :a4 latex-a4-header-before-title
                            :usletter latex-usletter-header-before-title)
               :html html-header-before-title
               :embeddable-html embeddable-html-fragment-header-before-title
               :verify-only ""))
  (let [[k title & pages] cs]
    (verify (= k :title))
    (let [[show-title fmt-passed-down]
          (if (= (:fmt fmt) :embeddable-html)
            [false (assoc fmt :fmt :html)]
            [true fmt])]
      (when show-title
        (output-title fmt-passed-down title))
      (print (case (:fmt fmt)
                   :latex latex-header-after-title
                   :html html-header-after-title
                   :embeddable-html embeddable-html-fragment-header-after-title
                   :verify-only ""))
      (doseq [[k pg] (partition 2 pages)]
        (verify (= k :page))
        (output-page fmt-passed-down pg))))
  (print (case (:fmt fmt)
               :latex latex-footer
               :html html-footer
               :embeddable-html embeddable-html-fragment-footer
               :verify-only "")))


(defn hash-from-pairs [pairs]
  (zipmap (map first pairs) (map second pairs)))


;; Supported command line args:

;; links-to-clojure (default if nothing specified on command line):
;; Generate HTML and LaTeX files with links from the symbols to
;; clojure.org and clojure.github.com URLs where they are documented.

;; links-to-clojuredocs: Generate HTML and LaTeX files with links from
;; the symbols to clojuredocs.org URLs where they are documented.

;; nolinks: Do not include any links in the output files.  Except for
;; that and the likely difference in appearance in HTML of anchor text
;; from text with no links, there should be no difference in the
;; appearance of the output files compared to the choices above.

(let [supported-args #{"nolinks" "links-to-clojure" "links-to-clojuredocs"}
      link-target-site (if (< (count *command-line-args*) 1)
                         :links-to-clojure
                         (let [arg (first *command-line-args*)]
                           (if (supported-args arg)
                             (keyword (first *command-line-args*))
                             (do
                               (.println *err*
                                (format "Unrecognized argument: %s\nSupported args are: %s\n"
                                        arg
                                        (str/join " " (seq supported-args))))
                               (. System (exit 1))))))
      symbol-name-to-url (hash-from-pairs (symbol-url-pairs link-target-site))]
  (binding [*symbol-name-to-url* symbol-name-to-url]
    (binding [*out* (java.io.OutputStreamWriter.
                     (java.io.BufferedOutputStream.
                      (java.io.FileOutputStream. "cheatsheet-full.html")))
              *err* (java.io.PrintWriter.
                     (java.io.BufferedOutputStream.
                      (java.io.FileOutputStream. "warnings.log")))
              *warn-about-unknown-symbols* true]
      (output-cheatsheet {:fmt :html} cheatsheet-structure)
      ;; Print out a list of all symbols in our symbol-name-to-url
      ;; table that we never looked up.
      (let [never-used (set/difference
                        (set (keys symbol-name-to-url))
                        @symbols-looked-up)]
        (.println *err* (format "\n\nSorted list of %d symbols in lookup table that were never used:\n"
                                (count never-used)))
        (.println *err* (str/join "\n" (sort (seq never-used))))
        (.println *err* "\n\nSorted list of links to documentation for symbols that were never used:\n\n")
        (.println *err* (str/join "<br>"
                                  (map #(format "<a href=\"%s\">%s</a>\n"
                                                (symbol-name-to-url %) %)
                                       (sort (seq never-used))))))
      (.close *out*)
      (.close *err*))
    (doseq [x [ {:filename "cheatsheet-embeddable.html",
                 :format {:fmt :embeddable-html}}
                {:filename "cheatsheet-a4-color.tex",
                 :format {:fmt :latex, :paper :a4, :colors :color}}
                {:filename "cheatsheet-a4-grey.tex",
                 :format {:fmt :latex, :paper :a4, :colors :grey}}
                {:filename "cheatsheet-a4-bw.tex",
                 :format {:fmt :latex, :paper :a4, :colors :bw}}
                {:filename "cheatsheet-usletter-color.tex",
                 :format {:fmt :latex, :paper :usletter, :colors :color}}
                {:filename "cheatsheet-usletter-grey.tex",
                 :format {:fmt :latex, :paper :usletter, :colors :grey}}
                {:filename "cheatsheet-usletter-bw.tex",
                 :format {:fmt :latex, :paper :usletter, :colors :bw}}
                ]]
      (binding [*out* (java.io.OutputStreamWriter.
                       (java.io.BufferedOutputStream.
                        (java.io.FileOutputStream. (:filename x))))]
        (output-cheatsheet (:format x) cheatsheet-structure)
        (.close *out*)))))

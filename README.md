# ScalaWordNet

Scala bindings around [WordNet](http://wordnet.princeton.edu), [WS4j](https://github.com/Sciss/ws4j),
and [JWNL](http://jwordnet.sourceforge.net/).
The original author was Sujit Pal (Scalcium), adapted by Hanns Holger Rutz
to be more idiomatic Scala and less Python. (C)opyright 2015,
all rights reserved. Published under the GNU General Public License v2+.

The file `WordNetTest.scala` gives an overview of the API.

Background:

- [NLTK-like Wordnet Interface in Scala](http://sujitpal.blogspot.co.at/2014/04/nltk-like-wordnet-interface-in-scala.html)
- [Scalcium](https://github.com/sujitpal/scalcium)

## Installing the database

Because of the combination of WS4j (jawjaw) and JWNL, currently the WordNet database must 
be present in two different forms. Hopefully this will be resolved in a future version.

### WordNet-3.0

Download the original WordNet package from http://wordnet.princeton.edu/wordnet/download/ and 
put a symlink to this into `link`.

### wnjpn.db

In addition to WordNet-3.0, also the English-Japanese database must be installed. This
is because jawjaw and WS4j apparently rely on that SQL database.

Download http://nlpwww.nict.go.jp/wn-ja/data/1.1/wnjpn.db.gz
and unzip the `.db` file into the `config` directory.

There is perhaps an alternative, English only, version here:
http://sourceforge.net/projects/wnsql/files/wnsql3/sqlite/3.1/sqlite-31.db.zip/download
(The format doesn't seem to be compatible with Jawjaw)
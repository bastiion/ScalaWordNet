# ScalaWordNet

Scala bindings around [WordNet](http://wordnet.princeton.edu) and WS4j.
The original author was Sujit Pal (Scalcium), adapted by Hanns Holger Rutz
to be more idiomatic Scala and less Python. (C)opyright 2015,
all rights reserved. Published under the GNU General Public License v2+.

The file `WordNetTest` gives an overview of the API.

Background:

- [NLTK-like Wordnet Interface in Scala](http://sujitpal.blogspot.co.at/2014/04/nltk-like-wordnet-interface-in-scala.html)
- [Scalcium](https://github.com/sujitpal/scalcium)

## WordNet-3.0

Put a symlink to this into `link`.

## wnjpn.db

In addition to WordNet-3.0, also the English-Japanese database must be installed. This
doesn't seem to make sense, but jawjaw apparently relies on that SQL database in addition
to WordNet-3.0 dict.

To run the tests, download http://nlpwww.nict.go.jp/wn-ja/data/1.1/wnjpn.db.gz
and unzip the `.db` file into the `config` directory.

There is perhaps an alternative, English only, version here:
http://sourceforge.net/projects/wnsql/files/wnsql3/sqlite/3.1/sqlite-31.db.zip/download
(The format doesn't seem to be compatible with Jawjaw)
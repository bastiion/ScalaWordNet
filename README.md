# ScalaWordNet

Fiddling around with Scala bindings and [WordNet](http://wordnet.princeton.edu).

Resources:

- [NLTK-like Wordnet Interface in Scala](http://sujitpal.blogspot.co.at/2014/04/nltk-like-wordnet-interface-in-scala.html)
- [Scalcium](https://github.com/sujitpal/scalcium)

The current source is taken from Scalcium.

We are using Maven Central artifacts now for WS4j and Jawjaw. Currently two tests fail:

 - `car.lesk_similarity(bus)`   
 - `wn.synset('car.n.01').examples`
 
It's unclear why these don't work.

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
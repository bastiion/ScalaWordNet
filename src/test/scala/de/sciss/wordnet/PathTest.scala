package de.sciss.wordnet

object PathTest extends App {
  val wn        = WordNet()
//  val s1        = wn.synsets("red"    ).head
//  val s2        = wn.synsets("pigment").head
  val s1        = wn.synset("cat", Noun)
  val s2        = wn.synset("dog", Noun)
  val list      = wn.shortestHypernymPath(s1, s2).getOrElse((Nil, Nil))

  def printList(list: List[Synset]): Unit = {
    println(list.size)
    list.zipWithIndex.foreach { case (ss, i) =>
      println(f"$i%02d: ${ss.getWords.head} ${ss.getGloss}")
    }
  }

  println(s"SHORTEST PATH FOR ${s1.getWord(0).getLemma} AND ${s2.getWord(0).getLemma}")
  println("---")
  printList(list._1)
  println("---")
  printList(list._2)

  println(s"\nANTONYMS FOR ${s1.getWord(0).getLemma}")
  println(wn.antonyms(s1.getWord(0)).map(_.getLemma))

  //  println(s"PATHS for ${s1.getGloss}")
  //  wn.hypernymPaths(s1).foreach(printList)
  //
  //  println(s"PATHS for ${s2.getGloss}")
  //  wn.hypernymPaths(s2).foreach(printList)
}

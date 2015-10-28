package de.sciss.wordnet

import edu.cmu.lti.ws4j.util.PathFinder

/* XXX TODO -- this is currently wrong. The returned
   path is always of length 1 and seems to correspond
   to the least-common hypernym.
 */
object PathTest extends App {
  val wn        = WordNet()
  val pf        = new PathFinder(wn.lexdb)
  val s1        = wn.synsets("red"    ).head
  val s2        = wn.synsets("pigment").head
  val list      = wn.shortestPath(s1, s2)
  println(list.size)
  list.zipWithIndex.foreach { case (ss, i) =>
    println(f"$i%02d: $ss")
  }
}

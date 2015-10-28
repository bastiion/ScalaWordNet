/*
 *  ScalaWordNet
 *
 *  Copyright (c) 2013-2015 Sujit Pal.
 *  Copyright (c) 2015 Hanns Holger Rutz.
 *  All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 */

// original source: https://github.com/sujitpal/scalcium
//
// changes by HHR:
// - move package from `com.mycompany.scalcium.wordnet` to `de.sciss.wordnet`
// - code clean up (remove unused imports, IntelliJ inspections etc.)

package de.sciss.wordnet

import java.io.{InputStream, File, FileInputStream}

import edu.cmu.lti.jawjaw.util.WordNetUtil
import edu.cmu.lti.lexical_db.NictWordNet
import edu.cmu.lti.lexical_db.data.Concept
import edu.cmu.lti.ws4j.RelatednessCalculator
import edu.cmu.lti.ws4j.impl.{JiangConrath, LeacockChodorow, Lesk, Lin, Path, Resnik, WuPalmer}
import net.didion.jwnl.JWNL
import net.didion.jwnl.data.list.{PointerTargetNode, PointerTargetNodeList}
import net.didion.jwnl.data.{IndexWord, POS, PointerType, PointerUtils, Synset, Word}
import net.didion.jwnl.dictionary.Dictionary

import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.collection.mutable.ArrayBuffer

object WordNet {
  def apply(wnConfig: File): WordNet = new WordNet(new FileInputStream(wnConfig))
  def apply()              : WordNet = new WordNet(classOf[WordNet].getResourceAsStream("/wnconfig.xml"))
}
class WordNet(wnConfig: InputStream) {

  JWNL.initialize(wnConfig)
  val dict = Dictionary.getInstance()

  val lexdb = new NictWordNet()
  val Path_Similarity = new Path            (lexdb)
  val LCH_Similarity  = new LeacockChodorow (lexdb)
  val WUP_Similarity  = new WuPalmer        (lexdb)
  val RES_Similarity  = new Resnik          (lexdb)
  val JCN_Similarity  = new JiangConrath    (lexdb)
  val LIN_Similarity  = new Lin             (lexdb)
  val Lesk_Similarity = new Lesk            (lexdb)

  def allSynsets(pos: POS): Stream[Synset] =
    dict.getIndexWordIterator(pos)
      .flatMap(iword => iword.asInstanceOf[IndexWord].getSenses)
      .toStream

  def synsets(lemma: String): List[Synset] =
    POS.getAllPOS
      .flatMap(pos => synsets(lemma, pos.asInstanceOf[POS]))(breakOut)

  def synsets(lemma: String, pos: POS): List[Synset] = {
    val iword = dict.getIndexWord(pos, lemma)
    if (iword == null) List.empty[Synset]
    else iword.getSenses.toList
  }

  def synset(lemma: String, pos: POS,
             sid: Int): Option[Synset] = {
    val iword = dict.getIndexWord(pos, lemma)
    if (iword != null) Some(iword.getSense(sid))
    else None
  }

  def lemmas(s: String): List[Word] =
    synsets(s)
      .flatMap(ss => lemmas(Some(ss)))
      .filter(_.getLemma == s)

  def lemmas(oss: Option[Synset]): List[Word] =
    oss.fold(List.empty[Word])(_.getWords.toList)

  def lemma(oss: Option[Synset], wid: Int): Option[Word] =
    oss match {
      case Some(x) => Option(lemmas(oss)(wid))
      case None => None
    }

  def lemma(oss: Option[Synset], lem: String): Option[Word] =
    oss.flatMap { ss =>
      val words = ss.getWords
        .filter(w => lem == w.getLemma)
      words.headOption
    }

  ////////////////// similarities /////////////////////

  def pathSimilarity(loss: Option[Synset],
                     ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, Path_Similarity)

  def lchSimilarity(loss: Option[Synset],
                    ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, LCH_Similarity)

  def wupSimilarity(loss: Option[Synset],
                    ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, WUP_Similarity)

  // WS4j Information Content Finder (ICFinder) uses
  // SEMCOR, Resnik, JCN and Lin similarities are with
  // the SEMCOR corpus.
  def resSimilarity(loss: Option[Synset],
                    ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, RES_Similarity)

  def jcnSimilarity(loss: Option[Synset],
                    ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, JCN_Similarity)

  def linSimilarity(loss: Option[Synset],
                    ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, LIN_Similarity)

  def leskSimilarity(loss: Option[Synset],
                     ross: Option[Synset]): Double =
    getPathSimilarity(loss, ross, Lesk_Similarity)

  def getPathSimilarity(loss: Option[Synset],
                        ross: Option[Synset],
                        sim: RelatednessCalculator): Double = {
    val lconcept = getWS4jConcept(loss)
    val rconcept = getWS4jConcept(ross)
    if (lconcept == null || rconcept == null) 0.0D
    else sim.calcRelatednessOfSynset(lconcept, rconcept)
      .getScore
  }

  def getWS4jConcept(oss: Option[Synset]): Concept =
    oss match {
      case Some(ss) =>
        val pos = edu.cmu.lti.jawjaw.pobj.POS.valueOf(
          ss.getPOS.getKey)
        val synset = WordNetUtil.wordToSynsets(
          ss.getWord(0).getLemma, pos)
          .head
        new Concept(synset.getSynset, pos)
      case _ => null
    }

  ////////////////// Morphy ///////////////////////////

  def morphy(s: String, pos: POS): String = {
    val bf = dict.getMorphologicalProcessor
      .lookupBaseForm(pos, s)
    if (bf == null) "" else bf.getLemma
  }

  def morphy(s: String): String = {
    val bases = POS.getAllPOS.map(pos =>
      morphy(s, pos.asInstanceOf[POS]))
      .filter(str => !str.isEmpty)
      .toSet
    if (bases.isEmpty) "" else bases.toList.head
  }

  ////////////////// Synset ///////////////////////////

  def lemmaNames(oss: Option[Synset]): List[String] =
    oss match {
      case Some(ss) => ss.getWords
        .map(word => word.getLemma)(breakOut)

      case _ => List.empty[String]
    }

  def definition(oss: Option[Synset]): String =
    oss.fold("") { ss =>
      ss.getGloss
        .split(";")
        .filter(s => !isQuoted(s.trim))
        .mkString(";")
    }

  def examples(oss: Option[Synset]): List[String] =
    oss match {
      case Some(ss) =>
        ss.getGloss
          .split(";")
          .filter(s => isQuoted(s.trim))
          .map(s => s.trim())(breakOut)

      case _ => List.empty[String]
    }

  def hyponyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.HYPONYM)

  def hypernyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.HYPERNYM)

  def partMeronyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.PART_MERONYM)

  def partHolonyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.PART_HOLONYM)

  def substanceMeronyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.SUBSTANCE_MERONYM)

  def substanceHolonyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.SUBSTANCE_HOLONYM)

  def memberHolonyms(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.MEMBER_HOLONYM)

  def entailments(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.ENTAILMENT)

  def entailedBy(oss: Option[Synset]): List[Synset] =
    relatedSynsets(oss, PointerType.ENTAILED_BY)

  def relatedSynsets(oss: Option[Synset],
                     ptr: PointerType): List[Synset] =
    oss match {
      case Some(ss) => ss.getPointers(ptr)
        .map(ptr => ptr.getTarget.asInstanceOf[Synset])(breakOut)
      case _ => List.empty[Synset]
    }

  def hypernymPaths(oss: Option[Synset]): List[List[Synset]] =
    oss match {
      case Some(ss) => PointerUtils.getInstance()
        .getHypernymTree(ss)
        .toList
        .map(ptnl => ptnl.asInstanceOf[PointerTargetNodeList]
          .map(ptn => ptn.asInstanceOf[PointerTargetNode].getSynset)
          .toList)(breakOut)

      case _ => List.empty[List[Synset]]
    }

  def rootHypernyms(oss: Option[Synset]): List[Synset] =
    hypernymPaths(oss)
      .map(hp => hp.reverse.head).distinct

  def lowestCommonHypernym(loss: Option[Synset],
                           ross: Option[Synset]): List[Synset] = {
    val lpaths = hypernymPaths(loss)
    val rpaths = hypernymPaths(ross)
    val pairs = for (lpath <- lpaths; rpath <- rpaths)
      yield (lpath, rpath)
    val lchs = ArrayBuffer[(Synset,Int)]()
    pairs.map { pair =>
      val lset = Set(pair._1).flatten
      val matched = pair._2
        .zipWithIndex
        .filter(si => lset.contains(si._1))
      if (matched.nonEmpty) lchs += matched.head
    }
    val lchss = lchs.sortWith((a, b) => a._2 < b._2)
      .map(lc => lc._1)
      .toList
    if (lchss.isEmpty) List.empty[Synset]
    else List(lchss.head)
  }

  def minDepth(oss: Option[Synset]): Int = {
    val lens = hypernymPaths(oss)
      .map(path => path.size)
      .sortWith((a,b) => a > b)
    if (lens.isEmpty) -1 else lens.head - 1
  }

  def format(ss: Synset): String =
    List(ss.getWord(0).getLemma,
      ss.getPOS.getKey,
      (ss.getWord(0).getIndex + 1).formatted("%02d"))
      .mkString(".")

  /////////////////// Words / Lemmas ////////////////////

  def antonyms(ow: Option[Word]): List[Word] =
    relatedLemmas(ow, PointerType.ANTONYM)

  def relatedLemmas(ow: Option[Word],
                    ptr: PointerType): List[Word] =
    ow match {
      case Some(w) => w.getPointers(ptr)
        .map(ptr => ptr.getTarget.asInstanceOf[Word])(breakOut)

      case _ => List.empty[Word]
    }

  def format(w : Word): String =
    List(w.getSynset.getWord(0).getLemma,
      w.getPOS.getKey,
      (w.getIndex + 1).formatted("%02d"),
      w.getLemma)
      .mkString(".")

  ////////////////// misc ////////////////////////////////

  def isQuoted(s: String): Boolean =
    s.isEmpty && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"'
}
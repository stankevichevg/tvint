package com.evst.tvint.frontend

import com.evst.tvint.testutil.Spec
import com.evst.tvint.ast._
import com.evst.tvint.ast.WhenBlock
import com.evst.tvint.ast.WordAstNode
import com.evst.tvint.ast.ConditionAstNode
import com.evst.tvint.ast.ThenBlock

/**
 * User: EStankevich
 * Date: 24.05.2014
 */
class TvintParserSpec extends Spec {
  "TvintParser" should {
    val parser = new TvintParser()

    "expression" in {
      parser.parse(parser.constantKey, "{word}") must be(
        ConstantKeyAstNode(WordAstNode("word"))
      )
      parser.parse(parser.whenBlock, "{.when true}") must be(
        WhenBlock(WordAstNode("true"), true)
      )
      parser.parse(parser.whenBlock, "{.when true}") must be(
        WhenBlock(WordAstNode("true"), true)
      )
      parser.parse(parser.whenBlock, "{.!when true}") must be(
        WhenBlock(WordAstNode("true"), false)
      )
      parser.parse(parser.thenBranch, "{.then true}") must be(
        ThenBlock(Seq(WordAstNode("true")))
      )
      parser.parse(parser.thenBranch, "{.then true1 true2}") must be(
        ThenBlock(Seq(WordAstNode("true1"), WordAstNode("true2")))
      )
      parser.parse(parser.whenThenElse, "{.when true} {.then true}") must be(
        ConditionAstNode(
          WhenBlock(WordAstNode("true"), true),
          ThenBlock(Seq(WordAstNode("true"))),
          null
        )
      )
      parser.parse(parser.whenThenElse, "{.when true} {.then true} {.else false false}") must be(
        ConditionAstNode(
          WhenBlock(WordAstNode("true"), true),
          ThenBlock(Seq(WordAstNode("true"))),
          ElseBlock(Seq(WordAstNode("false"), WordAstNode("false")))
        )
      )
      parser.parse(parser.compareBlock, "{.compare {word}}") must be(
        CompareBlock(ConstantKeyAstNode(WordAstNode("word")))
      )
      parser.parse(parser.expression,
        "{.case {.compare {word}} " +
          "{.when true1} {.then true1} {.else false1 false1}" +
          "{.!when true2} {.then false2 false2} {.else true2}" +
        "}") must be(
        CaseAstNode(
          CompareBlock(ConstantKeyAstNode(WordAstNode("word"))),
          Seq(
            ConditionAstNode(
              WhenBlock(WordAstNode("true1"), true),
              ThenBlock(Seq(WordAstNode("true1"))),
              ElseBlock(Seq(WordAstNode("false1"), WordAstNode("false1")))
            ),
            ConditionAstNode(
              WhenBlock(WordAstNode("true2"), false),
              ThenBlock(Seq(WordAstNode("false2"), WordAstNode("false2"))),
              ElseBlock(Seq(WordAstNode("true2")))
            )
          )
        )
      )
    }

    "text" in {

      val template =
        "{.case {.compare {word}} " +
          "{.when true1} {.then true1 ,} {.else false1, false1 ,}" +
          "{.!when true2} {.then false2, false2} {.else true2}" +
          "}."

      parser.parseTemplate(template, Map(("word", "true2"))) must be(
        "false1, false1, true2."
      )
      parser.parseTemplate(template, Map(("word", "true1"))) must be(
        "true1, false2, false2."
      )
    }
  }
}

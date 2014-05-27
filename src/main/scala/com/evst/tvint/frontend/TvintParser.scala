package com.evst.tvint.frontend

import scala.util.parsing.combinator.RegexParsers

/**
 * User: EStankevich
 * Date: 22.05.2014
 */
class TvintParser() extends RegexParsers {

  import com.evst.tvint.ast._

  lazy val punctuation = Set(",", ".", "?", "!", ";", ":")
  lazy val punctuations = punctuation.foldLeft(""){(acc, str) => acc + str}

  lazy val word: Parser[WordAstNode] = ("[A-Za-zА-Яа-я0-9_"+ punctuations + "]+").r ^^ {
    x => WordAstNode(x)
  }

  // {word}
  lazy val constantKey = "{" ~> word <~ "}" ^^ {
    key => ConstantKeyAstNode(key)
  }

  // {.when word}
  lazy val when: Parser[WhenBlock] = ("{" ~ ".when") ~> word <~ "}" ^^ {
    word => WhenBlock(word, true)
  }
  // {.!when word}
  lazy val notWhen: Parser[WhenBlock] = ("{" ~ ".!when") ~> word <~ "}" ^^ {
    word => WhenBlock(word, false)
  }
  lazy val whenBlock = when | notWhen

  // {.then EXPRESSIONS}
  lazy val thenBranch: Parser[ThenBlock] = "{" ~> ".then" ~> rep1(expression) <~ "}" ^^ {
    exp => ThenBlock(exp)
  }

  // {.else EXPRESSIONS}
  lazy val elseBranch: Parser[ElseBlock] = "{" ~> ".else" ~> rep1(expression) <~ "}" ^^ {
    exp => ElseBlock(exp)
  }

  lazy val whenThenElse = whenBlock ~ thenBranch ~ opt(elseBranch) ^^ {
    case w ~ t ~ e => ConditionAstNode(w, t, e.getOrElse(null))
  }

  lazy val compareBlock = "{" ~> ".compare" ~> constantKey <~ "}" ^^ {
    key => CompareBlock(key)
  }

  lazy val condition = "{" ~> ".case" ~> compareBlock ~ rep1(whenThenElse) <~ "}" ^^ {
    case compare ~ wte => CaseAstNode(compare, wte)
  }

  lazy val expression = word | condition

  lazy val template = rep1(expression) ^^ {
    exprs => Template(exprs)
  }

  def parse[T](parser: Parser[T], in: String): T = {
    parseAll(parser, in) match {
      case Success(result, _) => result
      case x@Failure(msg, z) => throw new ParseException(x.toString)
      case x@Error(msg, _) => throw new ParseException(x.toString)
    }
  }

  def parse(expr: ExpressionAstNode, params: Map[String, String]): String = {
    expr.toStringSeq(params).foldLeft(new StringBuilder) {
      (builder, part) => {
        if (!punctuation.contains(part) && !builder.isEmpty) builder append " "
        builder append part
      }
    }.toString
  }

  def parseTemplate(template: String, params: Map[String, String]): String =
    parse(parse(this.template, template), params)
}

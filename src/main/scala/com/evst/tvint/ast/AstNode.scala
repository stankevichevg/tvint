package com.evst.tvint.ast

sealed abstract class AstNode
abstract class ExpressionAstNode extends AstNode {
  def toStringSeq(params: Map[String, String]): Seq[String]

  def mapExpressions(exprs: Seq[ExpressionAstNode], params: Map[String, String]): Seq[String] = {
    exprs.map(expr => expr.toStringSeq(params)).flatten
  }
}

case class WordAstNode(value: String) extends ExpressionAstNode {
  override def toStringSeq(params: Map[String, String]): Seq[String] = Seq(value)
}

// {constantKey}
case class ConstantKeyAstNode(key: WordAstNode) extends ExpressionAstNode{
  override def toStringSeq(params: Map[String, String]): Seq[String] = Seq(params(key.value))
}

case class WhenBlock(word: WordAstNode, equal: Boolean) extends AstNode
case class ThenBlock(trueBranch: Seq[ExpressionAstNode]) extends AstNode
case class ElseBlock(falseBranch: Seq[ExpressionAstNode]) extends AstNode

case class CompareBlock(constant: ConstantKeyAstNode) extends AstNode

// {.when WORD1} {.then EXPRESSIONS} {.else EXPRESSIONS}
case class ConditionAstNode(
    when: WhenBlock,
    thenBranch: ThenBlock,
    elseBranch: ElseBlock
) extends AstNode

// {.case {.compare CONSTANT}
//      {.when WORD1} {.then EXPRESSIONS} {.else EXPRESSIONS}
//      {.!when WORDS} {.then EXPRESSIONS}
// }
case class CaseAstNode(
    compareBlock: CompareBlock,
    conditions: Seq[ConditionAstNode]
) extends ExpressionAstNode {

  override def toStringSeq(params: Map[String, String]): Seq[String] = {
    val compareConstant = params(compareBlock.constant.key.value)

    def condition(cond: ConditionAstNode): Seq[String] = cond match {
      case ConditionAstNode(WhenBlock(WordAstNode(value), equal), t, e) =>
        if (!(value.equals(compareConstant) ^ equal))
          mapExpressions(t.trueBranch, params)
        else if (e != null)
          mapExpressions(e.falseBranch, params)
        else
          Seq()
    }

    conditions.map(cond => condition(cond)).flatten
  }
}

case class Template(expressions: Seq[ExpressionAstNode]) extends ExpressionAstNode{
  override def toStringSeq(params: Map[String, String]): Seq[String] = mapExpressions(expressions, params)
}



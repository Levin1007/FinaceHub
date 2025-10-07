package controller


import java.time.LocalDate

object BudgetPlanner:

  case class Transaction(date: LocalDate, amount: BigDecimal, category: String)

  case class Budget(transactions: List[Transaction]):
    def add(tx: Transaction): Budget =
      copy(transactions = transactions :+ tx)

    def totalByCategory: Map[String, BigDecimal] =
      transactions.groupBy(_.category).view.mapValues(_.map(_.amount).sum).toMap

    def total: BigDecimal = transactions.map(_.amount).sum

    def balance: String =
      val totalAmt = total
      if totalAmt > 0 then s"Überschuss: $totalAmt €" else s"Defizit: ${totalAmt.abs} €"

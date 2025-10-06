package model

case class User(
  id: Int,
  name: String,
  password: String,
  balance: Double
) {
  def withBalance(newBalance: Double): User = 
    this.copy(balance = newBalance)
    
  def withoutPassword: User = 
    this.copy(password = "***")
}
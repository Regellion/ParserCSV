class Transaction
{
    private double transactionIncome;
    private double transactionExpense;
    private String description;

    private boolean isIncome = true;
    // Можно было бы сделать вообще все поля как у транзакции, но не вижу смысла, т.к. в ДЗ этого не требуется
    Transaction(double income, double expense, String description){
        this.description = description;
        transactionIncome = income;
        transactionExpense = expense;
        if(income == 0.0){
            isIncome = false;
        }
    }
    // геттер дохода
    double getTransactionIncome() {
        return transactionIncome;
    }
    // геттер расхода
    double getTransactionExpense() {
        return transactionExpense;
    }
    // определение прибыльности транзакции
    boolean isIncome() {
        return isIncome;
    }
    // геттер описания транзакции
    String getDescription() {
        return description;
    }
}

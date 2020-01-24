import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Main
{
    private static final String transactionFile = "data/movementList.csv";
    private static TreeMap<String, Double> detailsExpense = new TreeMap<>();

    public static void main(String[] args) {
        ArrayList<Transaction> transactions = loadTransactionsFromFile();
        System.out.println("Общее поступление на счет: " + getAllIncome(transactions) + "\n");
        System.out.println("Общее списание со счета: " + getAllExpense(transactions) + "\n");
        // Заполнения мапы расходов
        putToMap(transactions);
        System.out.println("Детализация расходов за отчетный перриод:");
        printMap();

    }

    // Взял метод из прошлого урока, для того чтобы не усложнять.
    // Метод заполнения списка транзакций
    private static ArrayList<Transaction> loadTransactionsFromFile(){
        // создаем лист транзакций
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            // Список линий
            List<String> lines = Files.readAllLines(Paths.get(transactionFile));
            for (String line : lines) {
                // Все / приводим к одному виду \
                line = line.replaceAll("/", "\\\\");
                // Делим линии на фрагменты
                String[] fragments = line.split(",");
                if (fragments.length > 9) {
                    System.out.println("Wrong line: " + line);
                } else if (fragments.length == 9) {
                    String newItem = fragments[7] + "." + fragments[8];
                    newItem = newItem.replaceAll("\"", "");
                    transactions.add(new Transaction(
                            Double.parseDouble(fragments[6]),
                            Double.parseDouble(newItem),
                            // Понимаю что регулярки лучше бы присовить переменным, но было уже не до этого)))
                            fragments[5].replaceAll(".+\\\\", "").replaceAll("(\\s+){2,}(.+)", "")));
                } else if (fragments.length == 8) {
                    if (!fragments[6].equals("Приход") && !fragments[7].equals("Расход")) {
                        transactions.add(new Transaction(
                            Double.parseDouble(fragments[6]),
                            Double.parseDouble(fragments[7]),
                            fragments[5].replaceAll(".+\\\\", "").replaceAll("(\\s+){2,}(.+)", "")));
                    }
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return transactions;
    }

    // Метод подсчета общего прихода
    private static double getAllIncome(ArrayList<Transaction> transactions){
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getTransactionIncome)
                .sum();
    }
    // Метод подсчета общего расхода
    private static double getAllExpense(ArrayList<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getTransactionExpense)
                .sum();
    }

    // Метод заполнения TreeMap для показа детального отчета по расходам.
    private static void putToMap(ArrayList<Transaction> transactions){
        for (Transaction transaction : transactions) {
            detailsExpense.put(transaction.getDescription(), 0.0);
        }
        for (String key : detailsExpense.keySet())
        {
            for (Transaction transaction : transactions) {
                if (key.equals(transaction.getDescription())) {
                    detailsExpense.put(key, detailsExpense.get(key) + transaction.getTransactionExpense());
                }
            }
        }
    }

    // Метод печати мапы
    private static void printMap(){
        for (String key : detailsExpense.keySet()){
            System.out.println("На "+ key.trim() + " было потрачено " + detailsExpense.get(key));
        }
    }
}

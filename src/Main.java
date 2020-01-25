import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main
{
    // Вводим константы для большей наглядности
    private static final int INCOME = 6;
    private static final int EXPENSE = 7;
    private static final int DESCRIPTION = 5;
    private static final int EXPENSE_DECIMAL_PART = 8;
    private static final String DELETING_PREVIOUS_PART_REGEX = ".+\\\\";
    private static final String DELETING_FOLLOWING_PART_REGEX = "(\\s+){2,}(.+)";


    public static void main(String[] args) {
        // загружаем файл
        File transactionFile = new File("data/movementList.csv");
        // Передаем в метод файл
        ArrayList<Transaction> transactions = loadTransactionsFromFile(transactionFile);
        System.out.println("Общее поступление на счет: " + getAllIncome(transactions) + "\n");
        System.out.println("Общее списание со счета: " + getAllExpense(transactions) + "\n");
        System.out.println("Детализация расходов за отчетный перриод:");
        printMap(fillingMapOfDetailedExpenses(transactions));

    }

    // Взял метод из прошлого урока, для того чтобы не усложнять.
    // Метод заполнения списка транзакций
    private static ArrayList<Transaction> loadTransactionsFromFile(File transactionFile){
        // создаем лист транзакций
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            // Список линий
            List<String> lines = Files.readAllLines(transactionFile.toPath());
            for (String line : lines) {
                // Все / приводим к одному виду \
                line = line.replaceAll("/", "\\\\");
                // Делим линии на фрагменты
                String[] fragments = line.split(",");
                if (fragments.length > 9) {
                    System.out.println("Wrong line: " + line);
                } else if (fragments.length == 9) {
                    String newItem = fragments[EXPENSE] + "." + fragments[EXPENSE_DECIMAL_PART];
                    newItem = newItem.replaceAll("\"", "");
                    transactions.add(new Transaction(
                            Double.parseDouble(fragments[INCOME]),
                            Double.parseDouble(newItem),
                            // Понимаю что регулярки лучше бы присовить переменным, но было уже не до этого)))
                            fragments[DESCRIPTION].replaceAll(DELETING_PREVIOUS_PART_REGEX, "").replaceAll(DELETING_FOLLOWING_PART_REGEX, "").trim()));
                } else if (fragments.length == 8) {
                    if (!fragments[INCOME].equals("Приход") && !fragments[EXPENSE].equals("Расход")) {
                        transactions.add(new Transaction(
                            Double.parseDouble(fragments[INCOME]),
                            Double.parseDouble(fragments[EXPENSE]),
                            fragments[DESCRIPTION].replaceAll(DELETING_PREVIOUS_PART_REGEX, "").replaceAll(DELETING_FOLLOWING_PART_REGEX, "").trim()));
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
    private static Map fillingMapOfDetailedExpenses(ArrayList<Transaction> transactions){
        TreeMap<String, Double> detailsExpense = new TreeMap<>();

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
        return detailsExpense;
    }

    // Метод печати мапы
    private static void printMap(Map<String, Double> map){
        for (String key : map.keySet()){
            System.out.println("На "+ key + " было потрачено " + map.get(key));
        }
    }
}

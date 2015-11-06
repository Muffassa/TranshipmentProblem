import java.io.File;
import java.util.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

public class TransportationProblem {

    private static Integer[] demand;
    private static Integer[] supply;
    private static Double[][] costs;
    private static Shipment[][] matrix;
    String result = "";

    private static class Shipment {
        final double costPerUnit;
        final int r, c;
        double quantity;

        public Shipment(double q, double cpu, int r, int c) {
            quantity = q;
            costPerUnit = cpu;
            this.r = r;
            this.c = c;
        }
    }

    public void init(){
        String[] _demand = MainForm.getRequestData();
        String[] _supply = MainForm.getStoreData();
        String[][] _costs =MainForm.getPriceData();

        demand = new Integer[_demand.length];
        for (int i = 0; i < _demand.length ; i++) {

            demand[i] = Integer.parseInt(_demand[i]);
        }

        supply = new Integer[_supply.length];
        for (int i = 0; i < _supply.length; i++)
        {
            supply[i] = Integer.parseInt(_supply[i]);
        }

        costs = new Double[_costs.length][_costs[0].length];
        for (int i = 0; i < _costs.length ; i++) {
            for (int j = 0; j < _costs[i].length; j++)
            {

                costs[i][j] = Double.parseDouble(_costs[i][j]);
            }
        }


    }

    public void northWestCornerRule() {
        matrix = new Shipment[supply.length][demand.length]; //создаем матрицу заказов размером запасы * потребности
        for (int r = 0, northwest = 0; r < supply.length; r++)
            for (int c = northwest; c < demand.length; c++) {

                int quantity = Math.min(supply[r], demand[c]); // в количество записываем наименьшее значение из потребности и запасов
                if (quantity > 0) {
                    matrix[r][c] = new Shipment(quantity, costs[r][c], r, c); // записываем в элемент матрицы количество товара на поставку, цену и координаты

                    supply[r] -= quantity;
                    demand[c] -= quantity;

                    if (supply[r] == 0) {
                        northwest = c; // если запасы заканчивются то останавливаемся
                        break;
                    }
                }
            }
        result += "\n\nNorth West result:\n\n"+ printResult();
    }

    public void steppingStone() {
        double maxReduction = 0;
        Shipment[] move = null;
        Shipment leaving = null;
        int counter = 0;

        fixDegenerateCase();

        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {

                if (matrix[r][c] != null)
                    continue;

                Shipment trial = new Shipment(0, costs[r][c], r, c);
                Shipment[] path = getClosedPath(trial);

                double reduction = 0;
                double lowestQuantity = Integer.MAX_VALUE;
                Shipment leavingCandidate = null;

                // прибавлениве вычитание минимального элемента из пути
                boolean plus = true;
                for (Shipment s : path) {
                    if (plus) {
                        reduction += s.costPerUnit;
                    } else {
                        reduction -= s.costPerUnit;
                        if (s.quantity < lowestQuantity) {
                            leavingCandidate = s;
                            lowestQuantity = s.quantity;
                        }
                    }
                    plus = !plus;
                }
                if (reduction < maxReduction) {
                    move = path;
                    leaving = leavingCandidate;
                    maxReduction = reduction;
                }
            }
        }

        if (move != null) {
            double q = leaving.quantity;
            boolean plus = true;
            for (Shipment s : move) {
                s.quantity += plus ? q : -q;
                matrix[s.r][s.c] = s.quantity == 0 ? null : s;
                plus = !plus;
            }
            result += "\n\nIntermediate result: "+"\n\n"+ printResult();
            steppingStone();
        }
    }

    static LinkedList<Shipment> matrixToList() {
        return stream(matrix)
                .flatMap(row -> stream(row))
                .filter(s -> s != null)
                .collect(toCollection(LinkedList::new));
    }

    // строим цикл
    static Shipment[] getClosedPath(Shipment s) {
        LinkedList<Shipment> path = matrixToList();
        path.addFirst(s);

        //Удаляем из пути элементы у которых нет соседей
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));

        // place the remaining elements in the correct plus-minus order
        Shipment[] stones = path.toArray(new Shipment[path.size()]);
        Shipment prev = s;
        for (int i = 0; i < stones.length; i++) {
            stones[i] = prev;
            prev = getNeighbors(prev, path)[i % 2];
        }
        return stones;
    }

    static Shipment[] getNeighbors(Shipment s, LinkedList<Shipment> lst) {
        Shipment[] nbrs = new Shipment[2];
        for (Shipment o : lst) {
            if (o != s) {
                if (o.r == s.r && nbrs[0] == null)
                    nbrs[0] = o;
                else if (o.c == s.c && nbrs[1] == null)
                    nbrs[1] = o;
                if (nbrs[0] != null && nbrs[1] != null)
                    break;
            }
        }
        return nbrs;
    }

    //Решаем проблемму
    static void fixDegenerateCase() {
        final double eps = Double.MIN_VALUE;

        if (supply.length + demand.length - 1 != matrixToList().size()) {

            for (int r = 0; r < supply.length; r++)
                for (int c = 0; c < demand.length; c++) {
                    if (matrix[r][c] == null) {
                        Shipment dummy = new Shipment(eps, costs[r][c], r, c);
                        if (getClosedPath(dummy).length == 0) {
                            matrix[r][c] = dummy;
                            return;
                        }
                    }
                }
        }
    }

    public String printResult() {
        String result = "";

        double totalCosts = 0;

        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {

                Shipment s = matrix[r][c];
                if (s != null && s.r == r && s.c == c) {
                    result += " " + s.quantity + " ";
                    totalCosts += (s.quantity * s.costPerUnit);
                } else
                    result += " - ";
            }
            result += "\n";
        }
        result += "\n Total costs: " + totalCosts + "\n\n";
        return result;
    }

    public  String result()
    {
        String result = "";
        result += "Запрос:\n";
        for (int i = 0; i < demand.length ; i++) {
            result +=" "+demand[i]+" ";
        }
        result += "\n Потребность:\n";
        for (int i = 0; i < supply.length ; i++) {
            result += " "+supply[i]+" ";
        }
        result += "\nЦены:\n";
        for (int i = 0; i < costs.length ; i++) {
            for (int j = 0; j < costs[i].length ; j++) {
                result += " "+ costs[i][j]+" ";
            }
            result += "\n";
        }
        return result;
    }

    public TransportationProblem() {
        demand = new Integer[0];
        supply = new Integer[0];
        costs = new Double[0][];
        matrix = new Shipment[0][];
            /*northWestCornerRule();
            steppingStone();
            printResult();*/

    }

    public Integer[] getDemand()
    {
        return demand;
    }

    public Integer[] getSuply()
    {
        return supply;
    }

    public Double[][] getCosts()
    {
        return costs;
    }

    public String getResult()
    {
        return result;
    }
}
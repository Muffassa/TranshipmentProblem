import java.util.*;
import static java.util.Arrays.stream;

public class TransportationProblem {

    private static LinkedList<Shipment> basicShipments;
    private static Integer[] demand = {150, 130, 150, 140};
    private static Integer[] supply = {200, 180, 190};
    private static Integer[][] costs = {
            {7, 8, 1, 2},
            {4, 5, 9, 8},
            {9, 2, 3, 6}
    };
    private static Shipment[][] matrix;
    private static Shipment lowestPotencialShipment;
    String result = "";

    private static class Shipment {
        final Integer costPerUnit;
        final Integer rowID, columnID;
        private Integer quantity;
        private Integer potential;
        public boolean isBasic;

        public Shipment(Integer q, Integer cpu, Integer rowID, Integer columnID) {
            quantity = q;
            costPerUnit = cpu;
            this.rowID = rowID;
            this.columnID = columnID;

            if(quantity != 0)
            {
                isBasic = true;
            }
            else isBasic = false;
        }

        public void setPotential(Integer potential) {
            this.potential = potential;
        }

        public void setQuantity(Integer quantity)
        {
            this.quantity = quantity;
        }

        public Integer getQuantity()
        {
            return quantity;
        }
    }

    public void init() {
        String[] _demand = MainForm.getRequestData();
        String[] _supply = MainForm.getStoreData();
        String[][] _costs = MainForm.getPriceData();

        demand = new Integer[_demand.length];
        for (int i = 0; i < _demand.length; i++) {

            demand[i] = Integer.parseInt(_demand[i]);
        }

        supply = new Integer[_supply.length];
        for (int i = 0; i < _supply.length; i++) {
            supply[i] = Integer.parseInt(_supply[i]);
        }

        costs = new Integer[_costs.length][_costs[0].length];
        for (int i = 0; i < _costs.length; i++) {
            for (int j = 0; j < _costs[i].length; j++) {

                costs[i][j] = Integer.parseInt(_costs[i][j]);
            }
        }


    }


    public static String printResult() {
        String result = "";

        double totalCosts = 0;

        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {

                Shipment s = matrix[r][c];
                if (s != null && s.rowID == r && s.columnID == c) {
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

    public TransportationProblem() {
        demand = new Integer[0];
        supply = new Integer[0];
        costs = new Integer[0][];
        matrix = new Shipment[0][];
            /*northWestCornerRule();
            steppingStone();
            printResult();*/

    }

    public static void northWest()
    {
        for (int i = 0; i < supply.length ; i++) {
            for (int j = 0; j < demand.length ; j++) {
                    Integer quantity = Math.min(supply[i], demand[j]);
                    matrix[i][j] = new Shipment(quantity, costs[i][j], i, j);
                    supply[i] -= quantity;
                    demand[j] -= quantity;

            }
        }
    }


    public static boolean isOptimal()
    {
        Integer[] u = new Integer[supply.length];
        Integer[] v = new Integer[demand.length];

        //Составляем массив из базисных элементов
        basicShipments = new LinkedList<Shipment>();
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j <matrix[i].length ; j++) {
                if(matrix[i][j].isBasic)
                {
                    basicShipments.add(matrix[i][j]);
                }
            }
        }

        //считаем u и v для каждой базисной точки
        u[0] = 0;
        for (int i = 0; i < basicShipments.size() ; i++) {
            Shipment c = basicShipments.get(i);
            if(v[c.columnID] == null)
            {
                v[c.columnID] = c.costPerUnit - u[c.rowID];
            }
            else if(u[c.rowID] == null)
            {
                u[c.rowID] = c.costPerUnit - v[c.columnID];
            }
        }

        //считаем потенциал для всех точек
        for (int i = 0; i < matrix.length ; i++) {
            for (int j = 0; j < matrix[i].length ; j++) {
                matrix[i][j].setPotential(matrix[i][j].costPerUnit - u[matrix[i][j].rowID] - v[matrix[i][j].columnID]);
            }
        }

        lowestPotential();
        if(lowestPotencialShipment.potential < 0)
        {
            return false;
        }
        return true;
    }

    //Функция возращает элемент с наименьшим потенциалом
    public static void lowestPotential()
    {
        lowestPotencialShipment = new Shipment(0,0,0,0);
        lowestPotencialShipment.setPotential(999);
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j < matrix[i].length ; j++) {
                if(matrix[i][j].potential < lowestPotencialShipment.potential)
                {
                    lowestPotencialShipment = matrix[i][j];
                }
            }
        }
    }

    private static Shipment[] getClosePath()
    {
        LinkedList<Shipment> path = new LinkedList<>(basicShipments);
        path.addFirst(lowestPotencialShipment);

        //выкидываем элементы которые не имеют соседей по вертикали или горизонтали
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));

        // place the remaining elements in the correct plus-minus order
        Shipment[] stones = path.toArray(new Shipment[path.size()]);
        Shipment prev = lowestPotencialShipment;
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
                if (o.rowID == s.rowID && nbrs[0] == null)
                    nbrs[0] = o;
                else if (o.columnID == s.columnID && nbrs[1] == null)
                    nbrs[1] = o;
                if (nbrs[0] != null && nbrs[1] != null)
                    break;
            }

        }
        return nbrs;
    }

    //составляем масив из бахи

    private static void optimize()
    {

        //ищем наименьшее количество товара для оптимизации
        int lowestQuantity = 999;
        Shipment[] path = getClosePath();
        for (int i = 1; i < path.length ; i++) {
            if (path[i].quantity < lowestQuantity)
            {
                lowestQuantity = path[i].quantity;
            }
        }

        //Оптимизируем цикл
        boolean sign = true;
        for (int i = 0; i < path.length ; i++) {
            if(sign) {
                matrix[path[i].rowID][path[i].columnID].setQuantity(matrix[path[i].rowID][path[i].columnID].getQuantity()+lowestQuantity);
            }
            else matrix[path[i].rowID][path[i].columnID].setQuantity(matrix[path[i].rowID][path[i].columnID].getQuantity()-lowestQuantity);
            sign = !sign;
        }
    }

    public Integer[] getDemand() {
        return demand;
    }

    public Integer[] getSuply() {
        return supply;
    }

    public Integer[][] getCosts() {
        return costs;
    }

    public String getResult() {
        return result;
    }

    public static void main(String[] args) {
        matrix = new Shipment[supply.length][demand.length];
        northWest();
        System.out.println(printResult());
        isOptimal();
        optimize();
        System.out.println(printResult());
        isOptimal();
        optimize();
        System.out.println(printResult());



    }


}
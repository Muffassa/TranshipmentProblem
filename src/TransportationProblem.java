import java.util.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

public class TransportationProblem {

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

        public Shipment(Integer q, Integer cpu, Integer rowID, Integer columnID) {
            quantity = q;
            costPerUnit = cpu;
            this.rowID = rowID;
            this.columnID = columnID;
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



    static void fixDegenerateCase() {
        final int eps = Integer.MIN_VALUE;

        if (supply.length + demand.length - 1 != matrix.length) {

            for (int r = 0; r < supply.length; r++)
                for (int c = 0; c < demand.length; c++) {
                    if (matrix[r][c] == null) {
                        Shipment dummy = new Shipment(eps, costs[r][c], r, c);
                        LinkedList<Shipment> _dummy = new LinkedList<>();
                        _dummy.add(dummy);
                        if (getClosePath(_dummy).length == 0) {
                            matrix[r][c] = dummy;
                            return;
                        }
                    }
                }
        }
    }


    public TransportationProblem() {
        demand = new Integer[0];
        supply = new Integer[0];
        costs = new Integer[0][];
        matrix = new Shipment[0][];
            init();
        lowestPotencialShipment = new Shipment(0,0,0,0);
        lowestPotencialShipment.setPotential(-999);
        matrix = new Shipment[supply.length][demand.length];
        northWest();
        result += printResult();
        while (lowestPotencialShipment.potential < 0)
        {
            optimize();
            result += printResult();
        }
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

    //Ищем путь
    private static Shipment[] getClosePath(LinkedList<Shipment> basicShipments)
    {
        LinkedList<Shipment> path = basicShipments;
        path.addFirst(lowestPotencialShipment);

        //выкидываем элементы которые не имеют соседей по вертикали или горизонтали
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));

        //сохрнаяем элементы
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


    private static void optimize()
    {

        LinkedList<Shipment>basicShipments = new LinkedList<Shipment>();


        fixDegenerateCase();

        //составляем матрицу из базисных элементов
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j <matrix[i].length ; j++) {
                /*matrix[i][j].refresh();*/
                if(matrix[i][j].quantity > 0)
                {
                    basicShipments.add(matrix[i][j]);
                }
            }
        }

        HashMap<Integer, Integer> u = new HashMap<Integer, Integer>(supply.length);
        HashMap<Integer, Integer> v = new HashMap<>(demand.length);

        //считаем u и v для каждой базисной точки
        u.put(0,0);
        boolean flag = false;
        for (int i = 0; i < basicShipments.size() ; i++) {
            Shipment c = basicShipments.get(i);
            //если u и v размером с supply и demand то выходим из цикла
            if(u.size() == supply.length && v.size() == demand.length)
            {
                break;
            }

            //если у элемента есть и v и u или у элемента нет v и u то переходим к следующему элементу
            if((v.get(c.columnID) != null && u.get(c.rowID) != null) || (v.get(c.columnID) == null && u.get(c.rowID) == null))
            {
                flag = true;
                if(!(i == basicShipments.size()-1))
                {
                    continue;
                }

            }

                if (v.get(c.columnID) == null) {
                    v.put(c.columnID, c.costPerUnit - u.get(c.rowID));
                } else if (u.get(c.rowID) == null) {
                    u.put(c.rowID, c.costPerUnit - v.get(c.columnID));
                }


            //цикл продолжается до того момента как все v и u будут заполнены
            if((i == basicShipments.size()-1) && flag)
            {
                i = 0;
                flag = false;
            }
        }

        //считаем потенциал для всех точек
        for (int i = 0; i < matrix.length ; i++) {
            for (int j = 0; j < matrix[i].length ; j++) {
                matrix[i][j].setPotential(matrix[i][j].costPerUnit - u.get(matrix[i][j].rowID) - v.get(matrix[i][j].columnID));
            }
        }

        lowestPotential();

        //ищем наименьшее количество товара для оптимизации
        int lowestQuantity = 999;
        Shipment[] path = getClosePath(basicShipments);
        boolean sing1 = false;
        for (int i = 1; i < path.length ; i++) {
            if(!sing1)
            {
                if(path[i].quantity < lowestQuantity)
                {
                    lowestQuantity = path[i].quantity;
                }
            }
            sing1 = !sing1;
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

    public String getResult() {
        return result;
    }



}
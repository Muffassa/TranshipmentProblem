import java.util.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

public class TransportationProblem {

    private static ArrayList<Integer> demand; /*= {150, 130, 150, 140};*/
    private static ArrayList<Integer> supply; /*= {200, 180, 190};*/
    private static Integer[][] costs = {
            {4, 5, 3, 6},
            {7, 2, 1, 5},
            {6, 1, 4, 2}
    };
    private static Shipment[][] matrix;
    private static String result = "";

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

        demand = new ArrayList<>();
        for (int i = 0; i < _demand.length; i++) {

            demand.add(Integer.parseInt(_demand[i]));
        }

        supply = new ArrayList<>();
        for (int i = 0; i < _supply.length; i++) {
            supply.add(Integer.parseInt(_supply[i]));
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

        for (int r = 0; r < supply.size(); r++) {
            for (int c = 0; c < demand.size(); c++) {

                Shipment s = matrix[r][c];
                if (s.quantity != null && s.rowID == r && s.columnID == c) {
                    result += "   " + s.quantity + "   ";
                    totalCosts += (s.quantity * s.costPerUnit);
                } else
                    result += "   -   ";
            }
            result += "\n";
        }
        result += "\n Total costs: " + totalCosts + "\n\n";
        return result;
    }


    private static boolean isOpen(int totalDemand, int totalSupply)
    {
        if(totalDemand == totalSupply)
        {
            return false;
        }
        else return true;
    }

    static void fixOpenCase() {
        int totalDemand = 0;
        int totalSupply = 0;

        for (int i = 0; i < demand.size() ; i++) {
            totalDemand += demand.get(i);
        }

        for (int i = 0; i < supply.size() ; i++) {
            totalSupply += supply.get(i);
        }

        if (isOpen(totalDemand, totalSupply))
        {
            if (totalDemand < totalSupply)
            {
                int digenerate = totalSupply - totalDemand;
                //добавляем фиктивный магазин
                demand.add(digenerate);
                Integer[][] _costs = new Integer[supply.size()][demand.size()];

                //добавляем стоимость 0 для фиктивного магазина
                for (int i = 0; i < _costs.length ; i++) {
                    for (int j = 0; j < _costs[i].length ; j++) {
                        _costs[i][j] = 0;
                    }
                }

                for (int i = 0; i < costs.length; i++) {
                    for (int j = 0; j < costs[i].length ; j++) {
                        _costs[i][j] = costs[i][j];
                    }
                }

                costs = new Integer[supply.size()][demand.size()];

                for (int i = 0; i < costs.length ; i++) {
                    for (int j = 0; j < costs[i].length ; j++) {
                        costs[i][j] = _costs[i][j];
                    }
                }
            }
            else if(totalDemand > totalSupply)
            {
                int digenerate = totalDemand - totalSupply;
                supply.add(digenerate);
                Integer[][] _costs = new Integer[supply.size()][demand.size()];

                //добавляем стоимость 0 для фиктивного магазина
                for (int i = 0; i < _costs.length ; i++) {
                    for (int j = 0; j < _costs[i].length ; j++) {
                        _costs[i][j] = 0;
                    }
                }

                for (int i = 0; i < costs.length; i++) {
                    for (int j = 0; j < costs[i].length ; j++) {
                        _costs[i][j] = costs[i][j];
                    }
                }

                costs = new Integer[supply.size()][demand.size()];

                for (int i = 0; i < costs.length ; i++) {
                    for (int j = 0; j < costs[i].length ; j++) {
                        costs[i][j] = _costs[i][j];
                    }
                }


            }
        }
    }


    public TransportationProblem() {
        init();
        fixOpenCase();
        matrix = new Shipment[supply.size()][demand.size()];
        northWest();
        fixDegenerateCase();
        result+= "\n\nNorthWest result:\n\n";
        result += printResult();
        int counter = 0;
       while(!isOptimal()) {
           optimize();
           fixDegenerateCase();
           result += printResult();
       }
    }

    public static void northWest()
    {
        for (int i = 0; i < supply.size() ; i++) {
            for (int j = 0; j < demand.size() ; j++) {
                    Integer quantity = Math.min(supply.get(i), demand.get(j));
                if(quantity > 0) {
                    matrix[i][j] = new Shipment(quantity, costs[i][j], i, j);
                    supply.add(i, supply.remove(i) - quantity);
                    demand.add(j, demand.remove(j) - quantity);
                }
                else if (quantity <= 0)
                {
                    matrix[i][j] = new Shipment(null, costs[i][j], i, j);
                }

            }
        }
    }

    //Функция возращает элемент с наименьшим потенциалом
    public static Shipment lowestPotential()
    {
        Shipment lowestPotencialShipment = new Shipment(0,0,0,0);
        lowestPotencialShipment.setPotential(999);
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j < matrix[i].length ; j++) {
                if(matrix[i][j].potential < lowestPotencialShipment.potential)
                {
                    lowestPotencialShipment = matrix[i][j];
                }
            }
        }
        return lowestPotencialShipment;
    }

    //Ищем путь
    private static Shipment[] getClosePath(Shipment shipment)
    {
        LinkedList<Shipment> path = findBasicShipments();
        path.addFirst(shipment);

        //выкидываем элементы которые не имеют соседей по вертикали или горизонтали
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));

        //сохрнаяем элементы
        Shipment[] root = path.toArray(new Shipment[path.size()]);
        Shipment prev = shipment;
        for (int i = 0; i < root.length; i++) {
            root[i] = prev;
            prev = getNeighbors(prev, path)[i % 2];
        }
        return root;
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

    private static LinkedList<Shipment> findBasicShipments()
    {
        LinkedList<Shipment>basicShipments = new LinkedList<Shipment>();

        //составляем матрицу из базисных элементов
        for (int i = 0; i <matrix.length ; i++) {
            for (int j = 0; j <matrix[i].length ; j++) {
                /*matrix[i][j].refresh();*/
                if(matrix[i][j].quantity != null)
                {
                    basicShipments.add(matrix[i][j]);
                }
            }
        }
        return basicShipments;
    }

    static void fixDegenerateCase() {
        final int eps = 0;

        if (supply.size() + demand.size() - 1 != findBasicShipments().size()) {

            for (int r = 0; r < supply.size(); r++)
                for (int c = 0; c < demand.size(); c++) {
                    if (matrix[r][c].quantity == null) {
                        Shipment dummy = new Shipment(eps, costs[r][c], r, c);
                        if (getClosePath(dummy).length == 0) {
                            matrix[r][c] = dummy;
                            return;
                        }
                    }
                }
        }
    }

    private static boolean isOptimal()
    {
        LinkedList<Shipment> basicShipments = new LinkedList<>();

        basicShipments = findBasicShipments();

        HashMap<Integer, Integer> u = new HashMap<Integer, Integer>(supply.size());
        HashMap<Integer, Integer> v = new HashMap<>(demand.size());

        //считаем u и v для каждой базисной точки
        u.put(0, 0);
        boolean flag = false;
        for (int i = 0; i < basicShipments.size() ; i++) {
            Shipment c = basicShipments.get(i);
            //если u и v размером с supply и demand то выходим из цикла
            if(u.size() == supply.size() && v.size() == demand.size())
            {
                break;
            }

            //если у элемента есть и v и u или у элемента нет v и u то переходим к следующему элементу
            if((v.get(c.columnID) != null && u.get(c.rowID) != null) || (v.get(c.columnID) == null && u.get(c.rowID) == null))
            {
                flag = true;
            }
            if (v.get(c.columnID) == null && u.get(c.rowID) != null) {
                v.put(c.columnID, c.costPerUnit - u.get(c.rowID));
            } else if (u.get(c.rowID) == null  && v.get(c.columnID) != null) {
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

        if(lowestPotential().potential < 0)
        {
            return false;
        }
        else return true;
    }



    private static void optimize()
    {
        //избавляемся от фиктивного элемента перед построением цикла
       /* for (int i = 0; i < basicShipments.size() ; i++) {
            if(basicShipments.get(i).costPerUnit == 0)
                basicShipments.remove(i);
        }*/
        Shipment[] path = getClosePath(lowestPotential());
        //ищем наименьшее количество товара для оптимизации
        int lowestQuantity = 999;
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
        boolean sign = false;
        matrix[path[0].rowID][path[0].columnID].setQuantity(lowestQuantity);
        for (int i = 1; i < path.length ; i++) {
            if(sign) {
                matrix[path[i].rowID][path[i].columnID].setQuantity(matrix[path[i].rowID][path[i].columnID].getQuantity()+lowestQuantity);
            }
            else {
                matrix[path[i].rowID][path[i].columnID].setQuantity(matrix[path[i].rowID][path[i].columnID].getQuantity() - lowestQuantity);
                if(matrix[path[i].rowID][path[i].columnID].getQuantity() == 0)
                {
                    matrix[path[i].rowID][path[i].columnID].setQuantity(null);
                }
            }
            sign = !sign;
        }

    }

    public String getResult() {
        return result;
    }


    /*public static void main(String[] args) {
        demand = new ArrayList<>();
        supply = new ArrayList<>();
        demand.add(220);
        demand.add(150);
        demand.add(250);
        demand.add(180);

        supply.add(300);
        supply.add(250);
        supply.add(200);
        TransportationProblem tp = new TransportationProblem();
        System.out.println(result);
    }*/
}
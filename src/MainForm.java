import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by muffass on 04.11.15.
 */
public class MainForm {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel termsPanel;
    private JPanel resultPanel;
    private JPanel requestPanel;
    private JPanel storePanel;
    private JTable requestTable;
    private JScrollPane requestTableJScroll;
    private JTable storeTable;
    private JScrollPane storeTableJScroll;
    private JPanel pricePanel;
    private JScrollPane priceTableJScroll;
    private JTable priceTable;
    private JTextPane resultPane;
    private JButton resultButton;
    private JComboBox requestComboBox;
    private JComboBox storeComboBox;
    private TableModel requestTableModel;
    private TableModel storeTableModel;
    private TableModel priceTableModel;
    private DefaultTableModel storeDefaultTableModel;
    private DefaultTableModel requestDefaultTableModel;
    private DefaultTableModel priceDefaultTableModel;
    private static String[] requestData;
    private static String[] storeData;
    private static String[][] priceData;
    TransportationProblem tp;

    MainForm()
    {
        tp = new TransportationProblem();
        ///Отрисовка окна
        JFrame jFrame = new JFrame("Transhipment Problem");

        jFrame.setSize(700, 400);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Таблица запросов
        requestData = new String[0];
        requestDefaultTableModel = new DefaultTableModel(0,0);
        requestDefaultTableModel.setColumnIdentifiers(new String[]{"Потребность"});
        requestTable.setModel(requestDefaultTableModel);
        requestTableModel = requestTable.getModel();

        //Таблица поставщиков
        storeData = new String[0];
        storeDefaultTableModel = new DefaultTableModel(0,0);
        storeDefaultTableModel.setColumnIdentifiers(new String[]{"Запас"});
        storeTable.setModel(storeDefaultTableModel);
        storeTableModel = storeTable.getModel();

        //Таблица цен
        priceData = new String[0][0];
        priceDefaultTableModel = new DefaultTableModel(storeDefaultTableModel.getRowCount(), requestDefaultTableModel.getRowCount());
        priceTable.setModel(priceDefaultTableModel);
        priceTableModel = priceTable.getModel();




        //Action for requestTable
        requestTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                requestData = new String[requestDefaultTableModel.getRowCount()];
                for (int i = 0; i < requestDefaultTableModel.getRowCount(); i++) {
                    requestData[i] = (String)requestDefaultTableModel.getValueAt(i, 0);
                }
            }
        });

        requestComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestDefaultTableModel.setRowCount(requestComboBox.getSelectedIndex());
                priceDefaultTableModel.setColumnCount(requestComboBox.getSelectedIndex());
            }
        });


        //Action for storeTable

        ///Метод для добавления полей для таблицы запасов
        storeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeDefaultTableModel.setRowCount(storeComboBox.getSelectedIndex());
                priceDefaultTableModel.setRowCount(storeComboBox.getSelectedIndex());
            }
        });

        //Сохраняет добавляемые данные в таблицу запасов
         storeTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                storeData = new String[storeDefaultTableModel.getRowCount()];
                for (int i = 0; i < storeDefaultTableModel.getRowCount(); i++) {
                    storeData[i] = (String)storeDefaultTableModel.getValueAt(i, 0);
                }
            }
        });


        ////////Action for priceTable
        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Инициализация стоимости
                priceData = new String[priceDefaultTableModel.getRowCount()][priceDefaultTableModel.getColumnCount()];
                for (int i = 0; i < priceDefaultTableModel.getRowCount() ; i++) {
                    for (int j = 0; j < priceDefaultTableModel.getColumnCount() ; j++) {
                        priceData[i][j] = (String)priceDefaultTableModel.getValueAt(i, j);
                    }
                }


               /* String result = "";

                result += "Запрос:\n";
                for (int rowID = 0; rowID < requestData.length ; rowID++) {
                    result += " " + requestData[rowID] + " ";
                }
                result += "\nЗапас:\n";
                for (int rowID = 0; rowID < storeData.length; rowID++)
                {
                    result += " "+ storeData[rowID] + " ";
                }
                result += "\nСтоимость\n";

                for (int rowID = 0; rowID < priceData.length ; rowID++) {
                    for (int columnID = 0; columnID < priceData[rowID].length; columnID++) {
                        result += priceData[rowID][columnID];
                    }
                    result += "\n";
                }*/
                tp.init();
                /*tp.northWestCornerRule();
                tp.steppingStone();*/
                resultPane.setText(tp.getResult() + "\n\nOptimal solution:\n\n"+ tp.printResult());

            }
        });


        jFrame.add(mainPanel);

        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });
    }

    public static String[][] getPriceData()
    {
        return priceData;
    }

    public static String[] getRequestData()
    {
        return requestData;
    }

    public static String[] getStoreData()
    {
        return storeData;
    }
}

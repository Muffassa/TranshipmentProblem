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
    private JLabel testLable;
    private TableModel requestTableModel;
    private TableModel storeTableModel;
    private TableModel priceTableModel;
    private DefaultTableModel storeDefaultTableModel;
    private DefaultTableModel requestDefaultTableModel;
    private DefaultTableModel priceDefaultTableModel;
    private Object[] requestData;
    private Object[] storeData;
    private Object[][] priceData;

    MainForm()
    {
        ///Отрисовка окна
        JFrame jFrame = new JFrame("Transhipment Problem");

        jFrame.setSize(700, 400);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Таблица запросов
        requestData = new Object[0];
        requestDefaultTableModel = new DefaultTableModel(0,0);
        requestDefaultTableModel.setColumnIdentifiers(new String[]{"ID", "Потребность"});
        requestTable.setModel(requestDefaultTableModel);
        requestTableModel = requestTable.getModel();

        //Таблица поставщиков
        storeData = new Object[0];
        storeDefaultTableModel = new DefaultTableModel(0,0);
        storeDefaultTableModel.setColumnIdentifiers(new String[]{"ID", "Запас"});
        storeTable.setModel(storeDefaultTableModel);
        storeTableModel = storeTable.getModel();

        //Таблица цен
        priceData = new Object[10][10];
        priceDefaultTableModel = new DefaultTableModel(storeDefaultTableModel.getRowCount(), requestDefaultTableModel.getRowCount());
        priceTable.setModel(priceDefaultTableModel);
        priceTableModel = priceTable.getModel();




        //Action for requestTable
        requestTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                requestData = new Object[requestDefaultTableModel.getRowCount()];
                for (int i = 0; i < requestDefaultTableModel.getRowCount(); i++) {
                    requestData[i] = requestDefaultTableModel.getValueAt(i, 1);
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
                storeData = new Object[storeDefaultTableModel.getRowCount()];
                for (int i = 0; i < storeDefaultTableModel.getRowCount(); i++) {
                    storeData[i] = storeDefaultTableModel.getValueAt(i, 1);
                }
            }
        });


        ////////Action for priceTable
        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String request = "";
                String store = "";
                String price = "";
                priceData = new Object[priceDefaultTableModel.getRowCount()][priceDefaultTableModel.getColumnCount()];
                for(int i = 0; i < priceDefaultTableModel.getRowCount(); i++)
                {
                    for(int j = 0; j < priceDefaultTableModel.getColumnCount(); j++)
                    {
                        priceData[i][j] = priceDefaultTableModel.getValueAt(i, j);
                    }
                }

                for (int i = 0; i <requestData.length ; i++) {
                    request += " " + requestData[i];
                }

                for (int i = 0; i < storeData.length; i++)
                {
                    store += " " + storeData[i];
                }

                for(int i = 0; i < priceData.length; i++)
                {
                    for(int j = 0; j < priceData[i].length; j++)
                    {
                        price += " " + priceData[i][j];
                    }
                    price += "\n";
                }

                resultPane.setText("Запрос: " + request+"\n" + "Склад: "+ store + "\n" +
                "Цены: \n" + price);


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

    public Object[][] getPriceData()
    {
        return priceData;
    }

    public Object[] getRequestData()
    {
        return requestData;
    }

    public Object[] getStoreData()
    {
        return storeData;
    }
}

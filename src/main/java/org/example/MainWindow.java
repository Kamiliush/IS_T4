package org.example;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private JButton readFileButton;
    private JButton saveToFileButton;
    private JButton readXMLButton;
    private JButton saveToXMLButton;
    private JButton readDatabaseButton;
    private JButton saveToDatabaseButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    private JLabel bottomLabel;

    private List<String> headers = Arrays.asList("Producent", "Przekątna ekranu",
            "Rozdzielczość ekranu", "Powierzchnia ekranu", "Ekran dotykowy",
            "Procesor", "Liczba rdzeni", "Taktowanie CPU", "Wielkość RAM",
            "Pojemność dysku", "Rodzaj dysku", "Karta Graficzna", "Pamięć GPU",
            "Nazwa systemu op.", "Napęd optyczny");

    private List<LaptopEntity> laptops;
    private List<LaptopEntity> laptopsShown;

    private List<List<String>> laptopsFromTable = new ArrayList<>();

    private String line;

    private String[] specs;

    private String regexForLettersOnly = "^[a-zA-Z]+$";
    private String regexForScreenDiagonal = "^[0-9]+\"$";
    private String regexForResolution = "\\d+x\\d+";
    private String regexForNumbersOnly = "^[0-9]+$";
    private String regexForPositiveNumbersOnly = "^[1-9][0-9]*$";
    private String regexForGigabytes = "^[0-9]+GB$";

    private boolean isFileRead = false;
    private boolean isTableValid = false;

    private final String DB_URL = "jdbc:mysql://localhost:3308/laptops?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "root";

    CustomTableCellRendererForDuplicates customTableCellRendererForDuplicates = new CustomTableCellRendererForDuplicates();

    public MainWindow() throws HeadlessException {
        super("Integracja Systemów - Zadanie T4 - Kamil Zagajewski");
        laptops = new ArrayList<>();
        tableModel = new DefaultTableModel();

        dataTable = new JTable(tableModel);
        setHeaders();
        tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if(e.getType() == TableModelEvent.UPDATE){
                    int editedRowIndex = e.getFirstRow();
                    int editedColumnIndex = e.getColumn();
                    //System.out.println("Wiersz i kolumna:" + editedRowIndex + " " + editedColumnIndex);
                    String oldValue = laptopsFromTable.get(editedRowIndex).get(editedColumnIndex);
                    String newValue = dataTable.getValueAt(editedRowIndex,editedColumnIndex).toString();
                    //System.out.println("Stara i nowa wartość: " + oldValue + ", " + newValue);

                    if(!oldValue.equals(newValue)){
                        customTableCellRendererForDuplicates.addEditedRow(editedRowIndex);
                        laptopsFromTable.get(editedRowIndex).set(editedColumnIndex, newValue);
                        repaint();
                    }


                }

            }
        });


        readFileButton = new JButton("Wczytaj dane z pliku CSV");
        readFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    readFile();
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage(), "Błąd pliku CSV", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

            }
        });

        saveToFileButton = new JButton("Zapisz dane do pliku CSV");
        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFileRead){
                    try{
                        System.out.println("Przycisk zapisania do csv wciśnięty");
                        saveToFile();
                    }
                    catch (Exception ex){
                        JOptionPane.showMessageDialog(null,ex.getMessage(), "Błąd zapisu do pliku", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Brak danych do zapisania", "Błąd zapisu do pliku", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        readXMLButton = new JButton("Wczytaj dane z pliku XML");

        readXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    readXMLfile();
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage(), "Błąd pliku XML", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        saveToXMLButton = new JButton("Zapisz dane do pliku XML");

        saveToXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFileRead){
                    try{
                        System.out.println("Przycisk zapisania do XML wciśnięty");
                        saveToXMLFile();
                    }
                    catch (Exception ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Błąd zapisu do XML", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Brak danych do zapisania", "Błąd zapisu do XML", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        readDatabaseButton = new JButton("Wczytaj dane z BD");
        readDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    try{
                        System.out.println("Przycisk wczytania z Bazy Danych wciśnięty");
                        readFromDatabase();
                    }
                    catch (Exception ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Błąd odczytu bazy danych", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
            }
        });

        saveToDatabaseButton = new JButton("Zapisz dane do BD");
        saveToDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFileRead){
                    try{
                        System.out.println("Przycisk zapisania do Bazy Danych wciśnięty");
                        saveToDatabase();
                    }
                    catch (Exception ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Błąd zapisu do bazy danych", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Brak danych do zapisania", "Błąd zapisu do bazy danych", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bottomLabel = new JLabel("Liczba znalezionych rekordów:    Liczba znalezionych powtórzeń: ");

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(dataTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(bottomLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(readFileButton);
        buttonPanel.add(saveToFileButton);
        buttonPanel.add(readXMLButton);
        buttonPanel.add(saveToXMLButton);
        buttonPanel.add(readDatabaseButton);
        buttonPanel.add(saveToDatabaseButton);
        contentPane.add(buttonPanel, BorderLayout.PAGE_START);



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 500);
        setLocationRelativeTo(null);
        setVisible(true);

    }
        /*--------------------------------------------------------------------*/
    private void readFile() throws IOException {
        BufferedReader bufferedReader = null;
        laptopsShown = new ArrayList<>(laptops);
        laptops = new ArrayList<>();
        String fileName = "katalog.csv";
        int i = 0;
        try{
            bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((line = bufferedReader.readLine()) != null){
                System.out.println(line);

                specs = line.split(";", -1);
//                for (String element: specs) {
//                    System.out.println(element + ",");
//                }
                laptops.add(new LaptopEntity(specs[0], specs[1], specs[2], specs[3], specs[4], specs[5],
                specs[6], specs[7], specs[8], specs[9], specs[10], specs[11], specs[12], specs[13], specs[14]));
            }
            laptops.forEach(element -> System.out.println(element.toString()));
            fillTable();
            isFileRead = true;
        }
        catch (Exception e){
            if(e instanceof FileNotFoundException){
                JOptionPane.showMessageDialog(null, "Nie udało się wczytać pliku o nazwie: " + fileName, "Błąd", JOptionPane.ERROR_MESSAGE);
            }
            else e.printStackTrace();
        }
        finally {
            bufferedReader.close();
        }

    }

    private void saveToFile() throws IOException {
        if(validateBeforeSave()){
            List<List> results = new ArrayList<>();
            int rows = dataTable.getRowCount();
            int columns = dataTable.getColumnCount();
            String value = null;

            for (int i = 0; i < rows; i++) {
                List<String> listOfRows = new ArrayList<>();
                for (int j = 0; j < columns; j++) {
                    value = dataTable.getValueAt(i, j) == null ? "" : dataTable.getValueAt(i, j).toString();
                    listOfRows.add(value);

                }
                results.add(listOfRows);
            }
            Random random = new Random();
            int randomNumber = random.nextInt(1000) + 1;
            String outputFileName = "katalog" + randomNumber + ".csv";
            System.out.println(outputFileName);

            File file = new File(outputFileName);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            for (var result: results) {
                try{
                    String buffer = "";
                    for (var element: result) {
                        buffer += element + ";";
                    }
                    bufferedWriter.write(buffer + "\n");
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(null,e.getMessage(), "Błąd zapisu do pliku", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
            bufferedWriter.close();
            JOptionPane.showMessageDialog(null, "Zapisano dane do pliku " + outputFileName, "Informacja", JOptionPane.INFORMATION_MESSAGE);

        }

    }

    private void setHeaders(){
        for (String header: headers) {
            tableModel.addColumn(header);
        }
        repaint();
        /*System.out.println("Liczba kolumn:" + dataTable.getColumnCount());
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setCellRenderer(customTableCellRendererForDuplicates);
        }*/

        dataTable.getColumnModel().getColumn(0).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForLettersOnly));
        dataTable.getColumnModel().getColumn(1).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForScreenDiagonal));
        dataTable.getColumnModel().getColumn(2).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForResolution));
        dataTable.getColumnModel().getColumn(3).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForLettersOnly));
        dataTable.getColumnModel().getColumn(4).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForLettersOnly));
        dataTable.getColumnModel().getColumn(5).setCellRenderer(new CustomTableCellRendererForDuplicates());
        dataTable.getColumnModel().getColumn(6).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForPositiveNumbersOnly));
        dataTable.getColumnModel().getColumn(7).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForPositiveNumbersOnly));
        dataTable.getColumnModel().getColumn(8).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForGigabytes));
        dataTable.getColumnModel().getColumn(9).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForGigabytes));
        dataTable.getColumnModel().getColumn(10).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForLettersOnly));
        dataTable.getColumnModel().getColumn(11).setCellRenderer(new CustomTableCellRendererForDuplicates());
        dataTable.getColumnModel().getColumn(12).setCellRenderer(new CustomTableCellRendererForDuplicates(regexForGigabytes));
        dataTable.getColumnModel().getColumn(13).setCellRenderer(new CustomTableCellRendererForDuplicates());
        dataTable.getColumnModel().getColumn(14).setCellRenderer(new CustomTableCellRendererForDuplicates());
        repaint();

    }

    private void fillTable(){
        tableModel.setRowCount(0);
        repaint();
        for (LaptopEntity laptop: laptops) {
            tableModel.addRow(laptop.toArrayOfStrings());

        }
//        laptopsShown = new ArrayList<>(laptops);
        if(isFileRead) CustomTableCellRendererForDuplicates.setLaptopsShown(laptopsShown);
        customTableCellRendererForDuplicates.clearEditedRows();
        repaint();
        getValuesFromTable();

    }

    private boolean validateBeforeSave(){
        int rows = dataTable.getRowCount();
        int columns = dataTable.getColumnCount();
        boolean valid = true;

        //System.out.println("Kolumn i wierszy: " + columns + " i " + rows);

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                TableCellRenderer renderer = dataTable.getCellRenderer(i, j);
                Component component = renderer.getTableCellRendererComponent(dataTable, dataTable.getValueAt(i,j), false, false, i, j);
                Color bgColor = component.getBackground();
                if (bgColor == Color.ORANGE){
                    JOptionPane.showMessageDialog(null, "Formularz zawiera błędy (pola zaznaczone na pomarańczowo)", "Błąd danych", JOptionPane.ERROR_MESSAGE);
                    //System.out.println("Formularz zawiera błędy (pola zaznaczone na pomarańczowo)");
                    valid = false;
                }
            }
        }
        return valid;
    }

    private void getValuesFromTable(){
        int rows = dataTable.getRowCount();
        int columns = dataTable.getColumnCount();
        //System.out.println((rows + ", " + columns));
        for (int i = 0; i< dataTable.getRowCount()-1; i++){
            List<String> rowValues = new ArrayList<>();
            for (int j = 0; j < dataTable.getColumnCount()-1; j++){
                rowValues.add(dataTable.getValueAt(i,j).toString());
            }
            laptopsFromTable.add(rowValues);
        }

    }

    private void readXMLfile(){

        try {
            JAXBContext context = JAXBContext.newInstance(Laptops.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File input = new File("katalog.xml");

            Laptops laptopsFromXML = (Laptops) unmarshaller.unmarshal(input);
            laptopsShown = new ArrayList<>(laptops);
            laptops = new ArrayList<>();
            laptops = laptopsFromXML.getLaptop().stream().map(e -> e.parseToLaptopEntity()).collect(Collectors.toList());
            fillTable();
            isFileRead = true;

        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Błąd wczytywania XML", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveToXMLFile(){
        if(validateBeforeSave()){
            List<List<String>> results = new ArrayList<>();
            int rows = dataTable.getRowCount();
            int columns = dataTable.getColumnCount();
            String value = null;
            for (int i = 0; i < rows; i++) {
                List<String> listOfRows = new ArrayList<>();
                for (int j = 0; j < columns; j++) {
                    value = dataTable.getValueAt(i, j) == null ? "" : dataTable.getValueAt(i, j).toString();
                    listOfRows.add(value);

                }
                results.add(listOfRows);
            }

            Random random = new Random();
            int randomNumber = random.nextInt(1000) + 1;
            String outputFileName = "katalog" + randomNumber + ".xml";
            System.out.println(outputFileName);

            List<Laptop> laptopList = results.stream().map(element ->
                    new Laptop( element.get(0), new Screen(element.get(4), element.get(1), element.get(2),
                            element.get(3)), new Processor(element.get(5), element.get(6), element.get(7)), element.get(8),
                            new Disc(element.get(10), element.get(9)), new Graphic_card(element.get(11), element.get(12)),
                            element.get(13), element.get(14))

                    ).collect(Collectors.toList());

            Laptops laptops = new Laptops(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), laptopList);

            try{
                JAXBContext jaxbContext = JAXBContext.newInstance(Laptops.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
                marshaller.marshal(laptops, fileOutputStream);
                fileOutputStream.close();

            }
            catch (Exception e){
                JOptionPane.showMessageDialog(null,e.getMessage(), "Wewnętrzny błąd zapisu do pliku XML", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Zapisano dane do pliku " + outputFileName, "Informacja", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    private  void readFromDatabase(){
        try{
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            String sql = "SELECT * FROM laptops";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            laptopsShown = new ArrayList<>(laptops);
            laptops = new ArrayList<>();

            while (resultSet.next()){
                String manufacturer = resultSet.getString("manufacturer");
                String screenDiagonal = resultSet.getString("screen_diagonal");
                String screenResolution = resultSet.getString("screen_resolution");
                String screenFinish = resultSet.getString("screen_finish");
                String touchScreenPresent = resultSet.getString("touch_screen_present");
                String CPU = resultSet.getString("cpu");
                String coreCount = resultSet.getString("core_count");
                String CPUFrequency = resultSet.getString("cpu_frequency");
                String RAM = resultSet.getString("ram");
                String diskCapacity = resultSet.getString("disk_capacity");
                String diskType = resultSet.getString("disk_type");
                String GPU = resultSet.getString("gpu");
                String GPUMemory = resultSet.getString("gpu_memory");
                String OS = resultSet.getString("os");
                String opticalDrive = resultSet.getString("optical_drive");

                LaptopEntity laptop = new LaptopEntity(manufacturer, screenDiagonal, screenResolution, screenFinish,
                        touchScreenPresent, CPU, coreCount, CPUFrequency, RAM, diskCapacity, diskType, GPU, GPUMemory,
                        OS, opticalDrive);

                laptops.add(laptop);
            }

            resultSet.close();
            statement.close();
            connection.close();
            fillTable();


            int duplicatesCounter = 0;
            for (LaptopEntity laptop: laptops) {
                if(isFileRead && laptopsShown.contains(laptop)) duplicatesCounter++;

            }
            int newRecordsCounter = laptops.size() - duplicatesCounter;
            bottomLabel.setText("Baza danych | Liczba nowych rekordów: " + newRecordsCounter
                    + ", Liczba znalezionych duplikatów: " + duplicatesCounter);

            isFileRead = true;

        }
        catch (SQLException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Wewnętrzny błąd odczytu z bazy danych", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveToDatabase() {
        if(validateBeforeSave()) {

            try {
                Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

                /*
                int rows = dataTable.getRowCount();
                int columns = dataTable.getColumnCount();

                System.out.println("Kolumn i wierszy: " + columns + " i " + rows);

                for (int i = 0; i < rows; i++){
                    String query = "INSERT INTO laptops (manufacturer, screen_diagonal, screen_resolution, screen_finish, " +
                            "touch_screen_present, CPU, core_count, CPU_frequency, RAM, disk_capacity, disk_type, GPU, " +
                            "GPU_memory, OS, optical_drive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    for (int j = 0; j < columns; j++){
                        TableCellRenderer renderer = dataTable.getCellRenderer(i, j);
                        Component component = renderer.getTableCellRendererComponent(dataTable, dataTable.getValueAt(i,j), false, false, i, j);
                        Color bgColor = component.getBackground();
                        if (bgColor == Color.RED){
                            System.out.println("Duplikat, pominięto rekord");
                            break;
                        }
                        preparedStatement.setString(j+1, dataTable.getValueAt(i,j).toString());

                    }
                }*/

                for (LaptopEntity laptop : laptops) {

                    if(laptopsShown.contains(laptop)){
                        //System.out.println("Duplikat, pominięto wiersz");
                        continue;
                    }

                    String query = "INSERT INTO laptops (manufacturer, screen_diagonal, screen_resolution, screen_finish, " +
                            "touch_screen_present, CPU, core_count, CPU_frequency, RAM, disk_capacity, disk_type, GPU, " +
                            "GPU_memory, OS, optical_drive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, laptop.getManufacturer());
                    preparedStatement.setString(2, laptop.getScreenDiagonal());
                    preparedStatement.setString(3, laptop.getScreenResolution());
                    preparedStatement.setString(4, laptop.getScreenFinish());
                    preparedStatement.setString(5, laptop.getTouchScreenPresent());
                    preparedStatement.setString(6, laptop.getCPU());
                    preparedStatement.setString(7, laptop.getCoreCount());
                    preparedStatement.setString(8, laptop.getCPUFrequency());
                    preparedStatement.setString(9, laptop.getRAM());
                    preparedStatement.setString(10, laptop.getDiskCapacity());
                    preparedStatement.setString(11, laptop.getDiskType());
                    preparedStatement.setString(12, laptop.getGPU());
                    preparedStatement.setString(13, laptop.getGPUMemory());
                    preparedStatement.setString(14, laptop.getOS());
                    preparedStatement.setString(15, laptop.getOpticalDrive());

                    preparedStatement.executeUpdate();
                }


                connection.close();
                JOptionPane.showMessageDialog(null, "Zapisano dane do bazy danych ", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Wewnętrzny błąd zapisu do bazy danych", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


}

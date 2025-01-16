package main.com;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.formdev.flatlaf.IntelliJTheme;
import com.sun.source.tree.Tree;

public class App {
    public static final JFrame frame = new JFrame("Depth search");

    public TreeMap<String, Integer> extensions_map = new TreeMap<>();
    public String path = "";
    public JLabel path_element = new JLabel();
    public DefaultTableModel model = new DefaultTableModel();

    public static void init() {
        try {
            InputStream mediumFontStream = App.class.getResourceAsStream("/fonts/Poppins-Medium.ttf");
            Font poppinsMedium = Font.createFont(Font.TRUETYPE_FONT, mediumFontStream).deriveFont(14f);
            UIManager.put("defaultFont", poppinsMedium);
            UIManager.put("Label.font", poppinsMedium);
            UIManager.put("Button.font", poppinsMedium);

            System.out.println("Font loaded succesfully!");
        } catch (IOException | FontFormatException e) {
            System.out.println("Error getting the font");
            throw new RuntimeException(e);
        }

        try {
            IntelliJTheme.setup(
                    App.class.getResourceAsStream("/themes/piConvert_theme.theme.json")
            );

            System.out.println("Theme loaded succesfully!");
        } catch(Exception e) {
            System.out.println("Failed to load theme");
        }

    }

    public JButton fileExplorer() {
        JButton btn = new JButton("Select start point");

        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(null);

            if(result == JFileChooser.APPROVE_OPTION) {
                this.path = chooser.getSelectedFile().getAbsolutePath();
                this.path_element.setText(this.path);
            }else {
                this.path = "";
            }

        });

        return btn;
    }

    public void UpdateTable() {
        model.setRowCount(0);

        for(Map.Entry entry : extensions_map.entrySet()) {
            model.addRow(
                    new Object[]{
                            entry.getKey(), entry.getValue()
                    }
            );
        }
    }

    public JScrollPane Table() {
        model.addColumn("Extension");
        model.addColumn("Count");

        JTable table = new JTable(model);

        DefaultTableCellRenderer centerRenderer=  new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(380, 200));

        return scrollPane;
    }

    public JButton ControlButton() {
        JButton btn = new JButton("Start search");

        btn.addActionListener(e -> {
            DepthSearch(new File(path));
        });

        return btn;
    }

    public void DepthSearch(File dir) {
        File[] files = dir.listFiles();
        if(files != null) {
            if(files.length > 200) return;

            for(File file : files) {
                if(file.isDirectory()) {
                    UpdateTable();
                    DepthSearch(file);
                }else {
                    String extension = getExtension(file);
                    if(!extension.isEmpty()) {
                        extensions_map.put(extension, extensions_map.getOrDefault(extension, 0) + 1);
                    }
                }
            }
        }
    }

    public String getExtension(File file) {
        String name = file.getName();

        for(int i = name.length() - 1; i > 0; i--) {
            if(name.charAt(i) == '.') return name.substring(i);
        }

        return "";
    }

    public App() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        panel.add(fileExplorer());
        panel.add(path_element);
        panel.add(Table());
        panel.add(ControlButton());

        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setSize(400, 400);
    }

    public static void main(String[] args) {
        init();

        SwingUtilities.invokeLater(App::new);
    }
}
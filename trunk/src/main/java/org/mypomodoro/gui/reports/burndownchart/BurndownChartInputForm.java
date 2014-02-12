package org.mypomodoro.gui.reports.burndownchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.gui.create.list.TypeComboBox;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Export form
 *
 */
public class BurndownChartInputForm extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected static final Dimension LABEL_DIMENSION = new Dimension(170, 20);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 20);
    private static final Dimension IMAGE_SIZE_DIMENSION = new Dimension(30, 20);
    private static final Dimension COLOR_SIZE_DIMENSION = new Dimension(60, 20);
    private GridBagConstraints c = new GridBagConstraints();
    // Dates form
    protected final JXDatePicker startDatePicker = new JXDatePicker(
            Labels.getLocale());
    protected final JXDatePicker endDatePicker = new JXDatePicker(
            Labels.getLocale());
    // Type form
    private JPanel typesInputFormPanel = new JPanel();
    protected final List<TypeComboBox> types = new ArrayList<TypeComboBox>();
    protected final List<JPanel> typePanelList = new ArrayList<JPanel>();
    // Chart form
    private JPanel chartInputFormPanel = new JPanel();
    private JTextField primaryYAxisName = new JTextField();
    private String defaultPrimaryYAxisName = Labels.getString("ReportListPanel.Chart.Remaining working hours");
    private JTextField primaryYAxisLegend = new JTextField();
    private String defaultPrimaryYAxisLegend = Labels.getString("ReportListPanel.Chart.Remaining");
    private JTextField primaryYAxisColor = new JTextField();
    private Color defaultPrimaryYAxisColor = ColorUtil.YELLOW_CHART;
    private JTextField secondaryYAxisName = new JTextField();
    private String defaultSecondaryYAxisName = Labels.getString("ReportListPanel.Chart.Completed tasks %");
    private JTextField secondaryYAxisLegend = new JTextField();
    private String defaultSecondaryYAxisLegend = Labels.getString("ReportListPanel.Chart.Completed");
    private JTextField secondaryYAxisColor = new JTextField();
    private Color defaultSecondaryYAxisColor = ColorUtil.RED_CHART;
    private JTextField targetLegend = new JTextField();
    private String defaultTargetLegend = Labels.getString("ReportListPanel.Chart.Target");
    private JTextField targetColor = new JTextField();
    private Color defaultTargetColor = ColorUtil.BLACK;
    // Image form
    private JPanel imageInputFormPanel = new JPanel();
    private JPanel imageSizePanel = new JPanel();
    private JTextField imageName = new JTextField();
    private String defaultImageName = "";
    private JComboBox imageFormatComboBox = new JComboBox();
    private final ImageFormat PNGFormat = new ImageFormat("PNG",
            ImageFormat.PNGExtention);
    private final ImageFormat JPGFormat = new ImageFormat("JPG",
            ImageFormat.JPGExtention);
    private JTextField imageWidth = new JTextField();
    private JTextField imageHeight = new JTextField();
    protected int defaultImageWidth = 800;
    protected int defaultImageHeight = 600;

    public BurndownChartInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setLayout(new GridBagLayout());

        addDatesInputForm();
        addTypeInputFormPanel();
        addChartInputFormPanel();
        addImageInputFormPanel();
    }

    private void addDatesInputForm() {
        c.gridx = 0;
        c.gridy = 0;
        //c.weighty = 0.5;
        FormLabel dateslabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Dates") + "*: ");
        dateslabel.setMinimumSize(LABEL_DIMENSION);
        dateslabel.setPreferredSize(LABEL_DIMENSION);
        add(dateslabel, c);
        c.gridx = 1;
        c.gridy = 0;
        //c.weighty = 0.5;
        startDatePicker.setDate(new Date());
        add(startDatePicker, c);
        c.gridx = 2;
        c.gridy = 0;
        //c.weighty = 0.5;
        endDatePicker.setDate(new Date());
        add(endDatePicker, c);
    }

    private void addTypeInputFormPanel() {
        c.gridx = 0;
        c.gridy = 1;
        //c.weighty = 0.5;                
        c.anchor = GridBagConstraints.NORTH;
        FormLabel typelabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Type") + "*: ");
        typelabel.setMinimumSize(LABEL_DIMENSION);
        typelabel.setPreferredSize(LABEL_DIMENSION);
        add(typelabel, c);
        c.gridx = 1;
        c.gridy = 1;
        //c.weighty = 0.5;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        typesInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cTypePanels = new GridBagConstraints();
        addTypesPanels(cTypePanels);
        add(typesInputFormPanel, c);
    }

    private void addTypesPanels(GridBagConstraints cTypePanels) {
        addTypesPanels(cTypePanels, 0);
    }

    private void addTypesPanels(final GridBagConstraints cTypePanels, final int gridY) {
        cTypePanels.gridx = 0;
        cTypePanels.gridy = gridY;
        cTypePanels.anchor = GridBagConstraints.WEST;
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new GridBagLayout());
        GridBagConstraints cTypePanel = new GridBagConstraints();
        TypeComboBox type = new TypeComboBox();
        type.setMinimumSize(COMBO_BOX_DIMENSION);
        type.setPreferredSize(COMBO_BOX_DIMENSION);
        type.setEditable(true);
        type.setBackground(ColorUtil.WHITE);
        type.setFont(new Font(type.getFont().getName(), Font.PLAIN, type.getFont().getSize()));
        cTypePanel.gridx = 0;
        cTypePanel.gridy = 0;
        typePanel.add(type, cTypePanel);
        cTypePanel.gridx = 1;
        cTypePanel.gridy = 0;
        if (gridY == 0) {
            cTypePanel.gridwidth = 2;
        }
        JButton plus = new JButton("+");
        /*plus.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(MouseEvent e) {
         addTypesPanels(cTypePanels, gridY + 1);
         }
         });*/
        plus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addTypesPanels(cTypePanels, gridY + 1);
            }
        });
        plus.setMinimumSize(new Dimension(44, 20));
        plus.setPreferredSize(new Dimension(44, 20));
        typePanel.add(plus, cTypePanel);
        if (gridY > 0) {
            cTypePanel.gridx = 2;
            cTypePanel.gridy = 0;
            JButton minus = new JButton("-");
            minus.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    typePanelList.get(gridY).setVisible(false);
                    typePanelList.remove(gridY);
                    types.remove(gridY);
                }
            });
            minus.setMinimumSize(new Dimension(44, 20));
            minus.setPreferredSize(new Dimension(44, 20));
            typePanel.add(minus, cTypePanel);
        }
        types.add(type);
        typePanelList.add(typePanel);
        typesInputFormPanel.add(typePanel, cTypePanels);
    }

    private void addChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 2;
        //c.weighty = 0.5;
        c.gridwidth = 3;
        chartInputFormPanel.setBorder(new TitledBorder(new EtchedBorder(), Labels.getString("ReportListPanel.Chart.Chart")));
        chartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addChartFields(cChart);
        add(chartInputFormPanel, c);
    }

    private void addImageInputFormPanel() {
        c.gridx = 0;
        c.gridy = 3;
        //c.weighty = 0.5;
        c.gridwidth = 3;
        imageInputFormPanel.setBorder(new TitledBorder(new EtchedBorder(), Labels.getString("ReportListPanel.Chart.Image")));
        imageInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cImage = new GridBagConstraints();
        addImageFields(cImage);
        add(imageInputFormPanel, c);
    }

    private void addChartFields(GridBagConstraints cChart) {
        // Primary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel primaryYAxisLabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Hours") + ": ");
        primaryYAxisLabel.setMinimumSize(LABEL_DIMENSION);
        primaryYAxisLabel.setPreferredSize(LABEL_DIMENSION);
        chartInputFormPanel.add(primaryYAxisLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        primaryYAxisName = new JTextField();
        primaryYAxisName.setText(defaultPrimaryYAxisName);
        primaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        chartInputFormPanel.add(primaryYAxisName, cChart);
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        chartInputFormPanel.add(new FormLabel(""), cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        primaryYAxisLegend = new JTextField();
        primaryYAxisLegend.setText(defaultPrimaryYAxisLegend);
        primaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        chartInputFormPanel.add(primaryYAxisLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        chartInputFormPanel.add(new FormLabel(""), cChart);
        cChart.gridx = 1;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        primaryYAxisColor = new JTextField();
        primaryYAxisColor.setEditable(false);
        primaryYAxisColor.setBackground(defaultPrimaryYAxisColor);
        primaryYAxisColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        primaryYAxisColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        primaryYAxisColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        primaryYAxisColor,
                        Labels.getString("ReportListPanel.Chart.Choose a color"),
                        primaryYAxisColor.getBackground());
                if (newColor != null) {
                    primaryYAxisColor.setBackground(newColor);
                }
            }
        });
        chartInputFormPanel.add(primaryYAxisColor, cChart);

        // Secondary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 3;
        //cChart.weighty = 0.5;
        FormLabel secondaryYAxisLabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Tasks") + ": ");
        secondaryYAxisLabel.setMinimumSize(LABEL_DIMENSION);
        secondaryYAxisLabel.setPreferredSize(LABEL_DIMENSION);
        chartInputFormPanel.add(secondaryYAxisLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 3;
        //cChart.weighty = 0.5;
        secondaryYAxisName = new JTextField();
        secondaryYAxisName.setText(defaultSecondaryYAxisName);
        secondaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        chartInputFormPanel.add(secondaryYAxisName, cChart);
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 4;
        //cChart.weighty = 0.5;
        chartInputFormPanel.add(new FormLabel(""), cChart);
        cChart.gridx = 1;
        cChart.gridy = 4;
        //cChart.weighty = 0.5;
        secondaryYAxisLegend = new JTextField();
        secondaryYAxisLegend.setText(defaultSecondaryYAxisLegend);
        secondaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        chartInputFormPanel.add(secondaryYAxisLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 5;
        //cChart.weighty = 0.5;
        chartInputFormPanel.add(new FormLabel(""), cChart);
        cChart.gridx = 1;
        cChart.gridy = 5;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        secondaryYAxisColor = new JTextField();
        secondaryYAxisColor.setEditable(false);
        secondaryYAxisColor.setBackground(defaultSecondaryYAxisColor);
        secondaryYAxisColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        secondaryYAxisColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        secondaryYAxisColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        secondaryYAxisColor,
                        Labels.getString("ReportListPanel.Chart.Choose a color"),
                        secondaryYAxisColor.getBackground());
                if (newColor != null) {
                    secondaryYAxisColor.setBackground(newColor);
                }
            }
        });
        chartInputFormPanel.add(secondaryYAxisColor, cChart);

        // Target
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 6;
        //cChart.weighty = 0.5;
        FormLabel targetLabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Target") + ": ");
        targetLabel.setMinimumSize(LABEL_DIMENSION);
        targetLabel.setPreferredSize(LABEL_DIMENSION);
        chartInputFormPanel.add(targetLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 6;
        //cChart.weighty = 0.5;
        targetLegend = new JTextField();
        targetLegend.setText(defaultTargetLegend);
        targetLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        targetLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        chartInputFormPanel.add(targetLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 7;
        //cChart.weighty = 0.5;
        chartInputFormPanel.add(new FormLabel(""), cChart);
        cChart.gridx = 1;
        cChart.gridy = 7;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        targetColor = new JTextField();
        targetColor.setEditable(false);
        targetColor.setBackground(defaultTargetColor);
        targetColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        targetColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        targetColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        targetColor,
                        Labels.getString("ReportListPanel.Chart.Choose a color"),
                        targetColor.getBackground());
                if (newColor != null) {
                    targetColor.setBackground(newColor);
                }
            }
        });
        chartInputFormPanel.add(targetColor, cChart);
    }

    private void addImageFields(GridBagConstraints cImage) {
        // Image name
        cImage.gridx = 0;
        cImage.gridy = 0;
        //cImage.weighty = 0.5;
        FormLabel imageNamelabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Name") + ": ");
        imageNamelabel.setMinimumSize(LABEL_DIMENSION);
        imageNamelabel.setPreferredSize(LABEL_DIMENSION);
        imageInputFormPanel.add(imageNamelabel, cImage);
        cImage.gridx = 1;
        cImage.gridy = 0;
        //cImage.weighty = 0.5;
        imageName = new JTextField();
        imageName.setText(defaultImageName);
        imageName.setMinimumSize(COMBO_BOX_DIMENSION);
        imageName.setPreferredSize(COMBO_BOX_DIMENSION);
        imageInputFormPanel.add(imageName, cImage);
        // Image formats
        cImage.gridx = 0;
        cImage.gridy = 1;
        //cImage.weighty = 0.5;
        FormLabel imageFormatLabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Format") + ": ");
        imageFormatLabel.setMinimumSize(LABEL_DIMENSION);
        imageFormatLabel.setPreferredSize(LABEL_DIMENSION);
        imageInputFormPanel.add(imageFormatLabel, cImage);
        cImage.gridx = 1;
        cImage.gridy = 1;
        //cImage.weighty = 0.5;
        Object imageFormats[] = new Object[]{PNGFormat, JPGFormat};
        imageFormatComboBox = new JComboBox(imageFormats);
        imageFormatComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
        imageFormatComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
        imageFormatComboBox.setBackground(ColorUtil.WHITE);
        imageInputFormPanel.add(imageFormatComboBox, cImage);
        // Image size
        cImage.gridx = 0;
        cImage.gridy = 2;
        //cImage.weighty = 0.5;
        FormLabel imageSizelabel = new FormLabel(
                Labels.getString("ReportListPanel.Chart.Size") + ": ");
        imageSizelabel.setMinimumSize(LABEL_DIMENSION);
        imageSizelabel.setPreferredSize(LABEL_DIMENSION);
        imageInputFormPanel.add(imageSizelabel, cImage);
        cImage.gridx = 1;
        cImage.gridy = 2;
        //cImage.weighty = 0.5;
        cImage.anchor = GridBagConstraints.WEST;
        imageSizePanel.setLayout(new GridBagLayout());
        GridBagConstraints cImageSize = new GridBagConstraints();
        cImageSize.gridx = 0;
        cImageSize.gridy = 0;
        imageWidth.setText("" + defaultImageWidth);
        imageWidth.setFont(new Font(imageHeight.getFont().getName(), Font.BOLD,
                imageHeight.getFont().getSize()));
        imageWidth.setMinimumSize(IMAGE_SIZE_DIMENSION);
        imageWidth.setPreferredSize(IMAGE_SIZE_DIMENSION);
        imageSizePanel.add(imageWidth, cImageSize);
        cImageSize.gridx = 1;
        cImageSize.gridy = 0;
        imageSizePanel.add(new JLabel(" X "), cImageSize);
        cImageSize.gridx = 2;
        cImageSize.gridy = 0;
        imageHeight.setText("" + defaultImageHeight);
        imageHeight.setFont(new Font(imageHeight.getFont().getName(), Font.BOLD,
                imageHeight.getFont().getSize()));
        imageHeight.setMinimumSize(IMAGE_SIZE_DIMENSION);
        imageHeight.setPreferredSize(IMAGE_SIZE_DIMENSION);
        imageSizePanel.add(imageHeight, cImageSize);
        imageInputFormPanel.add(imageSizePanel, cImage);
    }

    public String getImageExtention() {
        return ((ImageFormat) imageFormatComboBox.getSelectedItem()).getExtention();
    }

    public boolean isFilePNGFormat() {
        return ((ImageFormat) imageFormatComboBox.getSelectedItem()).isPNGFormat();
    }

    public boolean isFileJPGFormat() {
        return ((ImageFormat) imageFormatComboBox.getSelectedItem()).isJPGFormat();
    }

    public String getImageName() {
        return imageName.getText().trim();
    }

    public void initImageName() {
        imageName.setText(defaultImageName);
    }

    private class ImageFormat {

        public static final String PNGExtention = "png";
        public static final String JPGExtention = "jpg";
        private final String formatName;
        private final String extention;

        public ImageFormat(String formatName, String extention) {
            this.formatName = formatName;
            this.extention = extention;
        }

        public String getExtention() {
            return extention;
        }

        public boolean isPNGFormat() {
            return extention.equals(PNGExtention);
        }

        public boolean isJPGFormat() {
            return extention.equals(JPGExtention);
        }

        @Override
        public String toString() {
            return formatName;
        }
    }
}

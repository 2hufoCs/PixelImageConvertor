
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;

import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import java.util.ArrayList;

public class GraphicalInterface {
    JFrame f = new JFrame();

    public static void main(String[] args) throws IOException {
        new GraphicalInterface();
    }

    public GraphicalInterface() {
        PixelConversion pixelConv = new PixelConversion();
        // The frame that appears when there's an error with input
        JLabel errorLabel = new JLabel();
        errorLabel.setBounds(25, -25, 250, 150);
        JFrame errorFrame = new JFrame();
        errorFrame.add(errorLabel);
        errorFrame.setSize(300, 200);
        errorFrame.setLayout(null);

        // Image selection and path
        JButton chooseFile = new JButton("Select image...");
        chooseFile.setBounds(35, 400, 125, 25);
        JTextField imgPath = new JTextField();
        imgPath.setBounds(180, 400, 200, 25);

        // Ratio selection
        JLabel chooseRatio = new JLabel("Pixelization ratio:");
        chooseRatio.setBounds(50, 450, 100, 25);
        JComboBox<Integer> ratio = new JComboBox<Integer>();
        ratio.setBounds(150, 450, 150, 25);

        // Opens explorer when choosing file
        JFileChooser imgChooser = new JFileChooser(System.getProperty("user.dir") + "/Resources");
        imgChooser.setFileFilter(new ImageFilter());
        JLabel imgLabel = new JLabel();
        imgLabel.setBounds(0, 40, 320, 320);

        chooseFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = imgChooser.showOpenDialog(f);
                File file = imgChooser.getSelectedFile();

                if (returnVal == JFileChooser.APPROVE_OPTION && new ImageFilter().accept(file)) {
                    imgPath.setText(System.getProperty("user.dir") + "\\Resources\\" + file.getName());
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(imgPath.getText()));

                    } catch (Exception ex) {
                        System.out.println("Whoops, " + ex + ", at " + ex.getStackTrace());
                    }
                    ImageIcon imgIcon = showImage(img);
                    imgLabel.setIcon(imgIcon);
                    imgLabel.setBounds((f.getWidth() - imgIcon.getIconWidth()) / 2 - 10, imgLabel.getY(),
                            imgLabel.getWidth(), imgLabel.getHeight());

                    ArrayList<Integer> widthFactors = pixelConv.primeFactorisation(img.getWidth());
                    ArrayList<Integer> widthDivisors = pixelConv.getProducts(widthFactors);

                    ArrayList<Integer> heightFactors = pixelConv.primeFactorisation(img.getHeight());
                    ArrayList<Integer> heightDivisors = pixelConv.getProducts(heightFactors);

                    ArrayList<Integer> commonDivisors = new ArrayList<Integer>(widthDivisors);
                    commonDivisors.retainAll(heightDivisors);

                    ratio.removeAllItems();
                    ratio.addItem(1);
                    for (Integer val : commonDivisors) {
                        ratio.addItem(val);
                    }

                    System.out.println("width divisors: " + widthDivisors);
                    System.out.println("height divisors: " + heightDivisors);
                    System.out.println("Common divisors are: " + commonDivisors);
                }
            }
        });

        // Submit button
        JButton submit = new JButton("Submit");
        submit.setBounds(160, 520, 100, 25);

        // Once all fields are completed and submit is pressed, begin pixelization
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Frame stuff
        f.add(imgLabel);
        f.add(chooseFile);
        f.add(imgPath);
        f.add(imgChooser);
        f.add(chooseRatio);
        f.add(ratio);
        f.add(submit);

        f.setBounds(1300, 50, 420, 620);
        f.setLayout(null);
        f.setVisible(true);
    }

    public ImageIcon showImage(BufferedImage img) {
        int maxDim = Math.max(img.getWidth(), img.getHeight());
        float ratio = (float) 320 / maxDim;
        Image newImg = img.getScaledInstance((int) (img.getWidth() * ratio), (int) (img.getHeight() * ratio),
                Image.SCALE_DEFAULT);
        ImageIcon imgIcon = new ImageIcon(newImg);
        return imgIcon;
    }
}

class Utils {
    public final static String png = "png";
    public final static String jpg = "jpg";
    public final static String jpeg = "jpeg";

    public static String getExtension(File f) {
        String extension = null;
        String fileName = f.getName();
        int i = fileName.lastIndexOf(".");

        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }
}

class ImageFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        String extension = Utils.getExtension(f);
        if (extension == null)
            return false;
        if (extension.equals(Utils.png) || extension.equals(Utils.jpg) || extension.equals(Utils.jpeg))
            return true;
        return false;
    }

    @Override
    public String getDescription() {
        return "Images only";
    }
}
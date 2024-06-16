
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
    PixelConversion pixelConv = new PixelConversion();

    // The main frame and its components
    JFrame f = new JFrame();
    JButton chooseFile = new JButton("Select image...");
    JTextField imgPath = new JTextField();
    JLabel chooseRatio = new JLabel("Pixelization ratio:");
    JComboBox<Integer> ratio = new JComboBox<Integer>();
    JFileChooser imgChooser = new JFileChooser(System.getProperty("user.dir") + "/Resources"); // Get Resources folder
    JLabel imgLabel = new JLabel();
    JButton submit = new JButton("Submit");

    // A 2D array containing all the RGB values of the image
    int[][] imgPixels;

    // The frame that appears when there's an error with input
    JFrame errorFrame = new JFrame();
    JLabel errorLabel = new JLabel();

    public static void main(String[] args) throws IOException {
        new GraphicalInterface();
    }

    public GraphicalInterface() throws IOException {
        // Setting some initial values for the error frame
        errorLabel.setFont(new Font("Dialog", 4, 15));
        errorLabel.setBounds(25, 0, 250, 175);
        errorFrame.add(errorLabel);
        errorFrame.setSize(300, 200);
        errorFrame.setTitle("Error!");
        errorFrame.setLayout(null);

        // Image selection and path
        chooseFile.setBounds(35, 400, 125, 25);
        imgPath.setBounds(180, 400, 200, 25);

        // Ratio selection
        chooseRatio.setBounds(50, 450, 100, 25);
        ratio.setBounds(150, 450, 150, 25);

        // Opens explorer when choosing file
        imgChooser.setFileFilter(new ImageFilter());
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
                    imgPixels = pixelConv.getPixels(img);
                    showImage(img);
                    setRatioValues(img);
                }
            }
        });

        // Submit button
        submit.setBounds(160, 520, 100, 25);

        // Once all fields are completed and submit is pressed, begin pixelization
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (imgPath.getText().length() == 0) {
                    showError(
                            "No image was selected! Press \"Select image...\" to choose one from the Resources folder");
                } else if (!new File(imgPath.getText()).exists()) {
                    showError(
                            "It looks like the file doesn't exist. Did you move it or delete it? If you typed the path by hand, try using the \"Select Image...\" button to make sure it's still there.");
                    System.out.println("path is: " + imgPath.getText() + ", size is: " + imgPath.getText().length());
                } else if (!new ImageFilter().accept(new File(imgPath.getText()))) {
                    showError(
                            "Sorry, the only supported formats are .png, .jpg and .jpeg... You can convert you image to another extension using an online convertor!");
                } else {
                    BufferedImage imgToPixelate = null;
                    try {
                        imgToPixelate = ImageIO.read(new File(imgPath.getText()));
                    } catch (Exception ex) {
                        System.out.println("*fades out of existence* " + ex);
                    }
                    BufferedImage pixelatedImg = pixelConv.mergePixels(imgToPixelate, (int) ratio.getSelectedItem(),
                            imgPixels);
                    showImage(pixelatedImg);

                }
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

    public void setRatioValues(BufferedImage img) {
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

        if (commonDivisors.size() == 0) {
            showError(
                    "Sorry, it looks like there aren't any common divisors between the width and height of your image. Either crop it or choose another one.");
        }
    }

    public void showError(String errorTxt) {
        errorLabel.setText(
                "<html>" + errorTxt);
        errorFrame.setVisible(true);
    }

    public void showImage(BufferedImage img) {
        int maxDim = Math.max(img.getWidth(), img.getHeight());
        float ratio = (float) 320 / maxDim;
        Image newImg = img.getScaledInstance((int) (img.getWidth() * ratio), (int) (img.getHeight() * ratio),
                Image.SCALE_DEFAULT);
        ImageIcon imgIcon = new ImageIcon(newImg);
        imgLabel.setIcon(imgIcon);
        imgLabel.setBounds((f.getWidth() - imgIcon.getIconWidth()) / 2 - 10, imgLabel.getY(),
                imgLabel.getWidth(), imgLabel.getHeight());
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
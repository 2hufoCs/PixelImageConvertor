import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.Color;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

class PixelConversion {
    public int[][] getPixels(BufferedImage image) {
        // Cut the image into a ton of pixels, each pixel into rgb values
        byte pixels[] = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        int width = image.getWidth();
        int height = image.getHeight();
        int pixelLength = image.getColorModel().hasAlpha() ? 4 : 3; // each pixel have red, green and blue values
                                                                    // (exluding alpha channel)

        int result[][] = new int[height][width];
        for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
            if (row == 69420) {
                System.out.println(pixels.length + ", " + pixel);
            }
            int rgb = 0;
            rgb += ((int) pixels[pixel] & 0xff); // blue
            rgb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
            rgb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
            result[row][col] = rgb;

            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return result;
    }

    public BufferedImage mergePixels(BufferedImage img, int ratio, int[][] rgbValues) {
        BufferedImage finalImage = new BufferedImage(img.getWidth() / ratio, img.getHeight() / ratio,
                BufferedImage.TYPE_INT_RGB);
        int rows = finalImage.getHeight();
        int cols = finalImage.getWidth();

        // Setting the last pixel to an non rgb value, waiting until it's changed
        finalImage.setRGB(cols - 1, rows - 1, -1);
        int row, col = 0;
        int rgbColors[][] = new int[3][ratio * ratio];
        double startTime = System.currentTimeMillis();

        for (int i = 0; i < rows * cols; i++) {
            int currentRow = i / cols;
            int currentCol = i % cols;
            for (row = 0; row < ratio; row++) {
                for (col = 0; col < ratio; col++) {
                    // Use current row and current col instead of row and col
                    int rowIndex = row + currentRow * ratio;
                    int colIndex = col + currentCol * ratio;

                    rgbColors[0][row * ratio + col] = rgbValues[rowIndex][colIndex] & 0xff; // extract b
                    rgbColors[1][row * ratio + col] = (rgbValues[rowIndex][colIndex] >> 8) & 0xff; // extract g
                    rgbColors[2][row * ratio + col] = (rgbValues[rowIndex][colIndex] >> 16) & 0xff; // extract r
                }
            }
            int blueMean = (int) mean(rgbColors[0]);
            int greenMean = (int) mean(rgbColors[1]);
            int redMean = (int) mean(rgbColors[2]);
            finalImage.setRGB(currentCol, currentRow, new Color(redMean, greenMean, blueMean).getRGB());
        }
        System.out.println("merge pixels algorithm duration: " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("ratio = " + ratio + ", final img total pixels = "
                + finalImage.getWidth() * finalImage.getHeight());
        System.out
                .println("------------------------------------------------------------------------------------------");
        return finalImage;
    }

    public float mean(int[] numberList) {
        int sum = 0;
        for (int val : numberList) {
            sum += val;
        }
        float meanVal = sum / numberList.length;
        return meanVal;
    }

    // Efficient prime factorisation : complexity of O(sqrt(n))
    public ArrayList<Integer> primeFactorisation(int n) {
        // Divide it by 2 until we can't anymore
        ArrayList<Integer> primeFactors = new ArrayList<Integer>();
        while (n % 2 == 0) {
            primeFactors.add(2);
            n /= 2;
        }

        // n must be odd now, we can skip half of the elements (i += 2)
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            while (n % i == 0) {
                primeFactors.add(i);
                n /= i;
            }
        }

        // In case n is a prime number
        if (n > 2) {
            primeFactors.add(n);
        }

        return primeFactors;
    }

    public ArrayList<Integer> getProducts(ArrayList<Integer> numbers) {
        HashSet<Integer> products = new HashSet<Integer>();

        for (Integer i : numbers) {
            products.add(i);
        }

        // Even though there are like 3 or 4 nested for/while loops, complexity is
        // O((n^2 + n) / 2) = O(n^2)
        // It's fine considering there won't be that many prime factors, even for large
        // values
        int p;
        for (int baseProducts = 2; baseProducts < numbers.size(); baseProducts++) {
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for (int temp = 0; temp < baseProducts; temp++) {
                indexes.add(temp);
            }

            int endCursor = numbers.size() - 1;
            int i = indexes.size() - 1;
            while (i > 0 || indexes.get(i) < endCursor) {
                endCursor = numbers.size() - 1 - (indexes.size() - 1 - i);

                if (i < indexes.size() - 1) {
                    if (indexes.get(i) >= indexes.get(i + 1) - 1) {
                        i -= 1;
                    } else {
                        indexes.set(i, indexes.get(i) + 1);
                        i += 1;
                    }
                    endCursor = numbers.size() - 1 - (indexes.size() - 1 - i);
                    if (i != 0 || indexes.get(i) + 1 != indexes.get(i + 1)) {
                        continue;
                    }
                }

                p = 1;
                for (Integer index : indexes) {
                    p *= numbers.get(index);
                }
                products.add(p);

                if (indexes.get(i) >= endCursor) {
                    if (i == 0)
                        continue;
                    if (indexes.get(i - 1) < indexes.get(i) - 1) {
                        indexes.set(i, indexes.get(i - 1) + 2);
                    }
                    i -= 1;
                    endCursor = numbers.size() - 1 - (indexes.size() - 1 - i);
                    continue;
                }

                indexes.set(i, indexes.get(i) + 1);
                endCursor = numbers.size() - 1 - (indexes.size() - 1 - i);
            }
        }

        ArrayList<Integer> productList = new ArrayList<Integer>(products);
        Collections.sort(productList);
        return productList;
    }
}
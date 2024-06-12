import java.awt.image.*;

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
        int pixelLength = 3; // each pixel have red, green and blue values (exluding alpha channel)

        int result[][] = new int[height][width];
        for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
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

    public void mergePixels(BufferedImage img, int ratio) {
        // Given an array of rgb values, merge pixels into a single one based on those
        // rgb values
        int rgbValues[][] = getPixels(img);
        BufferedImage finalImage = new BufferedImage(img.getWidth(), img.getHeight(), 1);
        int newRgbValues[][] = new int[finalImage.getHeight() / ratio][finalImage.getWidth() / ratio];
        int rows = newRgbValues.length;
        int cols = newRgbValues[0].length;

        // Setting the last pixel to an non rgb value, waiting until it's changed
        newRgbValues[rows - 1][cols - 1] = -1;
        int row, col = 0;
        int rgbColors[][] = new int[3][ratio * ratio];
        System.out.println(ratio + ": " + rgbColors.length + ", " + rgbColors[0].length);

        for (int i = 0; newRgbValues[rows - 1][cols - 1] == -1; i++) {
            int currentRow = i / cols;
            int currentCol = i % cols;
            System.out.println(currentRow + ", " + currentCol);
            for (row = 0; row < ratio - 1; row += ratio) {
                for (col = 0; col < ratio - 1; col += ratio) {
                    // Use current row and current col instead of row and col
                    rgbColors[0][row * 3 + col] = rgbValues[row][col] & 0xff; // b
                    rgbColors[1][row * 3 + col] = (rgbValues[row][col] & 0xff) << 8; // g
                    rgbColors[2][row * 3 + col] = (rgbValues[row][col] & 0xff) << 16; // r
                    System.out.println("old pixel colors: " + rgbColors[0][row * 3 + col] + ", "
                            + rgbColors[1][row * 3 + col] + ", " + rgbColors[2][row * 3 + col]);
                }
            }
            int blueMean = (int) mean(rgbColors[0]);
            int greenMean = (int) mean(rgbColors[1]);
            int redMean = (int) mean(rgbColors[2]);
            int meanRgb = (blueMean & 0xff) + (greenMean & 0xff) << 8 + (redMean & 0xff) << 16;
            newRgbValues[currentRow][currentCol] = meanRgb;

            System.out.println("new pixel color is: " + meanRgb);
        }

        System.out.println("new rgb values are: " + newRgbValues);
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
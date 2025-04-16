package game.view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

class ImagePanel extends JPanel {
  private Image image;

  public ImagePanel(String imagePath) {
    try {
      // Debug information
      System.out.println("Attempting to load image: " + imagePath);

      if (imagePath != null) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
          // Load image from file path
          ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
          image = icon.getImage();
        } else {
          // Attempt to load image using class loader
          URL imageUrl = getClass().getClassLoader().getResource(imagePath);
          if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            image = icon.getImage();
          } else {
            System.out.println("Image file not found: " + imagePath);
          }
        }
      } else {
        System.out.println("Image path is null");
      }
    } catch (Exception e) {
      System.err.println("Failed to load image: " + e.getMessage());
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      // Get the size of the panel
      int panelWidth = getWidth();
      int panelHeight = getHeight();

      // Get the size of the image
      int imageWidth = image.getWidth(null);
      int imageHeight = image.getHeight(null);

      // Calculate scaling factors
      double scaleX = (double) panelWidth / imageWidth;
      double scaleY = (double) panelHeight / imageHeight;
      double scale = Math.min(scaleX, scaleY); // Use the smaller scale to fit the image

      // Calculate the new size of the image
      int newWidth = (int) (imageWidth * scale);
      int newHeight = (int) (imageHeight * scale);

      // Draw the scaled image
      g.drawImage(image, 0, 0, newWidth, newHeight, this);
    } else {
      // If the image is not loaded successfully, draw default text
      g.setColor(Color.RED);
      g.setFont(new Font("Arial", Font.BOLD, 20));
      String text = "Image Not Available";
      int textWidth = g.getFontMetrics().stringWidth(text);
      g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
    }
  }
}
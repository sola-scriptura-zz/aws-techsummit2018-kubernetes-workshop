package hello.logics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResizeImage {
	
	private BufferedImage orgImage;
	private BufferedImage resizedImage;
	
    public void readFile(String filepath) throws IOException {
        File input = new File(filepath);
        orgImage = ImageIO.read(input);
    }
    
    public void writeFile(String filepath, String formatName) throws IOException{
        File output = new File(filepath);
        ImageIO.write(resizedImage, formatName, output);
    }

    public void resize(int height, int width) {
        Image tmp = orgImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
    }
    
    public BufferedImage getOrgImage()
    {
    		return orgImage;
    }  
    
    public BufferedImage getResizedImage() 
    {
    		return resizedImage;
    }
    
}

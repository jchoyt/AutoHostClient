package stars.ahc.plugins.map.mapanimator;
import java.awt.image.BufferedImage;

/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */

/**
 * @author Steve
 *
 */
public interface AnimationImageGenerator
{
   public void setConfiguration( AnimationConfiguration config );
   public AnimationConfiguration getConfiguration();
   public BufferedImage getNextImage();
}

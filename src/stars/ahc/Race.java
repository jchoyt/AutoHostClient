/*
 * Created on Oct 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

import java.awt.Color;
import java.util.Random;

/**
 * Represents a race in a Stars! game, controlled by a Player 
 * 
 * @author Steve Leach
 * @see stars.ahc.Player
 */
public class Race
{
   private String playerName = null;
   private Color color = null;
   private String raceName = null;
   private String racePlural = null;
   
   
   public Color getColor()
   {
      if (color == null)
      {
         // Create new random colour

         Random rnd = new Random();
         
         float h = Utils.getRandomFloat();						// hue is totally random (0 to 1.0)
         float s = 1.0f;										// saturation is always maximum (1.0)
         float v = Utils.getRandomFloat() * 0.5f + 0.5f;		// value is random but high (0.5 to 1.0)
         
         color = Color.getHSBColor( h, s, v );         
      }
      
      return color;
   }
   public void setColor(Color color)
   {
      this.color = color;
   }
   public String getPlayerName()
   {
      return playerName;
   }
   public void setPlayerName(String playerName)
   {
      this.playerName = playerName;
   }
   public String getRaceName()
   {
      return raceName;
   }
   public void setRaceName(String raceName)
   {
      this.raceName = raceName;
   }
   public String getRacePlural()
   {
      return racePlural;
   }
   public void setRacePlural(String racePlural)
   {
      this.racePlural = racePlural;
   }
   
   public boolean equals( Race race )
   {
      return this.raceName.equalsIgnoreCase( race.raceName );
   }
   
   public boolean equals( String raceName )
   {
      return this.raceName.equalsIgnoreCase( raceName );
   }
}

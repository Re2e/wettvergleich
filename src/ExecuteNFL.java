import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class ExecuteNFL {

	public final int PINNACLE = 0;
	public final int PADDYPOWER = 1;
	public final int THEGREEK = 2;
	
	public float fractionalToDecimal(int up, int down){
		float tmp = (float) up/down+1;
		return tmp;
	}
	
	public float americanToDecimal(int val){
		float tmp;
		if(val > 0){
			tmp = (float) val/100+1;
		} else {
			tmp = (float) 100/(val*-1)+1;
		}
		return tmp;
	}
	
	
	public boolean checkPinnacleNFL(ArrayList<Game> pinnacleList){
		String surl = "http://www.pinnaclesports.com/League/Football/NFL/1/Lines.aspx";
		boolean worked = false;
		int beginTerm;
		int endTerm;
		int teamCount = 0;
		int chanceCount = 0;
		Game game = new Game(PINNACLE);
		String extract = "";
		
		 final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12";
	      try {
	         URL url = new URL(surl);
	         URLConnection conn = url.openConnection();
	         conn.addRequestProperty("User-Agent", userAgent);
	 
	         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         String str;
	         while ((str = in.readLine()) != null) {
	        	 if(str.contains("class=\"linesTeam\">")){
	        		beginTerm = str.indexOf("class=\"linesTeam\">") + 18;
	 	 			endTerm = str.indexOf("<", beginTerm);
	 	 			if(endTerm == -1){
	 	 				endTerm = str.length();
	 	 			}
	 	 			if(teamCount == 0){
	 	 				game.setTeam1(str.substring(beginTerm, endTerm));
	 	 				teamCount++;
	 	 				worked = true;
	 	 			} else {
	 	 				game.setTeam2(str.substring(beginTerm, endTerm));
	 	 				teamCount = 0;
	 	 			}
	 	 			
	        	 }
	        	 if(str.contains("class=\"linesMLine\">&nbsp;&nbsp;&nbsp;")){
		 	 			beginTerm = str.indexOf("class=\"linesMLine\">&nbsp;&nbsp;&nbsp;") + 37;
		 	 			endTerm = str.indexOf("<", beginTerm);
		 	 			if(endTerm == -1){
		 	 				endTerm = str.length();
		 	 			}
		 	 			extract = str.substring(beginTerm, endTerm);
		 	 			if(chanceCount == 0){
		 	 				if(!(extract.startsWith("0") || extract.startsWith("1") || extract.startsWith("2") || extract.startsWith("3") || extract.startsWith("4") || extract.startsWith("5") || extract.startsWith("6") || extract.startsWith("7") || extract.startsWith("8") || extract.startsWith("9"))){
		 	 					game.setChance1((float) 0.0, game.DECIMAL);
		 	 				} else {
			 	 				game.setChance1(Float.parseFloat(str.substring(beginTerm, endTerm)), game.DECIMAL);
		 	 				}
		 	 				chanceCount++;
		 	 			} else {
		 	 				if(!(extract.startsWith("0") || extract.startsWith("1") || extract.startsWith("2") || extract.startsWith("3") || extract.startsWith("4") || extract.startsWith("5") || extract.startsWith("6") || extract.startsWith("7") || extract.startsWith("8") || extract.startsWith("9"))){
		 	 					game.setChance2((float) 0.0, game.DECIMAL);
		 	 				} else {
			 	 				game.setChance2(Float.parseFloat(str.substring(beginTerm, endTerm)), game.DECIMAL);
		 	 				}
		 	 				chanceCount = 0;
		 	 				
		 	 				if(game.differentEnough()){
		 	 					pinnacleList.add(game);
		 	 				}
		 	 				game = new Game(PINNACLE);
		 	 			}
	        	 	}

	        	 }
	         
	         in.close();
	      } catch (MalformedURLException e) {
	         System.out.println(e.getMessage());
	      } catch (IOException e) {
	         System.out.println(e.getMessage());
	      }
	      
	      return worked;
	}
	
	
	public boolean checkTheGreekNFL(ArrayList<Game> thegreekList){
		boolean worked = false;
		int beginTerm;
		int endTerm;
		String extract = "";
		boolean runFurther = true;
        String str;
        float chance = 0;
        int teamCount = 0;
		Game game = new Game(THEGREEK);
		String surl = "http://www.thegreek.com/sportsbook/betting-lines/Football/NFL";	
		final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12";
		
	      try {
	         URL url = new URL(surl);
	         URLConnection conn = url.openConnection();
	         conn.addRequestProperty("User-Agent", userAgent);
	         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

	         while ((str = in.readLine()) != null) {
	        	 if((str.contains("<li class=\"name\""))){
		        		runFurther = true;	 
		        		in.readLine();	
		        		if((str = in.readLine()).startsWith(" ")){
		        			if(teamCount == 0){
		        				game.setTeam1(str.substring(29, str.length()));
					 	 		
					 	 		while (runFurther) {
				        			str = in.readLine();
				        			if((str.contains("<li class=\"money-line \">"))){
				        		 		str = in.readLine();
				        		 		beginTerm = str.indexOf(">")+1;
				   		 	 		 	endTerm = str.indexOf("<", beginTerm);
				   		 	 		 	extract = str.substring(beginTerm, endTerm);
				   		 	 		 	chance = americanToDecimal(Integer.parseInt(extract));			   		 	 		 	
				   		 	 		 	game.setChance1(chance, game.DECIMAL);
				   		 	 		 	game.setChance1(Integer.parseInt(extract), game.AMERICAN);		 	 		
							 	 		runFurther = false;
				        			}
				        			
				        			if((str.contains("<li class=\"money-line no-lines\">"))){

				        				game.setChance1(0, game.DECIMAL);		
							 	 		runFurther = false;
				        			}
					 	 		}
					 	 		teamCount++;
					 	 		worked = true;
					 	 		
		        			} else {
		        				game.setTeam2(str.substring(29, str.length()));
					 	 		while (runFurther) {
				        			str = in.readLine();
				        			if((str.contains("<li class=\"money-line \">"))){
				        		 		str = in.readLine();
				        		 		beginTerm = str.indexOf(">")+1;
				   		 	 		 	endTerm = str.indexOf("<", beginTerm);
				   		 	 		 	extract = str.substring(beginTerm, endTerm);
				   		 	 		 	chance = americanToDecimal(Integer.parseInt(extract));
					   		 	 		game.setChance2(chance, game.DECIMAL);
				   		 	 		 	game.setChance2(Integer.parseInt(extract), game.AMERICAN);		 	 		
							 	 		runFurther = false;
				        			}
				        			
				        			if((str.contains("<li class=\"money-line no-lines\">"))){

				        				game.setChance2(0, game.DECIMAL);				 	 		
							 	 		runFurther = false;
				        			}
					 	 		}
					 	 		teamCount = 0;
					 	 		if(game.differentEnough()){
			 	 					thegreekList.add(game);
			 	 		        }
			 	 		        game = new Game(THEGREEK);
		        			}
   		
		        		}        		
			 	 	}
	        }
	         
	         in.close();
	      } catch (MalformedURLException e) {
	         System.out.println(e.getMessage());
	      } catch (IOException e) {
	         System.out.println(e.getMessage());
	      }
	      
	      return worked;
	}
	
	public boolean checkPaddyPowerNFL(ArrayList<Game> thegreekList){
		String surl = "http://www.paddypower.com/bet/american-football/nfl-matches?ev_oc_grp_ids=369738";	
		final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12";
        int teamCount = 0;
		Game game = new Game(PADDYPOWER);
		boolean worked = false;
		
	      try {
	         URL url = new URL(surl);
	         URLConnection conn = url.openConnection();
	         conn.addRequestProperty("User-Agent", userAgent);
	 
	         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         String str;
	         
	         while ((str = in.readLine()) != null) {
	        	 if(teamCount == 0){
	        		 if(str.contains("BS_go_bet();")){
		        		 if(in.readLine().contains("return false\"")){
		        			 if(in.readLine().startsWith(">")){
		        				 str = in.readLine();
		        				 if(!(str.startsWith("<"))){
		    		        		 game.setTeam1(str);
		    		        		 
		    		        		 for(int i=0; i< 18; i++){
		    		        			 str = in.readLine();
		    		        			 if(i == 13){
		    		        				 if(!(str.contains("evens"))){
		    		        					 String first = str.substring(0, str.indexOf("/"));
		    		        					 String second = str.substring(str.indexOf("/")+1, str.length());
		    		        					 game.setChance1(Integer.parseInt(first), game.FRACTIONALup);
		    		        					 game.setChance1(Integer.parseInt(second), game.FRACTIONALdown);
		    		        					 game.setChance1(fractionalToDecimal(Integer.parseInt(first), Integer.parseInt(second)), game.DECIMAL);
		    		        					 
		    		        					 teamCount++;
		    		        					 worked = true;
		    		        				 }
		    		        			 }
		    		        			 
		    		        		 }
		        				 }
		        			 }
		        		 }
		        	 }
	        	 } else {
	        		 if(str.contains("BS_go_bet();")){
		        		 if(in.readLine().contains("return false\"")){
		        			 if(in.readLine().startsWith(">")){
		        				 str = in.readLine();
		        				 if(!(str.startsWith("<"))){
		        					 game.setTeam2(str);
		    		        		 
		    		        		 for(int i=0; i< 18; i++){
		    		        			 str = in.readLine();
		    		        			 if(i == 13){
		    		        				 if(!(str.contains("evens"))){
		    		        					 String first = str.substring(0, str.indexOf("/"));
		    		        					 String second = str.substring(str.indexOf("/")+1, str.length());
		    		        					 game.setChance2(Integer.parseInt(first), game.FRACTIONALup);
		    		        					 game.setChance2(Integer.parseInt(second), game.FRACTIONALdown);
		    		        					 game.setChance2(fractionalToDecimal(Integer.parseInt(first), Integer.parseInt(second)), game.DECIMAL);
		    		        					 
		    		        					 teamCount = 0;
			    		 					 	 if(game.differentEnough()){
			    		 			 	 			thegreekList.add(game);
			    		 			 	 		 }
			    		 			 	 		 game = new Game(PADDYPOWER);
		    		        				 }
		    		        			 } 
		    		        		 }
		        				 }
		        			 }
		        		 }
		        		 
		        	 }
	        	 }
	         }
	         
	         in.close();
	      } catch (MalformedURLException e) {
	         System.out.println(e.getMessage());
	      } catch (IOException e) {
	         System.out.println(e.getMessage());
	      }
	      
	      return worked;
	}
	
	
	public void checkSourcecode(){
		String surl = "http://www.paddypower.com/bet/american-football/nfl-matches?ev_oc_grp_ids=369738";	
		 final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12";
	      try {
	         URL url = new URL(surl);
	         URLConnection conn = url.openConnection();
	         conn.addRequestProperty("User-Agent", userAgent);
	 
	         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         String str;
	         
	         System.out.println("-----------------------------------------sourcecode-----------------------------------");
	         
	         
	         while ((str = in.readLine()) != null) {
	        	 System.out.println(str);
	         }
	         
	         in.close();
	      } catch (MalformedURLException e) {
	         System.out.println(e.getMessage());
	      } catch (IOException e) {
	         System.out.println(e.getMessage());
	      }
	}
	
	public void checkSourcecodePrep(){
		int beginTerm;
		int endTerm;
		String extract = "";
		boolean runFurther = true;
		
		
		 String surl = "http://www.paddypower.com/bet/american-football/nfl-matches?ev_oc_grp_ids=369738";	
		 final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12";
	      try {
	         URL url = new URL(surl);
	         URLConnection conn = url.openConnection();
	         conn.addRequestProperty("User-Agent", userAgent);
	 
	         BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         String str;
	         float chance = 0;
	         
	         while ((str = in.readLine()) != null) {
	        	 runFurther = true;

	        	 if(str.contains("BS_go_bet();")){
	        		 if(in.readLine().contains("return false\"")){
	        			 if(in.readLine().startsWith(">")){
	        				 str = in.readLine();
	        				 if(!(str.startsWith("<"))){
	        					 System.out.println("----");
	    		        		 System.out.println(str);
	    		        		 
	    		        		 for(int i=0; i< 18; i++){
	    		        			 str = in.readLine();
	    		        			 if(i == 13){
	    		        				 System.out.println(str);
	    		        				 if(!(str.contains("evens"))){
	    		        					 String first = str.substring(0, str.indexOf("/"));
	    		        					 String second = str.substring(str.indexOf("/")+1, str.length());
	    		        					 System.out.println(first + " "+ second);
	    		        					 System.out.println(fractionalToDecimal(Integer.parseInt(first), Integer.parseInt(second)));
	    		        				 }
	    		        			 }
	    		        			 
	    		        		 }
	    		        		 
	    		        		 
	    		        		 /*
	    		        		 while (runFurther) {
	    		        			 if(in.readLine().startsWith("<span class=\"tripper\"")){
	    		        				 str = in.readLine();
	    		        				 System.out.println(str);
	    		        				 runFurther = false;
	    		        			 }
	    		        			 
	    		        		 }
	    		        		 */
	        				 }
	        			 }
	        		 }
	        	 }
	        	 /*
	        	 if((str = in.readLine()) != null){
	        		 if(str.contains("<span")){
	        			 if((str = in.readLine()) != null){
	        				 System.out.println(str);
		    				 runFurther = false;
	        			 }
	        		 }
    			 }
    			 */
	        	 
	        		
	

	         }
	         
	         in.close();
	      } catch (MalformedURLException e) {
	         System.out.println(e.getMessage());
	      } catch (IOException e) {
	         System.out.println(e.getMessage());
	      }
	}
}

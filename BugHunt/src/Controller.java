import javax.swing.JFrame; // for JFrame
import javax.swing.JOptionPane;
import java.awt.*; // for graphics & MouseListener 
import java.awt.event.*; // need for events and MouseListener
import java.util.Timer;
import java.util.TimerTask; // need for Timer and TimerTask classes
////Kyles Branch//////
////////////////////////////////////
/////////////////////////////////////
///////// controller class 
////////////////////////////////////
////////////////////////////////////

class Controller extends TimerTask implements MouseListener  {
    public static final int SLOW_BUG = 0; // these are in order
    public static final int FAST_BUG = 1;
    public static final int VERY_FAST_BUG = 2;
    public static final int BUG_DONE = 3; // this should be last
    public static final int TIME=6000;
    public static final int TIME_TO_MOVE_BUGS_IN_MILLISECONDS = 70; // 80 milliseconds on timer
    public static final int NUMBER_OF_BUG_TYPES = 3;// to match the number of game levels slow + fast = 2
    public static final int MAX_NUMBER_OF_BUGS = 4; // cheap short cut for array sizing

    public JFrame gameJFrame;
    public Container gameContentPane;
    private int bugLevel[] = new int[MAX_NUMBER_OF_BUGS];
    private boolean gameIsReady = false;
    private Bug gameBug[][] = new Bug[NUMBER_OF_BUG_TYPES][MAX_NUMBER_OF_BUGS];
    private java.util.Timer gameTimer = new java.util.Timer();
    private int xMouseOffsetToContentPaneFromJFrame = 0;
    private int yMouseOffsetToContentPaneFromJFrame = 0;
    private static int timesMissed = 0;
    private static Timer timer;
    int wins =0;
    
    
    public Controller(String passedInWindowTitle, 
        int gameWindowX, int gameWindowY, int gameWindowWidth, int gameWindowHeight){
        gameJFrame = new JFrame(passedInWindowTitle);
        gameJFrame.setSize(gameWindowWidth, gameWindowHeight);
        gameJFrame.setLocation(gameWindowX, gameWindowY);
        gameJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameContentPane = gameJFrame.getContentPane();
        gameContentPane.setLayout(null); // not need layout, will use absolute system
        gameContentPane.setBackground(Color.white);
        gameJFrame.setVisible(true);
        // Event mouse position is given inside JFrame, where bug's image in JLabel is given inside ContentPane,
        //  so adjust for the border and frame
        int borderWidth = (gameWindowWidth - gameContentPane.getWidth())/2;  // 2 since border on either side
        xMouseOffsetToContentPaneFromJFrame = borderWidth;
        yMouseOffsetToContentPaneFromJFrame = gameWindowHeight - gameContentPane.getHeight()-borderWidth; // assume side border = bottom border; ignore title bar

        // create the bugs, now that JFrame has been initialized
        for(int i = 0; i <MAX_NUMBER_OF_BUGS;i++){
	        gameBug[SLOW_BUG][i] = new SlowBug(gameJFrame,2,10);// JFrame, hits required,% change direction
	        gameBug[FAST_BUG][i] = new FastBug(gameJFrame,2,10);// JFrame, hits required,% change direction  
	        gameBug[VERY_FAST_BUG][i] = new VeryFastBug(gameJFrame,2,10);// JFrame, hits required,% change direction           
        }
        resetGame(SLOW_BUG);
        gameTimer.schedule(this, 0, TIME_TO_MOVE_BUGS_IN_MILLISECONDS);    
 
        // register this class as a mouse event listener for the JFrame
        gameJFrame.addMouseListener(this);
        
    	}   
    
    public void resetGame(int startingBugLevel){
        gameIsReady = false;
        wins = 0;
        for(int i = 0; i <MAX_NUMBER_OF_BUGS;i++){
        	bugLevel[i] = startingBugLevel;
        	currentBug(i).create();
        }
        
        gameIsReady = true;
        timesMissed=0;
        timer();
    }
    
    private void bugGotHit(int i){
        currentBug(i).gotHit();
        if (currentBug(i).isDying()){
            currentBug(i).kill();
            bugLevel[i] ++;
           
            if (bugLevel[i] < BUG_DONE) { // not done, go to next level of bug
                currentBug(i).create();
            }else{
            	bugLevel[i]--;
            	wins++;
            }
        }
    }
    
    private boolean didIWin(){
    	boolean winner = false;
    	if(wins==MAX_NUMBER_OF_BUGS){
    		winner = true;
    	}
         return winner;
    }
    
    private Bug currentBug(int i){
    		return gameBug[bugLevel[i]][i];
    	
    }
    
    //run() to override run() in java.util.TimerTask
    // this is run when timer expires
    
    public static void timer(){
    	timer = new Timer();
		timer.schedule(new TimerTask() {

	        public void run() {
	            timesMissed++;
	            System.out.println("Times Missed: " + timesMissed);
	            timer.cancel();
	            timer();
	            
	        }}, TIME);
		
    	
    }
    
    public void run() {
        if (gameIsReady){
        	for(int i = 0; i <MAX_NUMBER_OF_BUGS;i++){
        		if(!currentBug(i).isDying()){
        			currentBug(i).move();
        		}
        	}
        	 if (didIWin()){   // did they win?
                 gameIsReady = false;
                 timer.cancel();
                 JOptionPane.showMessageDialog(gameJFrame,"You WON!");
                 JOptionPane.showMessageDialog(gameJFrame,"Let's play again!");
                 
                 resetGame(SLOW_BUG);
                 
               }
            if(timesMissed == 5){
            	timer.cancel();
                JOptionPane.showMessageDialog(gameJFrame,"You Missed 5 times(Loser)!");
                for(int j = 0; j < MAX_NUMBER_OF_BUGS; j++){
                	currentBug(j).kill();
                }
                resetGame(SLOW_BUG);
            }
        }
    }
    
    public void mousePressed(MouseEvent event){
        // make sure game is in progress
    	
        if (gameIsReady){
        	timer.cancel();
			timer();
			boolean[] boo= new boolean[1];
			boo[0] = true;
			for(int i = 0; i <MAX_NUMBER_OF_BUGS;i++){
				
	            if (currentBug(i).isBugHit(event.getX()-xMouseOffsetToContentPaneFromJFrame, event.getY()-yMouseOffsetToContentPaneFromJFrame)){
	                bugGotHit(i);
	                boo[0] = false;
	               
	            }
	           
	        }
			 if(boo[0]==true){
             	timesMissed++;
         		System.out.println("Times Missed: " + timesMissed);
         		
         }
        }
    }
    
    public void mouseEntered(MouseEvent event) {    
        ;
    }
    public void mouseExited(MouseEvent event) {
        ;
    }
    public void mouseClicked( MouseEvent event) {
        ;
    }
    public void mouseReleased( MouseEvent event) {
        ;
    }

    public static void main( String args[]){
        Controller myController = new Controller("Bug Game", 50,50, 800, 600);// window title, int gameWindowX, int gameWindowY, int gameWindowWidth, int gameWindowHeight){
    }
    
}
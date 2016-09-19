import javax.swing.JFrame; // for JFrame
import javax.swing.JOptionPane;
import java.awt.*; // for graphics & MouseListener 
import java.awt.event.*; // need for events and MouseListener
import java.util.TimerTask; // need for Timer and TimerTask classes

////////////////////////////////////
/////////////////////////////////////
///////// controller class 
////////////////////////////////////
////////////////////////////////////

class Controller extends TimerTask implements MouseListener  {
    public static final int SLOW_BUG = 0; // these are in order
    public static final int FAST_BUG = 1;
    public static final int BUG_DONE = 2; // this should be last
 
    public static final int TIME_TO_MOVE_BUGS_IN_MILLISECONDS = 70; // 80 milliseconds on timer
    public static final int NUMBER_OF_BUG_TYPES = 2;// to match the number of game levels slow + fast = 2
    public static final int MAX_NUMBER_OF_BUGS = 1; // cheap short cut for array sizing

    public JFrame gameJFrame;
    public Container gameContentPane;
    private int bugLevel;
    private boolean gameIsReady = false;
    private Bug gameBug[] = new Bug[NUMBER_OF_BUG_TYPES];
    private java.util.Timer gameTimer = new java.util.Timer();
    private int xMouseOffsetToContentPaneFromJFrame = 0;
    private int yMouseOffsetToContentPaneFromJFrame = 0;
    
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
        gameBug[SLOW_BUG] = new SlowBug(gameJFrame,3,10);// JFrame, hits required,% change direction
        gameBug[FAST_BUG] = new FastBug(gameJFrame,4,25);// JFrame, hits required,% change direction           

        resetGame(SLOW_BUG);
        gameTimer.schedule(this, 0, TIME_TO_MOVE_BUGS_IN_MILLISECONDS);    
 
        // register this class as a mouse event listener for the JFrame
        gameJFrame.addMouseListener(this);
    }   
    
    public void resetGame(int startingBugLevel){
        gameIsReady = false;
        bugLevel = startingBugLevel;
        currentBug().create();
        gameIsReady = true;
    }
    
    private void bugGotHit(){
        currentBug().gotHit();
        if (currentBug().isDying()){
            currentBug().kill();
            bugLevel ++;
            if (bugLevel < BUG_DONE) { // not done, go to next level of bug
                currentBug().create();
            }
        }
    }
    
    private boolean didIWin(){
         return bugLevel >= BUG_DONE;
    }
    
    private Bug currentBug(){
        return gameBug[bugLevel];
    }
    
    //run() to override run() in java.util.TimerTask
    // this is run when timer expires
    public void run() {
        if (gameIsReady){
            currentBug().move();
        }
    }

    public void mousePressed(MouseEvent event){
        // make sure game is in progress
        if (gameIsReady){
            if (currentBug().isBugHit(event.getX()-xMouseOffsetToContentPaneFromJFrame, event.getY()-yMouseOffsetToContentPaneFromJFrame)){
                bugGotHit();
                if (didIWin()){   // did they win?
                   gameIsReady = false; 
                   JOptionPane.showMessageDialog(gameJFrame,"You WON!");
                   JOptionPane.showMessageDialog(gameJFrame,"Let's play again!");
                   resetGame(SLOW_BUG);
                 }
            }
            else { // they missed so game is over
                    JOptionPane.showMessageDialog(gameJFrame,"You Missed (Loser)!");
                    currentBug().kill();
                    resetGame(SLOW_BUG);
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
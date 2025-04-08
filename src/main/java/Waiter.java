public class Waiter implements Consumer, Runnable {
    private final Buffer mealBuffer;
    private final TableBuffer tableBuffer;

    public Waiter(Buffer mealBuffer, TableBuffer tableBuffer) {
        this.mealBuffer = mealBuffer;
        this.tableBuffer = tableBuffer;
    }

    @Override
    public BufElement consume() throws InterruptedException {
        return mealBuffer.consume();
    }


    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
import java.util.ArrayList;
import java.util.LinkedList;

public class RR {
    int currentTime = 0;
    int idleTime = 0;
    int timeQuantum = 2;
    ArrayList<Integer> turnAroundTimes = new ArrayList<>();
    ArrayList<Integer> responseTimes = new ArrayList<>();
    ArrayList<Integer> waitingTimes;
    ArrayList<Integer> startTimes = new ArrayList<>();
    ArrayList<Integer> endTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdTurnAroundTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdResponseTimes = new ArrayList<>();
    ArrayList<Process> processList;
    ArrayList<Integer> ioQueue = new ArrayList<>();
    LinkedList<ArrayList<Integer>> readyQueue = new LinkedList<>();
    ArrayList<ArrayList<Integer>> gantChart = new ArrayList<>();
    private static final int arrivalTime = 0;
    private static final int pId = 1;
    private static final int cpuTime = 2;
    private static final int ioFlag = 3;

    RR(ArrayList<Process> processList) {
        this.processList = processList;
        this.waitingTimes = new ArrayList<>(processList.size());
        for (int i = 0; i < processList.size(); i++) {
            waitingTimes.add(i, 0);
        }
    }

    /**
     * Find the process
     * @param pid its the process id
     * @return will return the process
     */
    Process search(int pid) {
        for (Process process : processList) {
            if (process.pId == pid)
                return process;
        }
        return null;
    }

    /**
     * Check the process in readyQueue that it still has ioTime or not
     * @param pid its the process id
     * @return if the process still has io and its in the readyQueue => true , else => false
     */
    boolean readyQueueSearch(int pid) {
        for (int i = 0; i < this.readyQueue.size(); i++) {
            if (this.readyQueue.get(i).get(ioFlag) == 1 && this.readyQueue.get(i).get(1) == pid)
                return true;
        }
        return false;
    }

    /**
     * Check the process is in ioList or not
     * @param pid its the process id
     * @return if process is already in ioQueue => true , else => false
     */
    boolean ioQueueSearch(int pid) {
        for (Integer integer : ioQueue) {
            if (integer == pid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Run the RoundRobin Algorithm
     */
    void run() {
        for (int i = 0; i < this.processList.size(); i++) {
            Process process = this.processList.get(i);
            ArrayList<Integer> tmp = new ArrayList<>();
            // if io == 0 => consider cpuTime1+cpuTime2 , else consider cpuTime1
            if (process.getIoTime() == 0) {
                tmp.add(process.getArrivalTime());
                tmp.add(process.getpId());
                tmp.add(process.getCpuTime2() + process.getCpuTime1());
                // set ioFlag=-1 => know this process has no io time
                tmp.add(-1);
                this.readyQueue.add(tmp);
            } else {
                tmp.add(process.getArrivalTime());
                tmp.add(process.getpId());
                tmp.add(process.getCpuTime1());
                // set ioFlag=1 => know this process has io time
                tmp.add(1);
                this.readyQueue.add(tmp);
            }
        }
        while (!this.readyQueue.isEmpty()) {
            // sort based on arrival time if equal sort base on pid
            readyQueue.sort((l1, l2) -> {
                if (l1.get(arrivalTime).equals(l2.get(arrivalTime)))
                    return l1.get(pId).compareTo(l2.get(pId));
                return l1.get(arrivalTime).compareTo(l2.get(arrivalTime));
            });

            // take out the process from queue
            ArrayList<Integer> polledProcess = this.readyQueue.poll();

            /* compare process arrivalTime with currentTime ,
               if its bigger than currentTime => change currentTime to that position and add that time to idleTime
             */
            if (this.currentTime < polledProcess.get(arrivalTime)) {
                idleTime += polledProcess.get(arrivalTime) - this.currentTime;
                this.currentTime += polledProcess.get(arrivalTime) - this.currentTime;
            }

            // set new waitingTime for process , Add (currentTime - arrivalTime) to last waitingTime
            this.waitingTimes.set(polledProcess.get(pId) - 1, (this.currentTime - polledProcess.get(arrivalTime)) + this.waitingTimes.get(polledProcess.get(pId) - 1));

            // check the current cpuTime
            if (polledProcess.get(cpuTime) != 0) {
                ArrayList<Integer> t = new ArrayList<>();
                /* compare current cpuTime to tq
                if its less or equal to tq so the process will completely do its cpuTime ,
                else the process do its work until tq then add the process again to end of the queue
                 */
                if (polledProcess.get(cpuTime) <= this.timeQuantum) {
                    t.add(polledProcess.get(pId));
                    t.add(this.currentTime);
                    t.add(this.currentTime + polledProcess.get(cpuTime));
                    this.gantChart.add(t);
                    this.currentTime += polledProcess.get(cpuTime);
                } else {
                    t.add(polledProcess.get(pId));
                    t.add(this.currentTime);
                    t.add(this.currentTime + this.timeQuantum);
                    this.gantChart.add(t);
                    this.currentTime += this.timeQuantum;
                    ArrayList<Integer> tmp = new ArrayList<>();
                    tmp.add(this.currentTime);
                    tmp.add(polledProcess.get(pId));
                    tmp.add(polledProcess.get(cpuTime) - this.timeQuantum);
                    tmp.add(polledProcess.get(ioFlag));
                    this.readyQueue.add(tmp);
                }
            }

            /*
            Check if the process still has io and its not in the ioList,
            then send it to ioList to do its io
             */
            if (this.readyQueueSearch(polledProcess.get(pId)) == false && this.ioQueueSearch(polledProcess.get(pId)) == false) {
                this.ioQueue.add(polledProcess.get(pId));
                Process process = this.search(polledProcess.get(pId));
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(this.currentTime + process.getIoTime());
                tmp.add(polledProcess.get(pId));
                tmp.add(process.getCpuTime2());
                tmp.add(2);
                this.readyQueue.add(tmp);
            }
        }
    }

    /**
     * Calculate turnAroundTimes of processes
     * @return the list that contains turnAroundTimes of all processes
     */
    ArrayList<Integer> getTurnAroundTimes() {
        int p_end_time = 0;
        for (int i = 0; i < processList.size(); i++) {
            Process process = this.search(i + 1);
            for (int j = this.gantChart.size() - 1; j > -1; j--) {
                if ((i + 1) == this.gantChart.get(j).get(0)) {
                    int p_end_time_in_cpu = this.gantChart.get(j).get(2);
                    p_end_time = p_end_time_in_cpu;
                    if (process.cpuTime2 == 0) {
                        p_end_time += process.getIoTime();
                    }
                    break;
                }
            }
            this.turnAroundTimes.add(p_end_time - process.getArrivalTime());
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(process.pId);
            tmp.add(p_end_time - process.getArrivalTime());
            this.pIdTurnAroundTimes.add(tmp);
        }
        return turnAroundTimes;
    }

    /**
     * Calculate throughput of CPU
     * @return the throughput of CPU
     */
    Double getThroughput() {
        return (double) this.processList.size() * 1000 / this.currentTime;
    }

    /**
     * Calculate cpu Utilization
     * @return the utilization of CPU
     */
    Double getCpuUtilization() {
        int idle = 0;
        for (int i = 0; i < this.gantChart.size() - 1; i++) {
            idle += this.gantChart.get(i + 1).get(1) - this.gantChart.get(i).get(2);
        }
        return (double) (this.currentTime - idle) * 100 / this.currentTime;
    }

    /**
     * Calculate responseTimes of all processes
     * @return the list that contains responseTimes of all processes
     */
    ArrayList<Integer> getResponseTimes() {
        for (int i = 0; i < this.processList.size(); i++) {
            Process process = this.search(i + 1);
            for (int j = 0; j < this.gantChart.size(); j++) {
                if ((i + 1) == this.gantChart.get(j).get(0)) {
                    this.responseTimes.add(this.gantChart.get(j).get(1) - process.getArrivalTime());
                    ArrayList<Integer> tmp = new ArrayList<>();
                    tmp.add(process.pId);
                    tmp.add(this.gantChart.get(j).get(1) - process.getArrivalTime());
                    this.pIdResponseTimes.add(tmp);
                    break;
                }
            }
        }
        return responseTimes;
    }

    /**
     * Calculate waitingTimes of all processes
     * @return the list that contains waitingTimes of all processes
     */
    public ArrayList<Integer> getWaitingTimes() {
        return waitingTimes;
    }

    /**
     * Get the totalTime
     * @return the totalTime
     */
    public int getTotalTime() {
        return currentTime;
    }

    /**
     * Get the idleTime
     * @return the idleTime of CPU
     */
    public int getIdleTime() {
        return idleTime;
    }

    /**
     * Get startTimes of all processes
     * @return the list that contains startTimes of all processes
     */
    ArrayList<Integer> getStartTimes() {
        for (int pid = 1; pid < processList.size() + 1; pid++) {
            for (int i = 0; i < gantChart.size(); i++) {
                if (gantChart.get(i).get(0) == pid) {
                    startTimes.add(gantChart.get(i).get(1));
                    break;
                }
            }
        }
        return startTimes;
    }

    /**
     * Get endTimes of all processes
     * @return the list that contains endTimes of all processes
     */
    ArrayList<Integer> getEndTimes() {
        for (int pid = 1; pid < processList.size() + 1; pid++) {
            for (int i = gantChart.size() - 1; i > -1; i--) {
                if (gantChart.get(i).get(0) == pid) {
                    endTimes.add(gantChart.get(i).get(2));
                    break;
                }
            }
        }
        return endTimes;
    }

}

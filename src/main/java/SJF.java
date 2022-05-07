import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class SJF {
    int currentTime = 0;
    int idleTime = 0;
    ArrayList<Integer> turnAroundTimes = new ArrayList<>();
    ArrayList<Integer> responseTimes = new ArrayList<>();
    ArrayList<Integer> waitingTimes;
    ArrayList<Integer> startTimes = new ArrayList<>();
    ArrayList<Integer> endTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdTurnAroundTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdResponseTimes = new ArrayList<>();
    LinkedList<ArrayList<Integer>> readyQueue = new LinkedList<>();
    ArrayList<ArrayList<Integer>> gantChart = new ArrayList<>();
    ArrayList<Process> processList;
    private static final int cpuTime = 0;
    private static final int pId = 1;
    private static final int arrivalTime = 2;
    private static final int ioFlag = 3;
    private static final int readyFlag = 4;

    SJF(ArrayList<Process> processList) {
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
     * Sort the current ready processes list based on cpu burst time if equal sort base on pid
     * @param list the list of processes
     * @return the sorted list of processes
     */
    LinkedList<ArrayList<Integer>> sort(LinkedList<ArrayList<Integer>> list) {
        LinkedList<ArrayList<Integer>> readyList = new LinkedList<>();
        LinkedList<ArrayList<Integer>> notReadyList = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            // check if the process can do its job now
            if (list.get(i).get(readyFlag) == 1) {
                readyList.add(list.get(i));
            } else {
                notReadyList.add(list.get(i));
            }
        }
        readyList.sort((l1, l2) -> {
            if (l1.get(cpuTime).equals(l2.get(cpuTime)))
                return l1.get(pId).compareTo(l2.get(pId));
            return l1.get(cpuTime).compareTo(l2.get(cpuTime));
        });

        readyList.addAll(notReadyList);

        return readyList;
    }

    /**
     * Run the SJF Algorithm
     */
    void run() {
        // for sort base on arrival time if equal sort base on pid
        Comparator<Process> sortByArrivalTime = Process::compareTo;
        processList.sort(sortByArrivalTime);

        for (int i = 0; i < this.processList.size(); i++) {
            Process process = processList.get(i);
            ArrayList<Integer> tmp1 = new ArrayList<>();
            // if io == 0 => consider cpuTime1+cpuTime2 , else consider cpuTime1
            if (process.getIoTime() == 0) {
                tmp1.add(process.getCpuTime2() + process.getCpuTime1());
                tmp1.add(process.getpId());
                tmp1.add(process.getArrivalTime());
                // set ioFlag=-1 => know this process has no io time
                tmp1.add(-1);
                //-1 means the process can't do its job now
                tmp1.add(-1);
                this.readyQueue.add(tmp1);
            } else {
                tmp1.add(process.getCpuTime1());
                tmp1.add(process.getpId());
                tmp1.add(process.getArrivalTime());
                // set ioFlag=1 => know this process has io time
                tmp1.add(1);
                //-1 means the process can't do its job now
                tmp1.add(-1);
                readyQueue.add(tmp1);
            }
        }

        while (!readyQueue.isEmpty()) {
            for (int i = 0; i < readyQueue.size(); i++) {
                // check if the process arrivalTime is less or equal currentTime set it that it can do its job now
                if (this.readyQueue.get(i).get(arrivalTime) <= this.currentTime) {
                    ArrayList<Integer> tmp = new ArrayList<>();
                    tmp.add(this.readyQueue.get(i).get(cpuTime));
                    tmp.add(this.readyQueue.get(i).get(pId));
                    tmp.add(this.readyQueue.get(i).get(arrivalTime));
                    tmp.add(this.readyQueue.get(i).get(ioFlag));
                    //1 means the process can do its job now
                    tmp.add(1);
                    this.readyQueue.set(i, tmp);
                }
            }
            // sort based on cpu burst time if equal sort base on pid
            this.readyQueue = this.sort(this.readyQueue);
            // take out the process from queue
            ArrayList<Integer> polledProcess = readyQueue.poll();

            /* compare process arrivalTime with currentTime ,
               if its bigger than currentTime => change currentTime to that position and add that time to idleTime
             */
            if (this.currentTime < polledProcess.get(arrivalTime)) {
                idleTime += polledProcess.get(arrivalTime) - this.currentTime;
                this.currentTime += polledProcess.get(arrivalTime) - this.currentTime;
            }

            // set new waitingTime for process , Add (currentTime - arrivalTime) to last waitingTime
            this.waitingTimes.set(polledProcess.get(pId) - 1, this.currentTime - polledProcess.get(arrivalTime) + this.waitingTimes.get(polledProcess.get(pId) - 1));

            // check if the process has cpuTime then let the process do its cpuTime
            if (polledProcess.get(cpuTime) != 0) {
                ArrayList<Integer> t = new ArrayList<>();
                t.add(polledProcess.get(pId));
                t.add(this.currentTime);
                t.add(this.currentTime + polledProcess.get(cpuTime));
                this.gantChart.add(t);
                this.currentTime = this.currentTime + polledProcess.get(cpuTime);
            }

            // check if the process has ioTime then let the process do its ioTime and then add it to end of the readyQueue
            if (polledProcess.get(ioFlag) == 1) {
                Process tProcess = this.search(polledProcess.get(pId));
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                tmp.add(tProcess.cpuTime2);
                tmp.add(polledProcess.get(pId));
                tmp.add(this.currentTime + tProcess.getIoTime());
                tmp.add(2);
                tmp.add(-1);
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

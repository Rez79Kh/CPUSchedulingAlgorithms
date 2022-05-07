import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class FCFS {
    int currentTime = 0;
    int idleTime = 0;
    ArrayList<Integer> turnAroundTimes = new ArrayList<>();
    ArrayList<Integer> responseTimes = new ArrayList<>();
    ArrayList<Integer> waitingTimes;
    ArrayList<Integer> startTimes = new ArrayList<>();
    ArrayList<Integer> endTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdTurnAroundTimes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> pIdResponseTimes = new ArrayList<>();
    ArrayList<Process> processList;
    LinkedList<Process> readyQueue = new LinkedList<>();
    ArrayList<ArrayList<Integer>> gantChart = new ArrayList<>();

    FCFS(ArrayList<Process> processList) {
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
     * Run the FCFS Algorithm
     */
    void run() {
        this.readyQueue.addAll(processList);
        while (!readyQueue.isEmpty()) {
            // for sort base on arrival time if equal sort base on pid
            Comparator<Process> sortByArrivalTime = Comparator.naturalOrder();
            readyQueue.sort(sortByArrivalTime);

            // take out the process from queue
            Process process = readyQueue.poll();

            /* compare process arrivalTime with currentTime ,
               if its bigger than currentTime => change currentTime to that position and add that time to idleTime
             */
            if (this.currentTime < process.getArrivalTime()) {
                idleTime += process.getArrivalTime() - this.currentTime;
                this.currentTime += process.getArrivalTime() - this.currentTime;
            }

            // set new waitingTime for process , Add (currentTime - arrivalTime) to last waitingTime
            this.waitingTimes.set(process.getpId() - 1, this.currentTime - process.getArrivalTime() + this.waitingTimes.get(process.getpId() - 1));

            // check the process ioTime
            if (process.getIoTime() == 0) {
                // if the process doesn't have ioTime anymore then it will complete its totalCpuTime
                if (process.getCpuTime1() + process.getCpuTime2() != 0) {
                    ArrayList<Integer> t = new ArrayList<>();
                    t.add(process.getpId());
                    t.add(this.currentTime);
                    t.add(this.currentTime + process.getCpuTime1() + process.getCpuTime2());
                    this.gantChart.add(t);
                    this.currentTime = this.currentTime + process.getCpuTime1() + process.getCpuTime2();
                }
            } else {
                // if the process already have ioTime and it has not done its cpuTime1 yet then it will complete its cpuTime1
                if (process.getCpuTime1() != 0) {
                    ArrayList<Integer> t = new ArrayList<>();
                    t.add(process.getpId());
                    t.add(this.currentTime);
                    t.add(this.currentTime + process.getCpuTime1());
                    this.gantChart.add(t);
                    this.currentTime = this.currentTime + process.getCpuTime1();
                }
                // then the process will do its io and set it totalCpuTime to just cpuTime2 then add process to the end of the readyQueue
                Process newProcess = new Process(process.getpId(), this.currentTime + process.getIoTime(), process.getCpuTime2(), 0, 0);
                this.readyQueue.add(newProcess);
            }

        }
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
}

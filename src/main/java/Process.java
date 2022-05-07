public class Process implements Comparable<Process> {
    int pId, arrivalTime, cpuTime1, ioTime, cpuTime2;

    Process(int pId, int arrivalTime, int cpuTime1, int ioTime, int cpuTime2) {
        this.pId = pId;
        this.arrivalTime = arrivalTime;
        this.cpuTime1 = cpuTime1;
        this.ioTime = ioTime;
        this.cpuTime2 = cpuTime2;
    }

    /**
     * Get the process arrivalTime
     * @return the process arrivalTime
     */
    public Integer getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Get the process id
     * @return the process id
     */
    public Integer getpId() {
        return pId;
    }

    /**
     * Get the process first cpu burst time
     * @return the first cpu burst time of the process
     */
    public int getCpuTime1() {
        return cpuTime1;
    }

    /**
     * Get the process ioTime
     * @return the process ioTime
     */
    public int getIoTime() {
        return ioTime;
    }

    /**
     * Get the process second cpu burst time
     * @return the second cpu burst time of the process
     */
    public int getCpuTime2() {
        return cpuTime2;
    }

    /**
     * Sort the process list based on process arrivalTime ,
     * if equal then sort base on process id
     * @return the biggest arrivalTime or pid
     */
    public int compareTo(Process o) {
        if(this.getArrivalTime().equals(o.getArrivalTime())){
            return this.getpId().compareTo(o.getpId());
        }
        return this.getArrivalTime().compareTo(o.getArrivalTime());
    }
}

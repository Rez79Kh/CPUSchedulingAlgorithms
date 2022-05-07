import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Management {
    /**
     * Printing the outputs of the running algorithm
     * @param numberOfProcess its the number of process
     * @param startTimes its the list of startTimes of all processes
     * @param endTimes its the list of endTimes of all processes
     * @param responseTimes its the list of responseTimes of all processes
     * @param turnAroundTimes its the list of turnAroundTimes of all processes
     * @param waitingTimes its the list of waitingTimes of all processes
     * @param totalTime its the totalTime of cpu
     * @param idleTime its the idleTime of cpu
     * @param throughput its the throughput of cpu
     * @param cpuUtilization its the cpu utilization
     */
    void outputs(int numberOfProcess, ArrayList<Integer> startTimes, ArrayList<Integer> endTimes, ArrayList<Integer> responseTimes, ArrayList<Integer> turnAroundTimes, ArrayList<Integer> waitingTimes, Integer totalTime, Integer idleTime, Double throughput, Double cpuUtilization) {
        System.out.println("process_id             start_time             end_time             response_time             turnAround_time             waiting_time");
        for (int i = 0; i < numberOfProcess; i++) {
            System.out.print("    ");
            System.out.print(i + 1);
            System.out.print("                       ");
            System.out.print(startTimes.get(i));
            System.out.print("                    ");
            System.out.print(endTimes.get(i));
            System.out.print("                     ");
            System.out.print(responseTimes.get(i));
            System.out.print("                          ");
            System.out.print(turnAroundTimes.get(i));
            System.out.print("                          ");
            System.out.println(waitingTimes.get(i));
        }
        Double responseTimeAvg = getAvg(responseTimes);
        Double turnAroundTimeAvg = getAvg(turnAroundTimes);
        Double waitingTimeAvg = getAvg(waitingTimes);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.print("AVG");
        System.out.print("                                                                     ");
        System.out.print(responseTimeAvg);
        System.out.print("                       ");
        System.out.print(turnAroundTimeAvg);
        System.out.print("                       ");
        System.out.println(waitingTimeAvg);
        System.out.println();
        System.out.println("Total Time : " + totalTime);
        System.out.println("Idle Time : " + idleTime);
        System.out.println("Throughput : " + throughput);
        System.out.println("Cpu utilization : " + cpuUtilization);
        System.out.println("=========================================================================================================================================");
    }

    /**
     * Run the FCFS algorithm
     * @param inp its the list of input processes infos
     */
    void runFCFS(ArrayList<Process> inp) throws InterruptedException {
        System.out.println("=========================================================================================================================================");
        System.out.println("                                                                FCFS                                                                     ");
        System.out.println("=========================================================================================================================================");
        FCFS fcfs = new FCFS(inp);
        fcfs.run();
        ArrayList<Integer> fcfsStartTimes = fcfs.getStartTimes();
        ArrayList<Integer> fcfsEndTimes = fcfs.getEndTimes();
        ArrayList<Integer> fcfsResponseTimes = fcfs.getResponseTimes();
        ArrayList<Integer> fcfsTurnAroundTimes = fcfs.getTurnAroundTimes();
        ArrayList<Integer> fcfsWaitingTimes = fcfs.getWaitingTimes();
        Integer totalTime = fcfs.getTotalTime();
        Integer idleTime = fcfs.getIdleTime();
        Double throughput = fcfs.getThroughput();
        Double cpuUtilization = fcfs.getCpuUtilization();
        outputs(inp.size(), fcfsStartTimes, fcfsEndTimes, fcfsResponseTimes, fcfsTurnAroundTimes, fcfsWaitingTimes, totalTime, idleTime, throughput, cpuUtilization);

    }

    /**
     * Run the SJF algorithm
     * @param inp inp its the list of input processes infos
     */
    void runSJF(ArrayList<Process> inp) throws InterruptedException {
        System.out.println("                                                                SJF                                                                     ");
        System.out.println("=========================================================================================================================================");
        SJF sjf = new SJF(inp);
        sjf.run();
        ArrayList<Integer> sjfStartTimes = sjf.getStartTimes();
        ArrayList<Integer> sjfEndTimes = sjf.getEndTimes();
        ArrayList<Integer> sjfResponseTimes = sjf.getResponseTimes();
        ArrayList<Integer> sjfTurnAroundTimes = sjf.getTurnAroundTimes();
        ArrayList<Integer> sjfWaitingTimes = sjf.getWaitingTimes();
        Integer totalTime = sjf.getTotalTime();
        Integer idleTime = sjf.getIdleTime();
        Double throughput = sjf.getThroughput();
        Double cpuUtilization = sjf.getCpuUtilization();
        outputs(inp.size(), sjfStartTimes, sjfEndTimes, sjfResponseTimes, sjfTurnAroundTimes, sjfWaitingTimes, totalTime, idleTime, throughput, cpuUtilization);
    }

    /**
     * Run the RR algorithm
     * @param inp inp its the list of input processes infos
     */
    void runRR(ArrayList<Process> inp) throws InterruptedException {
        System.out.println("                                                                RR                                                                     ");
        System.out.println("=========================================================================================================================================");
        RR rr = new RR(inp);
        rr.run();
        ArrayList<Integer> rrStartTimes = rr.getStartTimes();
        ArrayList<Integer> rrEndTimes = rr.getEndTimes();
        ArrayList<Integer> rrResponseTimes = rr.getResponseTimes();
        ArrayList<Integer> rrTurnAroundTimes = rr.getTurnAroundTimes();
        ArrayList<Integer> rrWaitingTimes = rr.getWaitingTimes();
        Integer totalTime = rr.getTotalTime();
        Integer idleTime = rr.getIdleTime();
        Double throughput = rr.getThroughput();
        Double cpuUtilization = rr.getCpuUtilization();
        outputs(inp.size(), rrStartTimes, rrEndTimes, rrResponseTimes, rrTurnAroundTimes, rrWaitingTimes, totalTime, idleTime, throughput, cpuUtilization);
    }

    /**
     * Read inputs form file and initial the processes inputs
     * @param filePath its the path of input file
     * @return it returns the list of all processes that read from input file
     */
    public ArrayList<Process> initialInputs(String filePath) throws FileNotFoundException {
        ArrayList<Process> process_list = new ArrayList<Process>();
        File file =
                new File(filePath);
        Scanner sc = new Scanner(file);
        String[] titleLine = sc.nextLine().split(",");
        String column1Title = titleLine[0];
        String column2Title = titleLine[1];
        String column3Title = titleLine[2];
        String column4Title = titleLine[3];
        String column5Title = titleLine[4];
        System.out.println("===============================================================INPUTS===================================================================");
        System.out.print(column1Title);
        System.out.print("             ");
        System.out.print(column2Title);
        System.out.print("             ");
        System.out.print(column3Title);
        System.out.print("             ");
        System.out.print(column4Title);
        System.out.print("             ");
        System.out.print(column5Title);
        while (sc.hasNextLine()) {
            String[] infoLine = sc.nextLine().split(",");
            System.out.println();
            int pid = Integer.parseInt(infoLine[0]);
            int arr = Integer.parseInt(infoLine[1]);
            int cpu1 = Integer.parseInt(infoLine[2]);
            int io = Integer.parseInt(infoLine[3]);
            int cpu2 = Integer.parseInt(infoLine[4]);

            Process process = new Process(pid, arr, cpu1, io, cpu2);
            process_list.add(process);

            System.out.print("    ");
            System.out.print(pid);
            System.out.print("                       ");
            System.out.print(arr);
            System.out.print("                      ");
            System.out.print(cpu1);
            System.out.print("                     ");
            System.out.print(io);
            System.out.print("                    ");
            System.out.print(cpu2);
            System.out.print("                    ");
        }
        System.out.println();
        return process_list;
    }

    /**
     * Calculate the average of numbers in the list
     * @param list its the list of numbers
     * @return the average of the list of numbers
     */
    Double getAvg(ArrayList<Integer> list) {
        int sum = 0;
        for (Integer i : list)
            sum += i;
        Double avg = (double) sum / list.size();
        return avg;
    }
}

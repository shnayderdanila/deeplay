import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    Scanner in;
    PrintWriter out;

    void run() throws IOException
    {
        in = new Scanner(System.in);
        out = new PrintWriter(System.out);
        solve();
        out.flush();
    }

    void solve()
    {
        task1();
//        task2();
//        task3();
//        task4();
    }

    void task1() {
        int[] limit = ThreadLocalRandom.current().ints(100, -100, 100).toArray();
        Arrays.stream(limit).forEach((x -> out.print(x + " ")));
        Comparator<Integer> comparator = (x, y) -> {
            int a = Math.abs(x);
            int b = Math.abs(y);
            if(a % 2 == 1 && b % 2 == 1) {
                return Integer.compare(x, y);
            } else if (a % 2 == 1 && b % 2 == 0) {
                return -1;
            } else if (a % 2 == 0 && b % 2 == 1) {
                return 1;
            } else if (a == 0 && b % 2 == 0) {
                return -1;
            } else if (b == 0 && a % 2 == 0) {
                return 1;
            } else {
                return Integer.compare(y, x);
            }
        };
        out.println();
        Arrays.stream(limit).boxed().sorted(comparator).forEach((x -> out.print(x + " ")));
    }

    /**
     * Входные данные:
     *  строка вида: num num num num num num
     * */
    void task2() {
        int[] arr = Arrays.stream(in.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        Map<Integer, Integer> map = new HashMap<>();

        for (int i : arr) {
            map.compute(i, (k, v) -> (v == null) ? 1 : v + 1);
        }

        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().stream().sorted(Map.Entry.comparingByValue((x, y) -> Integer.compare(y, x))).iterator();
        Map.Entry<Integer, Integer> max = it.next();
        out.print(max.getKey());

        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            if(entry.getValue().equals(max.getValue())) {
                out.print(" " + entry.getKey());
            }
        }
    }



    void task3() {
        int countCubic = 100; // количество бросаний кубика
        int countGame = 100; // количество иммуляций игры
        int[] firstPlayerSeq = new int[]{1, 4, 1}; // числа первого игрока
        int[] secondPlayerSeq = new int[]{3, 4, 2}; // числа второго игрока


        int firstPlayerWin = 0;
        int secondPlayerWin = 0;

        SecureRandom random = new SecureRandom();
        for(int i = 0; i < countGame; i++) {
            int countFirstPlayer = 0;
            int countSecondPlayer = 0;
            int indexFirstPlayer = 0;
            int indexSecondPlayer = 0;
            for(int j = 0; j < countCubic; j++) {
                int cur = random.nextInt(1, 7);

                if(firstPlayerSeq[indexFirstPlayer] == cur) {
                    indexFirstPlayer++;
                } else {
                    indexFirstPlayer = 0;
                }

                if(secondPlayerSeq[indexSecondPlayer] == cur) {
                    indexSecondPlayer++;
                } else {
                    indexSecondPlayer = 0;
                }

                if(indexFirstPlayer == firstPlayerSeq.length) {
                    countFirstPlayer++;
                    indexFirstPlayer = 0;
                }
                if(indexSecondPlayer == secondPlayerSeq.length) {
                    countSecondPlayer++;
                    indexSecondPlayer = 0;
                }
            }

            if (countFirstPlayer > countSecondPlayer) {
                firstPlayerWin++;
            } else if (countFirstPlayer < countSecondPlayer) {
                secondPlayerWin++;
            }
        }

        double firstWinChance =  ((double) firstPlayerWin / (double) countGame);
        double secondWinChance = ((double) secondPlayerWin / (double)countGame);
        out.println("First win with chance: " + firstWinChance);
        out.println("Second win with chance: " + secondWinChance);
        out.println("Draw: " + (1 - firstWinChance - secondWinChance));

    }

    /**
     * входные данные:
     *  1) строка в указанном формате: num num num num
     *  2) целое число k
     * */
    void task4() {

        int[] arr = Arrays.stream(in.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        int k = in.nextInt();

        out.println("Array: " + Arrays.toString(arr));
        out.println("K: " + k);

        if (k < 2 || k > arr.length) {
            out.println("Невозможно");
            return;
        }

        int sum = Arrays.stream(arr).sum();

        int seqSum = (1 + k) * k / 2;

        if(seqSum > sum || (sum - seqSum) % k != 0) {
            out.println("Невозможно");
            return;
        }

        int l = (sum - seqSum) / k + 1;

        List<List<Integer>> result = new ArrayList<>();
        int[] curSum = new int[k];
        for(int i = l; i < l + k; i++) {
            result.add(new ArrayList<>());
        }

        Map<Integer, Integer> numbersCount = new HashMap<>();

        for (int j : arr) {
            numbersCount.compute(j, (key, value) -> value == null ? 1 : value + 1);
        }

        if(searchTerms(result, curSum, arr, l, 0, numbersCount)) {
            for (int i = 0; i < curSum.length - 1; i++) {
                out.print(result.get(i) + ", " + curSum[i] + ", ");
            }
            int last = curSum.length - 1;
            out.println(result.get(last) + ", " + curSum[last]);
        } else {
            out.println("Невозможно");
        }
    }

    private static boolean searchTerms(List<List<Integer>> result, int[] curSum, int[] array, int remain, int startIndex, Map<Integer, Integer> numbersCount) {
        if (numbersCount.containsValue(0) && new HashSet<>(numbersCount.values()).size() == 1) {
            return true;
        }
        boolean block = false;
        for(int i = 0; i < array.length; i++) {
            if(numbersCount.get(array[i]) > 0 && curSum[startIndex] + array[i] <= remain) {
                curSum[startIndex] += array[i];
                result.get(startIndex).add(array[i]);
                numbersCount.compute(array[i], (key, value) -> value - 1);

                if(remain == curSum[startIndex]) {
                    startIndex++;
                    remain++;
                    block = true;
                }

                if(searchTerms(result, curSum, array, remain, startIndex, numbersCount)) {
                    return true;
                }
                if(block) {
                    startIndex--;
                    remain--;
                    block = false;
                }
                curSum[startIndex] -= array[i];
                numbersCount.compute(array[i], (key, value) -> value + 1);
                result.get(startIndex).remove(result.get(startIndex).size() - 1);
            }
        }

        return false;
    }
}
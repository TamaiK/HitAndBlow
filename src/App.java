import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {

    // 列挙体
    
    enum CompareResult{
        NONE,
        HIT,
        BLOW,
    }

    enum GuessResult{
        NONE,
        CORRECT,
        INCORRECT,
    }

    // 変数

    private static Random random;
    private static Scanner scanner;

    private static List<String> correct;
    private static CompareResult[] compareResult;

    // 定数

    private static final int DIGITS_NUM = 4;
    private static final int MIN_NUM = 0;
    private static final int MAX_NUM = 9;

    private static final String FAILURE_INPUT = "";

    private static final String MESSAGE_FOR_BLANK = "";

    private static final String MESSAGE_FORMAT_FOR_REQUEST_INPUT = "%d桁の数字を入力して下さい：";
    private static final String MESSAGE_FORMAT_FOR_ERROR_FORMAT = "注意：%d桁の数字を入力してください。";

    private static final String MESSAGE_FORMAT_FOR_CORRECT = "おめでとう！%d回目で成功♪";
    private static final String MESSAGE_FORMAT_FOR_HIT_AND_BLOW_NUM = "ヒット：%d個、ブロー：%d個";

    static {
        init();
    }

    public static void main(String[] args) {

        playHitAndBlow();

        fin();
    }

    private static void init() {

        random = new Random();
        scanner = new Scanner(System.in);

        correct = new ArrayList<>();
        compareResult = new CompareResult[DIGITS_NUM];
    }

    private static void fin() {
        scanner.close();
    }

    // 関数

    private static void playHitAndBlow() {

        setCorrectNumber();

        int challengeCount = 0;
        GuessResult result = GuessResult.NONE;
        while (needNextGuessing(result)) {

            challengeCount++;

            String answer = getAnswer();

            checkCorrect(answer);

            result = getCheckResult();

            dispResult(result, challengeCount);
        }
    }

    private static void setCorrectNumber() {

        while (needCreateNumuber()) {

            String correctNum = String.valueOf(createRandomNumber(MIN_NUM, MAX_NUM));

            if (correct.indexOf(String.valueOf(correctNum)) < 0) {
                correct.add(correctNum);
            }
        }
    }

    private static boolean needCreateNumuber() {
        return correct.size() < DIGITS_NUM;
    }

    private static boolean needNextGuessing(GuessResult result) {
        return result != GuessResult.CORRECT;
    }

    private static String getAnswer() {

        String answer = FAILURE_INPUT;
        while (needInput(answer)) {

            dispRequestInput();

            String input = scanner.nextLine();

            if (!isNumber(input)) {
                dispErrorInput();
                continue;
            }

            if (!isInRange(input)) {
                dispErrorInput();
                continue;
            }

            answer = input;
        }

        return answer;
    }

    private static void dispRequestInput() {
        print(MESSAGE_FORMAT_FOR_REQUEST_INPUT, DIGITS_NUM);
    }

    private static boolean isNumber(String input) {

        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private static boolean needInput(String answer) {
        return answer == null || answer.isBlank();
    }

    private static void dispErrorInput() {
        println(MESSAGE_FORMAT_FOR_ERROR_FORMAT, DIGITS_NUM);
        dipsBlankLine();
    }

    private static boolean isInRange(String input) {
        return input.length() == DIGITS_NUM;
    }

    private static void checkCorrect(String answer) {

        initCompareResult();

        checkHit(answer);

        checkBlow(answer);
    }

    private static void initCompareResult() {

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            compareResult[digit] = CompareResult.NONE;
        }
    }

    private static void checkHit(String answer) {

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (answer.charAt(digit) == getCorrectCharAt(digit)) {

                compareResult[digit] = CompareResult.HIT;
            }
        }
    }

    private static char getCorrectCharAt(int digit) {
        return correct.get(digit).charAt(0);
    }

    private static void checkBlow(String answer) {

        List<Integer> isHitOrBlowList = new ArrayList<>();

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (compareResult[digit] == CompareResult.HIT) {
                isHitOrBlowList.add(digit);
            }
        }

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (compareResult[digit] == CompareResult.HIT) {
                continue;
            }

            for (int target = 0; target < DIGITS_NUM; target++) {

                if (getCorrectCharAt(digit) != answer.charAt(target)) {
                    continue;
                }

                if (isHitOrBlowList.indexOf(target) >= 0) {
                    continue;
                }

                isHitOrBlowList.add(target);
                compareResult[digit] = CompareResult.BLOW;
            }
        }
    }

    private static GuessResult getCheckResult() {

        for (CompareResult result : compareResult) {

            if (result != CompareResult.HIT) {
                return GuessResult.INCORRECT;
            }
        }

        return GuessResult.CORRECT;
    }

    private static void dispResult(App.GuessResult result, int challengeCount) {

        switch (result) {

        case CORRECT:
            dispCorrect(challengeCount);
            break;

        default:
        case INCORRECT:
            dispHitAndBlowNum();
            break;
        }
    }

    private static void dispCorrect(int challengeCount) {
        println(MESSAGE_FORMAT_FOR_CORRECT, challengeCount);
    }

    private static void dispHitAndBlowNum() {

        int hit = getHitCount();
        int blow = getBlowCount();

        println(MESSAGE_FORMAT_FOR_HIT_AND_BLOW_NUM, hit, blow);
        dipsBlankLine();
    }

    private static int getHitCount() {

        int hit = 0;
        for (CompareResult result : compareResult) {

            if (result == CompareResult.HIT) {
                hit++;
            }
        }

        return hit;
    }

    private static int getBlowCount() {

        int blow = 0;
        for (CompareResult result : compareResult) {

            if (result == CompareResult.BLOW) {
                blow++;
            }
        }

        return blow;
    }

    // 汎用関数

    private static void print(String str, Object... args) {
        System.out.print(String.format(str, args));
    }

    private static void println(String str) {
        System.out.println(str);
    }

    private static void println(String str, Object... args) {
        System.out.println(String.format(str, args));
    }

    private static int createRandomNumber(int min, int max) {

        int range = max - min + 1;
        return random.nextInt(range) + min;
    }

    private static void dipsBlankLine() {
        println(MESSAGE_FOR_BLANK);
    }
}

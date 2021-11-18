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

    private static int hintDigits;

    // 定数

    private static final int DIGITS_NUM = 5;
    private static final int MIN_NUM = 0;
    private static final int MAX_NUM = 9;
    private static final int HINT_ADD_INTERVAL = 3;
    private static final int FIRST_CHALLENGE = 0;
    private static final int MISSING_DIGIT = -1;

    private static final String FAILURE_INPUT = "";
    private static final String UNKNOWN_NUM = "X";

    private static final String MESSAGE_FOR_BLANK = "";

    private static final String MESSAGE_FORMAT_FOR_REQUEST_INPUT = "%d桁の数字を入力して下さい：";
    private static final String MESSAGE_FORMAT_FOR_ERROR_FORMAT = "注意：%d桁の数字を入力してください。";

    private static final String MESSAGE_FORMAT_FOR_CORRECT = "おめでとう！%d回目で成功♪";
    private static final String MESSAGE_FORMAT_FOR_HIT_AND_BLOW_NUM = "ヒット：%d個、ブロー：%d個";

    private static final String MESSAGE_FOR_HINT = "ヒント：";

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

        hintDigits = 0;
    }

    private static void fin() {
        scanner.close();
    }

    // 関数

    private static void playHitAndBlow() {

        setCorrectNumber();

        int challengeCount = 0;
        GuessResult result = GuessResult.NONE;
        while (canChallengeNextGuessing(result)) {

            hintDigitsAddIfNeeded(challengeCount);
            dispHint();

            challengeCount++;

            String answer = getAnswer();

            CompareResult[] scoringArray = new CompareResult[DIGITS_NUM];
            scoringAnswer(answer, scoringArray);

            result = getResult(scoringArray);

            dispResult(result, challengeCount, scoringArray);
        }
    }

    private static void setCorrectNumber() {

        while (isLackedCorrectNumuber()) {

            String correctNum = String.valueOf(createRandomNumber(MIN_NUM, MAX_NUM));

            if (correct.indexOf(String.valueOf(correctNum)) < 0) {
                correct.add(correctNum);
            }
        }
    }

    private static boolean isLackedCorrectNumuber() {
        return correct.size() < DIGITS_NUM;
    }

    private static void hintDigitsAddIfNeeded(int challengeCount) {

        if (challengeCount == FIRST_CHALLENGE) {
            return;
        }

        if (challengeCount % HINT_ADD_INTERVAL == 0) {
            hintDigits++;
        }
    }

    private static void dispHint() {

        if (hintDigits == 0) {
            return;
        }

        print(MESSAGE_FOR_HINT);

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (digit < hintDigits) {
                print(correct.get(digit));
                continue;
            }

            print(UNKNOWN_NUM);
        }

        dipsBlankLine();
    }

    private static boolean canChallengeNextGuessing(GuessResult result) {
        return result != GuessResult.CORRECT;
    }

    private static String getAnswer() {

        String answer = FAILURE_INPUT;
        while (isInput(answer)) {

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

    private static boolean isInput(String answer) {
        return answer == null || answer.isBlank();
    }

    private static void dispErrorInput() {
        println(MESSAGE_FORMAT_FOR_ERROR_FORMAT, DIGITS_NUM);
        dipsBlankLine();
    }

    private static boolean isInRange(String input) {
        return input.length() == DIGITS_NUM;
    }

    private static void scoringAnswer(String answer, CompareResult[] scoringArray) {

        initScoringResult(scoringArray);

        markingHit(answer, scoringArray);

        markingBlow(answer, scoringArray);
    }

    private static void initScoringResult(CompareResult[] scoringArray) {

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            scoringArray[digit] = CompareResult.NONE;
        }
    }

    private static void markingHit(String answer, CompareResult[] scoringArray) {

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (answer.charAt(digit) == getCorrectCharAt(digit)) {

                scoringArray[digit] = CompareResult.HIT;
            }
        }
    }

    private static char getCorrectCharAt(int digit) {
        return correct.get(digit).charAt(0);
    }

    private static void markingBlow(String answer, CompareResult[] scoringArray) {

        List<Integer> isHitOrBlowList = new ArrayList<>();

        settingHit(isHitOrBlowList, scoringArray);

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (scoringArray[digit] == CompareResult.HIT) {
                continue;
            }

            char correctChar = getCorrectCharAt(digit);
            int blowDigit = searchCharDigit(answer, isHitOrBlowList, correctChar);

            if (blowDigit != MISSING_DIGIT) {
                isHitOrBlowList.add(blowDigit);
                scoringArray[digit] = CompareResult.BLOW;
            }
        }
    }

    private static void settingHit(List<Integer> isHitOrBlowList, CompareResult[] scoringArray) {

        for (int digit = 0; digit < DIGITS_NUM; digit++) {

            if (scoringArray[digit] == CompareResult.HIT) {
                isHitOrBlowList.add(digit);
            }
        }
    }

    private static int searchCharDigit(String answer, List<Integer> isHitOrBlowList, char correctChar) {

        int digit = MISSING_DIGIT;

        for (int targetDigit = 0; targetDigit < DIGITS_NUM; targetDigit++) {

            if (correctChar != answer.charAt(targetDigit)) {
                continue;
            }

            if (isHitOrBlowList.indexOf(targetDigit) >= 0) {
                continue;
            }

            digit = targetDigit;
            break;
        }

        return digit;
    }

    private static GuessResult getResult(CompareResult[] scoringArray) {

        for (CompareResult result : scoringArray) {

            if (result != CompareResult.HIT) {
                return GuessResult.INCORRECT;
            }
        }

        return GuessResult.CORRECT;
    }

    private static void dispResult(GuessResult result, int challengeCount, CompareResult[] scoringArray) {

        switch (result) {

        case CORRECT:
            dispCorrect(challengeCount);
            break;

        default:
        case INCORRECT:
            dispHitAndBlowNum(scoringArray);
            break;
        }
    }

    private static void dispCorrect(int challengeCount) {
        println(MESSAGE_FORMAT_FOR_CORRECT, challengeCount);
    }

    private static void dispHitAndBlowNum(CompareResult[] scoringArray) {

        int hit = getHitCount(scoringArray);
        int blow = getBlowCount(scoringArray);

        println(MESSAGE_FORMAT_FOR_HIT_AND_BLOW_NUM, hit, blow);
        dipsBlankLine();
    }

    private static int getHitCount(CompareResult[] scoringArray) {

        int hit = 0;
        for (CompareResult result : scoringArray) {

            if (result == CompareResult.HIT) {
                hit++;
            }
        }

        return hit;
    }

    private static int getBlowCount(CompareResult[] scoringArray) {

        int blow = 0;
        for (CompareResult result : scoringArray) {

            if (result == CompareResult.BLOW) {
                blow++;
            }
        }

        return blow;
    }

    // 汎用関数

    private static void print(String str) {
        System.out.print(str);
    }

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

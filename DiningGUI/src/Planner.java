import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Planner {
    static Vector state;
    static Vector operators;
    static Random rand;
    static Vector plan;
    static String hold;
    static ArrayList<String> doplan = new ArrayList<String>();
    static Hashtable re = new Hashtable();
    static ArrayList<String> name = new ArrayList<String>();
    static ArrayList<ArrayList<String>> namelist = new ArrayList<ArrayList<String>>();
    static String asfilename;

    public static void main(String argv[]) {
        (new Planner()).start();/*
                                 * Vector c =initInitialStatefile("state.txt");
								 * for(int i= 0;i<10;i++){ Vector b =
								 * plandoing(doplan.get(i),c);
								 * System.out.println(doplan.get(i));
								 * System.out.println(c); System.out.println(b);
								 * c = new Vector(b); }
								 */
    }

    Planner() {
        rand = new Random();
    }

    public static Vector plandoing(String plan1, Vector now) {
        Vector re = new Vector();
        Pattern pat1 = Pattern.compile("Eating philosopher (.+)");
        Matcher mat1 = pat1.matcher(plan1);
        Pattern pat2 = Pattern.compile("put fork (.+) down on the table for philosopher (.+) 's (.+) hand");
        Matcher mat2 = pat2.matcher(plan1);
        Pattern pat3 = Pattern.compile("pick up fork (.+) from the table for philosopher (.+) 's (.+) hand");
        Matcher mat3 = pat3.matcher(plan1);
        
        if (mat1.find()) { // Eating philosopher (.+)
            String x = mat1.group(1);
            
            for (int j = 0; j < now.size(); j++) { // delete
                String a = (String) now.get(j);
                boolean flag = true;
                if (flag) {
                    re.addElement(a);
                }
            }
            hold = "";
            // add
            re.addElement("philosopher " + x + " is eating");
//			System.out.println("1");
        } else if (mat2.find()) { // put fork (.+) down on the table for philosopher (.+) 's (.+) hand
            String x = mat2.group(1);
            String y = mat2.group(2);
            String z = mat2.group(3);

            String check = "philosopher " + y + " has fork " + x + " on the " + z + " hand";

            for (int j = 0; j < now.size(); j++) {//delete
                String a = (String) now.get(j);
                boolean flag = true;

                if (a.equals(check)) {
                    flag = false;
                }

                if (flag) {
                    re.addElement(a);
                }
            }
            hold = y;
            //add
            re.addElement("philosopher " + y + " has nothing on the " + z + " hand");
            re.addElement("ontable fork " + x);
//			System.out.println("2");
        } else if (mat3.find()) { // pick up fork (.+) from the table for philosopher (.+) 's (.+) hand
            String x = mat3.group(1);
            String y = mat3.group(2);
            String z = mat3.group(3);

            String check = "philosopher " + y + " has nothing on the " + z + " hand";
            String check1 = "ontable fork " + x;

            for (int j = 0; j < now.size(); j++) {//delete
                String a = (String) now.get(j);
                boolean flag = true;

                if (a.equals(check)) {
                    flag = false;
                }
                if (a.equals(check1)) {
                    flag = false;
                }

                if (flag) {
                    re.addElement(a);
                }
            }
            hold = x;
            //add
            re.addElement("philosopher " + y + " has fork " + x + " on the " + z + " hand");
//			System.out.println("3");
        }
        state.clear();
        for (int j = 0; j < re.size(); j++) {
            state.addElement(re.get(j));
        }
//		System.out.println("ok");
        return re;
    }

    public static ArrayList getblock(Vector initialState) {
        name.clear();
        hold = "";
        namelist.clear();
        for (int i = 0; i < initialState.size(); i++) {
            String state = (String) initialState.elementAt(i);
            Pattern pat1 = Pattern.compile("ontable (.+)");
            Matcher mat1 = pat1.matcher(state);
            Pattern pat2 = Pattern.compile("holding (.+)");
            Matcher mat2 = pat2.matcher(state);
            if (mat1.find()) {
                String name1 = mat1.group(1);
                if (!name.contains(name1)) {
                    name.add(name1);
                    ArrayList<String> n = new ArrayList<String>();
                    n.add(name1);
                    namelist.add(n);

                }
            } else if (mat2.find()) {
                String name1 = mat2.group(1);
                hold = name1;
                if (!name.contains(name1)) {
                    name.add(name1);
                }
            }
        }

        boolean check = true;
        while (check) {
            check = false;
            for (int i = 0; i < initialState.size(); i++) {
                String state = (String) initialState.elementAt(i);
                Pattern pat1 = Pattern.compile("(.+) on (.+)");
                Matcher mat1 = pat1.matcher(state);
                if (mat1.find()) {
                    String name1 = mat1.group(1);
                    String name2 = mat1.group(2);
                    if (!name.contains(name1)) {
                        for (int j = 0; j < namelist.size(); j++) {
                            ArrayList<String> a = namelist.get(j);
                            if (a.contains(name2) && !a.contains(name1)) {
                                namelist.remove(j);
                                check = true;
                                name.add(name1);
                                a.add(name1);
                                namelist.add(j, a);
                            }
                        }
                    }
                }
            }
        }
        for (int i = namelist.size(); i < name.size(); i++) {
            ArrayList<String> a = new ArrayList<String>();
            namelist.add(a);
        }
        return namelist;

    }

    public static void start2(String infile, String goalfile) {
        rand = new Random();
        asfilename = infile;
        initOperators();
        Vector goalList = initGoalListfile(goalfile);
        Vector initialState = initInitialStatefile(infile);

        Hashtable theBinding = new Hashtable();
        plan = new Vector();
        planning(goalList, initialState, theBinding);

        System.out.println("***** This is a plan! *****");
        // コピー
        re.clear();
        for (Enumeration e = theBinding.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String value = (String) theBinding.get(key);
            re.put(key, value);
        }
        doplan.clear();
        for (int i = 0; i < plan.size(); i++) {
            Operator op = (Operator) plan.elementAt(i);
            System.out.println((op.instantiate(theBinding)).name);
            doplan.add((op.instantiate(theBinding)).name);
        }
    }

    public static void restart(String goalfile, Vector list) {
        rand = new Random();
        initOperators();
        Vector goalList = initGoalListfile(goalfile);
        Vector initialState = list;
        re.clear();
        Hashtable theBinding = new Hashtable();
        plan = new Vector();
        planning(goalList, initialState, theBinding);
        System.out.println("***** This is a plan! *****");
        // コピー
        re.clear();
        for (Enumeration e = theBinding.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String value = (String) theBinding.get(key);
            re.put(key, value);
        }
        doplan.clear();
        for (int i = 0; i < plan.size(); i++) {
            Operator op = (Operator) plan.elementAt(i);
            System.out.println((op.instantiate(theBinding)).name);
            doplan.add((op.instantiate(theBinding)).name);
        }

    }


    public static boolean checkOrder(Vector nowState, String op) {
        Pattern pat1 = Pattern.compile("Eating philosopher (.+)");
        Matcher mat1 = pat1.matcher(op);
        Pattern pat2 = Pattern.compile("put fork (.+) down on the table for philosopher (.+) 's (.+) hand");
        Matcher mat2 = pat2.matcher(op);
        Pattern pat3 = Pattern.compile("pick up fork (.+) from the table for philosopher (.+) 's (.+) hand");
        Matcher mat3 = pat3.matcher(op);
        if (mat1.find()) { // Eating philosopher ?philosopherName
            String philosopherName = mat1.group(1);

            char c = philosopherName.toCharArray()[0];
            String leftForkNum = String.valueOf(c - 'A' + 1);
            String rightForkNum;
            if (c == 'A') {
                rightForkNum = String.valueOf(5); // ここの人数はあとで修正
            } else {
                rightForkNum = String.valueOf(c - 'A');
            }
            String check1 = "philosopher " + philosopherName + " has fork " + leftForkNum + " on the left hand";
            String check2 = "philosopher " + philosopherName + " has fork " + rightForkNum + " on the right hand";
            boolean flag1 = false;
            boolean flag2 = false;
            for (int j = 0; j < nowState.size(); j++) { // delete
                String a = (String) nowState.get(j);
                if (a.equals(check1)) {
                    flag1 = true;
                }
                if (a.equals(check2)) {
                    flag2 = true;
                }
            }
            return flag1 && flag2;
        } else if (mat2.find()) { // put fork ?forkNum down on the table for philosopher ?philosopherName 's ?lr hand
            String forkNum = mat2.group(1);
            String philosopherName = mat2.group(2);
            String lr = mat3.group(3);

            String check1 = "philosopher " + philosopherName + " has fork " + forkNum + " on the " + lr + " hand";
            boolean flag1 = false;
            for (int j = 0; j < nowState.size(); j++) {//delete
                String a = (String) nowState.get(j);
                if (a.equals(check1)) {
                    flag1 = true;
                }
            }
            return flag1;
        } else if (mat3.find()) { // pick up fork ?forkNum from the table for philosopher ?philosopherName 's ?lr hand
            String forkNum = mat3.group(1);
            String philosopherName = mat3.group(2);
            String lr = mat3.group(3);

            String check1 = "ontable fork " + forkNum;
            String check2 = "philosopher " + philosopherName + " has nothing on the " + lr + " hand";
            boolean flag1 = false;
            boolean flag2 = false;
            for (int j = 0; j < nowState.size(); j++) {//delete
                String a = (String) nowState.get(j);
                if (a.equals(check1)) {
                    flag1 = true;
                }
                if (a.equals(check2)) {
                    flag2 = true;
                }
            }
            return flag1 && flag2;
        }
        return false;
    }

    /////////////////////////////////////////////////////////////

    //新たに追加したメソッド

    //競合解消

    /////////////////////////////////////////////////////////////

    public static void ConfRes0(Vector<Operator> operators, String thegoal, Hashtable theBinding) {
        for (int i = 0; i < operators.size(); i++) {
            Hashtable fbind = new Hashtable();
            for (String add : operators.elementAt(i).addList) {
                if ((new Unifier()).unify(add, thegoal, theBinding)) {
                    Operator op = operators.elementAt(i);
                    operators.removeElementAt(i);
                    operators.add(0, op);
//	              System.out.println(fbind);
//	              break;

                }
            }
        }
    }

    public static Vector rename(Vector a, Hashtable b) {
        Vector re = new Vector();
        for (int i = 0; i < a.size(); i++) {
            String op = (String) a.elementAt(i);
            String memo = Operator.instantiateString(op, b);
            re.add(memo);
        }
        return re;
    }

    public static ArrayList<String> changeInitialState(
            ArrayList<String> initialState, String order) {
        if (order.startsWith("add")) {
            order = order.substring(4); // orderから"add"を削除
            // System.out.println(order);
            if (!initialState.contains(order)) {
                initialState.add(order);
            }
        } else if (order.startsWith("append")) {
            order = order.substring(7); // orderから"append"を削除
            // System.out.println(order);
            if (!initialState.contains(order)) {
                initialState.add(order);
            }
        } else if (order.startsWith("delete")) {
            order = order.substring(7); // orderから"delete"を削除
            if (initialState.contains(order)) {
                // System.out.println(order);
                initialState.remove(initialState.indexOf(order));
            }
        } else if (order.startsWith("remove")) {
            order = order.substring(7); // orderから"remove"を削除
            if (initialState.contains(order)) {
                // System.out.println(order);
                initialState.remove(initialState.indexOf(order));
            }
        }
        System.out.println(initialState);
        return initialState;
    }

    public static ArrayList<String> changeGoalList(ArrayList<String> goalList,
                                                   String order) {
        if (order.startsWith("add")) {
            order = order.substring(4); // orderから"add"を削除
            // System.out.println(order);
            if (!goalList.contains(order)) {
                goalList.add(order);
            }
        } else if (order.startsWith("append")) {
            order = order.substring(7); // orderから"append"を削除
            // System.out.println(order);
            if (!goalList.contains(order)) {
                goalList.add(order);
            }
        } else if (order.startsWith("delete")) {
            order = order.substring(7); // orderから"delete"を削除
            if (goalList.contains(order)) {
                // System.out.println(order);
                goalList.remove(goalList.indexOf(order));
            }
        } else if (order.startsWith("remove")) {
            order = order.substring(7); // orderから"remove"を削除
            if (goalList.contains(order)) {
                // System.out.println(order);
                goalList.remove(goalList.indexOf(order));
            }
        }
        System.out.println(goalList);
        return goalList;
    }

    public void start() {
        initOperators();
        Vector goalList = initGoalListfile("test.txt");
        Vector initialState = initInitialStatefile("state.txt");
        state = initInitialStatefile("state.txt");
        Hashtable theBinding = new Hashtable();
        plan = new Vector();
        planning(goalList, initialState, theBinding);

        System.out.println("***** This is a plan! *****");
        for (int i = 0; i < plan.size(); i++) {
            Operator op = (Operator) plan.elementAt(i);
            System.out.println((op.instantiate(theBinding)).name);
            doplan.add((op.instantiate(theBinding)).name);
        }
    }

    static int count = 0;

    private static boolean planning(Vector theGoalList, Vector theCurrentState,
                                    Hashtable theBinding) {
        count++;
        if (count > 1000) {
            System.out.println("ERROR OCCURED");
            System.exit(0);
        }
        System.out.println("*** GOALS ***" + theGoalList);
        if (theGoalList.size() == 1) {
            String aGoal = (String) theGoalList.elementAt(0);
            if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) {
                return true;
            } else {
                return false;
            }
        } else {
            String aGoal = (String) theGoalList.elementAt(0);
            int cPoint = 0; // 現在のゴールリスト内の探索場所
            while (cPoint < operators.size()) {
                // System.out.println("cPoint:"+cPoint);
                // Store original binding
                Hashtable orgBinding = new Hashtable(); // theBindingのコピーを生成
                for (Enumeration e = theBinding.keys(); e.hasMoreElements(); ) {
                    String key = (String) e.nextElement();
                    String value = (String) theBinding.get(key);
                    orgBinding.put(key, value);
                }
                Vector orgState = new Vector(); // CurrentStateのコピー
                for (int i = 0; i < theCurrentState.size(); i++) {
                    orgState.addElement(theCurrentState.elementAt(i));
                }

                int tmpPoint = planningAGoal(aGoal, theCurrentState,
                        theBinding, cPoint); // 失敗したら-1
                // System.out.println("tmpPoint: "+tmpPoint);

                if (tmpPoint != -1) {
                    theGoalList.removeElementAt(0);
                    System.out.print("theCurrentState:");
                    System.out.println(theCurrentState);
                    if (planning(theGoalList, theCurrentState, theBinding)) {
                        // System.out.println("Success !");
                        return true;
                    } else {
                        cPoint = tmpPoint;
                        // System.out.println("Fail::"+cPoint);
                        theGoalList.insertElementAt(aGoal, 0);

                        theBinding.clear();
                        for (Enumeration e = orgBinding.keys(); e
                                .hasMoreElements(); ) {
                            String key = (String) e.nextElement();
                            String value = (String) orgBinding.get(key);
                            theBinding.put(key, value);
                        }
                        theCurrentState.removeAllElements();
                        for (int i = 0; i < orgState.size(); i++) {
                            theCurrentState.addElement(orgState.elementAt(i));
                        }
                    }
                } else {
                    theBinding.clear();
                    for (Enumeration e = orgBinding.keys(); e.hasMoreElements(); ) {
                        String key = (String) e.nextElement();
                        String value = (String) orgBinding.get(key);
                        theBinding.put(key, value);
                    }
                    theCurrentState.removeAllElements();
                    for (int i = 0; i < orgState.size(); i++) {
                        theCurrentState.addElement(orgState.elementAt(i));
                    }
                    return false;
                }
                // System.out.println("test:");
            }
            return false;
        }
    }

    private static int planningAGoal(String theGoal, Vector theCurrentState,
                                     Hashtable theBinding, int cPoint) {
        System.out.println("**" + theGoal);
        int size = theCurrentState.size();
        // System.out.println("StateSize:"+size);
        for (int i = 0; i < size; i++) {
            String aState = (String) theCurrentState.elementAt(i); // 1つずつマッチング
            if ((new Unifier()).unify(theGoal, aState, theBinding)) {
                return 0; // 調査中のゴールが現在の状態にあれば成功
            }
        }
        // なければルールを使って分解する
        ConfRes0(operators, theGoal, theBinding);
        for (int i = cPoint; i < operators.size(); i++) { // オペレーター全てを回す
            Operator anOperator = rename((Operator) operators.elementAt(i));
            // 現在のCurrent state, Binding, planをbackup
            Hashtable orgBinding = new Hashtable();
            for (Enumeration e = theBinding.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                String value = (String) theBinding.get(key);
                orgBinding.put(key, value);
            }
            Vector orgState = new Vector();
            for (int j = 0; j < theCurrentState.size(); j++) {
                orgState.addElement(theCurrentState.elementAt(j));
            }
            Vector orgPlan = new Vector();
            for (int j = 0; j < plan.size(); j++) {
                orgPlan.addElement(plan.elementAt(j));
            }

            Vector addList = (Vector) anOperator.getAddList(); // オペレーターのADDリストを入手
            for (int j = 0; j < addList.size(); j++) {
                if ((new Unifier()).unify(theGoal,
                        (String) addList.elementAt(j), theBinding)) { // 今のオペレータと今のゴールが一致したら
                    // System.out.print("Addlistelementat:");
                    // System.out.println(addList.elementAt(j));
                    // System.out.println("Cdlistelementat:");
                    // System.out.println(anOperator);
                    Operator newOperator = anOperator.instantiate(theBinding);
                    // System.out.println("NewAddlistelementat:");
                    // System.out.println(newOperator);
                    Vector newGoals = (Vector) newOperator.getIfList();
                    System.out.print("Newname:");
                    System.out.println(newOperator.name);
                    if (planning(newGoals, theCurrentState, theBinding)) { // &checkA(newOperator.name)
                        System.out.print("Ewname:");
                        System.out.println(newOperator.name);
                        newOperator = newOperator.instantiate(theBinding);
                        plan.addElement(newOperator);
                        theCurrentState = newOperator
                                .applyState(theCurrentState);
                        return i + 1;
                    } else {
                        // 失敗したら元に戻す．
                        theBinding.clear();
                        for (Enumeration e = orgBinding.keys(); e
                                .hasMoreElements(); ) {
                            String key = (String) e.nextElement();
                            String value = (String) orgBinding.get(key);
                            theBinding.put(key, value);
                        }
                        theCurrentState.removeAllElements();
                        for (int k = 0; k < orgState.size(); k++) {
                            theCurrentState.addElement(orgState.elementAt(k));
                        }
                        plan.removeAllElements();
                        for (int k = 0; k < orgPlan.size(); k++) {
                            plan.addElement(orgPlan.elementAt(k));
                        }
                    }
                }
            }
        }
        return -1;
    }

    static int uniqueNum = 0;

    private static Operator rename(Operator theOperator) {
        Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
        uniqueNum = uniqueNum + 1;
        return newOperator;
    }

    private Vector initGoalList() {
        Vector goalList = new Vector();

        goalList.addElement("philosopher A is eating");
        goalList.addElement("philosopher C is eating");
        return goalList;
    }

    public static Vector initGoalListfile(String fileName) {
        Vector goalList = new Vector();
        try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

            // 文字コードを指定してBufferedReaderオブジェクトを作る
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            // 変数lineに1行ずつ読み込むfor文
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                goalList.addElement(line);
            }

        } catch (IOException e) {
            e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
        }
        Vector onGoalList = new Vector();
        Vector otherList = new Vector();
        Vector finalGoalList = new Vector();

        return goalList;

    }

    private Vector initInitialState() {
        Vector initialState = new Vector();

        initialState.addElement("philosopher A has nothing on the left hand");
        initialState.addElement("philosopher A has nothing on the right hand");
        initialState.addElement("philosopher B has fork 2 on the left hand");
        initialState.addElement("philosopher B has fork 1 on the right hand");
        initialState.addElement("philosopher C has nothing on the left hand");
        initialState.addElement("philosopher C has nothing on the right hand");
        initialState.addElement("philosopher D has nothing on the left hand");
        initialState.addElement("philosopher D has nothing on the right hand");
        initialState.addElement("philosopher E has nothing on the left hand");
        initialState.addElement("philosopher E has nothing on the right hand");

        //		initialState.addElement("clear D");
//		initialState.addElement("clear E");

        initialState.addElement("ontable fork 3");
        initialState.addElement("ontable fork 4");
        initialState.addElement("ontable fork 5");
        return initialState;
    }

    public static Vector initInitialStatefile(String fileName) {
        Vector initialState = new Vector();
        try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

            // 文字コードを指定してBufferedReaderオブジェクトを作る
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            // 変数lineに1行ずつ読み込むfor文
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                initialState.addElement(line);
            }

        } catch (IOException e) {
            e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
        }
        return initialState;
    }
    
    private static int countPhilosopherNum(String fileName) {
        int philosopherCnt = 0; // 哲学者の人数をカウント

        try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

            // 文字コードを指定してBufferedReaderオブジェクトを作る
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            // 変数lineに1行ずつ読み込むfor文
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                Pattern pat1 = Pattern.compile("philosopher (.+) has nothing on the (.+) hand");
                Matcher mat1 = pat1.matcher(line);
                Pattern pat2 = Pattern.compile("philosopher (.+) has fork (.+) on the (.+) hand");
                Matcher mat2 = pat2.matcher(line);
                if (mat1.find() || mat2.find()) {
                    philosopherCnt++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
        }

        return philosopherCnt / 2; // 哲学者の人数を返す
    }

    private static void initOperators() {
//        String fileName = "state.txt";
//        String fileName = GraphFrame.asfilename.getText();
    	String fileName = asfilename;
        int num = countPhilosopherNum(fileName);
        operators = new Vector();

        for (int i = 0; i < num; i++) {
            // アルファベットの生成
            char c = (char) ('A' + i);
            String philosopherName = String.valueOf(c);

            String name = new String("Eating philosopher " + philosopherName);

            Vector ifList = new Vector();
            String leftForkNum = String.valueOf(i + 1);
            String rightForkNum;
            if (i == 0) {
                rightForkNum = String.valueOf(num);
            } else {
                rightForkNum = String.valueOf(i);
            }
            ifList.addElement(new String("philosopher " + philosopherName + " has fork " + leftForkNum + " on the left hand"));
            ifList.addElement(new String("philosopher " + philosopherName + " has fork " + rightForkNum + " on the right hand"));

            Vector addList = new Vector();
            addList.addElement(new String("philosopher " + philosopherName + " is eating"));

            Vector deleteList = new Vector();

            Operator operator = new Operator(name, ifList, addList, deleteList);

            operators.addElement(operator);
        }

        // OPERATOR 2
        /// NAME
        String name2 = new String("put fork ?y down on the table for philosopher ?x 's ?a hand");
        /// IF
        Vector ifList2 = new Vector();
        ifList2.addElement(new String("philosopher ?x has fork ?y on the ?a hand"));
        /// ADD-LIST
        Vector addList2 = new Vector();
        addList2.addElement(new String("philosopher ?x has nothing on the ?a hand"));
        addList2.addElement(new String("ontable fork ?y"));
        /// DELETE-LIST
        Vector deleteList2 = new Vector();
        deleteList2.addElement(new String("philosopher ?x has fork ?y on the ?a hand"));

        Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);

        operators.addElement(operator2);

        // OPERATOR 3
        /// NAME
        String name3 = new String("pick up fork ?y from the table for philosopher ?x 's ?a hand");
        /// IF
        Vector ifList3 = new Vector();
        ifList3.addElement(new String("ontable fork ?y"));
        ifList3.addElement(new String("philosopher ?x has nothing on the ?a hand"));
        /// ADD-LIST
        Vector addList3 = new Vector();
        addList3.addElement(new String("philosopher ?x has fork ?y on the ?a hand"));
        /// DELETE-LIST
        Vector deleteList3 = new Vector();
        deleteList3.addElement(new String("ontable fork ?y"));
        deleteList3.addElement(new String("philosopher ?x has nothing on the ?a hand"));

        Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);

        operators.addElement(operator3);

    }
}

class Operator {
    String name;
    Vector<String> ifList;
    Vector<String> addList;
    Vector<String> deleteList;

    Operator(String theName, Vector theIfList, Vector theAddList,
             Vector theDeleteList) {
        name = theName;
        ifList = theIfList;
        addList = theAddList;
        deleteList = theDeleteList;
    }

    public Vector getAddList() {
        return addList;
    }

    public Vector getDeleteList() {
        return deleteList;
    }

    public Vector getIfList() {
        return ifList;
    }

    public String toString() {
        String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n"
                + "ADD:" + addList + "\n" + "DELETE:" + deleteList;
        return result;
    }

    public Vector applyState(Vector theState) {
        for (int i = 0; i < addList.size(); i++) {
            theState.addElement(addList.elementAt(i));
        }
        for (int i = 0; i < deleteList.size(); i++) {
            theState.removeElement(deleteList.elementAt(i));
        }
        return theState;
    }

    public Operator getRenamedOperator(int uniqueNum) {
        Vector vars = new Vector();
        // IfListの変数を集める
        for (int i = 0; i < ifList.size(); i++) {
            String anIf = (String) ifList.elementAt(i);
            vars = getVars(anIf, vars);
        }
        // addListの変数を集める
        for (int i = 0; i < addList.size(); i++) {
            String anAdd = (String) addList.elementAt(i);
            vars = getVars(anAdd, vars);
        }
        // deleteListの変数を集める
        for (int i = 0; i < deleteList.size(); i++) {
            String aDelete = (String) deleteList.elementAt(i);
            vars = getVars(aDelete, vars);
        }
        Hashtable renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

        // 新しいIfListを作る
        Vector newIfList = new Vector();
        for (int i = 0; i < ifList.size(); i++) {
            String newAnIf = renameVars((String) ifList.elementAt(i),
                    renamedVarsTable);
            newIfList.addElement(newAnIf);
        }
        // 新しいaddListを作る
        Vector newAddList = new Vector();
        for (int i = 0; i < addList.size(); i++) {
            String newAnAdd = renameVars((String) addList.elementAt(i),
                    renamedVarsTable);
            newAddList.addElement(newAnAdd);
        }
        // 新しいdeleteListを作る
        Vector newDeleteList = new Vector();
        for (int i = 0; i < deleteList.size(); i++) {
            String newADelete = renameVars((String) deleteList.elementAt(i),
                    renamedVarsTable);
            newDeleteList.addElement(newADelete);
        }
        // 新しいnameを作る
        String newName = renameVars(name, renamedVarsTable);

        return new Operator(newName, newIfList, newAddList, newDeleteList);
    }

    private Vector getVars(String thePattern, Vector vars) {
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                vars.addElement(tmp);
            }
        }
        return vars;
    }

    private Hashtable makeRenamedVarsTable(Vector vars, int uniqueNum) {
        Hashtable result = new Hashtable();
        for (int i = 0; i < vars.size(); i++) {
            String newVar = (String) vars.elementAt(i) + uniqueNum;
            result.put((String) vars.elementAt(i), newVar);
        }
        return result;
    }

    private String renameVars(String thePattern, Hashtable renamedVarsTable) {
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                result = result + " " + (String) renamedVarsTable.get(tmp);
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    public Operator instantiate(Hashtable theBinding) {
        // name を具体化
        String newName = instantiateString(name, theBinding);
        // ifList を具体化
        Vector newIfList = new Vector();
        for (int i = 0; i < ifList.size(); i++) {
            String newIf = instantiateString((String) ifList.elementAt(i),
                    theBinding);
            newIfList.addElement(newIf);
        }
        // addList を具体化
        Vector newAddList = new Vector();
        for (int i = 0; i < addList.size(); i++) {
            String newAdd = instantiateString((String) addList.elementAt(i),
                    theBinding);
            newAddList.addElement(newAdd);
        }
        // deleteListを具体化
        Vector newDeleteList = new Vector();
        for (int i = 0; i < deleteList.size(); i++) {
            String newDelete = instantiateString(
                    (String) deleteList.elementAt(i), theBinding);
            newDeleteList.addElement(newDelete);
        }
        return new Operator(newName, newIfList, newAddList, newDeleteList);
    }

    public static String instantiateString(String thePattern,
                                           Hashtable theBinding) {
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                String newString = (String) theBinding.get(tmp);
                if (newString == null) {
                    result = result + " " + tmp;
                } else {
                    result = result + " " + newString;
                }
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private static boolean var(String str1) {
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }
}

class Unifier {
    StringTokenizer st1;
    String buffer1[];
    StringTokenizer st2;
    String buffer2[];
    Hashtable vars;

    Unifier() {
        // vars = new Hashtable();
    }

    public boolean unify(String string1, String string2, Hashtable theBindings) {
        Hashtable orgBindings = new Hashtable();
        for (Enumeration e = theBindings.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String value = (String) theBindings.get(key);
            orgBindings.put(key, value);
        }
        this.vars = theBindings;
        if (unify(string1, string2)) {
            return true;
        } else {
            // 失敗したら元に戻す．
            theBindings.clear();
            for (Enumeration e = orgBindings.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                String value = (String) orgBindings.get(key);
                theBindings.put(key, value);
            }
            return false;
        }
    }

    public boolean unify(String string1, String string2) {
        // 同じなら成功
        if (string1.equals(string2))
            return true;

        // 各々トークンに分ける
        st1 = new StringTokenizer(string1);
        st2 = new StringTokenizer(string2);

        // 数が異なったら失敗
        if (st1.countTokens() != st2.countTokens())
            return false;

        // 定数同士
        int length = st1.countTokens();
        buffer1 = new String[length];
        buffer2 = new String[length];
        for (int i = 0; i < length; i++) {
            buffer1[i] = st1.nextToken();
            buffer2[i] = st2.nextToken();
        }

        // 初期値としてバインディングが与えられていたら
        if (this.vars.size() != 0) {
            for (Enumeration keys = vars.keys(); keys.hasMoreElements(); ) {
                String key = (String) keys.nextElement();
                String value = (String) vars.get(key);
                replaceBuffer(key, value);
            }
        }

        for (int i = 0; i < length; i++) {
            if (!tokenMatching(buffer1[i], buffer2[i])) {
                return false;
            }
        }

        return true;
    }

    boolean tokenMatching(String token1, String token2) {
        if (token1.equals(token2))
            return true;
        if (var(token1) && !var(token2))
            return varMatching(token1, token2);
        if (!var(token1) && var(token2))
            return varMatching(token2, token1);
        if (var(token1) && var(token2))
            return varMatching(token1, token2);
        return false;
    }

    boolean varMatching(String vartoken, String token) {
        if (vars.containsKey(vartoken)) {
            if (token.equals(vars.get(vartoken))) {
                return true;
            } else {
                return false;
            }
        } else {
            replaceBuffer(vartoken, token);
            if (vars.contains(vartoken)) {
                replaceBindings(vartoken, token);
            }
            vars.put(vartoken, token);
        }
        return true;
    }

    void replaceBuffer(String preString, String postString) {
        for (int i = 0; i < buffer1.length; i++) {
            if (preString.equals(buffer1[i])) {
                buffer1[i] = postString;
            }
            if (preString.equals(buffer2[i])) {
                buffer2[i] = postString;
            }
        }
    }

    void replaceBindings(String preString, String postString) {
        Enumeration keys;
        for (keys = vars.keys(); keys.hasMoreElements(); ) {
            String key = (String) keys.nextElement();
            if (preString.equals(vars.get(key))) {
                vars.put(key, postString);
            }
        }
    }

    boolean var(String str1) {
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }

}

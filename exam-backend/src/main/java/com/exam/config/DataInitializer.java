package com.exam.config;

import com.exam.entity.ExamPaper;
import com.exam.entity.Question;
import com.exam.entity.Student;
import com.exam.entity.Teacher;
import com.exam.repository.ExamPaperRepository;
import com.exam.repository.QuestionRepository;
import com.exam.repository.StudentRepository;
import com.exam.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final ExamPaperRepository paperRepository;
    private final TeacherRepository teacherRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(StudentRepository sr, QuestionRepository qr, ExamPaperRepository pr,
                           TeacherRepository tr) {
        this.studentRepository = sr;
        this.questionRepository = qr;
        this.paperRepository = pr;
        this.teacherRepository = tr;
    }

    @Override
    public void run(String... args) {
        // 教师账号始终初始化（与学生的早期返回无关）
        if (teacherRepository.count() == 0) {
            Teacher admin = new Teacher("admin", "管理员", passwordEncoder.encode("123456"));
            teacherRepository.save(admin);
        }

        if (studentRepository.count() > 0) return;

        // 测试学生
        Student s1 = new Student("2024001", "张三", passwordEncoder.encode("123456"), "计科一班");
        Student s2 = new Student("2024002", "李四", passwordEncoder.encode("123456"), "计科一班");
        studentRepository.save(s1);
        studentRepository.save(s2);

        // ========== Java 选择题 20道 ==========
        addChoice("Java", "Java 中 int 占几个字节？", "2", "4", "8", "1", "B", 5);
        addChoice("Java", "以下哪个不是 Java 关键字？", "class", "void", "main", "public", "C", 5);
        addChoice("Java", "Java 源文件扩展名是？", ".class", ".java", ".jar", ".txt", "B", 5);
        addChoice("Java", "合法的变量名是？", "2name", "_name", "class", "float", "B", 5);
        addChoice("Java", "数组下标从几开始？", "1", "2", "-1", "0", "D", 5);
        addChoice("Java", "哪个包是自动导入的？", "java.util", "java.io", "java.lang", "java.net", "C", 5);
        addChoice("Java", "JVM 代表什么？", "Java Very Machine", "Java Virtual Machine", "Java Visual Machine", "Java Variable Method", "B", 5);
        addChoice("Java", "访问修饰符哪个范围最小？", "public", "protected", "default", "private", "D", 5);
        addChoice("Java", "子类继承父类用什么关键字？", "implements", "extends", "inherit", "super", "B", 5);
        addChoice("Java", "Math.random() 返回范围是？", "[0,1)", "[0,1]", "(0,1)", "(0,1]", "A", 5);
        addChoice("Java", "String 类在哪个包？", "java.util", "java.lang", "java.io", "java.text", "B", 5);
        addChoice("Java", "List 接口的实现类是？", "HashMap", "ArrayList", "HashSet", "TreeMap", "B", 5);
        addChoice("Java", "线程创建方式不包括？", "继承Thread", "实现Runnable", "实现Callable", "继承Process", "D", 5);
        addChoice("Java", "JDK 全称？", "Java Development Kit", "Java Deployment Kit", "Java Design Kit", "Java Debug Kit", "A", 5);
        addChoice("Java", "HashMap 允许 null 键吗？", "不允许", "允许一个", "允许多个", "编译错误", "B", 5);
        addChoice("Java", "异常处理关键字是？", "throw", "try-catch", "error", "以上都是", "D", 5);
        addChoice("Java", "final 修饰的类？", "可被继承", "不可被继承", "可被重写", "无影响", "B", 5);
        addChoice("Java", "抽象类的关键字是？", "final", "static", "abstract", "interface", "C", 5);
        addChoice("Java", "boolean 类型占几个字节？", "1", "2", "4", "JVM决定", "D", 5);
        addChoice("Java", "接口中方法默认修饰符？", "private", "protected", "public abstract", "final", "C", 5);

        // ========== Java 填空题 10道 ==========
        addBlank("Java", "HTML 的中文全称是______。", "超文本标记语言", 4);
        addBlank("Java", "Java 中所有类的父类是______。", "Object", 4);
        addBlank("Java", "JDK 的中文全称是______。", "Java开发工具包", 4);
        addBlank("Java", "声明常量用______关键字。", "final", 4);
        addBlank("Java", "二进制 1010 对应十进制______。", "10", 4);
        addBlank("Java", "Java 编译器命令是______。", "javac", 4);
        addBlank("Java", "接口用______关键字声明。", "interface", 4);
        addBlank("Java", "数组长度用______属性获取。", "length", 4);
        addBlank("Java", "字符串比较用______方法。", "equals", 4);
        addBlank("Java", "多态的三个条件：继承、重写、______。", "父类引用指向子类对象", 4);

        // ========== Java 简答题 5道 ==========
        addEssay("Java", "请简述面向对象的三大特性。", "封装、继承、多态。封装隐藏内部实现，继承实现代码复用，多态允许同一接口不同实现。", 15);
        addEssay("Java", "解释接口和抽象类的区别。", "接口只能定义抽象方法（Java8后可默认方法），抽象类可有具体方法；一个类可实现多个接口，只能继承一个抽象类。", 15);
        addEssay("Java", "什么是 Java 虚拟机？", "JVM 是运行 Java 字节码的虚拟机，实现平台无关性。包含类加载器、运行时数据区、执行引擎等。", 15);
        addEssay("Java", "简述 finally 的作用。", "finally 块中的代码无论是否发生异常都会执行，通常用于释放资源（关闭文件、数据库连接等）。", 15);
        addEssay("Java", "解释方法重载和重写的区别。", "重载：同一类中方法名相同、参数列表不同。重写：子类重新定义父类方法，方法签名相同。", 15);

        // ========== 高数选择题 20道 ==========
        addChoice("高数", "lim(x→0) sinx/x = ?", "0", "1", "∞", "-1", "B", 5);
        addChoice("高数", "f(x)=x²的导数是？", "x", "2x", "x²", "2", "B", 5);
        addChoice("高数", "∫ x dx = ?", "x²/2+C", "x²+C", "2x+C", "1+C", "A", 5);
        addChoice("高数", "e^0 等于？", "0", "1", "e", "∞", "B", 5);
        addChoice("高数", "ln 1 等于？", "0", "1", "e", "-1", "A", 5);
        addChoice("高数", "cos 0 等于？", "0", "1", "-1", "1/2", "B", 5);
        addChoice("高数", "函数 y=|x| 在 x=0 处？", "可导", "连续不可导", "不连续", "无定义", "B", 5);
        addChoice("高数", "∫₀¹ x dx = ?", "1", "0.5", "2", "0", "B", 5);
        addChoice("高数", "d/dx (e^x) = ?", "e^x", "xe^x", "1", "e", "A", 5);
        addChoice("高数", "二阶可导函数的拐点满足？", "f'(x)=0", "f''(x)=0", "f(x)=0", "f'(x)>0", "B", 5);
        addChoice("高数", "lim(x→∞) 1/x = ?", "∞", "1", "0", "-1", "C", 5);
        addChoice("高数", "级数 Σ(1/n²) 是否收敛？", "收敛", "发散", "条件收敛", "不确定", "A", 5);
        addChoice("高数", "f(x)=x³的极值点个数？", "0", "1", "2", "3", "A", 5);
        addChoice("高数", "∫ sinx dx = ?", "cosx+C", "-cosx+C", "sinx+C", "-sinx+C", "B", 5);
        addChoice("高数", "曲线 y=x² 在点(1,1)处切线斜率？", "1", "2", "0", "-1", "B", 5);
        addChoice("高数", "f(x)=lnx 定义域？", "x≥0", "x>0", "x≠0", "全体实数", "B", 5);
        addChoice("高数", "罗尔定理条件不包括？", "闭区间连续", "开区间可导", "端点函数值相等", "函数单调", "D", 5);
        addChoice("高数", "∫ (1/x) dx = ?", "lnx+C", "1+C", "x+C", "e^x+C", "A", 5);
        addChoice("高数", "二重积分表示？", "曲线长度", "曲边梯形面积", "曲顶柱体体积", "曲面面积", "C", 5);
        addChoice("高数", "微分方程 y'=y 的通解？", "y=Ce^x", "y=Cx", "y=x+C", "y=e^x+C", "A", 5);

        // ========== 高数填空题 10道 ==========
        addBlank("高数", "导数 d/dx (x³) = ______。", "3x²", 4);
        addBlank("高数", "∫₀² x dx = ______。", "2", 4);
        addBlank("高数", "lim(x→0) (1-cosx)/x² = ______。", "1/2", 4);
        addBlank("高数", "函数 f(x)=x²-4x+3 的最小值是______。", "-1", 4);
        addBlank("高数", "e 的近似值是______（两位小数）。", "2.72", 4);
        addBlank("高数", "d/dx (lnx) = ______。", "1/x", 4);
        addBlank("高数", "∫ cosx dx = ______ + C。", "sinx", 4);
        addBlank("高数", "f''(x) > 0 表示函数是______的（凹/凸）。", "凹", 4);
        addBlank("高数", "tanx 的导数是______。", "sec²x", 4);
        addBlank("高数", "0/0 型未定式可用______法则。", "洛必达", 4);

        // ========== 高数简答题 3道 ==========
        addEssay("高数", "请解释函数连续的定义。", "函数在一点连续指该点极限存在且等于函数值：lim(x→a) f(x) = f(a)。", 15);
        addEssay("高数", "什么是定积分？它的几何意义是什么？", "定积分是函数在闭区间上的积分和的极限。几何意义：曲线与 x 轴围成的有向面积。", 15);
        addEssay("高数", "简述拉格朗日中值定理。", "若 f(x) 在[a,b]连续，在(a,b)可导，则存在 ξ∈(a,b) 使 f'(ξ)=[f(b)-f(a)]/(b-a)。", 15);

        // ========== 英语选择题 20道 ==========
        addChoice("英语", "She ___ to school every day.", "go", "goes", "going", "gone", "B", 5);
        addChoice("英语", "I have ___ finished my homework.", "yet", "already", "still", "ever", "B", 5);
        addChoice("英语", "There ___ many books on the desk.", "is", "are", "was", "be", "B", 5);
        addChoice("英语", "He is ___ than his brother.", "tall", "taller", "tallest", "more tall", "B", 5);
        addChoice("英语", "\"apple\" 的意思是？", "香蕉", "苹果", "橘子", "葡萄", "B", 5);
        addChoice("英语", "Would you like ___ tea?", "some", "any", "a", "an", "A", 5);
        addChoice("英语", "The book ___ by Mark Twain.", "wrote", "was written", "is writing", "writes", "B", 5);
        addChoice("英语", "I look forward to ___ you.", "see", "seeing", "seen", "saw", "B", 5);
        addChoice("英语", "Not only he but also I ___ interested.", "is", "am", "are", "be", "B", 5);
        addChoice("英语", "\"abandon\" 意思是？", "接受", "放弃", "遵守", "到达", "B", 5);
        addChoice("英语", "He asked me ___ I had been there.", "that", "if", "what", "which", "B", 5);
        addChoice("英语", "It is no use ___ over spilt milk.", "cry", "crying", "cried", "to cry", "B", 5);
        addChoice("英语", "The number of students ___ increasing.", "is", "are", "were", "have been", "A", 5);
        addChoice("英语", "I wish I ___ a bird.", "am", "was", "were", "be", "C", 5);
        addChoice("英语", "\"environment\" 意思是？", "政府", "环境", "经济", "教育", "B", 5);
        addChoice("英语", "Hardly had he arrived ___ it began to rain.", "than", "when", "then", "while", "B", 5);
        addChoice("英语", "This is the house ___ I lived.", "which", "that", "where", "what", "C", 5);
        addChoice("英语", "You'd better ___ now.", "go", "to go", "going", "went", "A", 5);
        addChoice("英语", "Neither of them ___ a teacher.", "is", "are", "were", "have", "A", 5);
        addChoice("英语", "\"opportunity\" 意思是？", "困难", "机会", "责任", "能力", "B", 5);

        // ========== 英语填空题 10道 ==========
        addBlank("英语", "\"Hello\" 的中文意思是______。", "你好", 4);
        addBlank("英语", "\"Thank you\" 的中文意思是______。", "谢谢你", 4);
        addBlank("英语", "I ___ (be) a student. 填适当形式。", "am", 4);
        addBlank("英语", "She ___ (have) a cat. 填适当形式。", "has", 4);
        addBlank("英语", "The past tense of \"go\" is ______.", "went", 4);
        addBlank("英语", "\"Good morning\" 的中文意思是______。", "早上好", 4);
        addBlank("英语", "\"university\" 前面用 a 还是 an？______。", "a", 4);
        addBlank("英语", "He is good ___ English. 填介词。", "at", 4);
        addBlank("英语", "The plural of \"child\" is ______.", "children", 4);
        addBlank("英语", "\"important\" 的中文意思是______。", "重要的", 4);

        // ========== 英语简答题 3道 ==========
        addEssay("英语", "请用英语写一段自我介绍（不少于30词）。", "Hello, my name is...（参考答案不固定，根据内容完整性评分）", 15);
        addEssay("英语", "What are your hobbies? Write in English.", "I like reading, playing sports, and listening to music.（参考答案不固定）", 15);
        addEssay("英语", "翻译：知识就是力量。", "Knowledge is power.", 15);

        // ========== 数据结构选择题 15道 ==========
        addChoice("数据结构", "栈的特点是什么？", "先进先出", "先进后出", "随机存取", "按需存取", "B", 5);
        addChoice("数据结构", "队列的特点是什么？", "先进先出", "先进后出", "随机存取", "按需存取", "A", 5);
        addChoice("数据结构", "二分查找要求数据？", "无序", "有序", "链表存储", "任意", "B", 5);
        addChoice("数据结构", "链表比数组的优势是？", "随机访问", "插入删除快", "节省内存", "查找快", "B", 5);
        addChoice("数据结构", "完全二叉树的叶子结点在？", "最上层", "最下层", "任意层", "中间层", "B", 5);
        addChoice("数据结构", "冒泡排序时间复杂度？", "O(n)", "O(nlogn)", "O(n²)", "O(1)", "C", 5);
        addChoice("数据结构", "哪种排序是不稳定的？", "冒泡排序", "插入排序", "选择排序", "归并排序", "D", 5);
        addChoice("数据结构", "哈希表解决冲突常用方法？", "排序", "链地址法", "二分查找", "递归", "B", 5);
        addChoice("数据结构", "深度优先遍历用到的辅助结构？", "队列", "栈", "数组", "链表", "B", 5);
        addChoice("数据结构", "二叉搜索树左子树的值？", "大于根", "小于根", "等于根", "任意", "B", 5);
        addChoice("数据结构", "n 个节点的二叉树最小高度？", "n", "log₂(n+1)", "n/2", "1", "B", 5);
        addChoice("数据结构", "快速排序平均时间复杂度？", "O(n)", "O(nlogn)", "O(n²)", "O(logn)", "B", 5);
        addChoice("数据结构", "广度优先遍历用到的辅助结构？", "队列", "栈", "数组", "链表", "A", 5);
        addChoice("数据结构", "图的邻接矩阵空间复杂度？", "O(n)", "O(n²)", "O(n+e)", "O(e)", "B", 5);
        addChoice("数据结构", "堆是一种什么结构？", "线性表", "完全二叉树", "普通树", "图", "B", 5);

        // ========== 数据结构填空题 8道 ==========
        addBlank("数据结构", "栈的操作原则是______（四个字）。", "先进后出", 4);
        addBlank("数据结构", "队列的操作原则是______（四个字）。", "先进先出", 4);
        addBlank("数据结构", "线性表的两种存储结构是顺序存储和______。", "链式存储", 4);
        addBlank("数据结构", "二叉树第 i 层最多有______个结点。", "2^(i-1)", 4);
        addBlank("数据结构", "深度为 k 的二叉树最多有______个结点。", "2^k-1", 4);
        addBlank("数据结构", "图的遍历方式有深度优先和______。", "广度优先", 4);
        addBlank("数据结构", "n 个元素进行冒泡排序最多比较______次。", "n(n-1)/2", 4);
        addBlank("数据结构", "链表结点包含数据域和______域。", "指针", 4);

        // ========== 数据结构简答题 3道 ==========
        addEssay("数据结构", "简述栈和队列的区别。", "栈是先进后出（LIFO），只在一端操作；队列是先进先出（FIFO），一端入一端出。", 15);
        addEssay("数据结构", "什么是二叉搜索树？", "二叉搜索树是左子树所有节点值小于根、右子树所有节点值大于根的二叉树，支持快速查找。", 15);
        addEssay("数据结构", "简述图的深度优先遍历算法思想。", "从起始顶点出发，沿一条路径深入访问直到无法继续，然后回溯到上一顶点继续，用栈或递归实现。", 15);

        // ========== 判断题（4科） ==========
        addJudge("Java", "Java 是面向对象的编程语言。", "T", 4);
        addJudge("Java", "int 是引用数据类型。", "F", 4);
        addJudge("Java", "Java 支持多继承。", "F", 4);
        addJudge("Java", "static 方法可以直接用类名调用。", "T", 4);
        addJudge("Java", "构造方法可以有返回值。", "F", 4);
        addJudge("Java", "接口中的方法默认是 public。", "T", 4);
        addJudge("Java", "ArrayList 是线程安全的。", "F", 4);
        addJudge("Java", "finally 块中的代码一定会执行。", "T", 4);
        addJudge("高数", "可导函数一定连续。", "T", 4);
        addJudge("高数", "连续函数一定可导。", "F", 4);
        addJudge("高数", "sin²x + cos²x = 1。", "T", 4);
        addJudge("高数", "lim(x→0) sinx = 1。", "F", 4);
        addJudge("高数", "定积分的值可能为负。", "T", 4);
        addJudge("高数", "f''(x)>0 表示函数是凸的。", "F", 4);
        addJudge("高数", "e^x 的导数等于本身。", "T", 4);
        addJudge("高数", "级数 Σ(1/n) 是收敛的。", "F", 4);
        addJudge("英语", "\"apple\" 的意思是苹果。", "T", 4);
        addJudge("英语", "\"dog\" 的意思是猫。", "F", 4);
        addJudge("英语", "过去式中 go 变为 went。", "T", 4);
        addJudge("英语", "英语中形容词可以修饰动词。", "F", 4);
        addJudge("英语", "\"a\" 用在元音音素开头的单词前。", "F", 4);
        addJudge("英语", "现在进行时形式是 be + doing。", "T", 4);
        addJudge("英语", "\"Thank you\" 回答是 \"I'm sorry\"。", "F", 4);
        addJudge("英语", "英语句首字母要大写。", "T", 4);
        addJudge("数据结构", "栈是先进先出的数据结构。", "F", 4);
        addJudge("数据结构", "队列是先进先出的数据结构。", "T", 4);
        addJudge("数据结构", "二叉树每个节点最多有两个子节点。", "T", 4);
        addJudge("数据结构", "链表支持随机访问。", "F", 4);
        addJudge("数据结构", "冒泡排序是稳定排序。", "T", 4);
        addJudge("数据结构", "哈希表查找时间复杂度是 O(n)。", "F", 4);
        addJudge("数据结构", "图可以用邻接矩阵存储。", "T", 4);
        addJudge("数据结构", "深度优先遍历使用队列实现。", "F", 4);

        // ========== Java 多选题 6道 ==========
        addMulti("Java", "以下哪些是 Java 基本数据类型？（多选）", "int", "String", "boolean", "double", "A,C,D", 5);
        addMulti("Java", "以下哪些是访问修饰符？（多选）", "public", "private", "static", "protected", "A,B,D", 5);
        addMulti("Java", "以下哪些集合类线程安全？（多选）", "ArrayList", "Vector", "Hashtable", "HashMap", "B,C", 5);
        addMulti("Java", "以下哪些是 JDK 自带工具？（多选）", "javac", "java", "maven", "jar", "A,B,D", 5);
        addMulti("Java", "哪些属于面向对象特性？（多选）", "封装", "继承", "编译", "多态", "A,B,D", 5);
        addMulti("Java", "以下哪些能用于创建线程？（多选）", "继承Thread", "实现Runnable", "实现Serializable", "实现Callable", "A,B,D", 5);

        // ========== 高数多选题 5道 ==========
        addMulti("高数", "下列哪些函数在 x→0 时是等价无穷小？（多选）", "sinx", "tanx", "1-cosx", "arcsinx", "A,B,D", 5);
        addMulti("高数", "以下哪些函数的导数是本身？（多选）", "e^x", "lnx", "sinx", "a^x/ln(a) 当 a=e 时", "A,D", 5);
        addMulti("高数", "以下哪些级数是收敛的？（多选）", "Σ(1/n²)", "Σ(1/n)", "Σ(1/2^n)", "Σn", "A,C", 5);
        addMulti("高数", "以下哪些是微分方程 y''+y=0 的解？（多选）", "sinx", "cosx", "e^x", "lnx", "A,B", 5);
        addMulti("高数", "以下哪些是求极限的方法？（多选）", "洛必达法则", "等价无穷小替换", "配方", "夹逼准则", "A,B,D", 5);

        // ========== 英语多选题 5道 ==========
        addMulti("英语", "下列哪些单词表示颜色？（多选）", "blue", "yellow", "table", "green", "A,B,D", 5);
        addMulti("英语", "以下哪些是英语时态？（多选）", "一般现在时", "现在进行时", "现在将来时", "现在过去时", "A,B", 5);
        addMulti("英语", "以下哪些词是介词？（多选）", "in", "run", "on", "beautiful", "A,C", 5);
        addMulti("英语", "以下哪些可以用于问路？（多选）", "Where is...", "How can I get to...", "What color is...", "Could you tell me the way to...", "A,B,D", 5);
        addMulti("英语", "以下哪些是水果的英文？（多选）", "apple", "dog", "banana", "orange", "A,C,D", 5);

        // ========== 数据结构多选题 5道 ==========
        addMulti("数据结构", "以下哪些是线性结构？（多选）", "数组", "栈", "树", "队列", "A,B,D", 5);
        addMulti("数据结构", "以下排序算法平均 O(nlogn) 的是？（多选）", "冒泡排序", "快速排序", "归并排序", "堆排序", "B,C,D", 5);
        addMulti("数据结构", "以下哪些是二叉树的遍历方式？（多选）", "前序遍历", "中序遍历", "层序遍历", "环序遍历", "A,B,C", 5);
        addMulti("数据结构", "以下哪些可用于解决哈希冲突？（多选）", "链地址法", "开放定址法", "冒泡法", "再哈希法", "A,B,D", 5);
        addMulti("数据结构", "以下哪些是图的存储方式？（多选）", "邻接矩阵", "邻接表", "顺序表", "十字链表", "A,B,D", 5);

        // 补充部分题目解析
        addExplanations();

        // 创建试卷（含多选题）
        createPaper("Java 基础测验", "Java", 60, 100, 10, 5, 5, 5, 2);
        createPaper("高等数学测验", "高数", 60, 100, 10, 5, 5, 5, 2);
        createPaper("大学英语测验", "英语", 60, 100, 10, 5, 5, 5, 2);
        createPaper("数据结构测验", "数据结构", 60, 100, 10, 5, 5, 5, 2);
    }

    private void addChoice(String subject, String content, String a, String b, String c, String d, String answer, int score) {
        Question q = new Question();
        q.setSubject(subject);
        q.setType("CHOICE");
        q.setContent(content);
        q.setOptionA(a); q.setOptionB(b); q.setOptionC(c); q.setOptionD(d);
        q.setAnswer(answer);
        q.setScore(score);
        q.setDifficulty("EASY");
        questionRepository.save(q);
    }

    private void addBlank(String subject, String content, String answer, int score) {
        Question q = new Question();
        q.setSubject(subject);
        q.setType("BLANK");
        q.setContent(content);
        q.setAnswer(answer);
        q.setScore(score);
        q.setDifficulty("EASY");
        questionRepository.save(q);
    }

    private void addEssay(String subject, String content, String answer, int score) {
        Question q = new Question();
        q.setSubject(subject);
        q.setType("ESSAY");
        q.setContent(content);
        q.setAnswer(answer);
        q.setScore(score);
        q.setDifficulty("MEDIUM");
        questionRepository.save(q);
    }

    private void addJudge(String subject, String content, String answer, int score) {
        Question q = new Question();
        q.setSubject(subject);
        q.setType("JUDGE");
        q.setContent(content);
        q.setAnswer(answer);
        q.setScore(score);
        q.setDifficulty("EASY");
        questionRepository.save(q);
    }

    private void addMulti(String subject, String content, String a, String b, String c, String d, String answer, int score) {
        Question q = new Question();
        q.setSubject(subject);
        q.setType("MULTI_CHOICE");
        q.setContent(content);
        q.setOptionA(a); q.setOptionB(b); q.setOptionC(c); q.setOptionD(d);
        q.setAnswer(answer);
        q.setScore(score);
        q.setDifficulty("MEDIUM");
        questionRepository.save(q);
    }

    private void addExplanations() {
        List<Question> all = questionRepository.findAll();
        Map<String, Question> map = new HashMap<>();
        for (Question q : all) map.put(q.getContent(), q);

        setExp(map, "Java 中 int 占几个字节？", "int 是 Java 的基本数据类型，占用 4 个字节（32 位），取值范围为 -2^31 ~ 2^31-1。");
        setExp(map, "以下哪个不是 Java 关键字？", "main 是 Java 程序的入口方法名，不是关键字。class、void、public 都是 Java 的关键字。");
        setExp(map, "JVM 代表什么？", "JVM（Java Virtual Machine）即 Java 虚拟机，负责解释执行 Java 字节码，实现平台无关性。");
        setExp(map, "子类继承父类用什么关键字？", "Java 使用 extends 关键字实现继承，一个类只能继承一个父类。implements 用于实现接口。");
        setExp(map, "Math.random() 返回范围是？", "Math.random() 返回 [0, 1) 区间的 double 值，包含 0 但不包含 1。");
        setExp(map, "栈的特点是什么？", "栈（Stack）是一种后进先出（LIFO）的数据结构，插入和删除都在同一端（栈顶）进行。");
        setExp(map, "二分查找要求数据？", "二分查找（Binary Search）要求待查找的序列必须有序，每次将查找范围折半，时间复杂度 O(log n)。");
        setExp(map, "队列的特点是什么？", "队列（Queue）是一种先进先出（FIFO）的数据结构，元素从队尾入队、从队头出队。");
        setExp(map, "lim(x→0) sinx/x = ?", "重要极限：当 x→0 时，sinx/x 的极限为 1。可通过夹逼定理或几何方法证明。");
        setExp(map, "f(x)=x²的导数是？", "根据幂函数求导公式 d(x^n)/dx = n·x^(n-1)，f(x)=x² 的导数为 2x。");
        setExp(map, "She ___ to school every day.", "主语 She 是第三人称单数，一般现在时中谓语动词需用第三人称单数形式 goes。");
        setExp(map, "I have ___ finished my homework.", "\"already\" 用于完成时态，表示\"已经\"，常用于肯定句中。yet 用于否定和疑问句。");
        setExp(map, "He is ___ than his brother.", "比较级用法：\"比较级 + than\" 表示\"比……更\"，tall 的比较级是 taller。");
        setExp(map, "Would you like ___ tea?", "在表示请求、建议的疑问句中，用 some 而不用 any，表示希望得到肯定回答。");
    }

    private void setExp(Map<String, Question> map, String content, String explanation) {
        Question q = map.get(content);
        if (q != null) {
            q.setExplanation(explanation);
            questionRepository.save(q);
        }
    }

    private void createPaper(String title, String subject, int duration, int totalScore,
                              int choiceCount, int multiChoiceCount, int judgeCount,
                              int blankCount, int essayCount) {
        ExamPaper paper = new ExamPaper();
        paper.setTitle(title);
        paper.setSubject(subject);
        paper.setDuration(duration);
        paper.setTotalScore(totalScore);
        paper.setChoiceCount(choiceCount);
        paper.setMultiChoiceCount(multiChoiceCount);
        paper.setJudgeCount(judgeCount);
        paper.setBlankCount(blankCount);
        paper.setEssayCount(essayCount);
        paperRepository.save(paper);
    }
}

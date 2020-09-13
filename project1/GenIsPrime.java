import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

/**
 * This class programmatically generates the class file for the following Java application:
 * 
 * <pre>
 * public class IsPrime {
 *     // Entry point.
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         boolean result = isPrime(n);
 *         if (result) {
 *             System.out.println(n + " is a prime number");
 *         } else {
 *             System.out.println(n + " is not a prime number");
 *         }
 *     }
 *
 *     // Returns true if n is prime, and false otherwise.
 *     private static boolean isPrime(int n) {
 *         if (n < 2) {
 *             return false;
 *         }
 *         for (int i = 2; i <= n / i; i++) {
 *             if (n % i == 0) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * }
 * </pre>
 **/
public class GenIsPrime {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class Factorial {
        modifiers.add("public");
        e.addClass(modifiers, "IsPrime", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);
        
        // int n = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0); // Load first arg to stack
        e.addNoArgInstruction(ICONST_0); // Load 0 onto stack
        e.addNoArgInstruction(AALOAD); // Load array ref to stack
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                                     "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1); // store val into variable 1
        
        // int result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)I");
        e.addNoArgInstruction(ISTORE_2);

        // System.out.println(result);
        // Get System.out onto the stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out",
                                     "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                                    "(I)Ljava/lang/StringBuffer;");

        // if (result != 1) goto notprime "
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ILOAD_2);
        e.addBranchInstruction(IF_ICMPNE, "NOTPRIME");

        // otherwise
        // sb.append(" is not a prime number")
        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                                     "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
        e.addBranchInstruction(GOTO, "PRINT");

        // Label NOTPRIME
        e.addLabel("NOTPRIME");
        // sb.append(" is not a prime number")
        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                                     "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
        
        // label PRINT: System.out.println(sb.toString());
        e.addLabel("PRINT");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return
        e.addNoArgInstruction(RETURN);
 

        // =====================================================================

        // private static boolean isPrime(int n) {
        // n = variable 0
        // i = variable 1
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)I", null, true);

        // if (n > 1) goto A
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_1);
        e.addBranchInstruction(IF_ICMPGT, "A");

        // otherwise return 0 (false)
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        // A: i = 2
        e.addLabel("A");
        e.addNoArgInstruction(ICONST_2);
        e.addNoArgInstruction(ISTORE_1);

        // D: if (i > (n / i)) goto B
        e.addLabel("D");
        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(IDIV);
        e.addBranchInstruction(IF_ICMPGT, "B");

        // if (n % i != 0) goto C
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(IREM); // modulo
        e.addNoArgInstruction(ICONST_0);
        e.addBranchInstruction(IF_ICMPNE, "C");

        // return false
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        // C: i++, goto D
        e.addLabel("C");
        e.addIINCInstruction(1, 1);
        e.addBranchInstruction(GOTO, "D");

        // return true
        e.addLabel("B");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);
 
        e.write();
    }
}

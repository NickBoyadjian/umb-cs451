package jminusminus;

import java.lang.reflect.GenericDeclaration;
import java.util.ArrayList;
import java.util.TreeMap;
import static jminusminus.CLConstants.*;

public class JSwitchStatement extends JStatement {
    private JExpression parExpression;
    private ArrayList<JSwitchBlockStatementGroup> switchBlockStatementGroup;

    JSwitchStatement (int line, JExpression e, ArrayList<JSwitchBlockStatementGroup> s) {
        super(line);
        this.parExpression = e;
        this.switchBlockStatementGroup = s;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // Analyze the condition and make sure it is an integer
        parExpression = (JExpression) parExpression.analyze(context);
        parExpression.type().mustMatchExpected(line(), Type.INT);
        parExpression.type = Type.INT;


        for (JSwitchBlockStatementGroup s : switchBlockStatementGroup) {
            s = (JSwitchBlockStatementGroup) s.analyze(context);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // generate the code for the parenthesized expression
        parExpression.codegen(output);

        // Initialize all the data
        String defaultLabel = output.createLabel();
        String exitLabel = output.createLabel();
        TreeMap<Integer, String> switchCasePairs = new TreeMap<>();
        ArrayList<String> caseLabels = new ArrayList<>();

        // get the lo and hig values
        int hi = Integer.MIN_VALUE;
        int lo = Integer.MAX_VALUE;
        for (JSwitchBlockStatementGroup block : switchBlockStatementGroup) {
            if (block.hi > hi) hi = block.hi;
            if (block.lo < lo) lo = block.lo;
        }

        // Loop through the labels in each group and populate caseLabels and pairs
        for (JSwitchBlockStatementGroup group : switchBlockStatementGroup) {
            for (JExpression label : group.switchLabels) {
                if (label != null) {
                    String caseLabel = output.createLabel();
                    int value = ((JLiteralInt) label).getImage();
                    caseLabels.add(caseLabel);
                    switchCasePairs.put(value, caseLabel);
                }
            }
        }

        int nlabels = switchCasePairs.size();
        long table_space_cost = 4 + (( long ) hi - lo + 1);
        long table_time_cost = 3;
        long lookup_space_cost = 3 + 2 * ( long ) nlabels;
        long lookup_time_cost = nlabels;
        int opcode = ( nlabels > 0 && table_space_cost + 3 * table_time_cost <= lookup_space_cost
                +  3 * lookup_time_cost ) ? TABLESWITCH : LOOKUPSWITCH;


        if (opcode == TABLESWITCH) {
            output.addTABLESWITCHInstruction(defaultLabel, lo, hi, caseLabels);
        } else {
            output.addLOOKUPSWITCHInstruction(defaultLabel, nlabels, switchCasePairs);
        }

        int index = 0;
        boolean defaulted = false;
        // Loop through the statements in each group and generate the code
        for (JSwitchBlockStatementGroup group : switchBlockStatementGroup) {
            for (JExpression label : group.switchLabels) {
                if (label != null) {
                    output.addLabel(caseLabels.get(index));
                    index++;
                } else {
                    defaulted = true;
                    output.addLabel(defaultLabel);
                }
            }

            if (!defaulted)
                output.addLabel(defaultLabel);

            for (JStatement block : group.blockStatements) {
                if (block instanceof JBreakStatement) {
                    output.addBranchInstruction(GOTO, exitLabel);
                } else {
                    block.codegen(output);
                }
            }
        }

        output.addLabel(exitLabel);


    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JSwitchStatement:" + line, e);

        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        parExpression.toJSON(e1);

        switchBlockStatementGroup.forEach(statement -> {
            JSONElement e2 = new JSONElement();
            e.addChild("SwitchStatementGroup", e2);
            statement.toJSON(e2);
        });

    }

}

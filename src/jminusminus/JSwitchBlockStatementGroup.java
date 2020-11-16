package jminusminus;

import java.lang.reflect.Array;
import java.util.ArrayList;

class JSwitchBlockStatementGroup extends JAST {

    public ArrayList<JExpression> switchLabels;
    public ArrayList<JStatement> blockStatements;
    public int hi = Integer.MIN_VALUE;
    public int lo = Integer.MAX_VALUE;

    public JSwitchBlockStatementGroup(int line, ArrayList<JExpression> switchLabels, ArrayList<JStatement> blockStatements) {
        super(line);
        this.switchLabels = switchLabels;
        this.blockStatements = blockStatements;
    }


    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        // Analyze the case expressions and make sure they are integer literals
        for (int i = 0; i < switchLabels.size(); i++) {
            if (switchLabels.get(i) != null) {
                JLiteralInt literalInt =  (JLiteralInt) switchLabels.get(i).analyze(context);
                switchLabels.set(i, literalInt);
                switchLabels.get(i).type().mustMatchExpected(line(), Type.INT);
                switchLabels.get(i).type = Type.INT;

                if (literalInt.getImage() > hi) {
                    hi = literalInt.getImage();
                }

                if (literalInt.getImage() < lo) {
                    lo = literalInt.getImage();
                }
            }
        }

        // Analyze the statements in each case group
        for (int i = 0; i < blockStatements.size(); i++) {
            blockStatements.set(i, (JStatement) blockStatements.get(i).analyze(context));
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
//        switchLabels.forEach(label -> label.codegen(output));
//        blockStatements.forEach(block -> block.codegen(output));
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {

        switchLabels.forEach(label -> {
            JSONElement e = new JSONElement();
            if (label == null) {
                json.addChild("Default", e);
            } else {
                json.addChild("Case", e);
                label.toJSON(e);
            }
        });

        blockStatements.forEach(block -> {
            block.toJSON(json);
        });
    }

}

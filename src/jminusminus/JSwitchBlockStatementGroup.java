package jminusminus;

import java.util.ArrayList;

class JSwitchBlockStatementGroup extends JAST {

    private ArrayList<JExpression> switchLabels;
    private ArrayList<JStatement> blockStatements;

    public JSwitchBlockStatementGroup(int line, ArrayList<JExpression> switchLabels, ArrayList<JStatement> blockStatements) {
        super(line);
        this.switchLabels = switchLabels;
        this.blockStatements = blockStatements;
    }


    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {

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

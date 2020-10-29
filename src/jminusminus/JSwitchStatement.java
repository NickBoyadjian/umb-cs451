package jminusminus;

import java.util.ArrayList;

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
